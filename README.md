# ETS to JS 编译器

HarmonyOS ArkTS/ETS 转 JavaScript 编译器。该编译器将 ArkTS（扩展 TypeScript）代码转换为标准 JavaScript，可由 HarmonyOS 运行时执行。

## 项目概述

这是一个将 ArkTS/ETS 源代码翻译为 JavaScript 的编译器，使开发者能够使用现代化的声明式 UI 语法和装饰器，同时保持与 HarmonyOS 运行时环境的兼容性。

### 核心功能

- **装饰器支持**
  - `@Component` - 将 struct 转换为 class View
  - `@State` - 使用 ObservedPropertySimple 进行状态管理
  - `@Prop` - 组件属性装饰器
  - `@Link` - 双向数据绑定
  - `@Provide/@Consume` - 状态依赖注入
  - `@Builder` - Builder 函数转换

- **UI 组件**
  - ForEach - 列表渲染
  - If - 条件渲染
  - 声明式 UI 到 create/pop 模式转换

- **表达式支持**
  - 对象字面量
  - 数组字面量
  - 箭头函数
  - 模板字符串
  - 资源引用（`$r()`、`$rawfile()`）

- **高级特性**
  - 并行编译支持
  - 纯 JavaScript 模式（不含 ArkUI 运行时依赖）
  - SourceMap 生成
  - 代码格式化支持

## 技术栈

### 后端

- **Java 17** - 主要开发语言
- **Maven 3.8.3+** - 项目构建和依赖管理
- **GraalVM JavaScript Engine** - JavaScript 执行引擎（嵌入式依赖）

### TypeScript 解析器

- **TypeScript 5.9.3** - AST 解析
- **npm SDK 风格模块化架构** - 可维护的代码组织
- **参数传递模式** - 避免循环依赖的递归转换

## 环境要求

- Java 17
- Maven 3.8.3+
- Node.js 14+ (用于 TypeScript 解析器)

## 安装

```bash
# 克隆仓库
git clone https://github.com/yonglunyao/harmony-ets2js-compiler.git
cd harmony-ets2js-compiler

# 构建项目
mvn clean package
```

## 编译命令示例

### 基本编译

```bash
# 编译单个 ETS 文件
mvn exec:java -Dexec.mainClass="com.ets2jsc.EtsCompiler" -Dexec.args="<input.ets> <output.js>" -q

# 编译多个文件
mvn compile
```

### 使用自定义配置编译

```bash
# 使用纯 JavaScript 模式编译
mvn exec:java -Dexec.mainClass="com.ets2jsc.EtsCompiler" -q
```

### 批量编译

```bash
# 编译指定目录下的所有 ETS 文件
java -jar target/ets2jsc-1.0-SNAPSHOT.jar --batch <input-dir> <output-dir>

# 并行编译（4 线程）
java -jar target/ets2jsc-1.0-SNAPSHOT.jar --parallel 4 <input-dir> <output-dir>
```

### Java API 编译

```java
import com.ets2jsc.EtsCompiler;
import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.CompilationResult;
import java.nio.file.Paths;

// 创建编译器实例
CompilerConfig config = CompilerConfig.createDefault();
EtsCompiler compiler = new EtsCompiler(config);

// 编译单个文件
CompilationResult result = compiler.compile(
    Paths.get("src/main/ets/MyComponent.ets"),
    Paths.get("output/MyComponent.js")
);

// 批量并行编译
List<Path> sourceFiles = Arrays.asList(
    Paths.get("src/main/ets/Component1.ets"),
    Paths.get("src/main/ets/Component2.ets")
);
CompilationResult batchResult = compiler.compileBatchParallel(
    sourceFiles,
    Paths.get("output"),
    4  // 线程数
);

// 检查结果
if (result.isSuccess()) {
    System.out.println("编译成功");
} else {
    System.out.println("编译失败: " + result.getError());
}
```

### 配置选项

```java
CompilerConfig config = new CompilerConfig();

// 设置编译模式
config.setCompileMode(CompilerConfig.CompileMode.MODULE_JSON);

// 启用纯 JavaScript 模式
config.setPureJavaScript(true);

// 启用 SourceMap 生成
config.setGenerateSourceMap(true);

// 禁用类型验证
config.setValidateApi(false);

// 设置项目路径
config.setProjectPath("/path/to/project");
config.setBuildPath("/path/to/build");
```

## 使用方法

### Maven 命令

```bash
# 编译
mvn compile

# 运行测试
mvn test

# 运行指定测试
mvn test -Dtest=DecoratorTest
mvn test -Dtest=CompilerConfigTest
mvn test -Dtest=CodeGeneratorTest

# 运行指定包的测试
mvn test -Dtest="com.ets2jsc.ast.*"
mvn test -Dtest="com.ets2jsc.generator.*"

# 运行测试并显示详细输出
mvn test -X

# 清理编译产物
mvn clean
```

## 项目结构

```
src/main/java/com/ets2jsc/
├── EtsCompiler.java              # 编译器主入口
├── ParallelEtsCompiler.java       # 并行编译器
├── CompilationResult.java         # 编译结果
│
├── ast/                           # AST 节点定义
│   ├── AstNode.java
│   ├── SourceFile.java
│   ├── ClassDeclaration.java
│   ├── MethodDeclaration.java
│   ├── PropertyDeclaration.java
│   ├── Decorator.java
│   ├── ComponentStatement.java
│   ├── ComponentExpression.java
│   ├── ExpressionStatement.java
│   ├── IfStatement.java
│   ├── ForeachStatement.java
│   ├── ImportStatement.java
│   ├── ExportStatement.java
│   ├── Identifier.java
│   ├── Block.java
│   ├── BuiltInComponents.java
│   ├── ComponentRegistry.java
│   └── AstVisitor.java
│
├── parser/                        # 解析器实现
│   ├── TypeScriptScriptParser.java
│   ├── TypeScriptParser.java
│   ├── AstBuilder.java
│   └── internal/
│       ├── ProcessExecutor.java
│       ├── ConversionContext.java
│       ├── NodeConverterRegistry.java
│       ├── ExpressionConverterRegistry.java
│       ├── StatementConverterRegistry.java
│       └── converters/
│           ├── expressions/
│           ├── statements/
│
├── transformer/                    # AST 转换器
│   ├── DecoratorTransformer.java
│   ├── ComponentTransformer.java
│   ├── ComponentExpressionTransformer.java
│   ├── BuildMethodTransformer.java
│   ├── AstTransformer.java
│   └── decorators/
│       ├── PropertyTransformer.java
│       ├── PropertyTransformerRegistry.java
│       └── impl/
│           ├── StatePropertyTransformer.java
│           ├── PropPropertyTransformer.java
│           ├── LinkPropertyTransformer.java
│           ├── ProvidePropertyTransformer.java
│           └── ConsumePropertyTransformer.java
│
├── generator/                      # 代码生成器
│   ├── CodeGenerator.java
│   ├── JsWriter.java
│   ├── SourceMapGenerator.java
│   └── writer/
│       ├── CodeWriter.java
│       └── IndentationManager.java
│
├── config/                        # 配置
│   ├── CompilerConfig.java
│   └── ProjectConfig.java
│
├── core/                          # 核心功能
│   └── factory/
│       ├── ParserFactory.java
│       ├── TransformerFactory.java
│       ├── GeneratorFactory.java
│       ├── DefaultParserFactory.java
│       ├── DefaultTransformerFactory.java
│       └── DefaultGeneratorFactory.java
│
├── constant/                      # 常量定义
│   ├── Decorators.java
│   ├── Components.java
│   ├── RuntimeFunctions.java
│   └── Symbols.java
│
└── util/                          # 工具类
    ├── StringUtils.java
    └── ValidationUtils.java

src/main/resources/typescript-parser/  # TypeScript 解析器模块
├── index.js                          # npm 标准入口 (CLI + 库)
└── src/
    └── javascript/                   # JavaScript 源码模块
        ├── ast/                      # AST 处理模块
        │   ├── preprocessor.js       # ETS 预处理
        │   ├── converter.js          # 主 AST 转换器
        │   └── converters/           # 节点类型转换器
        │       ├── literals.js       # 字面量节点
        │       ├── statements.js     # 语句节点
        │       ├── declarations.js   # 声明节点
        │       └── expressions.js    # 表达式节点
        ├── codegen/                  # 代码生成模块
        │   └── index.js              # JSON → 代码
        └── common/                   # 公共工具模块
            ├── constants.js          # 常量定义
            └── utils.js              # 工具函数

src/test/java/com/ets2jsc/
├── ast/                           # AST 节点单元测试
│   ├── DecoratorTest.java
│   ├── ComponentStatementTest.java
│   ├── IfStatementTest.java
│   ├── ForeachStatementTest.java
│   ├── ImportExportTest.java
│   ├── CallExpressionTest.java
│   ├── BlockTest.java
│   ├── ClassDeclarationTest.java
│   ├── MethodDeclarationTest.java
│   ├── PropertyDeclarationTest.java
│   └── ExpressionStatementTest.java
│
├── config/                        # 配置测试
│   └── CompilerConfigTest.java
│
├── core/                          # 核心功能测试
│   ├── ParallelCompilationTest.java
│   ├── CompilationResultTest.java
│   └── context/
│       ├── CompilationContextTest.java
│       └── TransformationContextTest.java
│
├── generator/                      # 代码生成器测试
│   ├── CodeGeneratorTest.java
│   └── writer/
│       ├── CodeWriterTest.java
│       └── IndentationManagerTest.java
│
├── integration/                    # 集成测试
│   ├── SimpleComponentTest.java
│   ├── ForEachTest.java
│   ├── Iteration4Test.java
│   ├── PureJavaScriptTest.java
│   └── TsJsAutoValidationTestOptimizedTest.java  # 自动化验证测试
│
├── transformer/                    # 转换器测试
│   ├── DecoratorTransformerTest.java
│   ├── StatePropertyTest.java
│   └── decorators/
│       └── PropertyTransformerRegistryTest.java
│
└── util/                          # 工具类测试
    ├── StringUtilsTest.java
    ├── ValidationUtilsTest.java
    ├── AstDebugger.java
    ├── CompilerRunner.java
    ├── EnhancedSimpleParser.java
    └── DebugTest.java
```

## TypeScript 解析器架构

TypeScript 解析器采用 **npm SDK 风格模块化架构**，具有以下特点：

### 架构原则

1. **按功能域分层** - `ast/`、`codegen/`、`common/`
2. **语言分层** - `src/javascript/` 支持多语言扩展
3. **单一职责** - 每个模块功能单一明确
4. **参数传递模式** - 避免循环依赖
5. **零魔法数字** - 所有常量提取到 `common/constants.js`

### 模块说明

| 模块 | 行数 | 职责 |
|------|------|------|
| `index.js` | 111 | CLI + 库统一入口 |
| `src/javascript/ast/` | 2,187 | AST 转换处理 |
| `src/javascript/codegen/` | 456 | 代码生成 |
| `src/javascript/common/` | 317 | 公共工具 |
| **总计** | **3,071** | 完整实现 |

### 使用方式

```bash
# CLI 使用
node src/main/resources/typescript-parser/index.js <input.ets> <output.json>

# 库使用 (JavaScript)
const { parse, parseFile } = require('./src/main/resources/typescript-parser/index');
const ast = parse('const x = 42;');
```

## 文档

详细文档位于 [docs](docs) 目录：

- [01-技术文档.md](docs/01-技术文档.md) - 技术文档
- [迭代2-技术实现方案-part1.md](docs/迭代2-技术实现方案-part1.md) - 迭代 2 技术设计（第 1 部分）
- [迭代2-技术实现方案-part2.md](docs/迭代2-技术实现方案-part2.md) - 迭代 2 技术设计（第 2 部分）
- [迭代3-需求说明书.md](docs/迭代3-需求说明书.md) - 迭代 3 需求说明
- [迭代3-技术实现方案.md](docs/迭代3-技术实现方案.md) - 迭代 3 技术设计
- [迭代4-技术实现方案.md](docs/迭代4-技术实现方案.md) - 迭代 4 技术设计

## 开发指南

### 添加新功能

1. 在 `ast/` 包中定义 AST 节点
2. 在 `parser/` 中实现解析逻辑
3. 在 `transformer/` 中创建转换器
4. 在 `generator/` 中生成代码
5. 在 `src/test/java/com/ets2jsc/` 对应包中添加测试

### TypeScript 解析器扩展

如需扩展 TypeScript 解析器功能：

1. **添加新的节点转换器** - 在 `src/javascript/ast/converters/` 中添加
2. **添加新的代码生成器** - 在 `src/javascript/codegen/index.js` 中添加
3. **添加新的常量** - 在 `src/javascript/common/constants.js` 中添加
4. **添加新的工具函数** - 在 `src/javascript/common/utils.js` 中添加

### 测试组织规范

测试用例应按照被测试的代码包进行组织：

| 目标代码包 | 测试包位置 |
|------------|--------------|
| `ast/` | `src/test/java/com/ets2jsc/ast/` |
| `config/` | `src/test/java/com/ets2jsc/config/` |
| `core/` | `src/test/java/com/ets2jsc/core/` |
| `generator/` | `src/test/java/com/ets2jsc/generator/` |
| `transformer/` | `src/test/java/com/ets2jsc/transformer/` |
| `util/` | `src/test/java/com/ets2jsc/util/` |
| 集成测试 | `src/test/java/com/ets2jsc/integration/` |
| 工具类 | `src/test/java/com/ets2jsc/util/` |

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定测试
mvn test -Dtest=DecoratorTest

# 运行指定包的测试
mvn test -Dtest="com.ets2jsc.ast.*"

# 详细输出模式
mvn test -X
```

## 测试覆盖

项目采用自动化测试验证 TypeScript → JavaScript 转换的正确性：

- **测试通过率**: 75.0% (75/100 测试用例)
- **测试文件**: TsJsAutoValidationTestOptimizedTest
- **测试框架**: JUnit 5
- **覆盖范围**: 基础语法、装饰器、组件、表达式、语句等

## 许可证

本项目采用 MIT 许可证。

## 贡献

欢迎贡献！请随时提交 Pull Request。

### 代码规范

在提交代码前，请确保：

- 遵循 [CODING_STANDARDS.md](docs/CODING_STANDARDS.md) 中的编码规范
- 无魔法数字（所有常量已提取）
- 避免返回 null（优先使用异常或 Optional）
- 使用自定义异常类
- 使用 SLF4J 进行日志记录
- 控制圈复杂度 ≤ 10
- 所有注释和命名使用英文

## 作者

- yonglunyao - 初始工作

## 致谢

- GraalVM - JavaScript 引擎
- TypeScript - 解析器基础
- HarmonyOS 社区 - 平台支持
