# ETS 到 JS 编译器技术文档

## 目录

1. [概述](#概述)
2. [项目架构](#项目架构)
3. [编译流程](#编译流程)
4. [核心组件详解](#核心组件详解)
5. [AST 转换](#ast-转换)
6. [代码生成](#代码生成)
7. [装饰器处理](#装饰器处理)
8. [UI 组件处理](#ui-组件处理)
9. [构建模式](#构建模式)
10. [关键数据结构](#关键数据结构)
11. [实现指南](#实现指南)

---

## 概述

### 功能定位

该编译器是 HarmonyOS ArkTS 应用的核心工具，负责将 ArkTS/ETS (Extended TypeScript) 代码转换为可在 HarmonyOS 设备上运行的 JavaScript 代码。ArkTS 是 TypeScript 的超集，添加了声明式 UI、状态管理、装饰器等特性。

### 输入输出

**输入：**
- `.ets` 文件 - ArkTS 源码
- `.ts` 文件 - TypeScript 源码
- `.js` 文件 - JavaScript 源码
- 项目配置文件（`manifest.json`、`module.json`、`aceBuild.json` 等）

**输出：**
- JavaScript 代码
- Source Map（源码映射）
- 声明文件（.d.ts）
- ABC 字节码（ArkTS Bytecode）

---

## 项目架构

### 目录结构

```
developtools_ace_ets2bundle/
├── compiler/                          # 主编译器代码
│   ├── src/                           # TypeScript 源码
│   │   ├── fast_build/               # 快速构建系统
│   │   │   ├── ark_compiler/         # ArkTS 编译核心
│   │   │   │   ├── common/           # 公共模块
│   │   │   │   ├── bundle/           # Bundle 模式
│   │   │   │   ├── module/           # Module 模式
│   │   │   │   ├── babel-plugin.ts   # Babel 插件
│   │   │   │   ├── transform.ts      # 核心转换
│   │   │   │   ├── rollup-plugin-gen-abc.ts  # Rollup 插件
│   │   │   │   ├── generate_bundle_abc.ts    # Bundle ABC 生成
│   │   │   │   └── generate_module_abc.ts    # Module ABC 生成
│   │   │   ├── ets_ui/               # UI 相关处理
│   │   │   ├── system_api/           # API 处理
│   │   │   └── visual/               # 可视化支持
│   │   ├── interop/                  # 互操作处理
│   │   ├── process_*.ts              # 各种处理模块
│   │   ├── ark_utils.ts              # 工具函数
│   │   ├── pre_define.ts             # 预定义常量
│   │   ├── component_map.ts          # 组件映射
│   │   └── validate_ui_syntax.ts     # UI 语法验证
│   ├── codegen/                      # 代码生成
│   │   └── codegen_ets.js            # ETS 代码生成器
│   ├── components/                   # 组件定义（JSON）
│   ├── form_components/              # 表单组件定义
│   ├── lib/                          # 编译后的 JS
│   ├── main.js                       # 主入口
│   └── package.json                  # 项目配置
├── arkui-plugins/                    # ArkUI 插件系统
└── arkui_noninterop_tools/           # 非互操作工具
```

### 关键文件说明

| 文件 | 功能 |
|------|------|
| `main.js` | 编译器入口，项目配置初始化 |
| `pre_define.ts` | 预定义常量、装饰器、关键字 |
| `component_map.ts` | 组件映射表，定义所有 UI 组件 |
| `process_ui_syntax.ts` | UI 语法处理，装饰器转换 |
| `process_component_build.ts` | 组件构建处理 |
| `process_custom_component.ts` | 自定义组件处理 |
| `transform.ts` | 快速构建的核心转换逻辑 |
| `rollup-plugin-gen-abc.ts` | Rollup 插件，生成 ABC |

---

## 编译流程

### 整体流程图

```
┌─────────────────────────────────────────────────────────────┐
│                         入口阶段                              │
│  读取配置文件 (manifest.json, module.json, aceBuild.json)    │
│  初始化项目配置 (projectConfig)                              │
│  确定编译模式 (jsbundle/moduleJson/esmodule)                 │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      文件收集阶段                              │
│  解析入口文件 (EntryAbility, Pages)                          │
│  收集所有源文件 (.ets, .ts, .js)                             │
│  收集 Worker 文件                                            │
│  收集测试文件 (TestRunner)                                   │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      解析与验证阶段                            │
│  TypeScript 编译器解析源码                                    │
│  构建 AST (抽象语法树)                                        │
│  类型检查                                                    │
│  UI 语法验证                                                 │
│  API 可用性检查                                              │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      AST 转换阶段                              │
│  装饰器转换 (@Component, @State, @Prop 等)                   │
│  UI 组件转换 (组件调用 → 创建函数)                            │
│  状态管理转换 (响应式变量包装)                                │
│  Builder 函数转换                                            │
│  资源引用转换 ($r, $rawfile)                                 │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      代码生成阶段                              │
│  从转换后的 AST 生成 JavaScript 代码                         │
│  生成 Source Map                                             │
│  生成类型声明文件 (.d.ts)                                    │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      后处理阶段                                │
│  代码压缩 (Terser)                                           │
│  代码分割                                                    │
│  懒加载处理                                                  │
│  生成 ABC 字节码 (es2abc)                                    │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                       输出阶段                                │
│  写入 JS 文件                                                │
│  写入 Source Map                                             │
│  写入 ABC 文件                                               │
│  写入资源索引文件                                            │
└─────────────────────────────────────────────────────────────┘
```

### 编译模式

编译器支持三种编译模式：

#### 1. jsbundle 模式
- 传统打包模式
- 生成单个或多个 Bundle 文件
- 适用于 FA (Feature Ability) 模型

#### 2. moduleJson 模式
- Stage 模型默认模式
- 基于 module.json 配置
- 支持模块化加载

#### 3. esmodule 模式
- ES Module 模式
- 现代化模块系统
- 更好的 Tree Shaking

---

## 核心组件详解

### 1. 主入口 (main.js)

**职责：**
- 初始化项目配置
- 读取配置文件
- 确定编译入口
- 启动编译流程

**关键函数：**

```javascript
// 初始化项目配置
function initProjectConfig(projectConfig) {
  // 设置路径
  // 确定编译模式
  // 初始化标志位
}

// 加载入口文件信息
function loadEntryObj(projectConfig) {
  // 读取 manifest.json/module.json
  // 解析页面列表
  // 解析 Ability 入口
  // 构建 entryObj 映射表
}
```

**项目配置结构 (projectConfig)：**

```typescript
{
  // 路径配置
  projectPath: string;           // 项目根目录
  buildPath: string;             // 构建输出目录
  manifestFilePath: string;      // manifest.json 路径
  aceModuleJsonPath: string;     // module.json 路径

  // 编译配置
  compileMode: string;           // 编译模式: jsbundle/moduleJson/esmodule
  compileHar: boolean;           // 是否编译 HAR
  isPreview: boolean;            // 是否预览模式

  // 入口配置
  entryObj: Object;              // 入口文件映射 {key: filepath}
  entryArrayForObf: string[];    // 混淆入口数组

  // 特性开关
  partialUpdateMode: boolean;    // 部分更新模式
  processTs: boolean;            // 是否处理 TS
  pandaMode: string;             // Panda 模式

  // 其他配置
  bundleName?: string;
  moduleName?: string;
  minAPIVersion?: number;
}
```

### 2. 预定义系统 (pre_define.ts)

定义了编译器使用的所有常量、关键字和标识符。

**装饰器定义：**

```typescript
// 组件装饰器
@Component                    // 自定义组件
@Entry                       // 页面入口组件
@Preview                     // 预览组件
@CustomDialog                // 自定义弹窗
@Reusable                    // 可复用组件
@ComponentV2                 // V2 组件

// 状态装饰器
@State                       // 组件内部状态
@Prop                        // 父组件传递属性
@Link                        // 双向绑定
@Provide                     // 提供状态
@Consume                     // 消费状态
@ObjectLink                  // 对象链接
@StorageProp                 // Storage 属性
@StorageLink                 // Storage 链接
@LocalStorageProp            // LocalStorage 属性
@LocalStorageLink            // LocalStorage 链接

// V2 状态装饰器
@Local                       // 本地状态
@Param                       // 参数
@Once                        // 一次性参数
@Event                       // 事件
@Consumer                    // 消费者
@Provider                    // 提供者

// 观察者装饰器
@Observed                    // 被观察类
@ObservedV2                  // V2 被观察类
@Track                       // 跟踪属性

// 方法装饰器
@Builder                     // 构建函数
@LocalBuilder                // 本地构建函数
@Extend                     // 扩展样式
@Styles                      // 全局样式
@AnimatableExtend            // 动画扩展
@Watch                       // 监听器
@BuilderParam                // Builder 参数
@Require                     // 必需参数
@Env                         // 环境变量
```

**运行时函数名：**

```typescript
// 组件生命周期
COMPONENT_CREATE_FUNCTION = 'create'
COMPONENT_POP_FUNCTION = 'pop'
COMPONENT_RENDER_FUNCTION = 'render'
COMPONENT_INITIAL_RENDER_FUNCTION = 'initialRender'

// 状态管理
CREATE_STATE_METHOD = 'createState'
CREATE_PROP_METHOD = 'createProp'
CREATE_LINK_METHOD = 'createLink'

// 视图栈
VIEWSTACKPROCESSOR = 'ViewStackProcessor'
STARTGETACCESSRECORDINGFOR = 'startGetAccessRecordingFor'
STOPGETACCESSRECORDING = 'stopGetAccessRecording'

// 其他
OBSERVECOMPONENTCREATION = 'observeComponentCreation'
UPDATE_FUNC_BY_ELMT_ID = 'updateFuncByElmtId'
```

### 3. 组件映射 (component_map.ts)

维护所有 UI 组件的元数据。

**组件元数据结构：**

```typescript
// 组件配置示例 (从 components/*.json 读取)
{
  "name": "Text",              // 组件名
  "attrs": ["font", "fontSize", "fontColor", ...],  // 属性列表
  "events": ["onClick", "onTouch", ...],            // 事件列表
  "styles": ["width", "height", ...],               // 样式列表
  "atomic": false,             // 是否原子组件
  "single": false,             // 是否单子组件
  "parents": [...],            // 允许的父组件
  "children": [...],           // 允许的子组件
  "systemApi": false,          // 是否系统 API
  "noDebugLine": false         // 是否跳过调试行
}
```

**关键集合：**

```typescript
INNER_COMPONENT_NAMES: Set<string>        // 所有内置组件名
BUILDIN_CONTAINER_COMPONENT: Set<string>  // 容器组件
AUTOMIC_COMPONENT: Set<string>            // 原子组件
BUILDIN_STYLE_NAMES: Set<string>          // 所有内置样式名
GESTURE_TYPE_NAMES: Set<string>           // 手势类型
CUSTOM_BUILDER_PROPERTIES: Set<string>    // 支持 Builder 的属性
```

---

## AST 转换

### TypeScript AST 操作

编译器使用 TypeScript Compiler API 进行 AST 操作。

**核心类型：**

```typescript
import * as ts from 'typescript';

// 常用节点类型
ts.SourceFile              // 源文件节点
ts.ClassDeclaration        // 类声明
ts.MethodDeclaration       // 方法声明
ts.PropertyDeclaration     // 属性声明
ts.CallExpression          // 调用表达式
ts.EtsComponentExpression  // ETS 组件表达式（扩展）
ts.Decorator               // 装饰器
```

**AST 转换流程：**

```typescript
// 1. 解析源码为 AST
const sourceFile = ts.createSourceFile(
  fileName,
  sourceCode,
  ts.ScriptTarget.Latest,
  true,  // setParentNodes
  ts.ScriptKind.ETS
);

// 2. 遍历 AST
ts.forEachChild(sourceFile, (node) => {
  if (ts.isClassDeclaration(node)) {
    // 处理类声明
    processClassDeclaration(node);
  }
  // ... 其他节点类型
});

// 3. 创建新节点
const newClass = ts.factory.createClassDeclaration(
  decorators,           // 装饰器
  modifiers,            // 修饰符
  name,                 // 类名
  typeParameters,       // 类型参数
  heritageClauses,      // 继承子句
  members               // 成员
);

// 4. 打印 AST 为代码
const printer = ts.createPrinter();
const newCode = printer.printNode(
  ts.EmitHint.Unspecified,
  newClass,
  sourceFile
);
```

### 主要转换操作

#### 1. 装饰器转换

**@Component 装饰器处理：**

```typescript
// 源码
@Component
struct MyComponent {
  @State message: string = 'Hello';
  build() {
    Text(this.message)
  }
}

// 转换后的 AST 结构
class MyComponent extends View {
  message__: ObservedPropertySimple;

  constructor() {
    super();
    this.message__ = this.createState('message', () => this.message);
  }

  render() {
    Text.create(this.message__.get());
    Text.pop();
  }
}
```

**装饰器处理流程：**

```typescript
function processDecorator(decorator: ts.Decorator): void {
  const decoratorName = getDecoratorName(decorator);

  switch (decoratorName) {
    case '@Component':
      processComponentDecorator(decorator);
      break;
    case '@State':
      processStateDecorator(decorator);
      break;
    case '@Prop':
      processPropDecorator(decorator);
      break;
    // ... 其他装饰器
  }
}
```

#### 2. 状态属性转换

**@State 转换示例：**

```typescript
// 原始代码
@State count: number = 0;

// 转换后
private count__: ObservedPropertySimple<number>;

constructor() {
  this.count__ = this.initializeConsumeV2('count',
    () => this.count,
    (newValue) => { this.count = newValue; });
}

get count(): number {
  return this.count__.get();
}

set count(newValue: number) {
  this.count__.set(newValue);
}
```

**转换实现逻辑：**

```typescript
function processStateProperty(
  property: ts.PropertyDeclaration
): ts.PropertyDeclaration {
  const propertyName = property.name.getText();
  const privateName = propertyName + '__';
  const propertyType = property.type;

  // 1. 创建私有属性
  const privateProperty = ts.factory.createPropertyDeclaration(
    [ts.factory.createModifier(ts.SyntaxKind.PrivateKeyword)],
    privateName,
    undefined,
    ts.factory.createTypeReferenceNode('ObservedPropertySimple', [propertyType]),
    undefined
  );

  // 2. 创建 getter/setter
  const getter = ts.factory.createGetAccessorDeclaration(
    undefined,
    propertyName,
    [],
    propertyType,
    ts.factory.createBlock([
      ts.factory.createReturnStatement(
        ts.factory.createPropertyAccessExpression(
          ts.factory.createThis(),
          privateName
        )
      )
    ], true)
  );

  // 3. 在构造函数中初始化
  const initStatement = ts.factory.createExpressionStatement(
    ts.factory.createBinaryExpression(
      ts.factory.createPropertyAccessExpression(
        ts.factory.createThis(),
        privateName
      ),
      ts.SyntaxKind.EqualsToken,
      ts.factory.createCallExpression(
        ts.factory.createPropertyAccessExpression(
          ts.factory.createThis(),
          'createState'
        ),
        undefined,
        [
          ts.factory.createStringLiteral(propertyName),
          ts.factory.createArrowFunction(
            undefined,
            undefined,
            [],
            undefined,
            ts.SyntaxKind.EqualsGreaterThanToken,
            ts.factory.createPropertyAccessExpression(
              ts.factory.createThis(),
              propertyName
            )
          )
        ]
      )
    )
  );

  return privateProperty;
}
```

#### 3. build 方法转换

**核心转换逻辑：**

```typescript
function processBuildMethod(
  method: ts.MethodDeclaration
): ts.MethodDeclaration {
  // 1. 将 build 改名为 render (或 initialRender)
  const newName = partialUpdateMode
    ? 'initialRender'
    : 'render';

  // 2. 处理方法体
  const newBody = processComponentBlock(method.body);

  // 3. 返回更新后的方法
  return ts.factory.updateMethodDeclaration(
    method,
    method.modifiers,
    method.asteriskToken,
    newName,
    method.questionToken,
    method.typeParameters,
    method.parameters,
    method.type,
    newBody
  );
}

function processComponentBlock(block: ts.Block): ts.Block {
  const newStatements: ts.Statement[] = [];

  for (const statement of block.statements) {
    if (ts.isExpressionStatement(statement)) {
      const expression = statement.expression;

      // 识别组件调用
      if (ts.isEtsComponentExpression(expression)) {
        const componentName = expression.expression.getText();

        // 转换为 create/pop 模式
        newStatements.push(createCreateStatement(componentName));
        processComponentAttributes(expression, newStatements);
        newStatements.push(createPopStatement(componentName));
      }
    }
  }

  return ts.factory.updateBlock(block, newStatements);
}
```

### UI 组件转换

#### 组件调用转换模式

**原始代码：**
```typescript
build() {
  Column() {
    Text('Hello')
      .fontSize(16)
      .onClick(() => {
        console.log('clicked');
      })
  }
  .width('100%')
}
```

**转换后代码：**
```typescript
render() {
  Column.create();
  Column.width('100%');

  Text.create('Hello');
  Text.fontSize(16);
  Text.onClick(() => {
    console.log('clicked');
  });
  Text.pop();

  Column.pop();
}
```

#### 转换实现

```typescript
function processUiComponent(
  expression: ts.EtsComponentExpression
): ts.Statement[] {
  const statements: ts.Statement[] = [];
  const componentName = expression.expression.getText();

  // 1. 创建组件 (Component.create())
  statements.push(
    ts.factory.createExpressionStatement(
      ts.factory.createCallExpression(
        ts.factory.createPropertyAccessExpression(
          ts.factory.createIdentifier(componentName),
          'create'
        ),
        undefined,
        expression.arguments || []
      )
    )
  );

  // 2. 处理属性配置
  if (expression.objectLiteral) {
    for (const property of expression.objectLiteral.properties) {
      const attrName = property.name.getText();
      const attrValue = property.initializer;

      statements.push(
        ts.factory.createExpressionStatement(
          ts.factory.createCallExpression(
            ts.factory.createPropertyAccessExpression(
              ts.factory.createIdentifier(componentName),
              attrName
            ),
            undefined,
            [attrValue]
          )
        )
      );
    }
  }

  // 3. 处理链式调用 (.attr())
  let chainCall = expression.parent;
  while (ts.isCallExpression(chainCall)) {
    if (ts.isPropertyAccessExpression(chainCall.expression)) {
      const methodName = chainCall.expression.name.getText();

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

  // 4. 添加 pop()
  statements.push(
    ts.factory.createExpressionStatement(
      ts.factory.createCallExpression(
        ts.factory.createPropertyAccessExpression(
          ts.factory.createIdentifier(componentName),
          'pop'
        ),
        undefined,
        []
      )
    )
  );

  return statements;
}
```

#### 特殊组件处理

**ForEach 转换：**

```typescript
// 原始代码
ForEach(items, (item: Item, index: number) => {
  Text(item.name)
}, (item: Item) => item.id)

// 转换后
ForEach.create();
forEach.itemGenerator = (item: Item, index: number) => {
  Text.create(item.name);
  Text.pop();
};
forEach.keyGenerator = (item: Item) => item.id;
ForEach.pop();
```

**If 转换：**

```typescript
// 原始代码
if (condition) {
  Text('True')
} else {
  Text('False')
}

// 转换后
If.create();
if (condition) {
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

---

## 代码生成

### TypeScript 到 JavaScript 转换

编译器使用 TypeScript 编译器进行基础转换：

```typescript
import * as ts from 'typescript';

// 编译选项
const compilerOptions: ts.CompilerOptions = {
  target: ts.ScriptTarget.ES2017,
  module: ts.ModuleKind.ES2015,
  moduleResolution: ts.ModuleResolutionKind.NodeJs,
  strict: true,
  esModuleInterop: true,
  skipLibCheck: true,
  sourceMap: true,
  declaration: true
};

// 编译函数
function compileTypeScript(sourceCode: string, fileName: string): {
  jsCode: string;
  sourceMap: any;
  declarations: string;
} {
  const sourceFile = ts.createSourceFile(
    fileName,
    sourceCode,
    ts.ScriptTarget.Latest,
    true
  );

  // 使用 TypeScript 编译器生成 JS
  const result = ts.transpileModule(sourceCode, {
    compilerOptions,
    reportDiagnostics: true
  });

  return {
    jsCode: result.outputText,
    sourceMap: result.sourceMapText,
    declarations: generateDeclarations(sourceFile)
  };
}
```

### 快速构建模式 (Rollup + Babel)

现代构建流程使用 Rollup 进行模块打包：

```typescript
import rollup from 'rollup';
import { genAbc } from './rollup-plugin-gen-abc';

async function buildWithRollup(projectConfig) {
  const bundle = await rollup.rollup({
    input: projectConfig.entryObj,
    plugins: [
      genAbc(),  // 自定义插件
      babelPlugin(projectConfig),
      resolvePlugin(),
      commonjsPlugin()
    ],
    onwarn: (warning) => {
      // 处理警告
    }
  });

  await bundle.write({
    dir: projectConfig.buildPath,
    format: 'es',
    chunkFileNames: '[name].js',
    entryFileNames: '[name].js',
    sourcemap: true
  });
}
```

#### Rollup 插件 - transform 钩子

```typescript
export function transformForModule(code: string, id: string): string {
  // 1. 判断文件类型
  if (isTsOrEtsSourceFile(id)) {
    // 2. TypeScript 编译为 JS
    const { jsCode, sourceMap } = compileTypeScript(code, id);

    // 3. 处理懒加载导入
    const processedCode = processLazyImport(id, jsCode);

    // 4. 保存源文件信息
    ModuleSourceFile.newSourceFile(id, processedCode, metaInfo);

    // 5. 保留 source map
    preserveSourceMap(id, sourceMap);

    return processedCode;
  }

  return code;
}
```

### ABC 字节码生成

最终输出是 ABC (ArkTS Bytecode) 格式：

```typescript
import { execSync } from 'child_process';

function generateAbc(jsFilePath: string, abcFilePath: string): void {
  // 调用 es2abc 工具
  execSync(`es2abc --input="${jsFilePath}" --output="${abcFilePath}"`, {
    stdio: 'inherit'
  });
}

// 批量生成
async function generateAllAbc(files: string[]): Promise<void> {
  const tasks = files.map(file =>
    generateAbc(file, file.replace('.js', '.abc'))
  );

  await Promise.all(tasks);
}
```

---

## 装饰器处理

### 装饰器分类

编译器将装饰器分为以下几类进行处理：

#### 1. 组件装饰器

| 装饰器 | 处理方式 |
|--------|----------|
| @Component | 将 struct 转换为继承 View 的 class，添加构造函数和生命周期 |
| @Entry | 标记为页面入口，生成 loadDocument 函数 |
| @Preview | 生成预览相关代码 |
| @CustomDialog | 添加弹窗控制器逻辑 |
| @Reusable/@ReusableV2 | 添加复用逻辑 |

#### 2. 状态装饰器

| 装饰器 | 存储类型 | 转换方式 |
|--------|----------|----------|
| @State | ObservedPropertySimple | 组件内部状态，自动更新 UI |
| @Prop | SynchedPropertySimpleOneWay | 父到子单向绑定 |
| @Link | SynchedPropertySimpleTwoWay | 双向绑定 |
| @Provide | AppStorage.SetAndProp | 提供全局状态 |
| @Consume | AppStorage.GetAndLink | 消费全局状态 |
| @ObjectLink | SynchedPropertyObjectTwoWay | 对象双向绑定 |

#### 3. 方法装饰器

| 装饰器 | 功能 |
|--------|------|
| @Builder | 转换为可复用的 UI 构建函数 |
| @LocalBuilder | 本地作用域 Builder |
| @Extend | 扩展组件属性 |
| @Styles | 全局样式定义 |
| @Watch | 添加属性监听器 |

### 装饰器处理实现

```typescript
function processDecorators(
  node: ts.ClassDeclaration | ts.StructDeclaration
): ts.ClassDeclaration {
  const decorators = node.decorators || [];
  const stateProps: ts.PropertyDeclaration[] = [];
  const regularProps: ts.PropertyDeclaration[] = [];

  // 分类属性
  for (const member of node.members) {
    if (ts.isPropertyDeclaration(member)) {
      const decorator = getFirstDecorator(member);
      if (decorator) {
        const decoratorName = getDecoratorName(decorator);
        if (decoratorName === '@State') {
          stateProps.push(member);
        }
      } else {
        regularProps.push(member);
      }
    }
  }

  // 转换状态属性
  const newStateProps = stateProps.map(prop =>
    processStateProperty(prop)
  );

  // 生成构造函数
  const constructor = createConstructorWithStateInit(
    stateProps,
    newStateProps
  );

  // 返回新的类声明
  return ts.factory.createClassDeclaration(
    node.decorators,
    node.modifiers,
    node.name,
    node.typeParameters,
    node.heritageClauses,
    [
      ...newStateProps,
      ...regularProps,
      constructor,
      ...processMethods(node.members)
    ]
  );
}
```

---

## UI 组件处理

### 组件处理流程

```
UI 组件调用
    │
    ▼
识别组件类型 (内置/自定义)
    │
    ├─→ 内置组件:
    │   查找 COMPONENT_MAP
    │   生成 create/pop 代码
    │   处理属性和事件
    │
    └─→ 自定义组件:
        查找类定义
        生成 new Component() 调用
        处理参数传递
```

### 内置组件处理

```typescript
function processBuiltinComponent(
  expression: ts.EtsComponentExpression
): ts.Statement[] {
  const componentName = expression.expression.getText();
  const componentMeta = COMPONENT_MAP[componentName];

  if (!componentMeta) {
    throw new Error(`Unknown component: ${componentName}`);
  }

  const statements: ts.Statement[] = [];

  // 1. 创建组件
  statements.push(createComponentCreation(componentName));

  // 2. 处理标准属性
  if (expression.arguments && expression.arguments[0]) {
    const propsObj = expression.arguments[0];
    statements.push(...processComponentProps(propsObj, componentMeta));
  }

  // 3. 处理事件
  statements.push(...processComponentEvents(expression, componentMeta));

  // 4. 处理样式
  statements.push(...processComponentStyles(expression, componentMeta));

  // 5. 处理子组件
  statements.push(...processComponentChildren(expression, componentMeta));

  // 6. 关闭组件
  statements.push(createComponentPop(componentName));

  return statements;
}
```

### 自定义组件处理

```typescript
function processCustomComponent(
  expression: ts.CallExpression,
  componentName: string
): ts.Statement[] {
  const statements: ts.Statement[] = [];

  // 1. 分析组件装饰器
  const componentMeta = analyzeComponentDecorator(componentName);

  // 2. 创建组件实例
  const newExpr = ts.factory.createNewExpression(
    ts.factory.createIdentifier(componentName),
    undefined,
    [
      ts.factory.createIdentifier('__ctx__'),
      ts.factory.createIdentifier('parent')
    ]
  );

  // 3. 处理参数对象
  if (expression.arguments[0]) {
    const params = processComponentParameters(
      expression.arguments[0],
      componentMeta
    );

    statements.push(
      ts.factory.createExpressionStatement(
        ts.factory.createCallExpression(
          ts.factory.createPropertyAccessExpression(
            newExpr,
            'updateWithValueParams'
          ),
          undefined,
          [params]
        )
      )
    );
  }

  // 4. 调用组件渲染
  statements.push(
    ts.factory.createExpressionStatement(
      ts.factory.createCallExpression(
        ts.factory.createPropertyAccessExpression(
          newExpr,
          'render'
        ),
        undefined,
        []
      )
    )
  );

  return statements;
}
```

---

## 构建模式

### Bundle 模式

适用于传统的 FA 模型应用：

```typescript
function generateBundleAbc() {
  const options = {
    input: projectConfig.entryObj,
    output: {
      dir: projectConfig.buildPath,
      format: 'cjs'
    }
  };

  // 生成单个 bundle
  const bundle = rollup.rollup(options);
  bundle.write({
    file: path.join(projectConfig.buildPath, 'index.js'),
    format: 'cjs',
    name: 'App'
  });

  // 转换为 ABC
  generateAbc(
    path.join(projectConfig.buildPath, 'index.js'),
    path.join(projectConfig.buildPath, 'index.abc')
  );
}
```

### Module 模式

适用于 Stage 模型应用：

```typescript
function generateModuleAbc() {
  const moduleFiles = getAllModuleFiles();

  // 为每个模块生成独立的 ABC
  for (const file of moduleFiles) {
    const abcPath = file.replace('.js', '.abc');
    generateAbc(file, abcPath);

    // 生成模块配置
    generateModuleConfig(file, abcPath);
  }
}
```

---

## 关键数据结构

### 组件信息

```typescript
interface ComponentInfo {
  currentClassName: string;        // 当前组件类名
  entryComponent: string;          // 入口组件名
  parentComponent: string;         // 父组件名
  isEntry: boolean;                // 是否入口组件
}
```

### 存储文件信息

```typescript
interface StoredFileInfo {
  entryComponent: string;          // 入口组件
  hasStateVarInBuild: boolean;     // build 中是否有状态变量
  hasViewStackInBuild: boolean;    // build 中是否有视图栈
  lazyForEachInfo: LazyForEachInfo; // LazyForEach 信息
  processLazyForEach: boolean;     // 是否处理 LazyForEach
}
```

### 装饰器集合

```typescript
// 全局装饰器集合
const stateCollection: Set<string> = new Set();
const linkCollection: Set<string> = new Set();
const propCollection: Set<string> = new Set();
const provideCollection: Set<string> = new Set();
const consumeCollection: Set<string> = new Set();
```

---

## 实现指南

### 重写该编译器的关键步骤

#### 1. 基础设施

```
需要实现：
1. TypeScript/JavaScript 解析器
   - 可使用 TypeScript Compiler API
   - 或使用 Babel/ swc/ esprima 等

2. AST 遍历和操作框架
   - 访问者模式
   - 节点转换工具

3. 代码生成器
   - AST → 代码
   - Source Map 生成
```

#### 2. 核心转换实现

```python
# Python 实现示例架构

class EtsCompiler:
    def __init__(self, config):
        self.config = config
        self.decorators = self.load_decorators()
        self.components = self.load_components()

    def compile(self, source_files):
        results = []
        for file in source_files:
            # 1. 解析
            ast = self.parse_source(file)

            # 2. 验证
            self.validate_ui_syntax(ast)

            # 3. 转换
            transformed_ast = self.transform_ast(ast)

            # 4. 生成代码
            js_code = self.generate_js(transformed_ast)

            results.append({
                'file': file,
                'code': js_code,
                'sourcemap': self.generate_sourcemap(ast, transformed_ast)
            })

        return results

    def transform_ast(self, ast):
        # 转换装饰器
        ast = self.transform_decorators(ast)

        # 转换 UI 组件
        ast = self.transform_ui_components(ast)

        # 转换状态管理
        ast = self.transform_state(ast)

        return ast
```

#### 3. 关键转换实现

```python
class ComponentTransformer(ast.NodeTransformer):
    def visit_ClassDef(self, node):
        # 处理 struct -> class 转换
        if self.is_struct(node):
            return self.transform_struct(node)
        return node

    def transform_struct(self, struct_node):
        # 创建类定义
        class_node = ast.ClassDef(
            name=struct_node.name,
            bases=[ast.Name(id='View')],
            keywords=[],
            body=[],
            decorator_list=[]
        )

        # 转换成员
        for member in struct_node.body:
            if isinstance(member, ast.FunctionDef) and member.name == 'build':
                class_node.body.extend(self.transform_build_method(member))
            elif self.has_decorator(member, 'State'):
                class_node.body.append(self.transform_state_property(member))

        return class_node

    def transform_build_method(self, build_method):
        statements = []

        for stmt in build_method.body:
            if self.is_component_call(stmt):
                # Text('Hello') -> Text.create(); Text.pop();
                statements.extend(self.transform_component(stmt))

        # 创建 render 方法
        render_method = ast.FunctionDef(
            name='render',
            args=ast.arguments(...),
            body=statements,
            decorator_list=[]
        )

        return [render_method]
```

#### 4. 组件注册

```python
# 需要维护的组件元数据
COMPONENTS = {
    'Text': {
        'attrs': ['font', 'fontSize', 'fontColor'],
        'atomic': True
    },
    'Column': {
        'attrs': ['space', 'alignItems'],
        'atomic': False
    },
    # ... 更多组件
}

# 使用组件信息
def transform_component(node):
    component_name = node.func.id
    meta = COMPONENTS.get(component_name)

    if not meta:
        raise ValueError(f"Unknown component: {component_name}")

    statements = []

    # 生成创建语句
    statements.append(f"{component_name}.create({node.args[0]})")

    # 生成属性语句
    for keyword in node.keywords:
        if keyword.arg in meta['attrs']:
            statements.append(
                f"{component_name}.{keyword.arg}({keyword.value})"
            )

    # 生成 pop 语句
    statements.append(f"{component_name}.pop()")

    return statements
```

#### 5. 状态管理转换

```python
class StateTransformer(ast.NodeTransformer):
    def visit_AnnAssign(self, node):
        # 检查 @State 装饰器
        if self.has_decorator(node, 'State'):
            return self.transform_state_property(node)
        return node

    def transform_state_property(self, node):
        var_name = node.target.id
        var_type = node.annotation
        init_value = node.value

        # 生成: private varName__: ObservedPropertySimple<type>;
        private_var = ast.AnnAssign(
            target=ast.Name(id=f'{var_name}__', annotation=None),
            annotation=ast.Subscript(
                value=ast.Name(id='ObservedPropertySimple'),
                slice=var_type
            ),
            value=None,
            simple=1
        )

        # 生成 getter/setter
        getter = ast.FunctionDef(
            name=f'get {var_name}',
            args=ast.arguments(posonlyargs=[], args=[], kwonlyargs=[]),
            body=[ast.Return(value=ast.Name(id=f'{var_name}__'))],
            decorator_list=[]
        )

        # 构造函数初始化
        init_stmt = ast.Assign(
            targets=[ast.Name(id=f'{var_name}__')],
            value=ast.Call(
                func=ast.Attribute(
                    value=ast.Name(id='this'),
                    attr='createState'
                ),
                args=[
                    ast.Constant(value=var_name),
                    ast.Lambda(
                        args=ast.arguments(...),
                        body=ast.Name(id=var_name)
                    )
                ]
            )
        )

        return [private_var, getter, init_stmt]
```

### 关键技术点

#### 1. AST 解析

- **TypeScript**: 使用 `ts.createSourceFile()`
- **Python**: 使用 `ast` 模块（需扩展支持 TS 语法）或 `libcst`
- **Go**: 使用 `go/parser` 或第三方库

#### 2. 装饰器处理

需要实现装饰器收集和转换：

```python
def collect_decorators(node):
    """收集节点上的所有装饰器"""
    decorators = {
        'component': False,
        'state': False,
        'prop': False,
        # ...
    }

    for decorator in node.decorator_list:
        if isinstance(decorator, ast.Name):
            decorators[decorator.id.lower()] = True
        elif isinstance(decorator, ast.Call):
            decorators[decorator.func.id.lower()] = True

    return decorators
```

#### 3. 作用域处理

需要正确处理 `this` 上下文：

```typescript
// 原始代码
Text(this.message)

// 需要正确处理 this 引用
class MyComponent {
  render() {
    Text.create(this.message__.get());
    Text.pop();
  }
}
```

#### 4. 类型擦除

```python
def erase_type_annotations(node):
    """擦除类型注点"""
    if isinstance(node, ast.AnnAssign):
        node.annotation = None
    elif isinstance(node, ast.FunctionDef):
        node.returns = None
        for arg in node.args.args:
            arg.annotation = None
    return node
```

### 测试策略

```python
class TestCompiler:
    def test_simple_component(self):
        source = """
        @Component
        struct Hello {
            build() {
                Text('Hello')
            }
        }
        """

        expected = """
        class Hello extends View {
            render() {
                Text.create('Hello');
                Text.pop();
            }
        }
        """

        result = self.compiler.compile(source)
        assert normalize_code(result) == normalize_code(expected)

    def test_state_property(self):
        source = """
        @Component
        struct Counter {
            @State count: number = 0
            build() {
                Text(this.count)
            }
        }
        """

        # 验证状态属性转换正确
        # 验证 getter/setter 生成正确
        # 验证构造函数初始化正确
```

---

## 高级特性处理

### 1. 懒加载导入 (Lazy Import)

编译器支持自动将普通导入转换为懒加载导入，优化启动性能。

**转换示例：**

```typescript
// 原始代码
import { MyComponent } from './MyComponent';

// 转换后
import lazy { MyComponent } from './MyComponent';
```

**实现逻辑：**

```typescript
function transformLazyImport(
  sourceNode: ts.SourceFile,
  options: LazyImportOptions
): ts.SourceFile {
  const transformer: ts.TransformerFactory<ts.SourceFile> = context => {
    const visitor: ts.Visitor = node => {
      if (ts.isImportDeclaration(node)) {
        return updateImportDecl(node, options);
      }
      return node;
    };
    return node => ts.visitEachChild(node, visitor, context);
  };

  const result = ts.transform(sourceNode, [transformer]);
  return result.transformed[0];
}

function updateImportDecl(
  node: ts.ImportDeclaration,
  options: LazyImportOptions
): ts.ImportDeclaration {
  // 不转换的情况：
  // - import 'xxx' (side-effect only)
  // - import type { T } from 'xxx' (type-only)
  // - import * as ns from 'xxx' (namespace)
  // - import lazy { x } from 'xxx' (already lazy)

  const importClause = node.importClause;
  if (!importClause || importClause.isTypeOnly) {
    return node;
  }

  // 设置 isLazy 标志
  // @ts-ignore
  importClause.isLazy = true;

  return ts.factory.updateImportDeclaration(
    node,
    node.modifiers,
    importClause,
    node.moduleSpecifier,
    node.assertClause
  );
}
```

**配置选项：**

```typescript
interface LazyImportOptions {
  autoLazyImport: boolean;      // 是否启用自动懒加载
  reExportCheckMode: string;     // 重导出检查模式
  autoLazyFilter: {
    include?: string[];          // 包含的包名列表
    exclude?: string[];          // 排除的包名列表
  };
}
```

### 2. 系统模块处理

处理 HarmonyOS 系统模块的特殊导入。

**支持的模块前缀：**

```typescript
const SYSTEM_PREFIXES = [
  '@ohos.',     // HarmonyOS API
  '@system.',   // 系统模块
  '@kit.',      // Kit 模块
  '@arkts.'     // ArkTS 内置
];
```

**处理逻辑：**

```typescript
function processSystemModule(source: string, filePath: string): string {
  // 正则匹配导入语句
  const IMPORT_REG = /(import|export)\s+(.+)\s+from\s+['"](\S+)['"]/g;

  source.replace(IMPORT_REG, (match, type, spec, modulePath) => {
    // 检查是否为系统模块
    if (/^@(system|ohos|kit|arkts)\./i.test(modulePath.trim())) {
      // 验证模块是否存在
      if (!systemModules.includes(modulePath.trim() + '.d.ts')) {
        throw new Error(
          `Cannot find module '${modulePath}' or its type declarations.`
        );
      }

      // 处理系统 API 预处理
      source = processSystemApi(source, false, filePath, true);
    }

    return match;
  });

  return source;
}
```

### 3. 资源引用处理

转换 `$r()` 和 `$rawfile()` 资源引用。

**$r() 转换：**

```typescript
// 原始代码
Text($r('app.string.name'))

// 转换后
Text.create(__getResourceId__(
  10003,              // RESOURCE_TYPE.string
  0,                  // bundleName (使用默认)
  'app_module',        // moduleName
  'name'              // resourceId
));
```

**转换实现：**

```typescript
function processResourceExpression(node: ts.CallExpression): ts.Expression {
  const funcName = node.expression.getText();

  if (funcName === '$r') {
    // 解析资源路径: 'app.string.name'
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
      [
        ts.factory.createStringLiteral(filename)
      ]
    );
  }

  return node;
}
```

### 4. ForEach/LazyForEach 详细处理

**ForEach 完整转换：**

```typescript
// 原始代码
ForEach(
  this.items,
  (item: Item, index: number) => {
    Text(item.name).fontSize(16)
  },
  (item: Item) => item.id.toString()
)

// 转换后
ForEach.create();
const __itemGenFunction__ = (item: Item, index: number) => {
  Text.create(item.name);
  Text.fontSize(16);
  Text.pop();
};
const __itemIdFunction__ = (item: Item) => item.id.toString();
const __updateFunction__ = (item: Item, index: number) => {
  // 依赖跟踪
  this.observeDeepRefresh();
  return __itemGenFunction__(item, index);
};
ForEach.itemGenerator(__itemGenFunction__);
ForEach.keyGenerator(__itemIdFunction__);
ForEach.pop();
```

**LazyForEach 优化转换：**

```typescript
// 原始代码
LazyForEach(dataSource, (item: Item, index: number) => {
  Text(item.name)
})

// 转换后 (带优化)
LazyForEach.create();
const __myIds__ = [];
const __itemGenFunction__ = (
  item: Item,
  index: number,
  isInitialItem?: boolean,
  ids?: string[]
) => {
  if (isInitialItem || isInitialItem === undefined) {
    Text.create(item.name);
    Text.pop();
  } else {
    // 增量更新
    this.updateLazyForEachElements(ids, item);
  }
  return ids;
};
LazyForEach.itemGenerator(__itemGenFunction__);
LazyForEach.pop();
```

**实现关键点：**

```typescript
function processForEachComponent(
  node: ts.ExpressionStatement,
  isLazy: boolean
): ts.Statement[] {
  const statements: ts.Statement[] = [];

  // 1. 创建 ForEach 组件
  statements.push(createForEachCreate());

  // 2. 生成 itemGenerator 函数
  const arrowFunc = extractArrowFunction(node.arguments[1]);
  const itemGenFunc = createItemGenFunction(arrowFunc, isLazy);

  // 3. 生成 keyGenerator 函数
  const keyGenFunc = createKeyGenerator(node.arguments[2]);

  // 4. 生成 update 函数（LazyForEach 优化用）
  if (isLazy) {
    const updateFunc = createUpdateFunction(arrowFunc);
    statements.push(createVariableDeclaration('__updateFunction__', updateFunc));
  }

  // 5. 设置生成器
  statements.push(
    ts.factory.createExpressionStatement(
      ts.factory.createCallExpression(
        ts.factory.createPropertyAccessExpression(
          ts.factory.createIdentifier('ForEach'),
          'itemGenerator'
        ),
        undefined,
        [itemGenFunc]
      )
    )
  );

  statements.push(
    ts.factory.createExpressionStatement(
      ts.factory.createCallExpression(
        ts.factory.createPropertyAccessExpression(
          ts.factory.createIdentifier('ForEach'),
          'keyGenerator'
        ),
        undefined,
        [keyGenFunc]
      )
    )
  );

  // 6. pop
  statements.push(createForEachPop());

  return statements;
}
```

### 5. If 语句详细处理

**If 转换（部分更新模式）：**

```typescript
// 原始代码
if (this.condition) {
  Text('True')
} else {
  Text('False')
}

// 转换后
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

**嵌套 If 处理：**

```typescript
function processIfStatement(
  node: ts.IfStatement,
  branchId: number = 0
): ts.IfStatement {
  const thenBlock = processIfBlock(node.thenStatement, branchId);
  const elseBlock = node.elseStatement
    ? processIfBlock(node.elseStatement, branchId + 1)
    : ts.factory.createBlock([
        createIfBranchFunc(branchId + 1, [])
      ]);

  return ts.factory.createIfStatement(
    node.expression,
    thenBlock,
    elseBlock
  );
}

function processIfBlock(block: ts.Block, branchId: number): ts.Block {
  return ts.factory.createBlock([
    createIfBranchId(branchId),
    ...processComponentBlock(block),
    createIfPop()
  ], true);
}
```

### 6. Builder 函数处理

**@Builder 转换：**

```typescript
// 原始代码
@Builder
function MyBuilder(param: string) {
  Text(param)
}

// 转换后
function MyBuilder(param: string, __builder__: BuilderParam = undefined) {
  Text.create(param);
  Text.pop();
}
```

**Builder 参数处理：**

```typescript
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
  );

  return ts.factory.updateFunctionDeclaration(
    node,
    node.decorators,
    node.modifiers,
    node.asteriskToken,
    node.name,
    node.typeParameters,
    params,
    node.type,
    // 处理函数体，添加 context stack 管理
    processBuilderBody(node.body)
  );
}
```

**Builder 调用转换：**

```typescript
// 原始代码
MyBuilder('hello')

// 转换后
{
  const __builder__ = new BuilderParam();
  MyBuilder('hello', __builder__);
  __builder__.build();
}
```

### 7. 存储管理 (AppStorage/LocalStorage)

**AppStorage 转换：**

```typescript
// @Provide/@Consume 转换
@Provide appName: string = 'MyApp';

// 转换后
private appName__: ObservedPropertySimple<string>;

constructor() {
  super();
  this.appName__ = AppStorage.SetAndProp('appName',
    () => this.appName,
    (newValue) => { this.appName = newValue; });
}

get appName(): string {
  return this.appName__.get();
}

set appName(newValue: string) {
  this.appName__.set(newValue);
}
```

**LocalStorage 转换：**

```typescript
// @LocalStorageProp/@LocalStorageLink 转换
@LocalStorageProp('theme') theme: string = 'light';

// 转换后
private theme__: ObservedPropertySimple<string>;

constructor() {
  super();
  const localStorage = new LocalStorage();
  this.theme__ = localStorage.createProp('theme',
    () => this.theme,
    (newValue) => { this.theme = newValue; });
}
```

### 8. Sendable 并发处理

**@Sendable 装饰器处理：**

```typescript
// 原始代码
@Sendable
class MyData {
  value: number = 0;
}

// 转换后
class MyData {
  value: number | undefined = undefined;

  constructor() {
    'use sendable';
    this.value = undefined;
  }
}
```

**转换逻辑：**

```typescript
function processSendableClass(
  node: ts.ClassDeclaration
): ts.ClassDeclaration {
  // 1. 移除 @Sendable 装饰器
  const newDecorators = removeSendableDecorator(node.decorators);

  // 2. 将所有属性类型改为 union with undefined
  const newMembers = node.members.map(member => {
    if (ts.isPropertyDeclaration(member)) {
      return transformOptionalMemberForSendable(member);
    }
    return member;
  });

  // 3. 添加/更新构造函数
  const constructor = createSendableConstructor(
    findConstructor(node.members),
    node.heritageClauses?.length > 0
  );

  return ts.factory.createClassDeclaration(
    newDecorators,
    node.modifiers,
    node.name,
    node.typeParameters,
    node.heritageClauses,
    [...newMembers, constructor]
  );
}
```

### 9. 部分更新机制 (Partial Update)

**elmid 系统：**

```typescript
// 为每个组件生成唯一 ID
function generateElmtId(): number {
  return globalElmtId++;
}

// 在组件创建时分配 ID
function processComponentCreation(componentName: string): ts.Statement {
  return ts.factory.createExpressionStatement(
    ts.factory.createCallExpression(
      ts.factory.createPropertyAccessExpression(
        ts.factory.createIdentifier(componentName),
        'create'
      ),
      undefined,
      [
        ts.factory.createObjectLiteralExpression([
          ts.factory.createPropertyAssignment(
            'id',
            ts.factory.createCallExpression(
              ts.factory.createIdentifier('generateElmtId'),
              undefined,
              []
            )
          )
        ])
      ]
    )
  );
}
```

**updateFuncByElmtId：**

```typescript
// 生成更新函数
function createUpdateFunction(
  elmtId: number,
  componentClass: string
): ts.MethodDeclaration {
  return ts.factory.createMethodDeclaration(
    [ts.factory.createModifier(ts.SyntaxKind.PublicKeyword)],
    undefined,
    'updateFuncByElmtId',
    undefined,
    undefined,
    [
      ts.factory.createParameterDeclaration(
        undefined,
        undefined,
        'elmtId',
        undefined,
        ts.factory.createKeywordTypeNode(ts.SyntaxKind.NumberKeyword)
      )
    ],
    undefined,
    ts.factory.createBlock([
      ts.factory.createIfStatement(
        ts.factory.createBinaryExpression(
          ts.factory.createIdentifier('elmtId'),
          ts.SyntaxKind.EqualsEqualsEqualsToken,
          ts.factory.createNumericLiteral(elmtId)
        ),
        ts.factory.createBlock([
          // 执行更新逻辑
          ts.factory.createExpressionStatement(
            ts.factory.createCallExpression(
              ts.factory.createPropertyAccessExpression(
                ts.factory.createThis(),
                'initialRender'
              ),
              undefined,
              []
            )
          )
        ], true)
      )
    ], true)
  );
}
```

### 10. Source Map 生成

**Source Map 结构：**

```typescript
interface SourceMap {
  version: number;
  file: string;
  sources: string[];
  names: string[];
  mappings: string;  // VLQ 编码的映射
  sourcesContent?: string[];
}
```

**生成逻辑：**

```typescript
class SourceMapGenerator {
  private static instance: SourceMapGenerator;
  private sourceMaps: Map<string, SourceMap> = new Map();

  static getInstance(): SourceMapGenerator {
    if (!SourceMapGenerator.instance) {
      SourceMapGenerator.instance = new SourceMapGenerator();
    }
    return SourceMapGenerator.instance;
  }

  updateSourceMap(
    sourceFilePath: string,
    sourcemap: any
  ): void {
    // 修正 sources 路径
    sourcemap.sources = [path.relative(
      projectConfig.projectRootPath,
      sourceFilePath
    )];

    // 移除 sourcesContent（减少文件大小）
    if (sourcemap.sourcesContent) {
      delete sourcemap.sourcesContent;
    }

    // 更新文件名
    sourcemap.file = path.basename(sourceFilePath);

    this.sourceMaps.set(sourceFilePath, sourcemap);
  }

  fillSourceMapPackageInfo(
    sourceFilePath: string,
    sourcemap: any
  ): void {
    // 填充包信息
    const belongModule = this.getBelongModule(sourceFilePath);
    if (belongModule) {
      sourcemap.packageName = belongModule.packageName;
      sourcemap.moduleName = belongModule.moduleName;
    }
  }
}
```

### 11. 模块解析

**模块解析流程：**

```typescript
function resolveModule(
  moduleRequest: string,
  importerFile: string
): string {
  // 1. 检查是否为相对路径
  if (moduleRequest.startsWith('./') || moduleRequest.startsWith('../')) {
    return resolveRelativeModule(moduleRequest, importerFile);
  }

  // 2. 检查是否为 node_modules/oh_modules
  if (!moduleRequest.startsWith('@')) {
    return resolveNodeModules(moduleRequest, importerFile);
  }

  // 3. 检查系统模块
  if (/^@(ohos|system|kit|arkos)\./.test(moduleRequest)) {
    return resolveSystemModule(moduleRequest);
  }

  // 4. 检查 HAR 包
  const harPath = resolveHarModule(moduleRequest);
  if (harPath) {
    return harPath;
  }

  throw new Error(`Cannot find module '${moduleRequest}'`);
}

function resolveHarModule(moduleRequest: string): string | null {
  // 在 oh_modules 目录查找 HAR 包
  const harPaths = [
    path.join(projectConfig.projectPath, 'oh_modules', moduleRequest, 'src/main/ets/index.ets'),
    path.join(projectConfig.projectPath, 'oh_modules', moduleRequest, 'index.ets'),
    path.join(projectConfig.projectPath, 'oh_modules', moduleRequest, 'index.ts'),
  ];

  for (const harPath of harPaths) {
    if (fs.existsSync(harPath)) {
      return harPath;
    }
  }

  return null;
}
```

### 12. Mock 配置处理

**测试模拟支持：**

```typescript
// mock-config.json5 配置
{
  "decorator": "Mock",
  "packageName": "@ohos.mock",
  "etsSourceRoot": "./src/main/ets",
  "mockConfigPath": "./mock/mock-config.json5",
  "mockConfigKey2ModuleInfo": {
    "MockComponent": {
      "moduleName": "entry",
      "filePath": "/src/main/ets/MockComponent"
    }
  }
}

// 处理 Mock
function processMockNode(
  node: ts.CallExpression,
  mockConfig: MockConfig
): ts.Node {
  const mockTarget = node.arguments[0].text;
  const moduleInfo = mockConfig.mockConfigKey2ModuleInfo[mockTarget];

  if (!moduleInfo) {
    return node;
  }

  // 生成 Mock 导入
  const mockImport = ts.factory.createImportDeclaration(
    undefined,
    ts.factory.createImportClause(
      false,
      ts.factory.createIdentifier(mockTarget),
      undefined
    ),
    ts.factory.createStringLiteral(
      `${moduleInfo.moduleName}/src/main/ets/${moduleInfo.filePath}`
    )
  );

  // 替换原始调用为 Mock 调用
  const mockCall = ts.factory.createCallExpression(
    ts.factory.createPropertyAccessExpression(
      ts.factory.createIdentifier(mockTarget),
      'get'
    ),
    undefined,
    []
  );

  return ts.factory.createAsExpression(mockCall, node.type);
}
```

---

## 遗漏技术点总结

经过深入分析，原文档遗漏了以下关键技术点：

### 1. **导入系统**
- 懒加载导入自动转换 (import lazy)
- 模块路径解析算法
- HAR 包查找逻辑
- 系统模块验证

### 2. **资源管理**
- $r() 资源引用转换
- $rawfile() 处理
- 资源 ID 生成算法
- 资源索引文件生成

### 3. **列表渲染**
- ForEach 完整转换逻辑
- LazyForEach 优化实现
- itemGenerator/keyGenerator 生成
- 增量更新机制

### 4. **条件渲染**
- If 语句分支 ID 分配
- 嵌套 If 处理
- branchId() 函数调用
- else 分支处理

### 5. **Builder 系统**
- @Builder 参数处理
- BuilderParam 类型
- context stack 管理
- Builder 调用转换

### 6. **存储系统**
- AppStorage 实现机制
- LocalStorage 隔离
- Provide/Consume 配对
- 存储初始化顺序

### 7. **并发支持**
- @Sendable 装饰器处理
- 类型转换 (optional 处理)
- 并发构造函数生成
- 'use sendable' 标记

### 8. **性能优化**
- 部分更新机制 (elmtid)
- updateFuncByElmtId 生成
- 依赖收集
- 增量渲染

### 9. **构建工具集成**
- Rollup 插件系统
- Source Map 链式处理
- Babel 转换集成
- Terser 压缩

### 10. **开发体验**
- Mock 系统支持
- 测试框架集成
- 调试信息生成
- 错误提示优化

### 11. **互操作性**
- ArkTS 演进处理
- TypeScript 互操作
- JS 互操作
- 类型擦除策略

### 12. **高级特性**
- Worker 支持
- @Reusable 组件复用
- Navigation 路由
- 自定义对话框控制器

---

## 完整实现清单

要实现功能对等的编译器，需要完成：

### 核心功能 (必须)
- [ ] TypeScript/JavaScript 解析器
- [ ] AST 遍历和转换框架
- [ ] struct → class 转换
- [ ] 所有装饰器处理
- [ ] UI 组件 create/pop 模式
- [ ] 状态管理系统
- [ ] build → render 转换
- [ ] 基础代码生成

### 高级功能 (重要)
- [ ] 懒加载导入
- [ ] ForEach/LazyForEach
- [ ] If 条件渲染
- [ ] @Builder 系统
- [ ] AppStorage/LocalStorage
- [ ] 部分更新机制
- [ ] Source Map 生成
- [ ] 模块解析

### 优化功能 (增强)
- [ ] @Sendable 并发
- [ ] @Reusable 复用
- [ ] Mock 系统
- [ ] Worker 支持
- [ ] 资源引用处理
- [ ] ABC 字节码生成
- [ ] 代码压缩

### 工具支持
- [ ] 错误诊断
- [ ] 类型检查
- [ ] API 验证
- [ ] 性能分析

---

## 总结

该 ETS 到 JS 编译器是一个**高度复杂**的转换系统，完整实现需要：

### 技术要求
1. **编译器基础**: AST 操作、符号解析、类型推断
2. **运行时理解**: ArkTS 运行时、状态管理、UI 渲染机制
3. **构建工具**: Rollup/webpack 插件开发、Source Map 处理
4. **HarmonyOS**: 系统模块、资源系统、包管理

### 实现难点
1. **装饰器语义**: 每个装饰器都有特定的运行时行为
2. **组件映射**: 需要完整的组件元数据定义
3. **状态管理**: ObservedProperty* 系列类的正确使用
4. **部分更新**: elmtid 系统和增量渲染
5. **性能优化**: 懒加载、增量更新、代码分割

### 建议实现策略
1. **分阶段实现**: 先核心功能，后高级特性
2. **测试驱动**: 每个功能都需要完整测试
3. **参考实现**: 详细研究现有代码的每个模块
4. **文档同步**: 保持文档和代码的一致性

### 功能保证
为确保功能对等，需要：
1. ✅ **100% 的语法支持**: 所有 ETS 语法特性
2. ✅ **所有装饰器**: V1 和 V2 装饰器
3. ✅ **完整组件支持**: 所有内置组件
4. ✅ **状态管理**: 所有状态装饰器
5. ✅ **构建模式**: Bundle、Module、ESModule
6. ✅ **优化特性**: 懒加载、部分更新、复用

---

*文档版本: 2.0*
*最后更新: 2025-01-29*
*补充遗漏技术点后的完整版本*
