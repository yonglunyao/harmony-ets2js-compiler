# ETS to JS Compiler - DDD 分层架构设计

## 概述

本文档定义 ETS to JS Compiler 的基于领域驱动设计（DDD）的分层架构。架构遵循依赖倒置原则，确保业务逻辑不依赖技术实现细节。

---

## 架构原则

1. **依赖倒置**：高层模块不依赖低层模块，都依赖抽象
2. **单一职责**：每层有明确的职责边界
3. **接口隔离**：模块间通过接口通信
4. **开闭原则**：对扩展开放，对修改关闭

---

## 分层结构

```
src/main/java/com/ets2jsc/
├── domain/                    # 领域层 - 核心业务逻辑
│   ├── model/                 # 领域模型
│   │   ├── ast/              # AST 节点模型
│   │   ├── compilation/      # 编译结果模型
│   │   └── config/           # 配置模型
│   └── service/              # 领域服务接口
│       ├── ParserService.java
│       ├── TransformerService.java
│       └── GeneratorService.java
│
├── application/              # 应用层 - 编排和协调
│   ├── compile/             # 编译流程编排
│   │   ├── CompilationPipeline.java      # 编译管道
│   │   └── CompilationPipelineFactory.java
│   └── facade/              # 应用门面（可选）
│       └── CompilerFacade.java
│
├── infrastructure/          # 基础设施层 - 技术实现
│   ├── parser/             # 解析器实现
│   │   ├── TypeScriptScriptParser.java
│   │   ├── AstBuilder.java
│   │   ├── converters/      # 表达式/语句转换器
│   │   └── internal/        # 内部接口
│   ├── transformer/         # 转换器实现
│   │   ├── chain/          # 责任链
│   │   └── decorators/     # 装饰器处理
│   ├── generator/          # 代码生成器实现
│   │   ├── CodeGenerator.java
│   │   ├── strategy/       # 生成策略
│   │   ├── writer/         # 代码写入
│   │   └── context/        # 生成上下文
│   └── factory/            # 基础设施工厂
│       ├── TransformerFactory.java
│       └── GeneratorFactory.java
│
├── interfaces/             # 接口层 - 外部交互
│   └── cli/               # 命令行接口
│       ├── EtsCompilerLauncher.java
│       └── command/       # 命令模式实现
│           ├── SingleFileCompilationCommand.java
│           ├── BatchCompilationCommand.java
│           └── ProjectCompilationCommand.java
│
└── shared/                # 共享层 - 跨层通用组件
    ├── constant/          # 常量定义
    ├── exception/         # 异常定义
    ├── util/             # 工具类
    └── events/           # 事件系统（可选）
```

---

## 层职责说明

### 1. 领域层 (Domain Layer)

**职责**：定义核心业务模型和业务规则

**包含内容**：
- **model/**：纯业务模型，不依赖任何框架
  - `ast/`：AST 节点（SourceFile, ClassDeclaration, MethodDeclaration 等）
  - `compilation/`：编译模型（CompilationRequest, CompilationResult, CompilationOutput）
  - `config/`：配置模型（CompilerConfig, OutputConfiguration）

- **service/**：领域服务接口
  ```java
  public interface ParserService extends AutoCloseable {
      SourceFile parseFile(Path sourcePath) throws ParserException;
      SourceFile parseString(String fileName, String sourceCode) throws ParserException;
      boolean canParse(Path sourcePath);
      void close();
  }
  ```

**依赖规则**：只能依赖 `shared/`，不依赖其他层

---

### 2. 应用层 (Application Layer)

**职责**：编排业务流程，协调领域服务

**包含内容**：
- **compile/**：编译流程编排
  ```java
  public class CompilationPipeline implements AutoCloseable {
      private final ParserService parser;
      private final TransformerService transformer;
      private final GeneratorService generator;

      public CompilationResult execute(Path sourcePath, Path outputPath) {
          SourceFile sourceFile = parser.parseFile(sourcePath);
          SourceFile transformed = transformer.transform(sourceFile, config);
          generator.generateToFile(transformed, outputPath, config);
          return CompilationResult.success(sourcePath, outputPath, duration);
      }
  }
  ```

- **facade/**（可选）：对外统一门面
  ```java
  public class CompilerFacade implements AutoCloseable {
      public CompilationResult compile(Path sourcePath, Path outputPath);
      public CompilationResult compileBatch(List<Path> sources, Path outputDir);
      public CompilationResult compileProject(Path projectDir, Path outputDir);
  }
  ```

**依赖规则**：依赖 `domain/` 和 `shared/`

---

### 3. 基础设施层 (Infrastructure Layer)

**职责**：提供技术实现，实现领域层定义的接口

**包含内容**：
- **parser/**：解析器实现
  - `TypeScriptScriptParser`：调用 TypeScript 解析器
  - `AstBuilder`：构建 AST
  - `converters/`：表达式/语句转换器

- **transformer/**：转换器实现
  - `chain/`：责任链模式
  - `decorators/`：装饰器处理（@Component, @State 等）

- **generator/**：代码生成器实现
  - `CodeGenerator`：主生成器
  - `strategy/`：策略模式
  - `writer/`：文件写入

**依赖规则**：依赖 `domain/` 和 `shared/`，实现 `domain/service/` 接口

---

### 4. 接口层 (Interfaces Layer)

**职责**：处理外部交互，委托给应用层

**包含内容**：
- **cli/**：命令行接口
  ```java
  public class EtsCompilerLauncher {
      public static int execute(String[] args) {
          CompilationPipeline pipeline = CompilationPipelineFactory.createDefault();
          try (pipeline) {
              return pipeline.execute(sourcePath, outputPath).isSuccess() ? 0 : 1;
          }
      }
  }
  ```

- **command/**：命令模式实现
  ```java
  public interface CompilationCommand {
      CompilationResult execute() throws CompilationException;
      String getName();
      boolean canExecute();
  }
  ```

**依赖规则**：只能依赖 `application/` 和 `shared/`

---

### 5. 共享层 (Shared Layer)

**职责**：跨层通用组件

**包含内容**：
- **constant/**：常量定义
  - `Symbols`：符号常量
  - `RuntimeFunctions`：运行时函数名
  - `Components`：组件常量
  - `Decorators`：装饰器名称

- **exception/**：异常定义
  - `ParserException`
  - `CompilationException`
  - `CodeGenerationException`

- **util/**：工具类
  - `FileUtil`
  - `StringUtil`
  - `SourceFileFinder`
  - `ResourceFileCopier`

- **events/**（可选）：事件系统
  - `CompilationListener`
  - `CompilationEventDispatcher`

**依赖规则**：不依赖其他层

---

## 依赖关系图

```
┌─────────────────────────────────────────────────────────────┐
│                        interfaces/                          │
│                      (CLI Entry Point)                       │
└────────────────────────────┬────────────────────────────────┘
                             │ depends on
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                       application/                          │
│              (Compilation Orchestration)                     │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
               │ depends on                   │ depends on
               ▼                              ▼
┌─────────────────────────┐      ┌──────────────────────────┐
│        domain/          │      │     infrastructure/      │
│   (Business Models &    │◄─────┤  (Technical              │
│    Service Interfaces)  │      │   Implementations)       │
└──────────┬──────────────┘      └──────────┬───────────────┘
           │                                 │
           │                                 │
           └─────────────┬───────────────────┘
                         │ depends on
                         ▼
              ┌─────────────────────┐
              │      shared/        │
              │  (Common Utilities) │
              └─────────────────────┘
```

---

## 关键设计决策

### 决策1：领域服务接口 vs 应用门面

**问题**：是否需要 `CompilerFacade`？

**决策**：可选
- 如果只有单一入口（CLI），`CompilationPipeline` 足够
- 如果有多入口（CLI, API, IDE Plugin），添加 `CompilerFacade`

### 决策2：compiler/ 包的处理

**问题**：现有的 `compiler/` 包如何处理？

**决策**：拆分到各层
- `compiler/ICompiler.java` → 移到 `domain/service/` 或保留作为兼容接口
- `compiler/BaseCompiler.java` → 移到 `application/compile/`
- `compiler/command/` → 移到 `interfaces/cli/command/`

### 决策3：api/, impl/, di/ 包的处理

**问题**：现有的 Facade 系统如何处理？

**决策**：整合到分层架构
- `api/` 接口与 `domain/service/` 接口合并
- `impl/` 实现移到 `infrastructure/`
- `di/` 功能整合到 `application/` 的工厂中

---

## 包路径映射

| 原包路径 | 新包路径 | 说明 |
|---------|---------|------|
| `ast/` | `domain/model/ast/` | 领域模型 |
| `config/` | `domain/model/config/` | 领域模型 |
| `constant/` | `shared/constant/` | 共享常量 |
| `exception/` | `shared/exception/` | 共享异常 |
| `util/` | `shared/util/` | 共享工具 |
| `parser/` | `infrastructure/parser/` | 基础设施 |
| `transformer/` | `infrastructure/transformer/` | 基础设施 |
| `generator/` | `infrastructure/generator/` | 基础设施 |
| `cli/` | `interfaces/cli/` | 接口层 |
| `command/` | `interfaces/cli/command/` | 接口层 |
| `compiler/BaseCompiler` | `application/compile/` | 应用层 |
| `compiler/ICompiler` | `domain/service/` 或保留 | 领域服务 |

---

## 编译流程

```
┌─────────────────────────────────────────────────────────────┐
│  interfaces/cli/EtsCompilerLauncher                         │
│    → 解析命令行参数                                          │
│    → 调用 CompilationPipelineFactory.createPipeline()        │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│  application/compile/CompilationPipeline                    │
│    → execute(sourcePath, outputPath)                        │
│    1. parser.parseFile(sourcePath)   ──┐                   │
│    2. transformer.transform(sourceFile) │                  │
│    3. generator.generateToFile(...)    │                   │
└────────────────────────────┬────────────────────────────────┘
                             │
                ┌────────────┼────────────┐
                ▼            ▼            ▼
         ┌──────────┐  ┌──────────┐  ┌──────────┐
         │  Parser  │  │Transformer│ │ Generator │
         │ Service  │  │  Service  │  │ Service  │
         └────┬─────┘  └─────┬─────┘  └────┬─────┘
              │             │             │
              ▼             ▼             ▼
         ┌──────────────────────────────────────┐
         │         infrastructure/               │
         │  · TypeScriptScriptParser            │
         │  · AstTransformer chain              │
         │  · CodeGenerator                     │
         └──────────────────────────────────────┘
```

---

## 清理任务清单

### Phase 1: 清理嵌套目录
- [ ] 修复 `interfaces/cli/cli/` 嵌套
- [ ] 删除空目录（`interfaces/api`, `interfaces/apishared` 等）
- [ ] 删除 `interfaces/clishared`, `interfaces/spishared`

### Phase 2: 统一接口系统
- [ ] 决定保留 `domain/service/` 还是 `api/` 接口
- [ ] 移除重复的接口定义
- [ ] 更新所有引用

### Phase 3: 整合 compiler/ 包
- [ ] `BaseCompiler` 移到 `application/compile/` 或删除
- [ ] `ICompiler` 移到 `domain/service/` 或保留
- [ ] `command/` 移到 `interfaces/cli/command/`

### Phase 4: 整合 api/, impl/, di/
- [ ] `api/` 接口合并到 `domain/service/`
- [ ] `impl/` 实现移到 `infrastructure/`
- [ ] `di/` 功能整合到 `application/`

### Phase 5: 验证
- [ ] 所有测试通过
- [ ] PMD 检查通过
- [ ] 编译成功

---

## 成功标准

- [ ] 清晰的分层边界
- [ ] 单向依赖（interfaces → application → infrastructure → domain → shared）
- [ ] 无重复接口定义
- [ ] 无嵌套目录错误
- [ ] 所有测试通过
- [ ] 0 PMD 违规
