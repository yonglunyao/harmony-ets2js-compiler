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

## 编码规范要求

### ⚠️ MANDATORY: 遵循编码规范

**所有代码编写必须严格遵循 `docs/CODING_STANDARDS.md` 中定义的编码规范。**

在提交任何代码之前，请确保：

#### 强制要求 (Must Follow)

1. **优先使用 Lombok**
   - 对于简单的 getter 和 setter 方法，必须使用 Lombok 注解（@Getter, @Setter）替代手写方法
   - 新增类时，默认使用 Lombok 生成 getter/setter

2. **禁止使用魔鬼数字**
   - 所有常量必须定义在专门的常量类中（如 `Symbols.java`、`RuntimeFunctions.java`）
   - 不允许在代码中直接使用魔法数字或字符串

2. **避免返回null**
   - 优先使用异常机制处理错误情况
   - 对于可能不存在返回值的方法，使用 `Optional<T>` 而非返回 `null`

3. **使用自定义异常**
   - 禁止直接使用 `RuntimeException`
   - 使用 `exception` 包中的自定义异常类（`ParserException`、`ParserInitializationException` 等）

4. **使用SLF4J日志框架**
   - 禁止使用 `System.err` 或 `System.out` 输出日志
   - 使用 `@Slf4j` 或创建 `Logger` 实例进行日志记录

5. **控制圈复杂度**
   - 单方法圈复杂度必须 ≤ 10
   - 建议控制在 5 以内

6. **英文注释和命名**
   - 所有代码注释、文档、变量命名必须使用英文
   - 禁止使用中文注释

#### 代码审查清单

在提交 Pull Request 前，请确认以下项目：

- [ ] 无魔鬼数字（所有常量已提取）
- [ ] 无不合理的 null 返回（优先使用异常或 Optional）
- [ ] 使用了具体的异常类型（非 RuntimeException）
- [ ] 使用 SLF4J 进行日志记录
- [ ] 圈复杂度 ≤ 10
- [ ] 所有注释和命名使用英文
- [ ] 编写/更新了单元测试
- [ ] 添加了必要的 JavaDoc 文档
- [ ] 代码通过 `mvn test` 测试

#### 查看完整规范

详细的编码规范请参阅：**`docs/CODING_STANDARDS.md`**

---

## 常用命令

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