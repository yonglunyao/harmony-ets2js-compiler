## 三、重要功能实现方案 (P1)

### 需求 #6: ForEach 转换

#### 6.1 原始实现分析

**参考文件**: `compiler/src/process_component_build.ts`

**核心函数**: `processForEachComponentNew()`

```typescript
function processForEachComponentNew(
  node: ts.ExpressionStatement,
  newStatements: ts.Statement[]
): void {
  const newNode = collectForEachAttribute(node);

  // 1. 创建属性语句列表
  const attributeList: ts.ExpressionStatement[] = [];

  // 2. 创建 ForEach.create()
  const propertyNode = ts.factory.createExpressionStatement(
    ts.factory.createCallExpression(
      ts.factory.createPropertyAccessExpression(
        newNode.expression.expression,
        'create'
      ),
      undefined,
      []
    )
  );

  // 3. 生成 itemGenerator 函数
  const forEachArrowFunc = processForEachFunctionBlock(newNode.expression);
  const itemGenFunc = createItemGenFunction(newNode, forEachArrowFunc);

  // 4. 生成 keyGenerator 函数
  const itemIdFunc = createItemIdFunc(newNode);

  // 5. 设置生成器
  newStatements.push(
    ts.factory.createExpressionStatement(
      ts.factory.createCallExpression(
        ts.factory.createPropertyAccessExpression('ForEach', 'itemGenerator'),
        undefined,
        [itemGenFunc]
      )
    )
  );

  // 6. pop
  newStatements.push(createForEachPop());
}
```

#### 6.2 实现方案

##### 步骤1: ForEach 表达式解析

```java
// src/main/java/com/ets2jsc/ast/ForEachExpression.java
package com.ets2jsc.ast;

/**
 * ForEach 表达式
 * 对应原始实现中的 ForEach 处理
 */
public class ForEachExpression extends ComponentExpression {
    private Expression arrayExpression;      // 数据源
    private ArrowFunction itemGenerator;       // 项生成器
    private ArrowFunction keyGenerator;        // 键生成器

    @Override
    public String getComponentName() {
        return "ForEach";
    }

    @Override
    public List<Expression> getArguments() {
        // ForEach 参数不作为 create 参数
        return new ArrayList<>();
    }
}
```

##### 步骤2: ForEach 转换器

```java
// src/main/java/com/ets2jsc/transformer/ForEachTransformer.java
package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ForEach 转换器
 * 对应原始实现中的 processForEachComponentNew
 */
public class ForEachTransformer {

    /**
     * 转换 ForEach 表达式
     *
     * 输入: ForEach(items, (item, index) => { Text(item.name) }, (item) => item.id)
     * 输出:
     *   ForEach.create();
     *   const __itemGenFunction__ = (item, index) => {
     *     Text.create(item.name);
     *     Text.pop();
     *   };
     *   const __keyGenFunction__ = (item) => item.id;
     *   ForEach.itemGenerator(__itemGenFunction__);
     *   ForEach.keyGenerator(__keyGenFunction__);
     *   ForEach.pop();
     */
    public List<Statement> transform(ForEachExpression forEach) {
        List<Statement> statements = new ArrayList<>();

        // 1. 生成唯一标识符
        String itemGenFuncName = "__itemGenFunction__";
        String keyGenFuncName = "__keyGenFunction__";

        // 2. ForEach.create()
        statements.add(createForEachCreate());

        // 3. itemGenerator 函数
        FunctionDeclaration itemGenFunc = createItemGeneratorFunction(
            itemGenFuncName,
            forEach.getItemGenerator()
        );
        statements.add(itemGenFunc);

        // 4. keyGenerator 函数
        FunctionDeclaration keyGenFunc = createKeyGeneratorFunction(
            keyGenFuncName,
            forEach.getKeyGenerator()
        );
        statements.add(keyGenFunc);

        // 5. 设置生成器
        statements.add(createSetItemGenerator(itemGenFuncName));
        statements.add(createSetKeyGenerator(keyGenFuncName));

        // 6. ForEach.pop()
        statements.add(createForEachPop());

        return statements;
    }

    private Statement createForEachCreate() {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression("ForEach", "create"),
                new ArrayList<>()
            )
        );
    }

    /**
     * 创建 itemGenerator 函数
     * 对应原始实现中的 createItemGenFunctionStatement
     */
    private FunctionDeclaration createItemGeneratorFunction(
        String funcName,
        ArrowFunction originalArrowFunc
    ) {
        // 转换箭头函数体内的组件调用
        BlockExpression transformedBody = transformGeneratorBody(originalArrowFunc.getBody());

        return new FunctionDeclaration(
            "",
            "const",
            funcName,
            originalArrowFunc.getParameters(),
            transformedBody
        );
    }

    /**
     * 转换生成器函数体中的组件调用
     * 对应原始实现中的 processForEachBlock
     */
    private BlockExpression transformGeneratorBody(Expression body) {
        List<Statement> statements = new ArrayList<>();

        if (body instanceof BlockExpression) {
            BlockExpression block = (BlockExpression) body;

            for (Statement stmt : block.getStatements()) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();

                    if (expr instanceof ComponentExpression) {
                        // 使用组件转换器处理
                        ComponentCreatePopTransformer transformer = new ComponentCreatePopTransformer();
                        statements.addAll(transformer.transform((ComponentExpression) expr));
                    } else {
                        statements.add(stmt);
                    }
                }
            }
        }

        return new BlockExpression(statements);
    }

    private FunctionDeclaration createKeyGeneratorFunction(
        String funcName,
        ArrowFunction originalKeyFunc
    ) {
        // keyGenerator 通常很简单，直接保留即可
        return new FunctionDeclaration(
            "",
            "const",
            funcName,
            originalKeyFunc.getParameters(),
            originalKeyFunc.getBody()
        );
    }

    private Statement createSetItemGenerator(String funcName) {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression("ForEach", "itemGenerator"),
                List.of(new IdentifierExpression(funcName))
            )
        );
    }

    private Statement createSetKeyGenerator(String funcName) {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression("ForEach", "keyGenerator"),
                List.of(new IdentifierExpression(funcName))
            )
        );
    }

    private Statement createForEachPop() {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression("ForEach", "pop"),
                new ArrayList<>()
            )
        );
    }
}
```

#### 6.3 测试验证

```java
@Test
public void testSimpleForEach() {
    String input = "ForEach(items, (item) => Text(item.name))";
    String output = compile(input);

    assertTrue(output.contains("ForEach.create()"));
    assertTrue(output.contains("ForEach.itemGenerator"));
    assertTrue(output.contains("ForEach.pop()"));
}

@Test
public void testForEachWithIndex() {
    String input = "ForEach(items, (item, index) => Text(item.name + index))";
    String output = compile(input);

    // 验证参数正确传递
    assertTrue(output.contains("(item, index) =>"));
}
```

---

### 需求 #7: If 条件渲染转换

#### 7.1 原始实现分析

**参考文件**: `compiler/src/process_component_build.ts`

**核心函数**: `processIfStatement()`

```typescript
function processIfStatement(
  node: ts.IfStatement,
  newStatements: ts.Statement[]
): void {
  // 1. If.create()
  const ifCreate = createIfCreate();

  // 2. 处理 then 分支
  const newThenNode = processInnerIfStatement(node.thenStatement, 0);

  // 3. 处理 else 分支
  const newElseNode = node.elseStatement
    ? processInnerIfStatement(node.elseStatement, 1)
    : undefined;

  // 4. 构建新的 if 语句
  const newIfNode = ts.factory.createIfStatement(
    node.expression,
    ts.factory.createBlock([
      ifCreate,
      createIfBranchId(0),
      newThenNode,
      createIfPop()
    ], true),
    ts.factory.createBlock([
      ifCreate,
      createIfBranchId(1),
      newElseNode,
      createIfPop()
    ], true)
  );

  // 5. If.pop()
  const ifPop = createIfPop();

  // 6. 组合输出
  newStatements.push(ifCreate, newIfNode, ifPop);
}
```

#### 7.2 实现方案

##### 步骤1: If 表达式解析

```java
// src/main/java/com/ets2jsc/ast/IfStatement.java
package com.ets2jsc.ast;

/**
 * If 条件渲染语句
 */
public class IfStatement extends Statement {
    private Expression condition;
    private Statement thenStatement;
    private Statement elseStatement;

    @Override
    public String toString() {
        // 如果已经在 If.create() 上下文中，添加 branchId
        return toStringWithBranchId(false);
    }

    public String toStringWithBranchId(boolean withBranchId) {
        StringBuilder sb = new StringBuilder();

        sb.append("if (").append(condition).append(") ");
        sb.append("{\n");

        // then 分支
        if (withBranchId) {
            sb.append("  If.branchId(0);\n");
        }
        sb.append("  ").append(thenStatement.toString().indent(2));
        sb.append("}\n");

        // else 分支
        if (elseStatement != null) {
            sb.append(" else {\n");
            if (withBranchId) {
                sb.append("  If.branchId(1);\n");
            }
            sb.append("  ").append(elseStatement.toString().indent(2));
            sb.append("}\n");
        }

        return sb.toString();
    }
}
```

##### 步骤2: If 转换器

```java
// src/main/java/com/ets2jsc/transformer/IfTransformer.java
package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import java.util.ArrayList;
import java.util.List;

/**
 * If 条件渲染转换器
 * 对应原始实现中的 processIfStatement
 */
public class IfTransformer {
    private int branchIdCounter = 0;

    /**
     * 转换 if 条件渲染
     *
     * 输入: if (condition) { Text('Yes') } else { Text('No') }
     * 输出:
     *   If.create();
     *   if (condition) {
     *     If.branchId(0);
     *     Text.create('Yes');
     *     Text.pop();
     *   } else {
     *     If.branchId(1);
     *     Text.create('No');
     *     Text.pop();
     *   }
     *   If.pop();
     */
    public List<Statement> transform(IfStatement ifStmt) {
        List<Statement> statements = new ArrayList<>();

        // 1. If.create()
        statements.add(createIfCreate());

        // 2. 转换 then 和 else 分支
        IfStatement transformedIf = transformBranches(ifStmt);
        statements.add(transformedIf);

        // 3. If.pop()
        statements.add(createIfPop());

        return statements;
    }

    private Statement createIfCreate() {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression("If", "create"),
                new ArrayList<>()
            )
        );
    }

    /**
     * 转换 if 分支，添加 branchId
     * 对应原始实现中的 processInnerIfStatement
     */
    private IfStatement transformBranches(IfStatement original) {
        branchIdCounter = 0;

        // 转换 then 分支
        Statement transformedThen = transformBranch(original.getThenStatement(), 0);

        // 转换 else 分支
        Statement transformedElse = null;
        if (original.getElseStatement() != null) {
            transformedElse = transformBranch(original.getElseStatement(), 1);
        }

        return new IfStatement(
            original.getCondition(),
            transformedThen,
            transformedElse
        );
    }

    /**
     * 转换单个分支，添加 branchId 调用和组件转换
     * 对应原始实现中的 processIfBlock
     */
    private Statement transformBranch(Statement branch, int branchId) {
        List<Statement> statements = new ArrayList<>();

        // 添加 branchId 调用
        statements.add(
            new ExpressionStatement(
                new MethodCallExpression(
                    new MemberExpression("If", "branchId"),
                    List.of(new LiteralExpression(branchId))
                )
            )
        );

        // 处理分支内的语句（可能是组件调用）
        if (branch instanceof BlockExpression) {
            BlockExpression block = (BlockExpression) branch;

            for (Statement stmt : block.getStatements()) {
                if (stmt instanceof ExpressionStatement) {
                    Expression expr = ((ExpressionStatement) stmt).getExpression();

                    if (expr instanceof ComponentExpression) {
                        // 转换组件调用
                        ComponentCreatePopTransformer transformer = new ComponentCreatePopTransformer();
                        statements.addAll(transformer.transform((ComponentExpression) expr));
                    } else {
                        statements.add(stmt);
                    }
                } else if (stmt instanceof IfStatement) {
                    // 嵌套 if 语句
                    IfTransformer nestedTransformer = new IfTransformer();
                    statements.addAll(nestedTransformer.transform((IfStatement) stmt));
                } else {
                    statements.add(stmt);
                }
            }
        } else {
            statements.add(branch);
        }

        return new BlockExpression(statements);
    }

    private Statement createIfPop() {
        return new ExpressionStatement(
            new MethodCallExpression(
                new MemberExpression("If", "pop"),
                new ArrayList<>()
            )
        );
    }
}
```

#### 7.3 测试验证

```java
@Test
public void testSimpleIf() {
    String input = "if (condition) { Text('Yes') } else { Text('No') }";
    String output = compile(input);

    assertTrue(output.contains("If.create()"));
    assertTrue(output.contains("If.branchId(0)"));
    assertTrue(output.contains("If.branchId(1)"));
    assertTrue(output.contains("If.pop()"));
}

@Test
public void testNestedIf() {
    String input = "if (a) { if (b) { Text('X') } }";
    String output = compile(input);

    // 验证嵌套 if 的正确转换
    assertTrue(output.contains("If.branchId(0)"));
    assertTrue(output.contains("If.branchId(1)"));
}
```

---

### 需求 #11: 表达式完整支持

#### 11.1 原始实现分析

**参考文件**: `compiler/src/process_ui_syntax.ts` 中的表达式处理

**核心逻辑**:
```typescript
// 表达式保持原样输出，只在特定场景下需要转换
function processExpression(node: ts.Expression): ts.Expression {
  switch (node.kind) {
    case ts.SyntaxKind.BinaryExpression:
      return ts.factory.createBinaryExpression(
        node.left,
        node.operatorToken,
        node.right
      );
    case ts.SyntaxKind.ConditionalExpression:
      return ts.factory.createConditionalExpression(
        node.condition,
        node.whenTrue,
        node.whenFalse
      );
    // ... 其他表达式类型
  }
}
```

#### 11.2 实现方案

##### 步骤1: 完善表达式 AST 节点

```java
// src/main/java/com/ets2jsc/expressions/BinaryExpression.java
package com.ets2jsc.expressions;

public class BinaryExpression extends Expression {
    private Expression left;
    private String operator;
    private Expression right;

    @Override
    public String toString() {
        return left.toString() + " " + operator + " " + right.toString();
    }
}

// src/main/java/com/ets2jsc/expressions/ConditionalExpression.java
public class ConditionalExpression extends Expression {
    private Expression condition;
    private Expression whenTrue;
    private Expression whenFalse;

    @Override
    public String toString() {
        return condition.toString() + " ? " +
               whenTrue.toString() + " : " +
               whenFalse.toString();
    }
}

// src/main/java/com/ets2jsc/expressions/ArrayLiteralExpression.java
public class ArrayLiteralExpression extends Expression {
    private List<Expression> elements;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements.get(i).toString());
        }
        sb.append("]");
        return sb.toString();
    }
}

// src/main/java/com/ets2jsc/expressions/ObjectLiteralExpression.java
public class ObjectLiteralExpression extends Expression {
    private List<ObjectProperty> properties;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < properties.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(properties.get(i).toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
```

##### 步骤2: TypeScriptScriptParser 表达式转换

```java
// 修改 src/main/java/com/ets2jsc/parser/TypeScriptScriptParser.java
public class TypeScriptScriptParser {

    /**
     * 将表达式转换为字符串
     * 对应原始实现中的表达式处理逻辑
     */
    private String convertExpressionToString(Expression expr) {
        if (expr == null) {
            return "";
        }

        // 大部分表达式直接使用 toString()
        // 只在特殊情况下需要特殊处理

        if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            return convertBinaryExpression(binExpr);
        }

        if (expr instanceof ConditionalExpression) {
            ConditionalExpression condExpr = (ConditionalExpression) expr;
            return convertConditionalExpression(condExpr);
        }

        // 其他表达式类型...

        return expr.toString();
    }

    private String convertBinaryExpression(BinaryExpression expr) {
        // 确保运算符优先级正确
        String left = convertExpressionToString(expr.getLeft());
        String right = convertExpressionToString(expr.getRight());

        // 如果子表达式也是二元表达式，需要检查优先级
        boolean needsParensLeft = needsParentheses(expr.getLeft(), expr, true);
        boolean needsParensRight = needsParentheses(expr.getRight(), expr, false);

        if (needsParensLeft) {
            left = "(" + left + ")";
        }
        if (needsParensRight) {
            right = "(" + right + ")";
        }

        return left + " " + expr.getOperator() + " " + right;
    }

    /**
     * 检查是否需要括号
     */
    private boolean needsParentheses(Expression child, Expression parent, boolean isLeft) {
        // 简化版：只处理常见情况
        if (!(child instanceof BinaryExpression)) {
            return false;
        }

        // 实际实现需要完整的运算符优先级表
        // 这里简化处理
        return false;
    }
}
```

#### 11.3 测试验证

```java
@Test
public void testBinaryExpression() {
    String input = "a + b * c";
    String output = compileExpression(input);

    assertEquals("a + b * c", output);
}

@Test
public void testConditionalExpression() {
    String input = "a ? b : c";
    String output = compileExpression(input);

    assertEquals("a ? b : c", output);
}

@Test
public void testArrayLiteral() {
    String input = "[1, 2, 3]";
    String output = compileExpression(input);

    assertEquals("[1, 2, 3]", output);
}

@Test
public void testObjectLiteral() {
    String input = "{ a: 1, b: 2 }";
    String output = compileExpression(input);

    assertEquals("{ a: 1, b: 2 }", output);
}
```

---

### 需求 #8: @Prop 装饰器

#### 8.1 原始实现分析

**参考文件**: `compiler/src/pre_define.ts`, `compiler/src/process_component_member.ts`

**核心定义**:
```typescript
// pre_define.ts
export const COMPONENT_PROP_DECORATOR: string = '@Prop';

// 运行时类型
export const SYNCHED_PROPERTY_SIMPLE_ONE_WAY: string = 'SynchedPropertySimpleOneWay';
export const CREATE_PROP_METHOD: string = 'createProp';
```

#### 8.2 实现方案

##### 步骤1: 定义 Prop 装饰器处理器

```java
// src/main/java/com/ets2jsc/transformer/PropDecoratorHandler.java
package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Prop 装饰器处理器
 * 对应原始实现中的 @Prop 处理逻辑
 */
public class PropDecoratorHandler implements DecoratorHandler {

    @Override
    public boolean canHandle(String decoratorName) {
        return "@Prop".equals(decoratorName);
    }

    /**
     * 处理 @Prop 装饰的属性
     *
     * 输入: @Prop count: number = 0;
     * 输出:
     *   private count__: SynchedPropertySimpleOneWay<number>;
     *   constructor(parent) {
     *     super(parent);
     *     this.count__ = this.createProp('count', () => this.count);
     *   }
     */
    public PropertyDeclaration transformProperty(PropertyDeclaration prop) {
        String propertyName = prop.getName();
        String privateName = propertyName + "__";

        // 1. 创建私有属性
        PropertyDeclaration privateProp = new PropertyDeclaration(
            Modifier.PRIVATE,
            privateName,
            "SynchedPropertySimpleOneWay<" + prop.getType() + ">",
            null
        );

        // 2. 创建 getter/setter
        GetterDeclaration getter = new GetterDeclaration(
            propertyName,
            "return this." + privateName + ".get();"
        );

        SetterDeclaration setter = new SetterDeclaration(
            propertyName,
            "this." + privateName + ".set(newValue);"
        );

        // 3. 生成构造函数初始化语句
        Statement initStmt = new ExpressionStatement(
            new AssignmentExpression(
                new MemberExpression("this", privateName),
                new MethodCallExpression(
                    new MemberExpression("this", "createProp"),
                    List.of(
                        new LiteralExpression(propertyName),
                        new ArrowExpression(
                            List.of(),
                            new MemberExpression("this", propertyName)
                        )
                    )
                )
            )
        );

        // 返回转换后的属性（包含 getter/setter/init）
        return new TransformedProperty(privateProp, getter, setter, initStmt);
    }

    @Override
    public String getRuntimeType() {
        return "SynchedPropertySimpleOneWay";
    }

    @Override
    public String getCreateMethod() {
        return "createProp";
    }
}
```

##### 步骤2: 注册装饰器处理器

```java
// src/main/java/com/ets2jsc/transformer/DecoratorRegistry.java
public class DecoratorRegistry {
    private static final Map<String, DecoratorHandler> handlers = new HashMap<>();

    static {
        handlers.put("@State", new StateDecoratorHandler());
        handlers.put("@Prop", new PropDecoratorHandler());
        handlers.put("@Link", new LinkDecoratorHandler());
        handlers.put("@Provide", new ProvideDecoratorHandler());
        handlers.put("@Consume", new ConsumeDecoratorHandler());
        // ... 其他装饰器
    }

    public static DecoratorHandler getHandler(String decoratorName) {
        return handlers.get(decoratorName);
    }
}
```

#### 8.3 测试验证

```java
@Test
public void testPropDecorator() {
    String input =
        "@Component\n" +
        "struct Child {\n" +
        "  @Prop count: number = 0;\n" +
        "  build() {\n" +
        "    Text(this.count)\n" +
        "  }\n" +
        "}";

    String output = compile(input);

    // 验证使用正确的运行时类型
    assertTrue(output.contains("SynchedPropertySimpleOneWay"));
    assertTrue(output.contains("createProp"));
    assertTrue(output.contains("get count()"));
    assertTrue(output.contains("set count(newValue)"));
}
```

---

### 需求 #9: @Link 装饰器

#### 9.1 原始实现分析

**参考文件**: `compiler/src/pre_define.ts`

**核心定义**:
```typescript
export const COMPONENT_LINK_DECORATOR: string = '@Link';
export const SYNCHED_PROPERTY_SIMPLE_TWO_WAY: string = 'SynchedPropertySimpleTwoWay';
export const CREATE_LINK_METHOD: string = 'createLink';
```

#### 9.2 实现方案

@Link 的实现与 @Prop 非常相似，只是运行时类型和创建方法不同。

```java
// src/main/java/com/ets2jsc/transformer/LinkDecoratorHandler.java
public class LinkDecoratorHandler implements DecoratorHandler {

    @Override
    public boolean canHandle(String decoratorName) {
        return "@Link".equals(decoratorName);
    }

    @Override
    public PropertyDeclaration transformProperty(PropertyDeclaration prop) {
        String propertyName = prop.getName();
        String privateName = propertyName + "__";

        // 使用 SynchedPropertySimpleTwoWay 而不是 SynchedPropertySimpleOneWay
        PropertyDeclaration privateProp = new PropertyDeclaration(
            Modifier.PRIVATE,
            privateName,
            "SynchedPropertySimpleTwoWay<" + prop.getType() + ">",
            null
        );

        // getter/setter 与 @Prop 相同

        // 但初始化使用 createLink
        Statement initStmt = new ExpressionStatement(
            new AssignmentExpression(
                new MemberExpression("this", privateName),
                new MethodCallExpression(
                    new MemberExpression("this", "createLink"),
                    List.of(
                        new LiteralExpression(propertyName),
                        new ArrowExpression(
                            List.of(),
                            new MemberExpression("this", propertyName)
                        )
                    )
                )
            )
        );

        return new TransformedProperty(privateProp, getter, setter, initStmt);
    }

    @Override
    public String getRuntimeType() {
        return "SynchedPropertySimpleTwoWay";  // 不同
    }

    @Override
    public String getCreateMethod() {
        return "createLink";  // 不同
    }
}
```

---

## 四、增强功能实现方案 (P2)

### 需求 #10: @Provide/@Consume 装饰器

#### 10.1 原始实现分析

**参考文件**: `compiler/src/pre_define.ts`

**核心定义**:
```typescript
export const APP_STORAGE: string = 'AppStorage';
export const APP_STORAGE_SET_AND_PROP: string = 'SetAndProp';
export const APP_STORAGE_SET_AND_LINK: string = 'SetAndLink';
export const INITIALIZE_CONSUME_FUNCTION: string = 'initializeConsume';
export const ADD_PROVIDED_VAR: string = 'addProvidedVar';
```

#### 10.2 实现方案

```java
// src/main/java/com/ets2jsc/transformer/ProvideDecoratorHandler.java
public class ProvideDecoratorHandler implements DecoratorHandler {

    @Override
    public PropertyDeclaration transformProperty(PropertyDeclaration prop) {
        String propertyName = prop.getName();
        String privateName = propertyName + "__";

        // @Prop 使用 AppStorage.SetAndProp
        Statement initStmt = new ExpressionStatement(
            new AssignmentExpression(
                new MemberExpression("this", privateName),
                new MethodCallExpression(
                    new MemberExpression("AppStorage", "SetAndProp"),
                    List.of(
                        new LiteralExpression(propertyName),
                        new ArrowExpression(
                            List.of(),
                            new MemberExpression("this", propertyName)
                        ),
                        new ArrowExpression(
                            List.of("newValue"),
                            new AssignmentExpression(
                                new MemberExpression("this", propertyName),
                                new IdentifierExpression("newValue")
                            )
                        )
                    )
                )
            )
        )
        );

        return new TransformedProperty(..., initStmt);
    }
}

// src/main/java/com/ets2jsc/transformer/ConsumeDecoratorHandler.java
public class ConsumeDecoratorHandler implements DecoratorHandler {

    @Override
    public PropertyDeclaration transformProperty(PropertyDeclaration prop) {
        String propertyName = prop.getName();
        String privateName = propertyName + "__";

        // @Consume 使用 AppStorage.SetAndLink
        Statement initStmt = new ExpressionStatement(
            new AssignmentExpression(
                new MemberExpression("this", privateName),
                new MethodCallExpression(
                    new MemberExpression("AppStorage", "SetAndLink"),
                    List.of(
                        new LiteralExpression(propertyName),
                        new ArrowExpression(
                            List.of(),
                            new MemberExpression("this", propertyName)
                        ),
                        new ArrowExpression(
                            List.of("newValue"),
                            new AssignmentExpression(
                                new MemberExpression("this", propertyName),
                                new IdentifierExpression("newValue")
                            )
                        )
                    )
                )
            )
        )
        );

        return new TransformedProperty(..., initStmt);
    }
}
```

---

### 需求 #13: @Builder 函数

#### 13.1 原始实现分析

**参考文件**: `compiler/src/process_component_class.ts`

**核心逻辑**:
```typescript
// Builder 函数需要添加 BuilderParam 参数
function processBuilderFunction(
  node: ts.FunctionDeclaration
): ts.FunctionDeclaration {
  const params = [...node.parameters];

  // 添加 BuilderParam 参数
  params.push(
    ts.factory.createParameterDeclaration(
      undefined,
      undefined,
      '__builder__',
      undefined,
      ts.factory.createTypeReferenceNode('BuilderParam'),
      ts.factory.createIdentifier('undefined')
    )
  )

  return ts.factory.updateFunctionDeclaration(
    node,
    node.decorators,
    node.modifiers,
    node.asteriskToken,
    node.name,
    node.typeParameters,
    params,
    node.type,
    processBuilderBody(node.body)  // 处理函数体
  );
}
```

#### 13.2 实现方案

```java
// src/main/java/com/ets2jsc/transformer/BuilderDecoratorHandler.java
package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;

/**
 * @Builder 装饰器处理器
 */
public class BuilderDecoratorHandler {

    /**
     * 转换 @Builder 函数
     *
     * 输入: @Builder function MyBuilder(param: string) { Text(param) }
     * 输出:
     *   function MyBuilder(param: string, __builder__: BuilderParam = undefined) {
     *     Text.create(param);
     *     Text.pop();
     *   }
     */
    public FunctionDeclaration transformBuilderFunction(FunctionDeclaration func) {
        // 1. 添加 BuilderParam 参数
        List<Parameter> params = new ArrayList<>(func.getParameters());

        Parameter builderParam = new Parameter(
            "__builder__",
            "BuilderParam",
            new LiteralExpression("undefined")
        );
        params.add(builderParam);

        // 2. 转换函数体中的组件调用
        BlockExpression newBody = transformBuilderBody(func.getBody());

        return new FunctionDeclaration(
            func.getModifiers(),
            "function",
            func.getName(),
            params,
            newBody
        );
    }

    /**
     * 转换 Builder 函数体
     * 处理函数体内的组件调用为 create/pop 模式
     */
    private BlockExpression transformBuilderBody(BlockExpression body) {
        List<Statement> statements = new ArrayList<>();

        for (Statement stmt : body.getStatements()) {
            if (stmt instanceof ExpressionStatement) {
                Expression expr = ((ExpressionStatement) stmt).getExpression();

                if (expr instanceof ComponentExpression) {
                    ComponentCreatePopTransformer transformer = new ComponentCreatePopTransformer();
                    statements.addAll(transformer.transform((ComponentExpression) expr));
                } else {
                    statements.add(stmt);
                }
            } else {
                statements.add(stmt);
            }
        }

        return new BlockExpression(statements);
    }

    /**
     * 转换 Builder 调用点
     *
     * 输入: MyBuilder('hello')
     * 输出:
     *   {
     *     const __builder__ = new BuilderParam();
     *     MyBuilder('hello', __builder__);
     *     __builder__.build();
     *   }
     */
    public Statement transformBuilderCall(FunctionCallExpression call) {
        List<Statement> statements = new ArrayList<>();

        // 1. 创建 BuilderParam
        statements.add(
            new VariableDeclaration(
                "const",
                "__builder__",
                new NewExpression(
                    "BuilderParam",
                    new ArrayList<>()
                )
            )
        );

        // 2. 调用 Builder 函数
        List<Expression> args = new ArrayList<>(call.getArguments());
        args.add(new IdentifierExpression("__builder__"));

        statements.add(
            new ExpressionStatement(
                new FunctionCallExpression(call.getFunctionName(), args)
            )
        );

        // 3. 调用 build
        statements.add(
            new ExpressionStatement(
                new MethodCallExpression(
                    new MemberExpression("__builder__", "build"),
                    new ArrayList<>()
                )
            )
        );

        return new BlockExpression(statements);
    }
}
```

---

### 需求 #14: 资源引用处理

#### 14.1 原始实现分析

**参考文件**: `compiler/src/pre_define.ts`

**资源类型定义**:
```typescript
export const RESOURCE_TYPE = {
  color: 10001,
  float: 10002,
  string: 10003,
  plural: 10004,
  boolean: 10005,
  intarray: 10006,
  integer: 10007,
  pattern: 10008,
  strarray: 10009,
  media: 10010,
  font: 10011,
  profile: 10012
};
```

**核心转换函数**: `processResourceExpression()`

```typescript
function processResourceExpression(node: ts.CallExpression): ts.Expression {
  const funcName = node.expression.getText();

  if (funcName === '$r') {
    const resourcePath = node.arguments[0].text;
    const [module, type, name] = parseResourcePath(resourcePath);

    const resourceType = RESOURCE_TYPE[type];

    return ts.factory.createCallExpression(
      ts.factory.createIdentifier('__getResourceId__'),
      undefined,
      [
        ts.factory.createNumericLiteral(resourceType),
        ts.factory.createIdentifier('undefined'),  // bundleName
        ts.factory.createStringLiteral(module),
        ts.factory.createStringLiteral(name)
      ]
    );
  }

  if (funcName === '$rawfile') {
    const filename = node.arguments[0].text;

    return ts.factory.createCallExpression(
      ts.factory.createIdentifier('__getRawFileId__'),
      undefined,
      [ts.factory.createStringLiteral(filename)]
    );
  }
}
```

#### 14.2 实现方案

```java
// src/main/java/com/ets2jsc/transformer/ResourceTransformer.java
package com.ets2jsc.transformer;

import com.ets2jsc.ast.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源引用转换器
 * 对应原始实现中的资源处理
 */
public class ResourceTransformer {

    private static final Map<String, Integer> RESOURCE_TYPES = new HashMap<>();

    static {
        RESOURCE_TYPES.put("color", 10001);
        RESOURCE_TYPES.put("float", 10002);
        RESOURCE_TYPES.put("string", 10003);
        RESOURCE_TYPES.put("plural", 10004);
        RESOURCE_TYPES.put("boolean", 10005);
        RESOURCE_TYPES.put("intarray", 10006);
        RESOURCE_TYPES.put("integer", 10007);
        RESOURCE_TYPES.put("pattern", 10008);
        RESOURCE_TYPES.put("strarray", 10009);
        RESOURCE_TYPES.put("media", 10010);
        RESOURCE_TYPES.put("font", 10011);
        RESOURCE_TYPES.put("profile", 10012);
    }

    /**
     * 转换 $r() 资源引用
     */
    public Expression transformResourceCall(FunctionCallExpression call) {
        String funcName = call.getFunctionName();

        if ("$r".equals(funcName)) {
            return transformRResource(call);
        } else if ("$rawfile".equals(funcName)) {
            transformRawfileResource(call);
        }

        return call;
    }

    /**
     * 转换 $r() 调用
     *
     * 输入: $r('app.string.name')
     * 输出: __getResourceId__(10003, undefined, 'app_module', 'name')
     */
    private Expression transformRResource(FunctionCallExpression call) {
        String resourcePath = getResourcePath(call);

        // 解析路径: 'app.string.name' -> [module, type, name]
        String[] parts = resourcePath.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid resource path: " + resourcePath);
        }

        String module = parts[0];           // app
        String type = parts[1];             // string
        String name = parts[2];             // name

        Integer resourceType = RESOURCE_TYPES.get(type);
        if (resourceType == null) {
            throw new RuntimeException("Unknown resource type: " + type);
        }

        // 生成: __getResourceId__(type, bundle, module, name)
        List<Expression> args = List.of(
            new LiteralExpression(resourceType),
            new IdentifierExpression("undefined"),  // bundleName
            new LiteralExpression(module + "_module"),  // moduleName
            new LiteralExpression(name)
        );

        return new FunctionCallExpression("__getResourceId__", args);
    }

    /**
     * 转换 $rawfile() 调用
     *
     * 输入: $rawfile('icon.png')
     * 输出: __getRawFileId__('icon.png')
     */
    private Expression transformRawfileResource(FunctionCallExpression call) {
        String filename = getResourcePath(call);

        return new FunctionCallExpression(
            "__getRawFileId__",
            List.of(new LiteralExpression(filename))
        );
    }

    private String getResourcePath(FunctionCallExpression call) {
        Expression arg = call.getArguments().get(0);
        if (arg instanceof LiteralExpression) {
            return ((LiteralExpression) arg).getValue();
        }
        throw new RuntimeException("Resource path must be a string literal");
    }
}
```

---

## 五、测试验证方案

### 5.1 单元测试结构

```
src/test/java/com/ets2jsc/
├── component/              # 组件转换测试
│   ├── ComponentCreatePopTest.java
│   ├── ForEachTransformTest.java
│   └── IfTransformTest.java
├── decorator/              # 装饰器测试
│   ├── StateDecoratorTest.java
│   ├── PropDecoratorTest.java
│   └── LinkDecoratorTest.java
├── statement/               # 语句测试
│   ├── ImportStatementTest.java
│   └── ExportStatementTest.java
└── integration/             # 集成测试
    └── FullComponentTest.java
```

### 5.2 测试用例示例

```java
// src/test/java/com/ets2jsc/integration/FullComponentTest.java
@Test
public void testCompleteComponent() {
    // 完整的组件测试
    String input =
        "import { Text } from '@kit.ArkUI';\n" +
        "\n" +
        "@Component\n" +
        "struct MyComponent {\n" +
        "  @State count: number = 0;\n" +
        "  @Prop title: string = 'Hello';\n" +
        "\n" +
        "  build() {\n" +
        "    Column() {\n" +
        "      Text(this.title).fontSize(16)\n" +
        "      ForEach(this.items, (item) => {\n" +
        "        Text(item.name)\n" +
        "      }, (item) => item.id)\n" +
        "      if (this.showDetail) {\n" +
        "        Text('Detail')\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}";

    String output = compile(input);

    // 验证各个特性
    assertTrue(output.contains("import { Text }"));
    assertTrue(output.contains("ObservedPropertySimple<number>")); // @State
    assertTrue(output.contains("SynchedPropertySimpleOneWay<string>")); // @Prop
    assertTrue(output.contains("Column.create()"));
    assertTrue(output.contains("Text.create"));
    assertTrue(output.contains("ForEach.create"));
    assertTrue(output.contains("If.create"));
}
```

### 5.3 对基准实现的测试对比

建立测试对比机制，确保新实现的输出与原始编译器一致：

```java
// src/test/java/com/ets2jsc/compatibility/OriginalCompilerComparisonTest.java
@Test
public void compareWithOriginalCompiler() {
    String etsCode = """
        @Component
        struct Test {
          @State value: number = 0;
          build() {
            Text('Value: ' + this.value)
          }
        }
        """;

    // 1. 使用原始编译器编译
    String originalOutput = OriginalCompiler.compile(etsCode);

    // 2. 使用新编译器编译
    String newOutput = NewCompiler.compile(etsCode);

    // 3. 规范化后比较
    String normalizedOriginal = normalizeCode(originalOutput);
    String normalizedNew = normalizeCode(newOutput);

    assertEquals(normalizedOriginal, normalizedNew);
}

private String normalizeCode(String code) {
    // 移除空白差异、注释等
    return code
        .replaceAll("\\s+", "")
        .replaceAll("//.*", "");
}
```

---

## 六、开发计划

### 6.1 迭代计划

#### Sprint 1 (2周) - 核心 P0 功能
- [ ] 需求 #1: UI 组件 create/pop 转换
- [ ] 需求 #5: 链式调用拆分
- [ ] 需求 #2: Import 语句处理
- [ ] 需求 #3: Export 语句处理

**验收标准**:
- 简单组件可编译
- Import/Export 正确处理
- 单元测试通过率 > 80%

#### Sprint 2 (2周) - 重要 P1 功能 (第1部分)
- [ ] 需求 #6: ForEach 转换
- [ ] 需求 #7: If 条件渲染转换
- [ ] 需求 #11: 表达式完整支持

**验收标准**:
- ForEach/If 正确转换
- 所有表达式类型支持
- 集成测试通过

#### Sprint 3 (2周) - 重要 P1 功能 (第2部分)
- [ ] 需求 #4: 类型声明处理
- [ ] 需求 #8: @Prop 装饰器
- [ ] 需求 #9: @Link 装饰器

**验收标准**:
- 类型声明正确移除
- @Prop/@Link 正确转换
- 运行时类型正确

#### Sprint 4 (1周) - 增强 P2 功能
- [ ] 需求 #10: @Provide/@Consume 装饰器
- [ ] 需求 #13: @Builder 函数
- [ ] 需求 #14: 资源引用处理

**验收标准**:
- 所有装饰器支持
- Builder 函数正确转换
- 资源引用正确

### 6.2 技术债务管理

#### 代码重构
- 提取公共转换逻辑
- 统一错误处理
- 优化 AST 遍历性能

#### 文档更新
- 更新技术文档
- 补充 API 文档
- 编写迁移指南

---

## 附录

### A. 关键文件映射表

| 需求 | 原始实现文件 | 新实现文件 |
|------|-------------|-----------|
| #1 | process_component_build.ts | ComponentCreatePopTransformer.java |
| #2 | process_import.ts | ImportStatement.java, ModuleCollector.java |
| #3 | process_import.ts | ExportStatement.java, TypeDeclarationChecker.java |
| #4 | process_ui_syntax.ts | TypeDeclarationChecker.java |
| #5 | process_component_build.ts | ChainedCallTransformer.java |
| #6 | process_component_build.ts | ForEachTransformer.java |
| #7 | process_component_build.ts | IfTransformer.java |
| #8 | pre_define.ts, process_component_member.ts | PropDecoratorHandler.java |
| #9 | pre_define.ts, process_component_member.ts | LinkDecoratorHandler.java |
| #10 | pre_define.ts | ProvideDecoratorHandler.java, ConsumeDecoratorHandler.java |
| #11 | process_ui_syntax.ts | TypeScriptScriptParser.java |
| #13 | process_component_class.ts | BuilderDecoratorHandler.java |
| #14 | process_component_build.ts | ResourceTransformer.java |

### B. 运行时依赖

需要提供的运行时 API（由 ArkTS 运行时提供）：

| API | 用途 |
|-----|------|
| `View.create()` | 组件创建 |
| `View.pop()` | 组件关闭 |
| `ObservedPropertySimple<T>` | @State 运行时类型 |
| `SynchedPropertySimpleOneWay<T>` | @Prop 运行时类型 |
| `SynchedPropertySimpleTwoWay<T>` | @Link 运行时类型 |
| `AppStorage.SetAndProp()` | @Provide 初始化 |
| `AppStorage.SetAndLink()` | @Consume 初始化 |
| `ForEach.create/pop/itemGenerator/keyGenerator` | ForEach 组件 |
| `If/create/pop/branchId` | If 条件渲染 |
| `__getResourceId__()` | 资源引用 |
| `__getRawFileId__()` | Rawfile 资源 |
| `BuilderParam` | Builder 参数 |

### C. 参考资料

1. **原始编译器**: `developtools_ace_ets2bundle`
2. **技术文档**: `ETS_to_JS_Compiler_Technical_Documentation.md`
3. **需求文档**: `ETS编译器功能需求文档.md`
4. **ArkTS 官方文档**: https://developer.harmonyos.com/cn/docs/documentation/doc-references/ts-arkts-overview

---

*文档版本: v1.0*
*最后更新: 2026-01-30*
*基于: developtools_ace_ets2bundle 实现方案*
