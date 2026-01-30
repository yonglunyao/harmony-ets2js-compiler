# ETS to JS Compiler - CLAUDE

## 项目概述
ETS to JS 编译器，用于将 ArkTS/ETS 代码编译为 JavaScript 代码。

## 技术栈
- Java 17
- Maven 3.8.3
- TypeScript (通过 Node.js)

## 编译命令

```bash
# 从项目根目录执行
mvn clean compile

# 或者指定主类编译（用于测试）
mvn exec:java -Dexec.mainClass="com.ets2jsc.EtsCompiler" -Dexec.args="<input.ets> <output.js>" -q
```

## 运行单个测试

```bash
mvn test -Dtest SimpleComponentTest
```

## 批量编译

```bash
mvn compile
```

## 清理编译产物

```bash
mvn clean
```

## 项目结构

```
src/main/java/com/ets2jsc/
├── EtsCompiler.java          # 编译器主类
├── ast/                     # AST 节点定义
├── parser/                  # 解析器（TypeScriptScriptParser）
├── transformer/               # 转换器（Decorator, BuildMethod 等）
├── generator/               # 代码生成器（CodeGenerator）
├── config/                  # 编译器配置
└── constant/                # 常量定义
```

## 已实现功能

- P0: @Component 装饰器（struct → class View）
- P0: @State 状态装饰器（ObservedPropertySimple）
- P0: 组件 create/pop 转换
- P0: 基础表达式支持
- P0: ForEach 列表渲染
- P1: If 条件渲染
- P1: @Prop 装饰器
- P1: @Link 装饰器
- P1: @Provide/@Consume 装饰器
- P2: @Builder 函数
- P2: 资源引用（$r(), $rawfile()）

## 开发注意事项

1. 所有 Java 源文件都在 `src/main/java/com/ets2jsc/` 目录
2. TypeScript 解析脚本在 `src/main/resources/typescript-parser/` 目录
3. AST 节点模型在 `ast/` 包中
4. 转换器在 `transformer/` 包中
5. 代码生成器在 `generator/` 包中

## 常用命令

### 添加依赖
```bash
mvn dependency:tree -DincludeTypes=js
```

### 编译特定模块
```bash
mvn compile -pl :parser
mvn compile -pl :transformer
```
```