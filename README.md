# ETS to JS 编译器

HarmonyOS ArkTS/ETS 转 JavaScript 编译器。该编译器将 ArkTS（扩展 TypeScript）代码转换为标准 JavaScript，可由 HarmonyOS 运行时执行。

[English](README_EN.md) | 简体中文

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
  - 项目级编译（支持资源文件复制）

## 技术栈

- **Java 17** - 主要开发语言
- **Maven 3.8.3+** - 项目构建和依赖管理
- **GraalVM JavaScript Engine** - JavaScript 执行引擎（嵌入式依赖）
- **TypeScript 5.9.3** - AST 解析

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

## 使用说明

### CLI 使用

```bash
# 编译单个文件
java -jar target/ets2jsc-1.0-SNAPSHOT.jar <input.ets> <output.js>

# 编译项目目录
java -jar target/ets2jsc-1.0-SNAPSHOT.jar --project <source-dir> <output-dir>

# 编译项目并复制资源文件
java -jar target/ets2jsc-1.0-SNAPSHOT.jar --project --resources <source-dir> <output-dir>
```

### Java API

#### 基本用法

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import com.ets2jsc.interfaces.publicapi.model.PublicCompilationResult;
import java.nio.file.Paths;

// 使用默认配置创建编译器
try (EtsCompiler compiler = EtsCompiler.create()) {
    // 编译单个文件
    PublicCompilationResult result = compiler.compileFile(
        Paths.get("src/main/ets/MyComponent.ets"),
        Paths.get("output/MyComponent.js")
    );

    if (result.isSuccess()) {
        System.out.println("编译成功");
    }
}
```

#### 使用 Builder 模式

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import java.nio.file.Paths;

// 使用自定义配置创建编译器
try (EtsCompiler compiler = EtsCompiler.builder()
        .projectPath(Paths.get("/my/project"))
        .sourcePath("src/main/ets")
        .buildPath("build")
        .parallelMode(true)
        .threadCount(4)
        .generateSourceMap(true)
        .build()) {

    // 编译项目
    PublicCompilationResult result = compiler.compileProject(
        Paths.get("src"),
        Paths.get("output"),
        true  // 复制资源文件
    );
}
```

#### 批量编译

```java
import com.ets2jsc.interfaces.publicapi.EtsCompiler;
import java.nio.file.Paths;
import java.util.List;

try (EtsCompiler compiler = EtsCompiler.builder()
        .parallelMode(true)
        .threadCount(4)
        .build()) {

    List<Path> sourceFiles = List.of(
        Paths.get("src/Component1.ets"),
        Paths.get("src/Component2.ets")
    );

    // 编译批量文件并保留目录结构
    PublicCompilationResult result = compiler.compileBatchWithStructure(
        sourceFiles,
        Paths.get("src"),
        Paths.get("output")
    );
}
```

### 配置选项

```java
import com.ets2jsc.domain.model.config.CompilerConfig;

CompilerConfig config = CompilerConfig.createDefault();

// 编译模式
config.setCompileMode(CompilerConfig.CompileMode.MODULE_JSON);

// 输出选项
config.setGenerateSourceMap(true);
config.setMinifyOutput(false);

// 功能开关
config.setPureJavaScript(true);  // 生成不含 ArkUI 运行时的纯 JS
config.setValidateApi(true);
config.setProcessTs(true);
config.setEnableLazyImport(false);

// 路径配置
config.setProjectPath("/path/to/project");
config.setSourcePath("src/main/ets");
config.setBuildPath("build");
```

## 架构设计

项目采用 **领域驱动设计（DDD）分层架构**：

```
src/main/java/com/ets2jsc/
├── interfaces/                    # 接口层 - API 和 CLI
│   ├── publicapi/                # 公共 API
│   │   ├── EtsCompiler.java      # 主编译器门面
│   │   ├── EtsCompilerBuilder.java
│   │   └── model/                # 公共 API 模型
│   └── cli/                      # 命令行接口
│       └── command/
│
├── application/                  # 应用层 - 用例编排
│   ├── compile/                  # 编译编排
│   │   ├── CompilationPipeline.java
│   │   ├── CompilationPipelineFactory.java
│   │   ├── BatchCompilationService.java
│   │   ├── BatchCompilationServiceFactory.java
│   │   ├── SequentialBatchCompilationService.java
│   │   └── ParallelBatchCompilationService.java
│   └── di/                       # 依赖注入
│
├── domain/                       # 领域层 - 核心业务逻辑
│   ├── model/                    # 领域模型
│   │   ├── ast/                  # AST 节点定义
│   │   │   ├── AstNode.java
│   │   │   ├── AstVisitor.java
│   │   │   ├── BuiltInComponents.java
│   │   │   ├── ComponentRegistry.java
│   │   │   └── ...
│   │   ├── config/               # 配置模型
│   │   │   ├── CompilerConfig.java
│   │   │   ├── CompilationMode.java
│   │   │   └── OutputConfiguration.java
│   │   ├── compilation/          # 编译模型
│   │   │   ├── CompilationRequest.java
│   │   │   ├── CompilationResult.java
│   │   │   └── FileResult.java
│   │   └── source/               # 源文件模型
│   └── service/                  # 领域服务
│       └── ParserService.java
│
├── infrastructure/               # 基础设施层 - 技术实现
│   ├── parser/                   # TypeScript 解析器
│   │   ├── AstBuilder.java
│   │   ├── ConversionContext.java
│   │   ├── NodeConverterRegistry.java
│   │   ├── converters/
│   │   │   ├── expr/             # 表达式转换器 (40+)
│   │   │   └── stmt/             # 语句转换器 (40+)
│   │   └── internal/
│   ├── transformer/              # AST 转换
│   │   ├── AstTransformer.java
│   │   ├── BuildMethodTransformer.java
│   │   ├── chain/                # 转换链
│   │   └── decorators/           # 装饰器转换器
│   │       └── impl/
│   ├── generator/                # 代码生成
│   │   ├── ComponentCodeGenerator.java
│   │   ├── PropertyGenerator.java
│   │   ├── BlockGenerator.java
│   │   ├── JsWriter.java
│   │   ├── SourceMapGenerator.java
│   │   ├── strategy/            # 生成策略
│   │   ├── writer/              # 代码写入器
│   │   ├── context/             # 生成上下文
│   │   └── internal/
│   └── factory/                 # 工厂实现
│       ├── GeneratorFactory.java
│       └── TransformerFactory.java
│
└── shared/                      # 共享内核 - 横切关注点
    ├── constant/                # 常量（Symbols、RuntimeFunctions 等）
    ├── exception/               # 自定义异常
    ├── events/                  # 领域事件
    └── util/                    # 工具类
        ├── ResourceFileCopier.java
        ├── SourceFileFinder.java
        └── ...
```

### 层级职责

| 层级 | 职责 |
|------|------|
| **interfaces** | 公共 API、CLI、外部集成点 |
| **application** | 用例编排、工作流协调 |
| **domain** | 核心业务逻辑、领域模型、领域服务 |
| **infrastructure** | 技术实现（解析器、转换器、生成器） |
| **shared** | 横切工具、常量、异常 |

## TypeScript 解析器架构

TypeScript 解析器采用 **npm SDK 风格模块化架构**：

```
src/main/resources/typescript-parser/
├── index.js                          # CLI + 库统一入口
└── src/javascript/
    ├── ast/                          # AST 处理模块
    │   ├── preprocessor.js           # ETS 预处理
    │   ├── converter.js              # 主 AST 转换器
    │   └── converters/               # 节点类型转换器
    │       ├── literals.js           # 字面量节点
    │       ├── statements.js         # 语句节点
    │       ├── declarations.js       # 声明节点
    │       └── expressions.js        # 表达式节点
    ├── codegen/                      # 代码生成模块
    │   └── index.js                  # JSON → 代码
    └── common/                       # 公共工具模块
        ├── constants.js              # 常量定义
        └── utils.js                  # 工具函数
```

### 架构原则

1. **按功能域分层** - `ast/`、`codegen/`、`common/`
2. **语言分层** - `src/javascript/` 支持多语言扩展
3. **单一职责** - 每个模块功能单一明确
4. **参数传递模式** - 避免循环依赖
5. **零魔法数字** - 所有常量提取到 `common/constants.js`

## Maven 命令

```bash
# 编译
mvn compile

# 运行测试
mvn test

# 运行指定测试
mvn test -Dtest=SimpleComponentTest

# 详细输出模式
mvn test -X

# 清理编译产物
mvn clean

# 完整构建
mvn clean package
```

## 文档

详细文档位于 [docs](docs) 目录：

- [01-技术文档.md](docs/01-技术文档.md) - 技术文档
- [迭代2-技术实现方案-part1.md](docs/迭代2-技术实现方案-part1.md) - 迭代 2 技术设计（第 1 部分）
- [迭代2-技术实现方案-part2.md](docs/迭代2-技术实现方案-part2.md) - 迭代 2 技术设计（第 2 部分）
- [迭代3-需求说明书.md](docs/迭代3-需求说明书.md) - 迭代 3 需求说明
- [迭代3-技术实现方案.md](docs/迭代3-技术实现方案.md) - 迭代 3 技术设计
- [迭代4-技术实现方案.md](docs/迭代4-技术实现方案.md) - 迭代 4 技术设计
- [CODING_STANDARDS.md](docs/CODING_STANDARDS.md) - 编码规范和指南

## 开发指南

### 添加新功能

1. 在 `domain/model/ast/` 中定义 AST 节点
2. 在 `infrastructure/parser/converters/` 中实现解析逻辑
3. 在 `infrastructure/transformer/` 中创建转换器
4. 在 `infrastructure/generator/` 中生成代码
5. 在 `src/test/java/com/ets2jsc/` 中添加测试

### 扩展 TypeScript 解析器

1. 在 `src/javascript/ast/converters/` 中添加节点转换器
2. 在 `src/javascript/codegen/index.js` 中添加代码生成器
3. 在 `src/javascript/common/constants.js` 中添加常量
4. 在 `src/javascript/common/utils.js` 中添加工具函数

## 测试组织

| 目标代码包 | 测试包位置 |
|------------|--------------|
| `domain/model/ast` | `src/test/java/com/ets2jsc/domain/model/ast/` |
| `domain/model/config` | `src/test/java/com/ets2jsc/domain/model/config/` |
| `infrastructure/generator` | `src/test/java/com/ets2jsc/infrastructure/generator/` |
| `infrastructure/transformer` | `src/test/java/com/ets2jsc/infrastructure/transformer/` |
| 集成测试 | `src/test/java/com/ets2jsc/integration/` |

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

## 许可证

本项目采用 MIT 许可证。

## 作者

- yonglunyao - 初始工作

## 致谢

- GraalVM - JavaScript 引擎
- TypeScript - 解析器基础
- HarmonyOS 社区 - 平台支持
