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

## 环境要求

- Java 17
- Maven 3.8.3+
- GraalVM JavaScript 引擎（作为依赖包含）

## 安装

```bash
# 克隆仓库
git clone https://github.com/yonglunyao/harmony-ets2js-compiler.git
cd harmony-ets2js-compiler

# 构建项目
mvn clean package
```

## 使用方法

### 命令行编译

```bash
# 编译单个 ETS 文件
java -jar target/ets2jsc-1.0-SNAPSHOT.jar <input.ets> <output.js>

# 或使用 Maven exec
mvn exec:java -Dexec.mainClass="com.ets2jsc.EtsCompiler" -Dexec.args="<input.ets> <output.js>"
```

### Maven 命令

```bash
# 编译
mvn compile

# 运行测试
mvn test

# 运行指定测试
mvn test -Dtest=SimpleComponentTest

# 构建 JAR
mvn clean package
```

## 项目结构

```
src/main/java/com/ets2jsc/
├── EtsCompiler.java          # 编译器主入口
├── ast/                       # AST 节点定义
│   ├── AstNode.java
│   ├── SourceFile.java
│   ├── ClassDeclaration.java
│   └── ...
├── parser/                    # 解析器实现
│   └── TypeScriptScriptParser.java
├── transformer/               # AST 转换器
│   ├── DecoratorTransformer.java
│   ├── ComponentTransformer.java
│   └── BuildMethodTransformer.java
├── generator/                 # 代码生成器
│   └── CodeGenerator.java
├── config/                    # 配置
│   └── CompilerConfig.java
└── constant/                  # 常量定义
    ├── Decorators.java
    └── Components.java
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
5. 在 `src/test/java/` 中添加测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=SimpleComponentTest

# 详细输出模式
mvn test -X
```

## 许可证

本项目采用 MIT 许可证。

## 贡献

欢迎贡献！请随时提交 Pull Request。

## 作者

- yonglunyao - 初始工作

## 致谢

- GraalVM - JavaScript 引擎
- TypeScript - 解析器基础
- HarmonyOS 团队 - 平台支持
