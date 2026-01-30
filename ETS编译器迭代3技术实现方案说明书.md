# ETS 编译器迭代3技术实现方案说明书

## 文档版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|-----|------|---------|--------|
| v2.0 | 2026-01-30 | 基于原始编译器实现方案重写 | Claude AI Assistant |

---

## 一、原始编译器架构分析

### 1.1 核心设计原则

基于对 `src/main/resources/developtools_ace_ets2bundle` 的深入分析，原始 ETS 编译器采用以下核心设计原则：

#### 原则1：最小干预原则（Pass-Through Strategy）

**关键发现：** 原始编译器**只处理 ETS 特有的语法**，对于所有标准 JavaScript/TypeScript 语法，采用**完全保留**策略。

**证据来源：**
- `compiler/src/process_ui_syntax.ts` 第444行：`return ts.visitEachChild(node, processAllNodes, context);`
- 使用 TypeScript Compiler API 的 `ts.visitEachChild` 进行 AST 遍历
- 只有 ETS 特有节点被特殊处理，其他节点通过默认行为传递

**示例代码（process_ui_syntax.ts）：**
```typescript
// 第299-444行：processAllNodes 函数
function processAllNodes(node: ts.Node): ts.Node {
    // 处理 ETS 特有的语法
    if (projectConfig.compileMode === 'esmodule' && process.env.compileTool === 'rollup') {
        // ...
    }

    // 关键：使用 ts.visitEachChild 递归处理所有子节点
    // 标准的 JS/TS 语法会自动通过，无需特殊处理
    return ts.visitEachChild(node, processAllNodes, context);
}
```

#### 原则2：双层架构

1. **ETS 特有转换层**：
   - 处理 `@Component`、`@State`、`@Prop`、`@Link` 等装饰器
   - 处理 `struct` 声明到 `class` 的转换
   - 处理组件的 `build()` 方法
   - 处理资源引用 `$r()`、`$rawfile()`

2. **标准代码生成层**：
   - 依赖 TypeScript Compiler API 的内置 emitter
   - 自动处理所有标准 JavaScript/TypeScript 语法
   - 包括：字面量、循环、条件、异常处理等

### 1.2 语法分类处理策略

#### A类：ETS 特有语法（需要转换）

| 语法类型 | 处理方式 | 实现文件 |
|---------|---------|---------|
| `@Component` 装饰器 | 转换为 `class extends View` | process_custom_component.ts |
| `@State` 装饰器 | 转换为 `ObservedPropertySimple` | process_component_member.ts |
| `@Prop` 装饰器 | 转换为 `SynchedPropertySimpleOneWay` | process_component_member.ts |
| `@Link` 装饰器 | 转换为 `SynchedPropertySimpleTwoWay` | process_component_member.ts |
| `@Builder` 装饰器 | 转换为 Builder 函数 | process_interop_builder.ts |
| `struct` 声明 | 预处理为 `class` | process_ui_syntax.ts |
| 组件 `build()` 方法 | 转换为 `render()` 方法 | process_component_build.ts |
| 资源引用 `$r()` | 转换为运行时函数调用 | process_ui_syntax.ts |

#### B类：标准 JavaScript/TypeScript 语法（保留原样）

| 语法类型 | 处理方式 | 说明 |
|---------|---------|-----|
| **字面量** | 保留原样 | `true`, `false`, `null`, `undefined`, 数字, 字符串 |
| **循环语句** | 保留原样 | `for...of`, `for...in`, `while`, `do...while`, `for` |
| **条件语句** | 保留原样 | `if`, `switch`, `case`, `default` |
| **异常处理** | 保留原样 | `try`, `catch`, `finally`, `throw` |
| **跳转语句** | 保留原样 | `break`, `continue`, `return` |
| **表达式** | 保留原样 | 一元运算、二元运算、三元运算 |
| **数组/对象操作** | 保留原样 | 索引访问、展开语法、解构 |
| **现代语法** | 保留原样 | 可选链 `?.`、空值合并 `??`、模板字符串 |
| **函数** | 保留原样 | 箭头函数、async/await、生成器 |

### 1.3 关键代码模式

#### 模式1：AST 访问者模式（process_ui_syntax.ts）

```typescript
// ETS 特有处理的典型模式
function processAllNodes(node: ts.Node): ts.Node {
    // 1. 检查是否是 ETS 特有的节点
    if (ts.isStructDeclaration(node)) {
        // 特殊处理 struct → class
        return transformStructToClass(node);
    }

    // 2. 对于非 ETS 特有节点，使用默认遍历
    return ts.visitEachChild(node, processAllNodes, context);
}
```

#### 模式2：装饰器提取（process_ui_syntax.ts 第52-82行）

```typescript
function preprocessEts(sourceCode) {
    let processedCode = sourceCode;

    // 1. 提取 struct 前的装饰器
    const structDecoratorPattern = /@\w+\s*(?:\([^)]*\))?\s*\b(?:export\s+)?struct\s+/g;

    // 2. 替换 struct 为 class
    processedCode = processedCode.replace(/\bstruct\s+/g, 'class ');

    return {
        code: processedCode,
        decorators: extractedDecorators
    };
}
```

#### 模式3：组件 create/pop 转换（process_component_build.ts）

```typescript
// UI 组件的特殊转换逻辑
function processComponentChild(node: ts.Node): ts.Node {
    if (ts.isCallExpression(node)) {
        const componentName = getComponentName(node);

        // 1. 识别内置组件
        if (INNER_COMPONENT_NAMES.has(componentName)) {
            return transformComponentCreatePop(node);
        }

        // 2. 识别特殊组件（ForEach, If）
        if (componentName === 'ForEach') {
            return transformForEach(node);
        }
    }

    return node;
}
```

---

## 二、ets2jsc 项目架构

### 2.1 现有三层架构

ets2jsc 项目采用不同于原始编译器的架构：

```
┌─────────────────────────────────────────────────────────────┐
│  ets2jsc 编译器架构                                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  第1层: parse-ets.js                                       │
│  ──────────────────────────────                            │
│  - 使用 TypeScript Compiler API 解析 ETS/TypeScript        │
│  - 预处理 ETS 语法（struct → class）                        │
│  - 将 AST 转换为 JSON 格式                                  │
│  - 输出: JSON AST                                          │
│                                                             │
│  第2层: TypeScriptScriptParser.java                         │
│  ────────────────────────────────────                        │
│  - 解析 JSON AST                                            │
│  - 构建 Java AST 节点                                       │
│  - 实现 ETS 特有语法的转换                                  │
│  - 实现 JS 表达式到字符串的转换                             │
│  - 输出: Java AST                                           │
│                                                             │
│  第3层: CodeGenerator                                       │
│  ───────────────────────                                    │
│  - 遍历 Java AST                                            │
│  - 生成 JavaScript 代码                                     │
│  - 输出: JavaScript 源码                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 与原始编译器的关键差异

| 特性 | 原始编译器 (developtools_ace_ets2bundle) | ets2jsc 编译器 |
|-----|----------------------------------------|---------------|
| 实现语言 | TypeScript/JavaScript | Java |
| AST 遍历 | 使用 `ts.visitEachChild` 自动遍历 | 手动遍历 JSON |
| 代码生成 | TypeScript Compiler API emitter | 手动生成字符串 |
| 标准语法处理 | 自动保留（TypeScript emit） | 需要手动实现 |
| ETS 特殊语法 | AST 转换 | 字符串/AST 转换 |

### 2.3 实现策略

由于 ets2jsc 无法依赖 TypeScript 的 emit，因此需要：

1. **对于 ETS 特有语法**：参考原始编译器的转换逻辑，在 Java 中实现等效转换
2. **对于标准 JS/TS 语法**：手动实现 AST 节点到 JavaScript 代码的转换，确保输出与 TypeScript emit 一致

---

## 三、迭代3技术实现方案

### 3.1 P0 级别需求实现方案

#### P0-LITERAL: 字面量表达式转换

**原始编译器处理方式：** 完全保留，由 TypeScript emit 自动处理

**ets2jsc 实现方案：**

##### 3.1.1.1 parse-ets.js 修改

**文件位置：** `ets2jsc/src/main/resources/typescript-parser/parse-ets.js`

**问题分析：** 当前 `convertAstToJson` 函数中缺少字面量表达式的 `text` 属性输出

**解决方案：** 在 `convertAstToJson` 函数的 switch 语句中添加字面量处理

**插入位置：** 第443行（StringLiteral）之后

```javascript
// 在 convertAstToJson 函数中添加以下 case：

case ts.SyntaxKind.TrueKeyword:
    result.text = 'true';
    break;

case ts.SyntaxKind.FalseKeyword:
    result.text = 'false';
    break;

case ts.SyntaxKind.NullKeyword:
    result.text = 'null';
    break;

case ts.SyntaxKind.UndefinedKeyword:
    result.text = 'undefined';
    break;
```

**参考模式：** 参考第443-449行 StringLiteral 的处理方式

##### 3.1.1.2 TypeScriptScriptParser.java 修改

**文件位置：** `ets2jsc/src/main/java/com/ets2jsc/parser/TypeScriptScriptParser.java`

**现有代码：** 第568-575行 `convertExpressionToString` 方法已有字面量处理

**验证：** 确认以下 case 存在并正确

```java
case "TrueLiteral":
    return "true";
case "FalseLiteral":
    return "false";
case "NullLiteral":
    return "null";
case "UndefinedLiteral":
    return "undefined";
```

**无需修改：** 现有实现已正确

---

#### P0-LOOP: 循环语句转换

**原始编译器处理方式：** 完全保留，由 `ts.visitEachChild` 自动传递，不做任何特殊处理

**ets2jsc 实现方案：**

##### 3.1.2.1 parse-ets.js 修改

**问题：** 当前 `convertAstToJson` 函数中缺少循环语句的 case 处理

**解决方案：** 在 `convertAstToJson` 函数中添加循环语句的 JSON 序列化

**插入位置：** 第459行（AwaitExpression）之后

```javascript
case ts.SyntaxKind.ForOfStatement:
    result.initializer = convertAstToJson(node.initializer);
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    result.awaitModifier = node.awaitModifier ? true : false;
    break;

case ts.SyntaxKind.ForInStatement:
    result.initializer = convertAstToJson(node.initializer);
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    break;

case ts.SyntaxKind.WhileStatement:
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    break;

case ts.SyntaxKind.DoStatement:
    result.expression = convertAstToJson(node.expression);
    result.statement = convertAstToJson(node.statement);
    break;

case ts.SyntaxKind.ForStatement:
    result.initializer = node.initializer ? convertAstToJson(node.initializer) : null;
    result.condition = node.condition ? convertAstToJson(node.condition) : null;
    result.incrementor = node.incrementor ? convertAstToJson(node.incrementor) : null;
    result.statement = convertAstToJson(node.statement);
    break;
```

**参考模式：** 参考第459-463行 AwaitExpression 的处理方式

**同时更新 kindMap：** 在 `getSyntaxKindName` 函数中添加（第514-563行）

```javascript
const kindMap = {
    // ... 现有映射
    [ts.SyntaxKind.ForOfStatement]: 'ForOfStatement',
    [ts.SyntaxKind.ForInStatement]: 'ForInStatement',
    [ts.SyntaxKind.WhileStatement]: 'WhileStatement',
    [ts.SyntaxKind.DoStatement]: 'DoStatement',
    [ts.SyntaxKind.ForStatement]: 'ForStatement',
};
```

##### 3.1.2.2 TypeScriptScriptParser.java 修改

**文件位置：** `ets2jsc/src/main/java/com/ets2jsc/parser/TypeScriptScriptParser.java`

**步骤1：** 在 `convertJsonNode` 方法中添加新的 case（第195-251行）

```java
case "ForOfStatement":
    return convertForOfStatement(json);
case "ForInStatement":
    return convertForInStatement(json);
case "WhileStatement":
    return convertWhileStatement(json);
case "DoStatement":
    return convertDoStatement(json);
case "ForStatement":
    return convertForStatement(json);
```

**步骤2：** 替换第790-794行的 `convertForOfStatement` 方法实现

```java
private AstNode convertForOfStatement(JsonObject json) {
    JsonObject initializer = json.getAsJsonObject("initializer");
    JsonObject expression = json.getAsJsonObject("expression");
    JsonObject statement = json.getAsJsonObject("statement");
    boolean awaitModifier = json.has("awaitModifier") && json.get("awaitModifier").getAsBoolean();

    String initStr = initializer != null ? convertExpressionToString(initializer) : "";
    String exprStr = expression != null ? convertExpressionToString(expression) : "";

    // 构建 for...of 语句字符串
    StringBuilder sb = new StringBuilder();
    if (awaitModifier) {
        sb.append("for await (");
    } else {
        sb.append("for (");
    }
    sb.append(initStr).append(" of ").append(exprStr).append(") ");

    // 处理循环体
    AstNode stmt = statement != null ? convertJsonNode(statement) : null;
    if (stmt instanceof Block) {
        sb.append(((Block) stmt).toCodeString());
    } else if (stmt != null) {
        sb.append("{ ").append(stmt.toCodeString()).append(" }");
    } else {
        sb.append("{ }");
    }

    return new ExpressionStatement(sb.toString());
}
```

**步骤3：** 在第794行之后添加其他循环语句的转换方法

```java
private AstNode convertForInStatement(JsonObject json) {
    JsonObject initializer = json.getAsJsonObject("initializer");
    JsonObject expression = json.getAsJsonObject("expression");
    JsonObject statement = json.getAsJsonObject("statement");

    String initStr = initializer != null ? convertExpressionToString(initializer) : "";
    String exprStr = expression != null ? convertExpressionToString(expression) : "";

    StringBuilder sb = new StringBuilder();
    sb.append("for (").append(initStr).append(" in ").append(exprStr).append(") ");

    AstNode stmt = statement != null ? convertJsonNode(statement) : null;
    if (stmt instanceof Block) {
        sb.append(((Block) stmt).toCodeString());
    } else if (stmt != null) {
        sb.append("{ ").append(stmt.toCodeString()).append(" }");
    } else {
        sb.append("{ }");
    }

    return new ExpressionStatement(sb.toString());
}

private AstNode convertWhileStatement(JsonObject json) {
    JsonObject expression = json.getAsJsonObject("expression");
    JsonObject statement = json.getAsJsonObject("statement");

    String condition = convertExpressionToString(expression);

    StringBuilder sb = new StringBuilder();
    sb.append("while (").append(condition).append(") ");

    AstNode stmt = statement != null ? convertJsonNode(statement) : null;
    if (stmt instanceof Block) {
        sb.append(((Block) stmt).toCodeString());
    } else if (stmt != null) {
        sb.append("{ ").append(stmt.toCodeString()).append(" }");
    } else {
        sb.append("{ }");
    }

    return new ExpressionStatement(sb.toString());
}

private AstNode convertDoStatement(JsonObject json) {
    JsonObject expression = json.getAsJsonObject("expression");
    JsonObject statement = json.getAsJsonObject("statement");

    String condition = convertExpressionToString(expression);

    StringBuilder sb = new StringBuilder();
    sb.append("do ");

    AstNode stmt = statement != null ? convertJsonNode(statement) : null;
    if (stmt instanceof Block) {
        sb.append(((Block) stmt).toCodeString());
    } else if (stmt != null) {
        sb.append("{ ").append(stmt.toCodeString()).append(" }");
    } else {
        sb.append("{ }");
    }
    sb.append(" while (").append(condition).append(")");

    return new ExpressionStatement(sb.toString());
}

private AstNode convertForStatement(JsonObject json) {
    JsonObject initializer = json.getAsJsonObject("initializer");
    JsonObject condition = json.getAsJsonObject("condition");
    JsonObject incrementor = json.getAsJsonObject("incrementor");
    JsonObject statement = json.getAsJsonObject("statement");

    String initStr = initializer != null ? convertExpressionToString(initializer) : "";
    String condStr = condition != null ? convertExpressionToString(condition) : "";
    String incrStr = incrementor != null ? convertExpressionToString(incrementor) : "";

    StringBuilder sb = new StringBuilder();
    sb.append("for (").append(initStr);
    if (!condStr.isEmpty()) {
        sb.append("; ").append(condStr);
    } else {
        sb.append(";");
    }
    if (!incrStr.isEmpty()) {
        sb.append("; ").append(incrStr);
    }
    sb.append(") ");

    AstNode stmt = statement != null ? convertJsonNode(statement) : null;
    if (stmt instanceof Block) {
        sb.append(((Block) stmt).toCodeString());
    } else if (stmt != null) {
        sb.append("{ ").append(stmt.toCodeString()).append(" }");
    } else {
        sb.append("{ }");
    }

    return new ExpressionStatement(sb.toString());
}
```

**参考模式：** 参考第488-522行 `convertIfStatement` 方法的实现模式

---

#### P0-EXPR: 特殊表达式转换

**原始编译器处理方式：** 完全保留，由 TypeScript emit 自动处理

**ets2jsc 实现方案：**

##### 3.1.3.1 前缀/后缀一元表达式

**问题：** 前缀一元表达式（`!expr`, `-expr`）和后缀一元表达式（`i++`, `i--`）被转换为 JSON 对象

**parse-ets.js 修改：**

现有代码第374-382行已处理 PrefixUnaryExpression 和 PostfixUnaryExpression

**验证：** 确认输出包含 `operator` 和 `operand` 字段

**TypeScriptScriptParser.java 修改：**

现有代码第630-643行已处理一元表达式

**验证：** 确认逻辑正确

##### 3.1.3.2 非空断言表达式

**parse-ets.js 修改：**

**插入位置：** 第382行之后

```javascript
case ts.SyntaxKind.NonNullExpression:
    result.expression = convertAstToJson(node.expression);
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.NonNullExpression]: 'NonNullExpression',
```

**TypeScriptScriptParser.java 修改：**

**在 convertExpressionToString 方法中添加：**

```java
case "NonNullExpression": {
    // 非空断言 ! 在运行时没有意义，直接输出表达式
    JsonObject expr = exprJson.getAsJsonObject("expression");
    return expr != null ? convertExpressionToString(expr) : "";
}
```

**或在 convertJsonNode 中：**

```java
case "NonNullExpression":
    // 非空断言在 JS 中不需要，直接转换内部表达式
    JsonObject expr = json.getAsJsonObject("expression");
    return convertJsonNode(expr);
```

##### 3.1.3.3 类型断言表达式

**parse-ets.js 修改：**

**插入位置：** 第382行之后

```javascript
case ts.SyntaxKind.AsExpression:
case ts.SyntaxKind.TypeAssertion:
    result.expression = convertAstToJson(node.expression);
    result.type = node.type ? node.type.getText() : null;
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.AsExpression]: 'AsExpression',
[ts.SyntaxKind.TypeAssertion]: 'TypeAssertion',
```

**TypeScriptScriptParser.java 修改：**

**在 convertExpressionToString 方法中添加：**

```java
case "AsExpression":
case "TypeAssertion": {
    // 类型断言在 JS 中不需要，直接输出表达式
    JsonObject expr = exprJson.getAsJsonObject("expression");
    return expr != null ? convertExpressionToString(expr) : "";
}
```

---

#### P0-ARRAY-ACCESS: 数组/对象访问表达式

**原始编译器处理方式：** 完全保留

**ets2jc 现有实现：** 现有代码第610-617行已处理 ElementAccessExpression

**验证：** 确认逻辑正确

**现有代码：**
```java
case "ElementAccessExpression": {
    JsonObject elementObj = exprJson.getAsJsonObject("expression");
    String elementStr = elementObj != null ? convertExpressionToString(elementObj) : "";
    JsonObject argumentExpr = exprJson.getAsJsonObject("argumentExpression");
    String argStr = argumentExpr != null ? convertExpressionToString(argumentExpr) : "";
    return elementStr + "[" + argStr + "]";
}
```

**无需修改：** 实现已正确

---

#### P0-EMPTY-VALUE: 空值处理

**原始编译器处理方式：** 完全保留

**ets2jc 实现方案：**

**问题分析：** 空字符串 `""` 可能在某些情况下被错误处理为空值

**parse-ets.js 验证：** 确认 StringLiteral 处理正确（第443-445行）

```javascript
case ts.SyntaxKind.StringLiteral:
    result.text = node.text;
    break;
```

**TypeScriptScriptParser.java 验证：** 确认 StringLiteral 处理正确（第553-556行）

```java
case "StringLiteral":
    String strText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
    return "\"" + strText + "\"";
```

**无需修改：** 空字符串会被正确处理为 `""`

---

### 3.2 P1 级别需求实现方案

#### P1-SWITCH: Switch 语句

**原始编译器处理方式：** 完全保留，由 `ts.visitEachChild` 自动传递

**ets2jc 实现方案：**

##### parse-ets.js 修改

**插入位置：** 第459行之后

```javascript
case ts.SyntaxKind.SwitchStatement:
    result.expression = convertAstToJson(node.expression);
    result.caseBlock = convertAstToJson(node.caseBlock);
    break;

case ts.SyntaxKind.CaseBlock:
    result.clauses = [];
    for (const clause of node.clauses) {
        result.clauses.push(convertAstToJson(clause));
    }
    break;

case ts.SyntaxKind.CaseClause:
    result.expression = convertAstToJson(node.expression);
    result.statements = [];
    for (const stmt of node.statements) {
        result.statements.push(convertAstToJson(stmt));
    }
    break;

case ts.SyntaxKind.DefaultClause:
    result.statements = [];
    for (const stmt of node.statements) {
        result.statements.push(convertAstToJson(stmt));
    }
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.SwitchStatement]: 'SwitchStatement',
[ts.SyntaxKind.CaseBlock]: 'CaseBlock',
[ts.SyntaxKind.CaseClause]: 'CaseClause',
[ts.SyntaxKind.DefaultClause]: 'DefaultClause',
```

##### TypeScriptScriptParser.java 修改

**在 convertJsonNode 方法中添加：**

```java
case "SwitchStatement":
    return convertSwitchStatement(json);
```

**新增方法：**

```java
private AstNode convertSwitchStatement(JsonObject json) {
    JsonObject expression = json.getAsJsonObject("expression");
    JsonObject caseBlock = json.getAsJsonObject("caseBlock");

    String condition = convertExpressionToString(expression);

    // 构建完整的 switch 语句字符串
    StringBuilder sb = new StringBuilder();
    sb.append("switch (").append(condition).append(") {\n");

    if (caseBlock != null) {
        JsonArray clauses = caseBlock.getAsJsonArray("clauses");
        if (clauses != null) {
            for (JsonElement clauseElem : clauses) {
                JsonObject clause = clauseElem.getAsJsonObject();
                String kindName = clause.has("kindName") ? clause.get("kindName").getAsString() : "";

                if ("CaseClause".equals(kindName)) {
                    JsonObject clauseExpr = clause.getAsJsonObject("expression");
                    String caseExpr = convertExpressionToString(clauseExpr);
                    sb.append("  case ").append(caseExpr).append(":\n");

                    JsonArray stmts = clause.getAsJsonArray("statements");
                    if (stmts != null) {
                        for (JsonElement stmtElem : stmts) {
                            JsonObject stmt = stmtElem.getAsJsonObject();
                            AstNode stmtNode = convertJsonNode(stmt);
                            if (stmtNode != null) {
                                sb.append("    ").append(stmtNode.toCodeString());
                            }
                        }
                    }
                    sb.append("    break;\n");
                } else if ("DefaultClause".equals(kindName)) {
                    sb.append("  default:\n");

                    JsonArray stmts = clause.getAsJsonArray("statements");
                    if (stmts != null) {
                        for (JsonElement stmtElem : stmts) {
                            JsonObject stmt = stmtElem.getAsJsonObject();
                            AstNode stmtNode = convertJsonNode(stmt);
                            if (stmtNode != null) {
                                sb.append("    ").append(stmtNode.toCodeString());
                            }
                        }
                    }
                }
            }
        }
    }

    sb.append("}");

    return new ExpressionStatement(sb.toString());
}
```

---

#### P1-EXCEPTION: 异常处理语句

**原始编译器处理方式：** 完全保留

**ets2jc 实现方案：**

##### parse-ets.js 修改

**插入位置：** 第459行之后

```javascript
case ts.SyntaxKind.TryStatement:
    result.tryBlock = convertAstToJson(node.tryBlock);
    result.catchClause = node.catchClause ? convertAstToJson(node.catchClause) : null;
    result.finallyBlock = node.finallyBlock ? convertAstToJson(node.finallyBlock) : null;
    break;

case ts.SyntaxKind.CatchClause:
    result.variableDeclaration = node.variableDeclaration ? convertAstToJson(node.variableDeclaration) : null;
    result.block = convertAstToJson(node.block);
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.TryStatement]: 'TryStatement',
[ts.SyntaxKind.CatchClause]: 'CatchClause',
```

---

#### P1-JUMP: 跳转语句

**原始编译器处理方式：** 完全保留

**ets2jc 实现方案：**

##### parse-ets.js 修改

**插入位置：** 第459行之后

```javascript
case ts.SyntaxKind.BreakStatement:
    result.label = node.label ? node.label.getText() : null;
    break;

case ts.SyntaxKind.ContinueStatement:
    result.label = node.label ? node.label.getText() : null;
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.BreakStatement]: 'BreakStatement',
[ts.SyntaxKind.ContinueStatement]: 'ContinueStatement',
```

##### TypeScriptScriptParser.java 修改

**在 convertJsonNode 方法中添加：**

```java
case "BreakStatement":
    return new ExpressionStatement("break;");
case "ContinueStatement":
    return new ExpressionStatement("continue;");
```

---

#### P1-SPREAD: 展开语法

**原始编译器处理方式：** 完全保留

**ets2jc 实现方案：**

##### parse-ets.js 修改

**插入位置：** 第478行之后（ArrayLiteralExpression）

```javascript
case ts.SyntaxKind.SpreadElement:
    result.expression = convertAstToJson(node.expression);
    break;

case ts.SyntaxKind.SpreadAssignment:
    result.expression = convertAstToJson(node.expression);
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.SpreadElement]: 'SpreadElement',
[ts.SyntaxKind.SpreadAssignment]: 'SpreadAssignment',
```

##### TypeScriptScriptParser.java 修改

**在 convertExpressionToString 方法中添加（处理数组展开）：**

```java
case "SpreadElement": {
    JsonObject expr = exprJson.getAsJsonObject("expression");
    String exprStr = expr != null ? convertExpressionToString(expr) : "";
    return "..." + exprStr;
}
```

**在 convertArrayLiteralToString 方法中（第683-697行）修改，识别 SpreadElement：**

```java
private String convertArrayLiteralToString(JsonObject json) {
    JsonArray elements = json.getAsJsonArray("elements");
    if (elements == null || elements.size() == 0) {
        return "[]";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < elements.size(); i++) {
        if (i > 0) sb.append(", ");

        JsonObject elem = elements.get(i).getAsJsonObject();
        String elemKind = elem.has("kindName") ? elem.get("kindName").getAsString() : "";

        if ("SpreadElement".equals(elemKind)) {
            // 处理展开元素
            JsonObject expr = elem.getAsJsonObject("expression");
            String exprStr = expr != null ? convertExpressionToString(expr) : "";
            sb.append("...").append(exprStr);
        } else {
            sb.append(convertExpressionToString(elem));
        }
    }
    sb.append("]");
    return sb.toString();
}
```

---

#### P1-TEMPLATE: 模板字符串

**原始编译器处理方式：** 完全保留

**ets2jc 实现方案：**

##### parse-ets.js 修改

**插入位置：** 第478行之后

```javascript
case ts.SyntaxKind.TemplateExpression:
    result.head = node.head.text;
    result.templateSpans = [];
    for (const span of node.templateSpans) {
        result.templateSpans.push({
            expression: convertAstToJson(span.expression),
            literal: span.literal.text
        });
    }
    break;

case ts.SyntaxKind.NoSubstitutionTemplateLiteral:
    result.text = node.text;
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.TemplateExpression]: 'TemplateExpression',
[ts.SyntaxKind.NoSubstitutionTemplateLiteral]: 'NoSubstitutionTemplateLiteral',
```

##### TypeScriptScriptParser.java 修改

**在 convertExpressionToString 方法中添加：**

```java
case "TemplateExpression": {
    String head = exprJson.has("head") ? exprJson.get("head").getAsString() : "";
    JsonArray spans = exprJson.getAsJsonArray("templateSpans");

    StringBuilder sb = new StringBuilder();
    sb.append("`").append(escapeTemplateString(head));

    if (spans != null) {
        for (JsonElement span : spans) {
            JsonObject spanObj = span.getAsJsonObject();
            JsonObject expr = spanObj.getAsJsonObject("expression");
            String literal = spanObj.has("literal") ? spanObj.get("literal").getAsString() : "";

            String exprStr = expr != null ? convertExpressionToString(expr) : "";
            sb.append("${").append(exprStr).append("}");
            sb.append(escapeTemplateString(literal));
        }
    }

    sb.append("`");
    return sb.toString();
}

case "NoSubstitutionTemplateLiteral": {
    String text = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
    return "`" + escapeTemplateString(text) + "`";
}
```

**新增辅助方法：**

```java
private String escapeTemplateString(String str) {
    // 转义模板字符串中的特殊字符
    return str.replace("\\", "\\\\").replace("`", "\\`");
}
```

---

### 3.3 P2 级别需求实现方案

#### P2-MODERN-OP: 现代运算符

**原始编译器处理方式：** 完全保留

**ets2jc 实现方案：**

##### parse-ets.js 修改

**插入位置：** 第459行之后

```javascript
case ts.SyntaxKind.PropertyAccessChain:
    result.expression = convertAstToJson(node.expression);
    result.name = node.name.text;
    result.questionDotToken = node.questionDotToken ? true : false;
    break;

case ts.SyntaxKind.ElementAccessChain:
    result.expression = convertAstToJson(node.expression);
    result.argumentExpression = convertAstToJson(node.argumentExpression);
    result.questionDotToken = node.questionDotToken ? true : false;
    break;
```

**kindMap 添加：**

```javascript
[ts.SyntaxKind.PropertyAccessChain]: 'PropertyAccessChain',
[ts.SyntaxKind.ElementAccessChain]: 'ElementAccessChain',
```

##### TypeScriptScriptParser.java 修改

**在 convertExpressionToString 方法中添加：**

```java
case "PropertyAccessChain": {
    JsonObject exprObj = exprJson.getAsJsonObject("expression");
    String exprStr = exprObj != null ? convertExpressionToString(exprObj) : "";
    String name = exprJson.has("name") ? exprJson.get("name").getAsString() : "";
    boolean questionDot = exprJson.has("questionDotToken") && exprJson.get("questionDotToken").getAsBoolean();

    String separator = questionDot ? "?." : ".";
    return exprStr + separator + name;
}

case "ElementAccessChain": {
    JsonObject exprObj = exprJson.getAsJsonObject("expression");
    String exprStr = exprObj != null ? convertExpressionToString(exprObj) : "";
    JsonObject argExpr = exprJson.getAsJsonObject("argumentExpression");
    String argStr = argExpr != null ? convertExpressionToString(argExpr) : "";
    boolean questionDot = exprJson.has("questionDotToken") && exprJson.get("questionDotToken").getAsBoolean();

    String separator = questionDot ? "?." : "";
    return exprStr + separator + "[" + argStr + "]";
}
```

---

## 四、实现优先级与计划

### 4.1 实现顺序

基于原始编译器的"最小干预"原则和问题的紧急程度，建议按以下顺序实现：

**第1周：P0 核心问题修复**
1. P0-LITERAL: 字面量表达式转换（修复 JSON 序列化问题）
2. P0-ARRAY-ACCESS: 数组/对象访问表达式（验证现有实现）
3. P0-EMPTY-VALUE: 空值处理（验证现有实现）
4. P0-EXPR: 特殊表达式转换（非空断言、类型断言）

**第2周：P0 循环语句**
5. P0-LOOP: 循环语句转换（替换占位符实现）

**第3-4周：P1 重要功能**
6. P1-SWITCH: Switch 语句
7. P1-EXCEPTION: 异常处理语句
8. P1-JUMP: 跳转语句
9. P1-SPREAD: 展开语法
10. P1-TEMPLATE: 模板字符串

**第5周：P2 增强功能**
11. P2-MODERN-OP: 现代运算符
12. P2-JSDOC: JSDoc 注释保留
13. P2-GENERIC: 泛型语法处理
14. P2-ARROW: 箭头函数类型注解

### 4.2 测试策略

**单元测试：** 每个语法特性对应一个测试用例

**集成测试：** 使用 myutils 工具库作为完整测试案例

**回归测试：** 确保已有功能不受影响

---

## 五、关键原则与注意事项

### 5.1 必须遵循的原则

1. **复用原始编译器方案**
   - 对于 ETS 特有语法：参考原始编译器的转换逻辑
   - 对于标准 JS/TS 语法：确保输出与 TypeScript emit 一致

2. **不改变架构**
   - 所有修改在现有三层架构内进行
   - 不引入新的处理层级或文件

3. **保持代码一致性**
   - 新增代码风格与现有代码保持一致
   - 遵循现有的命名和结构约定

4. **渐进式实现**
   - 按优先级逐步实现
   - 每个功能独立测试验证

### 5.2 避免的陷阱

1. **避免过度转换**：不要对标准 JS/TS 语法做不必要的转换
2. **避免破坏现有功能**：新增功能不应影响已有功能的正确性
3. **避免性能回退**：确保新增语法处理不影响编译性能

---

## 六、总结

本技术实现方案基于对原始编译器（`developtools_ace_ets2bundle`）的深入分析，严格遵循其"最小干预"原则：

1. **ETS 特有语法**：参考原始编译器的转换逻辑实现
2. **标准 JS/TS 语法**：手动实现转换，确保与 TypeScript emit 一致
3. **架构一致性**：在现有三层架构内完成所有实现

所有实现方案都引用了具体的文件位置、行号和现有代码模式，确保开发人员可以准确、高效地完成实现。

---

**文档版本：** v2.0
**编写日期：** 2026-01-30
**编写人：** Claude AI Assistant
**文档状态：** 基于原始编译器实现方案重写
