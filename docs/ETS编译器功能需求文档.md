# ETS → JS 编译器功能需求文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0 |
| 创建日期 | 2026-01-30 |
| 项目名称 | ets2jsc 编译器 |
| 目标 | 实现 ETS/ArkTS 到 JavaScript 的完整编译 |

---

## 一、项目概述

### 1.1 目标

将 ArkTS/ETS 源码编译为可在 HarmonyOS 设备上运行的 JavaScript 代码。

### 1.2 当前完成状态

- ✅ @Component 装饰器处理（struct → class View）
- ✅ @State 装饰器处理（响应式属性）
- ✅ static/async 方法支持
- ✅ await 表达式支持
- ✅ 基础语句解析（VariableStatement, ForOfStatement, ReturnStatement）

### 1.3 核心缺失

- ❌ UI 组件的 create/pop 模式转换
- ❌ Import/Export 语句处理
- ❌ 链式调用拆分
- ❌ ForEach/If 等控制流转换
- ❌ 其他状态装饰器（@Prop, @Link 等）

---

## 二、功能需求列表

### 需求 #1: UI 组件 create/pop 转换

**优先级**: P0（核心功能）

**当前问题**:
```
输入 ETS:        Text('Hello').fontSize(16)
当前输出:        Text('Hello').fontSize(16)  // ❌ 未转换
预期输出:        Text.create('Hello');
                  Text.fontSize(16);
                  Text.pop();
```

**需求描述**:
将声明式 UI 组件调用转换为命令式的 create/pop 模式。

**转换规则**:

1. 简单组件：
```typescript
// 输入
Text('Hello')

// 输出
Text.create('Hello');
Text.pop();
```

2. 带属性的组件：
```typescript
// 输入
Text('Hello')
  .fontSize(16)
  .fontColor(Color.Red)

// 输出
Text.create('Hello');
Text.fontSize(16);
Text.fontColor(Color.Red);
Text.pop();
```

3. 嵌套组件（闭包表示子组件）：
```typescript
// 输入
Column() {
  Text('Hello')
  Button('Click')
}

// 输出
Column.create();
Text.create('Hello');
Text.pop();
Button.create('Click');
Button.pop();
Column.pop();
```

**技术要点**:
- 识别内置组件（Text, Column, Row, Button 等）
- 识别链式调用（.methodName()）
- 处理嵌套闭包（{} 表示子组件）
- 生成正确的语句序列

**依赖**: 需求 #2（链式调用识别）

---

### 需求 #2: Import 语句处理

**优先级**: P0（核心功能）

**当前问题**:
```
输入 ETS:        import { Permissions } from '@kit.AbilityKit';
当前输出:        (空，被忽略)
预期输出:        import { Permissions } from '@kit.AbilityKit';
```

**需求描述**:
解析并保留 ETS 源文件中的 import 语句，输出到编译后的 JS 文件顶部。

**支持格式**:

1. 命名导入：
```typescript
import { A, B } from 'module';
// 保持不变
```

2. 默认导入：
```typescript
import Module from 'module';
// 保持不变
```

3. 命名空间导入：
```typescript
import * as Module from 'module';
// 保持不变
```

4. 副作用导入：
```typescript
import 'module';
// 保持不变
```

5. 类型导入：
```typescript
import type { Type } from 'module';
// 转换为普通 import（JS 不支持类型）
import { Type } from 'module';
```

**技术要点**:
- parse-ets.js 需要输出 ImportDeclaration 节点
- TypeScriptScriptParser 需要转换 ImportDeclaration
- 按出现顺序在文件顶部输出所有 import 语句

**依赖**: 无

---

### 需求 #3: Export 语句处理

**优先级**: P0（核心功能）

**当前问题**:
```
输入 ETS:        export class PermissionUtils { ... }
当前输出:        class PermissionUtils { ... }  // ❌ 丢失 export
预期输出:        export class PermissionUtils { ... }
```

**需求描述**:
保留 ETS 源文件中的 export 关键字。

**支持格式**:

1. 导出类：
```typescript
export class MyClass { }
// 保持不变
```

2. 导出函数：
```typescript
export function myFunc() { }
// 保持不变
```

3. 导出变量/常量：
```typescript
export const myConst = 1;
// 保持不变
```

4. 导出类型/接口（需移除）：
```typescript
export interface MyInterface { }
// 输出为空（JS 中无类型）
```

**技术要点**:
- 识别并保留 export 关键字
- 移除类型相关的 export（interface, type, enum）

**依赖**: 需求 #4（类型声明处理）

---

### 需求 #4: 类型声明处理

**优先级**: P1（重要功能）

**当前问题**:
```
输入 ETS:        interface PermissionResult { ... }
当前输出:        (被忽略)
预期输出:        (移除，JS 中无接口)
```

**需求描述**:
正确处理 TypeScript 类型声明，编译时移除类型相关的语法。

**需要处理的节点**:

1. InterfaceDeclaration - 移除
```typescript
interface MyInterface {
  name: string;
}
// 编译后移除
```

2. TypeAliasDeclaration - 移除
```typescript
type MyType = string | number;
// 编译后移除
```

3. EnumDeclaration - 转换为对象
```typescript
enum Color {
  Red,
  Blue
}
// 转换为
const Color = {
  Red: 0,
  Blue: 1
};
```

4. 类型注解 - 移除
```typescript
function foo(x: number): string { }
// 转换为
function foo(x) { }
```

**技术要点**:
- parse-ets.js 输出类型声明节点
- TypeScriptScriptParser 识别并处理
- CodeGenerator 在生成代码时移除类型注解

**依赖**: 无

---

### 需求 #5: 链式调用拆分

**优先级**: P0（核心功能）

**当前问题**:
```
输入 ETS:        Text('Hello').fontSize(16).fontColor(Color.Red)
当前解析:        CallExpression → 转为字符串直接输出
预期解析:        ComponentExpression { chainedCalls: [...] }
```

**需求描述**:
正确解析组件的链式调用，转换为多个独立的语句。

**转换示例**:
```typescript
// 输入
Text('Hello')
  .fontSize(16)
  .fontColor(Color.Red)
  .onClick(() => { })

// 输出
Text.create('Hello');
Text.fontSize(16);
Text.fontColor(Color.Red);
Text.onClick(() => { });
Text.pop();
```

**技术要点**:
- 识别 PropertyAccessExpression 中的链式调用
- 构建完整的调用链结构
- 按顺序生成独立语句

**依赖**: 无（与需求 #1 并行实现）

---

### 需求 #6: ForEach 转换

**优先级**: P1（重要功能）

**当前问题**:
```
输入 ETS:        ForEach(items, (item) => Text(item.name), (item) => item.id)
当前输出:        (占位符 "// for...of loop")
预期输出:        完整的 ForEach.create/pop 结构
```

**需求描述**:
将 ForEach 组件转换为 ArkTS 运行时需要的格式。

**转换示例**:
```typescript
// 输入
ForEach(
  this.items,
  (item: Item, index: number) => {
    Text(item.name).fontSize(16)
  },
  (item: Item) => item.id
)

// 输出
ForEach.create();
const __itemGenFunction__ = (item: Item, index: number) => {
  Text.create(item.name);
  Text.fontSize(16);
  Text.pop();
};
const __keyGenFunction__ = (item: Item) => item.id;
ForEach.itemGenerator(__itemGenFunction__);
ForEach.keyGenerator(__keyGenFunction__);
ForEach.pop();
```

**技术要点**:
- 解析 ForEach 的三个参数（数组、项生成器、键生成器）
- 转换项生成器中的组件调用
- 生成唯一标识符避免冲突

**依赖**: 需求 #1（UI 组件转换）

---

### 需求 #7: If 条件渲染转换

**优先级**: P1（重要功能）

**当前问题**:
```
输入 ETS:        if (condition) { Text('Yes') } else { Text('No') }
当前输出:        (原样输出)
预期输出:        If.create/branchId/pop 结构
```

**需求描述**:
将 if 条件渲染转换为 ArkTS 运行时需要的分支格式。

**转换示例**:
```typescript
// 输入
if (this.condition) {
  Text('True')
} else {
  Text('False')
}

// 输出
If.create();
if (this.condition) {
  If.branchId(0);
  Text.create('True');
  Text.pop();
} else {
  If.branchId(1);
  Text.create('False');
  Text.pop();
}
If.pop();
```

**技术要点**:
- 包裹 if 语句在 If.create()/If.pop() 之间
- 在每个分支添加 If.branchId(id)
- 处理嵌套 if 语句
- 分支 ID 递增分配

**依赖**: 需求 #1（UI 组件转换）

---

### 需求 #8: @Prop 装饰器

**优先级**: P1（重要功能）

**当前问题**:
```
输入 ETS:        @Prop count: number = 0;
当前输出:        (被当作普通属性)
预期输出:        使用 SynchedPropertySimpleOneWay
```

**需求描述**:
实现 @Prop 装饰器，父组件到子组件的单向数据绑定。

**转换示例**:
```typescript
// 输入
@Component
struct Child {
  @Prop count: number = 0;
  build() {
    Text(this.count)
  }
}

// 输出
class Child extends View {
  private count__: SynchedPropertySimpleOneWay<number>;

  constructor(parent: View) {
    super(parent);
    this.count__ = this.createProp('count', () => this.count);
  }

  get count(): number {
    return this.count__.get();
  }

  set count(newValue: number) {
    this.count__.set(newValue);
  }

  render() {
    Text.create(this.count__.get());
    Text.pop();
  }
}
```

**技术要点**:
- 类似 @State 的转换模式
- 使用 SynchedPropertySimpleOneWay 替代 ObservedPropertySimple
- 使用 createProp() 替代 createState()
- 构造函数需要 parent 参数

**依赖**: 无（类似 @State 已实现）

---

### 需求 #9: @Link 装饰器

**优先级**: P1（重要功能）

**当前问题**:
```
输入 ETS:        @Link count: number;
当前输出:        (被当作普通属性)
预期输出:        使用 SynchedPropertySimpleTwoWay
```

**需求描述**:
实现 @Link 装饰器，父子组件的双向数据绑定。

**转换示例**:
```typescript
// 输入
@Link count: number;

// 输出
private count__: SynchedPropertySimpleTwoWay<number>;

constructor(parent: View) {
  super(parent);
  this.count__ = this.createLink('count', () => this.count);
}
```

**技术要点**:
- 使用 SynchedPropertySimpleTwoWay
- 使用 createLink() 方法
- 构造函数需要 parent 参数

**依赖**: 类似 @Prop

---

### 需求 #10: @Provide/@Consume 装饰器

**优先级**: P2（增强功能）

**需求描述**:
实现跨组件层级的状态传递，使用 AppStorage。

**转换示例**:
```typescript
// 输入
@Component
struct Parent {
  @Provide appName: string = 'MyApp';
}

@Component
struct Child {
  @Consume appName: string;
}

// Parent 输出
private appName__: ObservedPropertySimple<string>;

constructor() {
  super();
  this.appName__ = AppStorage.setAndProp('appName',
    () => this.appName,
    (newValue) => { this.appName = newValue; });
}

// Child 输出
private appName__: ObservedPropertySimple<string>;

constructor() {
  super();
  this.appName__ = AppStorage.getAndLink('appName',
    () => this.appName,
    (newValue) => { this.appName = newValue; });
}
```

**技术要点**:
- @Provide 使用 AppStorage.setAndProp
- @Consume 使用 AppStorage.getAndLink
- 确保使用相同的存储键名

**依赖**: 无

---

### 需求 #11: 表达式完整支持

**优先级**: P1（重要功能）

**当前问题**:
```
输入 ETS:        a + b * c
当前输出:        {"kind":...}  // 输出 JSON
预期输出:        a + b * c
```

**需求描述**:
支持所有 TypeScript 表达式类型的正确转换。

**需要支持的节点**:

| 节点类型 | 示例 | 优先级 |
|----------|------|--------|
| BinaryExpression | a + b, x > y | P1 |
| PrefixUnaryExpression | !flag, ++i | P1 |
| PostfixUnaryExpression | i++, obj.prop | P1 |
| ConditionalExpression | a ? b : c | P1 |
| ArrayLiteralExpression | [1, 2, 3] | P1 |
| ObjectLiteralExpression | { a: 1 } | P1 |
| ElementAccessExpression | arr[0] | P1 |
| SpreadElement | ...args | P2 |
| TemplateExpression | `hello ${name}` | P2 |

**技术要点**:
- parse-ets.js 输出完整表达式结构
- TypeScriptScriptParser 的 convertExpressionToString 完整实现
- 正确处理运算符优先级

**依赖**: 无

---

### 需求 #12: 语句完整支持

**优先级**: P2（增强功能）

**需要支持的节点**:

| 节点类型 | 示例 | 优先级 |
|----------|------|--------|
| ForStatement | for (let i = 0; i < 10; i++) | P2 |
| WhileStatement | while (condition) | P2 |
| DoStatement | do {} while (condition) | P2 |
| SwitchStatement | switch (x) | P2 |
| TryStatement | try {} catch {} | P2 |
| ThrowStatement | throw error | P2 |
| BreakStatement | break | P2 |
| ContinueStatement | continue | P2 |

**技术要点**:
- 逐个添加到 parse-ets.js
- 逐个添加到 TypeScriptScriptParser

**依赖**: 无

---

### 需求 #13: @Builder 函数

**优先级**: P2（增强功能）

**需求描述**:
将 @Builder 装饰的函数转换为 ArkTS Builder 函数格式。

**转换示例**:
```typescript
// 输入
@Builder
function MyBuilder(param: string) {
  Text(param)
}

// 调用
MyBuilder('hello')

// 输出
function MyBuilder(param: string, __builder__: BuilderParam = undefined) {
  Text.create(param);
  Text.pop();
}

// 调用转换
{
  const __builder__ = new BuilderParam();
  MyBuilder('hello', __builder__);
  __builder__.build();
}
```

**技术要点**:
- 添加 BuilderParam 参数
- 转换函数体内的组件调用
- 处理调用点的 BuilderParam 创建

**依赖**: 需求 #1（UI 组件转换）

---

### 需求 #14: 资源引用处理

**优先级**: P2（增强功能）

**需求描述**:
转换 $r() 和 $rawfile() 资源引用。

**转换示例**:
```typescript
// 输入
Text($r('app.string.name'))
Image($rawfile('icon.png'))

// 输出
Text.create(__getResourceId__(
  10003,              // RESOURCE_TYPE.string
  undefined,          // bundleName
  'app_module',       // moduleName
  'name'              // resourceId
));
Image.create(__getRawFileId__('icon.png'));
```

**技术要点**:
- 解析资源路径字符串
- 映射资源类型到常量
- 生成运行时函数调用

**依赖**: 无

---

## 三、实现优先级排序

### 第一阶段（核心必需）
1. **需求 #1**: UI 组件 create/pop 转换
2. **需求 #5**: 链式调用拆分
3. **需求 #2**: Import 语句处理
4. **需求 #3**: Export 语句处理

### 第二阶段（重要功能）
5. **需求 #6**: ForEach 转换
6. **需求 #7**: If 条件渲染转换
7. **需求 #11**: 表达式完整支持
8. **需求 #4**: 类型声明处理
9. **需求 #8**: @Prop 装饰器
10. **需求 #9**: @Link 装饰器

### 第三阶段（增强功能）
11. **需求 #10**: @Provide/@Consume 装饰器
12. **需求 #13**: @Builder 函数
13. **需求 #14**: 资源引用处理
14. **需求 #12**: 语句完整支持

---

## 四、验收标准

### 4.1 编译正确性
- [ ] 编译后的 JS 代码语法正确
- [ ] 无运行时错误
- [ ] UI 渲染符合预期

### 4.2 完整性
- [ ] 支持 80% 以上的常用 ETS 语法
- [ ] 支持所有内置组件
- [ ] 支持所有状态装饰器

### 4.3 测试覆盖
- [ ] 每个功能有对应的测试用例
- [ ] 测试覆盖率达到 70% 以上

---

## 五、附录

### 5.1 相关文档
- ETS_to_JS_Compiler_Technical_Documentation.md - 技术参考文档

### 5.2 当前代码结构
```
src/main/java/com/ets2jsc/
├── ast/                    # AST 节点定义
├── parser/                 # 解析器
│   ├── TypeScriptScriptParser.java
│   └── parse-ets.js
├── transformer/            # 转换器
│   ├── BuildMethodTransformer.java
│   ├── ComponentTransformer.java
│   └── DecoratorTransformer.java
├── generator/              # 代码生成器
│   └── CodeGenerator.java
└── constant/               # 常量定义
```

### 5.3 修改说明
本文档会随着项目进展持续更新。
