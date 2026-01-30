# ETS → JS 编译器迭代2技术实现方案说明书

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0 |
| 创建日期 | 2026-01-30 |
| 项目名称 | ets2jsc 编译器 |
| 基准实现 | developtools_ace_ets2bundle |
| 目标 | 基于原始实现方案完成迭代2功能需求 |

---

## 目录

1. [概述](#一概述)
2. [核心功能实现方案 (P0)](#二核心功能实现方案-p0)
3. [重要功能实现方案 (P1)](#三重要功能实现方案-p1)
4. [增强功能实现方案 (P2)](#四增强功能实现方案-p2)
5. [测试验证方案](#五测试验证方案)
6. [开发计划](#六开发计划)

---

## 一、概述

### 1.1 迭代1完成情况

✅ 已实现功能：
- `@Component` 装饰器处理（struct → class View）
- `@State` 装饰器处理（ObservedPropertySimple）
- static/async 方法支持
- await 表达式支持
- 基础语句解析（VariableStatement, ForOfStatement, ReturnStatement）

### 1.2 迭代2目标

基于 `developtools_ace_ets2bundle` 的实现方案，完成14个功能需求。

### 1.3 参考实现映射

| 原始实现文件 | 对应需求 | 核心逻辑 |
|-------------|---------|---------|
| `process_component_build.ts` | #1, #5, #6, #7 | UI组件转换、控制流 |
| `process_import.ts` | #2, #3 | 模块导入导出 |
| `process_ui_syntax.ts` | #4 | 类型声明处理 |
| `pre_define.ts` | #8, #9, #10 | 状态装饰器常量 |
| `process_custom_component.ts` | #1, #8, #9 | 自定义组件处理 |
| `process_component_member.ts` | #8, #9 | 成员变量转换 |

---

## 二、核心功能实现方案 (P0)

### 需求 #1: UI 组件 create/pop 转换

#### 1.1 原始实现分析

**参考文件**: `compiler/src/process_component_build.ts`

**核心数据结构**:
```typescript
// 组件类型枚举
enum ComponentType {
  innerComponent,      // 内置组件
  customComponent,     // 自定义组件
  forEachComponent,    // ForEach
  lazyForEachComponent, // LazyForEach
  ifComponent,         // If
  repeatComponent       // Repeat
}

// 组件处理结果
interface ProcessResult {
  statements: ts.Statement[];  // 生成的语句
  hasChild: boolean;         // 是否有子组件
}
```

**核心转换函数**: `processComponentChild()`

```typescript
export function processComponentChild(
  node: ts.Block,
  newStatements: ts.Statement[],
  log: LogInfo[],
  supplement: supplementType,
  isBuilder: boolean,
  parent: string
): void {
  for (const item of node.statements) {
    if (ts.isExpressionStatement(item)) {
      const componentType = getComponentType(item, log, name, parent);

      switch (componentType) {
        case ComponentType.innerComponent:
          processInnerComponent(item, newStatements);
          break;
        case ComponentType.customComponent:
          processCustomComponent(item, newStatements);
          break;
        // ... 其他类型
      }
    }
  }
}
```

#### 1.2 实现方案

##### 步骤1: 组件识别

**Java 实现方案**:

```java
// src/main/java/com/ets2jsc/ast/ComponentExpression.java
package com.ets2jsc.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * UI 组件表达式节点
 * 对应原始实现的 EtsComponentExpression
 */
public class ComponentExpression extends Expression {
    private String componentName;
    private List<Expression> arguments;
    private List<MethodCall> chainedCalls;
    private BlockExpression childBlock; // {} 表示的子组件

    public boolean isBuiltinComponent() {
        return ComponentRegistry.isBuiltinComponent(componentName);
    }

    public boolean hasChildren() {
        return childBlock != null && !childBlock.getStatements().isEmpty();
    }

    public boolean hasChainedCalls() {
        return chainedCalls != null && !chainedCalls.isEmpty();
    }
}
```

```java
// src/main/java/com/ets2jsc/ast/ComponentRegistry.java
package com.ets2jsc.ast;

import java.util.Set;
import java.util.HashSet;

/**
 * 组件注册表，基于原始实现的 component_map.ts
 */
public class ComponentRegistry {
    private static final Set<String> BUILTIN_COMPONENTS = new HashSet<>();

    static {
        // 内置组件列表（对应原始实现中的 INNER_COMPONENT_NAMES）
        String[] components = {
            "Text", "Button", "Image", "TextInput", "TextArea",
            "Column", "Row", "Stack", "Flex", "Grid", "List",
            "ForEach", "LazyForEach", "If", "Else"
        };
        for (String comp : components) {
            BUILTIN_COMPONENTS.add(comp);
        }
    }

    public static boolean isBuiltinComponent(String name) {
        return BUILTIN_COMPONENTS.contains(name);
    }
}
```

##### 步骤2: create/pop 转换器

**Java 实现方案**:

```java
// src/main/java/com/ets2jsc/transformer/ComponentCreatePopTransformer.java
package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI 组件 create/pop 转换器
 * 对应原始实现的 process_component_build.ts
 */
public class ComponentCreatePopTransformer {

    /**
     * 转换组件表达式为 create/pop 模式
     *
     * 输入: Text('Hello').fontSize(16)
     * 输出:
     *   Text.create('Hello');
     *   Text.fontSize(16);
     *   Text.pop();
     */
    public List<Statement> transform(ComponentExpression expr) {
        List<Statement> statements = new ArrayList<>();

        // 1. 生成 create 语句
        statements.add(createStatement(expr));

        // 2. 生成链式调用语句
        if (expr.hasChainedCalls()) {
            for (MethodCall call : expr.getChainedCalls()) {
                statements.add(createMethodCallStatement(expr, call));
            }
        }

        // 3. 处理子组件
        if (expr.hasChildren()) {
            transformChildBlock(expr.getChildBlock(), statements);
        }

        // 4. 生成 pop 语句
        statements.add(createPopStatement(expr));

        return statements;
    }

    /**
     * 生成 Component.create() 语句
     * 对应原始实现中的 createComponentCreation()
     */
    private Statement createStatement(ComponentExpression expr) {
        String funcName = expr.getComponentName() + ".create";
        List<Expression> args = expr.getArguments();

        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression(expr.getComponentName(), "create"),
                args
            )
        );
    }

    /**
     * 生成属性调用语句
     * 对应原始实现中的 bindComponentAttr()
     */
    private Statement createMethodCallStatement(ComponentExpression expr, MethodCall call) {
        String funcName = expr.getComponentName() + "." + call.getMethodName();

        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression(expr.getComponentName(), call.getMethodName()),
                call.getArguments()
            )
        );
    }

    /**
     * 生成 Component.pop() 语句
     * 对应原始实现中的 createComponentPop()
     */
    private Statement createPopStatement(ComponentExpression expr) {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression(expr.getComponentName(), "pop"),
                new ArrayList<>()
            )
        );
    }

    /**
     * 处理子组件块
     * 对应原始实现中的 processComponentBlock()
     */
    private void transformChildBlock(BlockExpression block, List<Statement> output) {
        for (Statement stmt : block.getStatements()) {
            if (stmt instanceof ExpressionStatement) {
                Expression expr = ((ExpressionStatement) stmt).getExpression();

                if (expr instanceof ComponentExpression) {
                    // 递归处理子组件
                    output.addAll(transform((ComponentExpression) expr));
                } else {
                    output.add(stmt);
                }
            }
        }
    }
}
```

##### 步骤3: 集成到现有代码生成器

```java
// 修改 src/main/java/com/ets2jsc/generator/CodeGenerator.java
public class CodeGenerator {
    private ComponentCreatePopTransformer componentTransformer;

    public CodeGenerator() {
        this.componentTransformer = new ComponentCreatePopTransformer();
    }

    public String generate(Expression expr) {
        if (expr instanceof ComponentExpression) {
            return generateComponentExpression((ComponentExpression) expr);
        }
        // ... 其他表达式类型
    }

    private String generateComponentExpression(ComponentExpression expr) {
        List<Statement> statements = componentTransformer.transform(expr);

        StringBuilder sb = new StringBuilder();
        for (Statement stmt : statements) {
            sb.append(generate(stmt));
            sb.append("\n");
        }
        return sb.toString();
    }
}
```

#### 1.3 测试验证

```java
// src/test/java/com/ets2jsc/ComponentCreatePopTest.java
@Test
public void testSimpleComponent() {
    // 输入
    String input = "Text('Hello')";

    // 预期输出
    String expected =
        "Text.create('Hello');\n" +
        "Text.pop();";

    assertEquals(expected, compile(input));
}

@Test
public void testComponentWithAttributes() {
    String input = "Text('Hello').fontSize(16).fontColor(Color.Red)";

    String expected =
        "Text.create('Hello');\n" +
        "Text.fontSize(16);\n" +
        "Text.fontColor(Color.Red);\n" +
        "Text.pop();";

    assertEquals(expected, compile(input));
}

@Test
public void testNestedComponent() {
    String input = "Column() { Text('Hello'); Button('Click') }";

    String expected =
        "Column.create();\n" +
        "Text.create('Hello');\n" +
        "Text.pop();\n" +
        "Button.create('Click');\n" +
        "Button.pop();\n" +
        "Column.pop();";

    assertEquals(expected, compile(input));
}
```

---

### 需求 #2: Import 语句处理

#### 2.1 原始实现分析

**参考文件**: `compiler/src/process_import.ts`

**核心逻辑**:
```typescript
export default function processImport(
  node: ts.ImportDeclaration | ts.ImportEqualsDeclaration | ts.ExportDeclaration,
  pagesDir: string,
  log: LogInfo[],
  asName: Map<string, string>
): void {
  // 1. 提取模块路径
  let filePath: string;
  if (ts.isImportDeclaration(node)) {
    filePath = node.moduleSpecifier.getText().replace(/'|"/g, '');
  }

  // 2. 解析导入名称
  if (node.importClause && node.importClause.name) {
    const defaultName = node.importClause.name.getText();
    asName.set(defaultName, defaultName);
  }

  // 3. 处理命名导入
  if (node.importClause && node.importClause.namedBindings) {
    const namedImports = node.importClause.namedBindings;
    if (ts.isNamedImports(namedImports)) {
      namedImports.elements.forEach(item => {
        asName.set(item.name.text, item.name.text);
      });
    }
  }

  // 4. 递归处理导入的文件
  const fileResolvePath = getFileFullPath(filePath, pagesDir);
  if (fs.existsSync(fileResolvePath)) {
    const sourceFile = generateSourceFileAST(fileResolvePath);
    visitAllNode(sourceFile, asName, path.dirname(fileResolvePath));
  }
}
```

#### 2.2 实现方案

##### 步骤1: 扩展 parse-ets.js

```javascript
// src/main/resources/parse-ets.js - 添加 ImportDeclaration 支持

function parseImportDeclaration(context) {
  const node = context.node;

  if (node.type !== 'ImportDeclaration') {
    return undefined;
  }

  // 解析 import 语句
  // import { A, B } from 'module'
  // import Module from 'module'
  // import * as Module from 'module'

  const source = node.source.value; // 'module'
  const specifiers = node.specifiers || [];

  const importDecl = {
    type: 'ImportDeclaration',
    source: source,
    specifiers: specifiers.map(spec => {
      if (spec.type === 'ImportDefaultSpecifier') {
        return {
          type: 'ImportDefaultSpecifier',
          local: spec.local.name
        };
      } else if (spec.type === 'ImportSpecifier') {
        return {
          type: 'ImportSpecifier',
          imported: spec.imported.name,
          local: spec.local.name
        };
      } else if (spec.type === 'ImportNamespaceSpecifier') {
        return {
          type: 'ImportNamespaceSpecifier',
          local: spec.local.name
        };
      }
      return spec;
    })
  };

  return importDecl;
}
```

##### 步骤2: 实现 ImportStatement 处理

```java
// src/main/java/com/ets2jsc/statement/ImportStatement.java
package com.ets2jsc.statement;

/**
 * Import 语句
 */
public class ImportStatement extends Statement {
    private String module;
    private List<ImportSpecifier> specifiers;
    private boolean isTypeOnly = false;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("import ");

        if (specifiers.isEmpty()) {
            // 副作用导入: import 'module'
            sb.append("'").append(module).append("'");
        } else {
            // 分类构建导入
            List<ImportSpecifier> defaultSpecs = new ArrayList<>();
            List<ImportSpecifier> namedSpecs = new ArrayList<>();
            List<ImportSpecifier> namespaceSpecs = new ArrayList<>();

            for (ImportSpecifier spec : specifiers) {
                if (spec.isDefault()) {
                    defaultSpecs.add(spec);
                } else if (spec.isNamespace()) {
                    namespaceSpecs.add(spec);
                } else {
                    namedSpecs.add(spec);
                }
            }

            // 构建导入字符串
            List<String> parts = new ArrayList<>();

            for (ImportSpecifier spec : defaultSpecs) {
                parts.add(spec.getLocalName());
            }

            if (!namedSpecs.isEmpty()) {
                StringBuilder namedPart = new StringBuilder("{ ");
                for (int i = 0; i < namedSpecs.size(); i++) {
                    ImportSpecifier spec = namedSpecs.get(i);
                    if (i > 0) namedPart.append(", ");
                    if (!spec.getImportedName().equals(spec.getLocalName())) {
                        namedPart.append(spec.getImportedName())
                               .append(" as ");
                    }
                    namedPart.append(spec.getLocalName());
                }
                namedPart.append(" }");
                parts.add(namedPart.toString());
            }

            for (ImportSpecifier spec : namespaceSpecs) {
                parts.add("* as " + spec.getLocalName());
            }

            sb.append(String.join(", ", parts));
            sb.append(" from '").append(module).append("'");
        }

        sb.append(";");
        return sb.toString();
    }
}
```

##### 步骤3: 收集所有 import 语句

```java
// src/main/java/com/ets2jsc/parser/ModuleCollector.java
package com.ets2jsc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块导入收集器
 */
public class ModuleCollector {
    private List<ImportStatement> imports = new ArrayList<>();

    public void collect(Statement statement) {
        if (statement instanceof ImportStatement) {
            imports.add((ImportStatement) statement);
        }
    }

    /**
     * 在文件顶部输出所有 import 语句
     * 对应原始实现中的收集 import 并在顶部输出
     */
    public String generateImports() {
        StringBuilder sb = new StringBuilder();
        for (ImportStatement importStmt : imports) {
            sb.append(importStmt.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
```

##### 步骤4: 集成到代码生成器

```java
// 修改 CodeGenerator.java
public class CodeGenerator {
    private ModuleCollector moduleCollector;

    public String generate(SourceFile sourceFile) {
        moduleCollector = new ModuleCollector();

        // 第一遍：收集所有 import 语句
        for (Statement stmt : sourceFile.getStatements()) {
            moduleCollector.collect(stmt);
        }

        StringBuilder sb = new StringBuilder();

        // 首先输出所有 import
        sb.append(moduleCollector.generateImports());
        sb.append("\n");

        // 然后输出其他语句
        for (Statement stmt : sourceFile.getStatements()) {
            if (!(stmt instanceof ImportStatement)) {
                sb.append(generate(stmt));
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
```

#### 2.3 测试验证

```java
@Test
public void testNamedImport() {
    String input = "import { A, B } from 'module';";
    String output = compile(input);

    assertTrue(output.contains("import { A, B } from 'module';"));
}

@Test
public void testDefaultImport() {
    String input = "import Module from 'module';";
    String output = compile(input);

    assertTrue(output.contains("import Module from 'module';"));
}

@Test
public void testMixedImport() {
    String input = "import Default, { A, B as C } from 'module';";
    String output = compile(input);

    assertTrue(output.contains("import Default, { A, B as C } from 'module';"));
}

@Test
public void testImportAtTop() {
    String input =
        "import { Text } from '@kit.ArkUI';\n" +
        "class MyComponent {\n" +
        "  build() {\n" +
        "    Text('Hello')\n" +
        "  }\n" +
        "}";

    String output = compile(input);

    // import 应该在最前面
    assertTrue(output.indexOf("import") < output.indexOf("class"));
}
```

---

### 需求 #3: Export 语句处理

#### 3.1 原始实现分析

**参考文件**: `compiler/src/process_import.ts`

**核心逻辑**:
```typescript
// 检查导出是否为类型相关（需要移除）
function isExportType(node: ts.ExportDeclaration): boolean {
  if (ts.isInterfaceDeclaration(node.exportClause) ||
      ts.isTypeAliasDeclaration(node.exportClause) ||
      ts.isEnumDeclaration(node.exportClause)) {
    return true;
  }
  return false;
}
```

#### 3.2 实现方案

##### 步骤1: ExportStatement 处理

```java
// src/main/java/com/ets2jsc/statement/ExportStatement.java
package com.ets2jsc.statement;

/**
 * Export 语句处理器
 */
public class ExportStatement extends Statement {
    private Statement declaration;
    private boolean isTypeExport; // 是否为类型导出

    @Override
    public String toString() {
        // 类型导出在 JS 中被移除
        if (isTypeExport) {
            return ""; // 返回空字符串，稍后会被过滤
        }

        // 保留 export 关键字
        StringBuilder sb = new StringBuilder();
        sb.append("export ");

        String declStr = declaration.toString();
        sb.append(declStr);

        return sb.toString();
    }
}
```

##### 步骤2: 类型声明识别

```java
// src/main/java/com/ets2jsc/parser/TypeDeclarationChecker.java
package com.ets2jsc.parser;

/**
 * 类型声明检查器
 */
public class TypeDeclarationChecker {

    /**
     * 判断是否为类型声明（在 JS 中应被移除）
     * 对应原始实现中的类型声明处理
     */
    public static boolean isTypeDeclaration(Statement stmt) {
        // interface 声明
        if (stmt instanceof InterfaceDeclaration) {
            return true;
        }

        // type 别名声明
        if (stmt instanceof TypeAliasDeclaration) {
            return true;
        }

        // enum 需要特殊处理，不在这里移除
        if (stmt instanceof EnumDeclaration) {
            return false;
        }

        // export interface/type
        if (stmt instanceof ExportStatement) {
            Statement decl = ((ExportStatement) stmt).getDeclaration();
            return isTypeDeclaration(decl);
        }

        return false;
    }
}
```

##### 步骤3: 集成到代码生成器

```java
// 修改 CodeGenerator.java
public String generate(SourceFile sourceFile) {
    StringBuilder sb = new StringBuilder();

    for (Statement stmt : sourceFile.getStatements()) {
        // 跳过类型声明
        if (TypeDeclarationChecker.isTypeDeclaration(stmt)) {
            continue;
        }

        String code = generate(stmt);

        // 跳过空字符串（类型导出的结果）
        if (code != null && !code.trim().isEmpty()) {
            sb.append(code);
            sb.append("\n");
        }
    }

    return sb.toString();
}
```

#### 3.3 测试验证

```java
@Test
public void testExportClass() {
    String input = "export class MyClass {}";
    String output = compile(input);

    assertTrue(output.contains("export class MyClass"));
}

@Test
public void testExportFunction() {
    String input = "export function myFunc() {}";
    String output = compile(input);

    assertTrue(output.contains("export function myFunc"));
}

@Test
public void testExportConst() {
    String input = "export const myConst = 1;";
    String output = compile(input);

    assertTrue(output.contains("export const myConst = 1;"));
}

@Test
public void testExportInterfaceRemoved() {
    String input = "export interface MyInterface {}";
    String output = compile(input);

    // interface 应该被移除
    assertFalse(output.contains("interface"));
    assertFalse(output.contains("MyInterface"));
}
```

---

### 需求 #5: 链式调用拆分

#### 5.1 原始实现分析

**参考文件**: `compiler/src/process_component_build.ts`

**核心逻辑**:
```typescript
// 处理链式调用
function processComponentChild(...) {
  // 检测链式调用
  let chainCall = node.parent;
  while (ts.isCallExpression(chainCall)) {
    if (ts.isPropertyAccessExpression(chainCall.expression)) {
      const methodName = chainCall.expression.name.getText();

      // 生成独立的属性调用语句
      statements.push(
        ts.factory.createExpressionStatement(
          ts.factory.createCallExpression(
            ts.factory.createPropertyAccessExpression(
              ts.factory.createIdentifier(componentName),
              methodName
            ),
            undefined,
            chainCall.arguments
          )
        )
      );
    }
    chainCall = chainCall.parent;
  }
}
```

#### 5.2 实现方案

##### 步骤1: 扩展 parse-ets.js 识别链式调用

```javascript
// parse-ets.js - 链式调用解析

function parseCallExpression(context) {
  const node = context.node;

  if (node.type !== 'CallExpression') {
    return undefined;
  }

  const call = {
    type: 'CallExpression',
    callee: parseExpression(node.callee, context),
    arguments: node.arguments.map(arg => parseExpression(arg, context)),
    chainedCalls: [] // 新增：链式调用列表
  };

  // 检查父节点是否为链式调用
  let parent = context.parent;
  while (parent && parent.type === 'CallExpression') {
    if (parent.callee.type === 'MemberExpression' &&
        parent.callee.object.type === 'Identifier' &&
        parent.callee.object.name === call.callee.name) {
      // 这是一个链式调用
      call.chainedCalls.push({
        type: 'MethodCall',
        methodName: parent.callee.property.name,
        arguments: parent.arguments
      });
    }
    parent = parent.parent;
  }

  return call;
}
```

##### 步骤2: 链式调用转换器

```java
// src/main/java/com/ets2jsc/transformer/ChainedCallTransformer.java
package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 链式调用转换器
 * 对应原始实现中的链式调用处理逻辑
 */
public class ChainedCallTransformer {

    /**
     * 转换链式调用为独立语句
     *
     * 输入: Text('Hello').fontSize(16).fontColor(Color.Red)
     * 输出:
     *   Text.create('Hello');
     *   Text.fontSize(16);
     *   Text.fontColor(Color.Red);
     */
    public List<Statement> transformChainedCall(ComponentExpression expr) {
        List<Statement> statements = new ArrayList<>();

        // 主调用转换为 create
        statements.add(createStatement(expr));

        // 处理链式调用
        for (MethodCall chainedCall : expr.getChainedCalls()) {
            statements.add(createMethodCallStatement(expr, chainedCall));
        }

        // pop 语句
        statements.add(createPopStatement(expr));

        return statements;
    }

    private Statement createStatement(ComponentExpression expr) {
        List<Expression> args = expr.getArguments();
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression(expr.getComponentName(), "create"),
                args
            )
        );
    }

    private Statement createMethodCallStatement(ComponentExpression comp, MethodCall call) {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression(comp.getComponentName(), call.getMethodName()),
                call.getArguments()
            )
        );
    }

    private Statement createPopStatement(ComponentExpression expr) {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression(expr.getComponentName(), "pop"),
                new ArrayList<>()
            )
        );
    }
}
```

##### 步骤3: 集成到 build 方法转换

```java
// 修改 BuildMethodTransformer.java
public class BuildMethodTransformer {
    private ChainedCallTransformer chainedCallTransformer;

    public MethodDeclaration transform(MethodDeclaration buildMethod) {
        BlockExpression body = buildMethod.getBody();
        List<Statement> newStatements = new ArrayList<>();

        for (Statement stmt : body.getStatements()) {
            if (stmt instanceof ExpressionStatement) {
                Expression expr = ((ExpressionStatement) stmt).getExpression();

                if (expr instanceof ComponentExpression) {
                    // 使用链式调用转换器
                    newStatements.addAll(
                        chainedCallTransformer.transformChainedCall((ComponentExpression) expr)
                    );
                } else {
                    newStatements.add(stmt);
                }
            } else {
                newStatements.add(stmt);
            }
        }

        return new MethodDeclaration(
            buildMethod.getModifiers(),
            "render",
            buildMethod.getParameters(),
            new BlockExpression(newStatements)
        );
    }
}
```

#### 5.3 测试验证

```java
@Test
public void testSingleChainedCall() {
    String input = "Text('Hello').fontSize(16)";
    String output = compile(input);

    assertTrue(output.contains("Text.create('Hello')"));
    assertTrue(output.contains("Text.fontSize(16)"));
    assertTrue(output.contains("Text.pop()"));
}

@Test
public void testMultipleChainedCalls() {
    String input = "Text('Hello').fontSize(16).fontColor(Color.Red)";
    String output = compile(input);

    // 验证顺序正确
    int createIdx = output.indexOf("Text.create");
    int fontSizeIdx = output.indexOf("Text.fontSize");
    int fontColorIdx = output.indexOf("Text.fontColor");
    int popIdx = output.indexOf("Text.pop");

    assertTrue(createIdx < fontSizeIdx);
    assertTrue(fontSizeIdx < fontColorIdx);
    assertTrue(fontColorIdx < popIdx);
}
```

---

