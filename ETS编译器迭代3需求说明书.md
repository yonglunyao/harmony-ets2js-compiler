# ETS 编译器迭代3需求说明书

## 一、项目概述

### 1.1 项目背景
ETS to JS 编译器（ets2jsc）已完成迭代0、迭代1和迭代2的开发，实现了基础的 ETS 到 JavaScript 转换能力。在对实际项目（myutils 工具库，包含11个工具类、约1500行代码）进行编译测试时，发现编译产物存在大量语法转换错误和缺失，导致编译后的 JavaScript 代码无法正常运行。

### 1.2 项目目标
完善 ets2jsc 编译器的语法支持能力，使其能够正确处理 ETS/TypeScript 的核心语法特性，确保编译产物的语法正确性和可执行性。

### 1.3 目标用户
- 使用 ETS 语言开发 HarmonyOS 应用的开发者
- 需要 ETS 到 JavaScript 转换能力的工具开发者
- ets2jsc 编译器的维护和扩展人员

---

## 二、现状问题分析

### 2.1 问题来源
通过分析 `src/test/resources/fixtures/myutils/dist/` 目录下的编译产物，对比源代码与编译输出，识别出以下问题类别。

### 2.2 问题分类与影响

#### 问题类别 1：字面量表达式转换失败
**优先级：** P0（阻塞性问题）

**问题描述：**
基础字面量被转换为 JSON 对象表示，而非实际的 JavaScript 字面量值。

**源代码示例：**
```typescript
return arr === null || arr === undefined;
return true;
return false;
```

**当前编译输出：**
```javascript
return arr === {"kind":106,"kindName":"NullKeyword"} || arr === undefined;
return {"kind":112,"kindName":"TrueKeyword"};
```

**预期输出：**
```javascript
return arr === null || arr === undefined;
return true;
```

**影响范围：**
- 所有包含布尔字面量的表达式
- 所有包含 null 的表达式
- 导致编译产物语法错误，无法执行

---

#### 问题类别 2：循环语句未实现
**优先级：** P0（阻塞性问题）

**问题描述：**
所有类型的循环语句（for...of、for...in、while、do...while、传统 for）均未实现，仅输出占位符注释。

**源代码示例：**
```typescript
for (const item of arr) {
    result.push(process(item));
}

for (let i = 0; i < arr.length; i++) {
    console.log(arr[i]);
}

while (condition) {
    // do something
}
```

**当前编译输出：**
```javascript
// for...of loop;
// variable declaration;
```

**预期输出：**
```javascript
for (const item of arr) {
    result.push(process(item));
}

for (let i = 0; i < arr.length; i++) {
    console.log(arr[i]);
}

while (condition) {
    // do something
}
```

**影响范围：**
- 所有使用循环的代码
- 数组处理、遍历操作
- 导致核心逻辑完全丢失

---

#### 问题类别 3：特殊表达式未转换
**优先级：** P0（阻塞性问题）

**问题描述：**
前缀一元表达式、非空断言、类型断言等特殊表达式未正确转换。

**源代码示例：**
```typescript
return !StringUtils.isEmpty(str);
return this.items!;
return value as number;
```

**当前编译输出：**
```javascript
return {"kind":225,"kindName":"PrefixUnaryExpression"}
return {"kind":236,"kindName":"NonNullExpression"}.trim();
return {"kind":235,"kindName":"AsExpression"}
```

**预期输出：**
```javascript
return !StringUtils.isEmpty(str);
return this.items.trim();
return value;
```

**影响范围：**
- 逻辑非运算
- 非空断言操作
- 类型断言操作

---

#### 问题类别 4：空值处理错误
**优先级：** P0（阻塞性问题）

**问题描述：**
空字符串字面量和空返回语句的转换存在问题。

**源代码示例：**
```typescript
return "";
return str || "";
```

**当前编译输出：**
```javascript
return ;
return str || ;
```

**预期输出：**
```javascript
return "";
return str || "";
```

**影响范围：**
- 所有返回空字符串的函数
- 所有使用空字符串默认值的表达式

---

#### 问题类别 5：数组/对象访问表达式未转换
**优先级：** P0（阻塞性问题）

**问题描述：**
元素访问表达式（数组索引、对象属性）未正确转换。

**源代码示例：**
```typescript
return arr[0];
return items[index];
return days[date.getDay()];
```

**当前编译输出：**
```javascript
return {"kind":213,"kindName":"ElementAccessExpression"}
return {"kind":213,"kindName":"ElementAccessExpression"}
return {"kind":213,"kindName":"ElementAccessExpression"}
```

**预期输出：**
```javascript
return arr[0];
return items[index];
return days[date.getDay()];
```

**影响范围：**
- 所有数组索引访问
- 所有动态属性访问
- 导致核心数据访问失败

---

#### 问题类别 6：Switch 语句未实现
**优先级：** P1（重要功能）

**问题描述：**
Switch 语句及 case/default 子句未实现。

**源代码示例：**
```typescript
switch (status) {
    case 'pending':
        return '处理中';
    case 'completed':
        return '已完成';
    default:
        return '未知';
}
```

**当前编译输出：**
未支持

**预期输出：**
```javascript
switch (status) {
    case 'pending':
        return '处理中';
    case 'completed':
        return '已完成';
    default:
        return '未知';
}
```

**影响范围：**
- 多条件分支场景
- 状态机实现
- 替代大量 if-else 的场景

---

#### 问题类别 7：异常处理语句未实现
**优先级：** P1（重要功能）

**问题描述：**
try-catch-finally 语句未实现。

**源代码示例：**
```typescript
try {
    JSON.parse(data);
} catch (error) {
    console.error('Parse error', error);
} finally {
    cleanup();
}
```

**当前编译输出：**
未支持

**预期输出：**
```javascript
try {
    JSON.parse(data);
} catch (error) {
    console.error('Parse error', error);
} finally {
    cleanup();
}
```

**影响范围：**
- 错误处理场景
- 资源清理场景
- 容错性要求高的代码

---

#### 问题类别 8：跳转语句未实现
**优先级：** P1（重要功能）

**问题描述：**
break 和 continue 语句未实现。

**源代码示例：**
```typescript
for (let i = 0; i < arr.length; i++) {
    if (shouldSkip) continue;
    if (shouldStop) break;
}
```

**当前编译输出：**
未支持

**预期输出：**
```javascript
for (let i = 0; i < arr.length; i++) {
    if (shouldSkip) continue;
    if (shouldStop) break;
}
```

**影响范围：**
- 循环控制
- 提前终止场景

---

#### 问题类别 9：展开语法未实现
**优先级：** P1（重要功能）

**问题描述：**
数组展开和对象展开语法未实现。

**源代码示例：**
```typescript
const combined = [...arr1, ...arr2];
const merged = {...obj1, ...obj2};
```

**当前编译输出：**
转换可能不正确

**预期输出：**
```javascript
const combined = [...arr1, ...arr2];
const merged = {...obj1, ...obj2};
```

**影响范围：**
- 数组合并操作
- 对象合并操作
- 函数参数展开

---

#### 问题类别 10：模板字符串未实现
**优先级：** P1（重要功能）

**问题描述：**
模板字符串和标签模板未实现。

**源代码示例：**
```typescript
const message = `Hello ${name}, you have ${count} messages`;
```

**当前编译输出：**
转换可能不正确

**预期输出：**
```javascript
const message = `Hello ${name}, you have ${count} messages`;
```

**影响范围：**
- 字符串拼接场景
- 动态内容生成
- 多行字符串

---

#### 问题类别 11：现代运算符未实现
**优先级：** P2（增强功能）

**问题描述：**
可选链、空值合并等现代 JavaScript 运算符未实现。

**源代码示例：**
```typescript
const value = obj?.prop?.nested;
const result = input ?? defaultValue;
```

**当前编译输出：**
未支持

**预期输出：**
```javascript
const value = obj?.prop?.nested;
const result = input ?? defaultValue;
```

**影响范围：**
- 简化空值检查
- 提升代码可读性
- 现代 JavaScript 代码兼容

---

#### 问题类别 12：JSDoc 注释丢失
**优先级：** P2（增强功能）

**问题描述：**
函数和方法的 JSDoc 文档注释在编译过程中完全丢失。

**源代码示例：**
```typescript
/**
 * 日期格式化
 * @param date 日期对象或时间戳
 * @param format 格式字符串
 * @returns 格式化后的日期字符串
 */
static format(date: Date, format: string): string {
    // ...
}
```

**当前编译输出：**
```javascript
static format(date /* Date */, format /* string */) {
    // ...
}
```

**预期输出：**
```javascript
/**
 * 日期格式化
 * @param date 日期对象或时间戳
 * @param format 格式字符串
 * @returns 格式化后的日期字符串
 */
static format(date, format) {
    // ...
}
```

**影响范围：**
- 代码文档化
- IDE 智能提示
- 代码可维护性

---

#### 问题类别 13：泛型语法处理不当
**优先级：** P2（增强功能）

**问题描述：**
泛型类型注解在编译产物中保留，可能导致运行时错误。

**源代码示例：**
```typescript
private data: ObservedPropertySimple<string> = 'value';
```

**当前编译输出：**
```javascript
private data: ObservedPropertySimple<string> = 'value';
```

**预期输出：**
```javascript
private data: ObservedPropertySimple = 'value';
// 或完全移除类型注解
```

**影响范围：**
- 泛型类型的使用
- TypeScript 类型系统兼容性

---

#### 问题类别 14：箭头函数内联类型注解
**优先级：** P2（增强功能）

**问题描述：**
箭头函数参数和返回值的类型注解处理可能存在问题。

**源代码示例：**
```typescript
arr.sort((a: T, b: T): number => a - b);
items.filter((item: Item): boolean => item.active);
```

**当前编译输出：**
需要验证是否正确

**预期输出：**
```javascript
arr.sort((a, b) => a - b);
items.filter((item) => item.active);
```

**影响范围：**
- 高阶函数使用
- 回调函数定义

---

## 三、功能需求

### 3.1 P0 级别需求（必须实现）

#### 需求 ID: P0-LITERAL
**需求名称：** 字面量表达式转换

**需求描述：**
编译器必须能够正确将 TypeScript/ETS 的字面量表达式转换为对应的 JavaScript 字面量。

**功能要求：**
1. 支持布尔字面量：true → true, false → false
2. 支持空值字面量：null → null
3. 支持未定义值：undefined → undefined
4. 支持数字字面量：保持原值
5. 支持字符串字面量：保持带引号的形式

**验收标准：**
- [ ] 编译产物中不包含 JSON 对象形式的字面量表示
- [ ] 所有字面量转换后的代码可被 JavaScript 引擎正确解析
- [ ] 通过 myutils 工具库中所有包含字面量的测试用例

**测试用例：**
```typescript
// 输入
const isActive = true;
const value = null;
const name = "test";
const count = 42;

// 预期输出
const isActive = true;
const value = null;
const name = "test";
const count = 42;
```

---

#### 需求 ID: P0-LOOP
**需求名称：** 循环语句转换

**需求描述：**
编译器必须支持所有类型的循环语句转换。

**功能要求：**
1. 支持 for...of 循环
2. 支持 for...in 循环
3. 支持 while 循环
4. 支持 do...while 循环
5. 支持传统 for 循环
6. 支持 for await...of 异步迭代

**验收标准：**
- [ ] 所有循环类型都能正确转换
- [ ] 循环体内的代码正确保留
- [ ] 循环变量声明正确转换
- [ ] 循环条件表达式正确转换
- [ ] 不再输出占位符注释

**测试用例：**
```typescript
// for...of
for (const item of array) {
    console.log(item);
}

// for...in
for (const key in object) {
    console.log(key);
}

// while
while (condition) {
    doSomething();
}

// do...while
do {
    something();
} while (condition);

// 传统 for
for (let i = 0; i < 10; i++) {
    console.log(i);
}
```

---

#### 需求 ID: P0-EXPR
**需求名称：** 特殊表达式转换

**需求描述：**
编译器必须支持特殊表达式的转换，包括一元运算符、非空断言、类型断言等。

**功能要求：**
1. 支持前缀一元运算符：!, -, +, ~, ++
2. 支持后缀一元运算符：++, --
3. 支持非空断言：正确处理或移除 `!`
4. 支持类型断言：移除 `as Type` 类型断言
5. 支持括号表达式：(expr)

**验收标准：**
- [ ] 一元运算符正确转换
- [ ] 非空断言表达式可正确处理
- [ ] 类型断言被移除或转换为等价代码
- [ ] 括号表达式正确保留

**测试用例：**
```typescript
// 前缀运算符
const result = !isActive;
const negative = -value;
const incremented = ++count;

// 非空断言
const value = possiblyNull!.toString();

// 类型断言
const num = value as number;

// 括号表达式
const result = (a + b) * c;
```

---

#### 需求 ID: P0-ARRAY-ACCESS
**需求名称：** 数组/对象访问表达式

**需求描述：**
编译器必须支持元素访问表达式的转换。

**功能要求：**
1. 支持数组索引访问：arr[index]
2. 支持对象属性访问：obj[key]
3. 支持嵌套访问：arr[index][nested]
4. 支持表达式作为索引：arr[getKey()]

**验收标准：**
- [ ] 所有元素访问表达式正确转换
- [ ] 索引表达式正确转换
- [ ] 嵌套访问正确处理

**测试用例：**
```typescript
const first = arr[0];
const value = obj[key];
const nested = arr[index][prop];
const dynamic = items[getIndex()];
```

---

#### 需求 ID: P0-EMPTY-VALUE
**需求名称：** 空值处理

**需求描述：**
编译器必须正确处理空字符串和空返回语句。

**功能要求：**
1. 空字符串字面量保留：""
2. 空 return 语句转换为 return;
3. 空字符串作为默认值正确处理

**验收标准：**
- [ ] 空字符串不会丢失
- [ ] return "" 正确转换
- [ ] 表达式中的空字符串默认值正确

**测试用例：**
```typescript
function getEmpty(): string {
    return "";
}

const result = input || "";
```

---

### 3.2 P1 级别需求（重要功能）

#### 需求 ID: P1-SWITCH
**需求名称：** Switch 语句

**需求描述：**
编译器应支持 switch 语句及其相关语法的转换。

**功能要求：**
1. 支持 switch 语句结构
2. 支持 case 子句
3. 支持 default 子句
4. 支持每个 case 中的语句块
5. 支持 break 语句（隐式或显式）

**验收标准：**
- [ ] switch 语句结构正确
- [ ] case 表达式正确转换
- [ ] default 分支正确
- [ ] case 内语句正确保留
- [ ] 代码可正确执行

**测试用例：**
```typescript
switch (type) {
    case 'A':
        handleA();
        break;
    case 'B':
        handleB();
        break;
    default:
        handleDefault();
}
```

---

#### 需求 ID: P1-EXCEPTION
**需求名称：** 异常处理

**需求描述：**
编译器应支持 try-catch-finally 异常处理语句。

**功能要求：**
1. 支持 try 块
2. 支持 catch 块及错误变量
3. 支持 finally 块
4. 支持嵌套异常处理

**验收标准：**
- [ ] try-catch-finally 结构正确
- [ ] catch 错误变量正确声明
- [ ] 各块内语句正确保留

**测试用例：**
```typescript
try {
    riskyOperation();
} catch (error) {
    handleError(error);
} finally {
    cleanup();
}
```

---

#### 需求 ID: P1-JUMP
**需求名称：** 跳转语句

**需求描述：**
编译器应支持 break 和 continue 跳转语句。

**功能要求：**
1. 支持 break 语句
2. 支持 continue 语句
3. 支持带标签的跳转（可选）

**验收标准：**
- [ ] break/continue 语句正确输出
- [ ] 可与循环语句正确配合

**测试用例：**
```typescript
for (let i = 0; i < 10; i++) {
    if (shouldSkip) continue;
    if (shouldStop) break;
}
```

---

#### 需求 ID: P1-SPREAD
**需求名称：** 展开语法

**需求描述：**
编译器应支持数组和对象的展开语法。

**功能要求：**
1. 支持数组展开：[...arr]
2. 支持对象展开：{...obj}
3. 支持函数参数展开：fn(...args)
4. 支持混合展开：[1, ...arr, 2]

**验收标准：**
- [ ] 展开语法正确保留
- [ ] 嵌套展开正确处理

**测试用例：**
```typescript
const combined = [...arr1, ...arr2];
const merged = {...obj1, ...obj2};
func(...args);
```

---

#### 需求 ID: P1-TEMPLATE
**需求名称：** 模板字符串

**需求描述：**
编译器应支持模板字符串和标签模板。

**功能要求：**
1. 支持基础模板字符串：`text`
2. 支持插值表达式：`${expr}`
3. 支持多行模板字符串
4. 支持标签模板（可选）

**验收标准：**
- [ ] 模板字符串语法正确
- [ ] 插值表达式正确转换
- [ ] 多行字符串正确保留

**测试用例：**
```typescript
const message = `Hello ${name}`;
const multi = `Line 1
Line 2`;
```

---

### 3.3 P2 级别需求（增强功能）

#### 需求 ID: P2-MODERN-OP
**需求名称：** 现代运算符

**需求描述：**
编译器应支持现代 JavaScript 运算符。

**功能要求：**
1. 可选链：obj?.prop
2. 空值合并：a ?? b
3. 空值合并赋值：a ??= b
4. 逻辑赋值：a ||= b, a &&= b

**验收标准：**
- [ ] 现代运算符正确保留
- [ ] 嵌套可选链正确处理

**测试用例：**
```typescript
const value = obj?.prop?.nested;
const result = input ?? defaultValue;
data ??= getDefault();
```

---

#### 需求 ID: P2-JSDOC
**需求名称：** JSDoc 注释保留

**需求描述：**
编译器应保留 JSDoc 文档注释。

**功能要求：**
1. 保留函数/方法的 JSDoc 注释
2. 保留 @param、@returns 等标签
3. 保留类和接口的文档注释
4. 可选择性地移除类型注解或保留

**验收标准：**
- [ ] JSDoc 注释块被保留
- [ ] 标签格式正确
- [ ] 注释位置正确

**测试用例：**
```typescript
/**
 * 计算总和
 * @param numbers 数字数组
 * @returns 总和
 */
function sum(numbers: number[]): number {
    return numbers.reduce((a, b) => a + b, 0);
}
```

---

#### 需求 ID: P2-GENERIC
**需求名称：** 泛型语法处理

**需求描述：**
编译器应正确处理泛型语法。

**功能要求：**
1. 移除或转换泛型类型注解
2. 保留泛型类/函数的名称
3. 处理嵌套泛型

**验收标准：**
- [ ] 泛型不会导致运行时错误
- [ ] 代码功能保持一致

**测试用例：**
```typescript
function identity<T>(value: T): T {
    return value;
}
```

---

#### 需求 ID: P2-ARROW
**需求名称：** 箭头函数类型注解

**需求描述：**
编译器应正确处理箭头函数的类型注解。

**功能要求：**
1. 移除参数类型注解
2. 移除返回值类型注解
3. 保留箭头函数语法

**验收标准：**
- [ ] 箭头函数语法正确
- [ ] 类型注解被正确移除

**测试用例：**
```typescript
const add = (a: number, b: number): number => a + b;
items.filter((item: Item): boolean => item.active);
```

---

## 四、非功能需求

### 4.1 性能要求
- 编译速度：单个 ETS 文件（1000行）编译时间 < 2秒
- 内存占用：编译过程内存占用 < 500MB

### 4.2 兼容性要求
- 支持目标 JavaScript 版本：ES2015+
- 输出代码兼容主流浏览器和 Node.js 环境

### 4.3 可靠性要求
- 编译产物语法正确率：100%
- 单元测试覆盖率：> 80%

### 4.4 可维护性要求
- 代码符合现有架构规范
- 新增代码包含必要注释
- 变更文档及时更新

---

## 五、验收标准

### 5.1 功能验收
所有 P0 级别需求必须全部实现并通过测试。
P1 级别需求实现率 >= 80%。
P2 级别需求实现率 >= 50%。

### 5.2 质量验收
- myutils 工具库（11个类，1500行代码）编译通过率 100%
- 编译产物无语法错误
- 编译产物可正常执行

### 5.3 测试验收
- 每个新功能对应至少一个单元测试
- 所有测试用例通过
- 无回归问题

---

## 六、交付物

### 6.1 代码交付物
1. 增强后的 parse-ets.js
2. 增强后的 TypeScriptScriptParser.java
3. 新增的 AST 节点类（如需要）
4. 单元测试代码

### 6.2 文档交付物
1. 功能实现清单
2. 测试报告
3. 已知问题列表
4. 使用指南更新（如需要）

---

## 七、进度要求

### 7.1 里程碑
- **M1（P0完成）：** 所有 P0 级别需求实现并测试通过
- **M2（P1完成）：** P1 级别需求实现率 >= 80%
- **M3（最终交付）：** 所有交付物完成，文档齐全

### 7.2 时间估算
- P0 需求：2-3 周
- P1 需求：2-3 周
- P2 需求：1-2 周
- 测试与文档：1 周
- **总计：** 6-9 周

---

## 八、约束与假设

### 8.1 技术约束
- 必须基于现有三层架构实现
- 不引入新的依赖库
- 保持与现有代码风格一致

### 8.2 资源约束
- 开发人员：1-2 人
- 测试人员：1 人
- 项目经理：1 人（兼职）

### 8.3 假设
- TypeScript 版本保持稳定
- 测试用例覆盖主要使用场景
- myutils 工具库代表典型应用场景

---

## 九、风险评估

### 9.1 技术风险
| 风险项 | 影响 | 概率 | 应对措施 |
|-------|------|------|---------|
| TypeScript AST 复杂度超预期 | 中 | 中 | 预留缓冲时间 |
| 隐含语法特性遗漏 | 中 | 中 | 增加测试覆盖 |
| 性能不达标 | 低 | 低 | 必要时优化算法 |

### 9.2 项目风险
| 风险项 | 影响 | 概率 | 应对措施 |
|-------|------|------|---------|
| 测试用例不充分 | 高 | 中 | 优先完善核心用例 |
| 进度延期 | 中 | 中 | 按优先级分批交付 |

---

## 十、附录

### 10.1 术语表
- **ETS**: ArkTS，HarmonyOS 开发语言
- **AST**: 抽象语法树
- **JSDoc**: JavaScript 文档注释格式
- **P0/P1/P2**: 优先级等级

### 10.2 参考文档
- TypeScript 语言规范
- HarmonyOS ETS 开发指南
- JavaScript 语言规范（ECMAScript）

### 10.3 测试用例来源
- myutils 工具库（11个类，1500行）
- 现有单元测试用例
- 新增测试用例

---

**文档版本：** v1.0
**编写日期：** 2026-01-30
**编写人：** Claude AI Assistant
**文档状态：** 待评审
