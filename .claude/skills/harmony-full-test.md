# harmony-full-test

ETS 编译器完整测试流程，包括单元测试、语法验证和 es2abc 字节码编译验证。

## 使用场景

当完成 ETS 编译器的修改或新增功能后，运行完整的测试套件验证编译器功能正确性。

## 测试层级

```
┌─────────────────────────────────────────────────────────────┐
│                    完整测试流程                              │
├─────────────────────────────────────────────────────────────┤
│  1. 单元测试 (JUnit)                                        │
│     ↓ 检查编译器核心功能和代码生成逻辑                        │
│  2. 语法验证 (node -c)                                       │
│     ↓ 检查生成的 JavaScript 语法正确性                       │
│  3. es2abc 验证                                             │
│     ↓ 检查 JavaScript 是否符合 HarmonyOS 字节码编译器要求    │
└─────────────────────────────────────────────────────────────┘
```

## 前置条件

1. Java 17+ 和 Maven 3.8+
2. Node.js（用于语法验证）
3. HarmonyOS SDK（包含 es2abc 工具）
   - `D:\env\ark\es2abc.exe`
   - 或 `C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\build-tools\ets-loader\bin\ark\build-win\bin\es2abc.exe`

---

## 第一层：单元测试 (JUnit)

### 运行所有单元测试

```bash
cd "D:/code/ets-har-builder/ets2jsc"
mvn test
```

### 运行特定测试类

```bash
# 测试基础组件
mvn test -Dtest SimpleComponentTest

# 测试状态属性
mvn test -Dtest StatePropertyTest

# 测试 ForEach 列表渲染
mvn test -Dtest ForEachTest

# 测试迭代 4 功能
mvn test -Dtest Iteration4Test
```

### 测试覆盖的功能

| 测试类 | 测试内容 | fixture 文件 |
|--------|----------|-------------|
| SimpleComponentTest | @Component、struct 转换 | simple-component.ets |
| StatePropertyTest | @State 状态装饰器 | state-property.ets |
| ForEachTest | ForEach 列表渲染 | foreach.ets |
| PureJavaScriptTest | 纯 JavaScript 语法 | test-import.ets 等 |
| Iteration4Test | 对象字面量、new 表达式、模板字符串、动态导入等 | test-iteration4.ets |

### 预期结果

所有测试应该通过（BUILD SUCCESS），无测试失败。

---

## 第二层：语法验证 (node -c)

### 验证单个文件

```bash
node -c "path/to/compiled.js"
```

### 批量验证所有编译输出

```bash
cd "D:/code/ets-har-builder/ets2jsc"

# 查找所有 JS 文件并验证语法
for file in src/test/resources/fixtures/**/*.js; do
  node -c "$file" 2>&1 && echo "✓ $(basename $file)" || echo "✗ $(basename $file) - SYNTAX ERROR"
done
```

### 预期结果

所有生成的 JavaScript 文件应通过 `node -c` 语法检查，无语法错误。

---

## 第三层：es2abc 字节码编译验证

### 步骤 1：编译 ETS 源文件

```bash
cd "D:/code/ets-har-builder/ets2jsc"

# 重新编译项目
mvn clean compile -q

# 批量编译所有 ETS 文件
for file in src/test/resources/fixtures/harmony-utils/src/main/ets/**/*.ets; do
  output="${file/src\/test\/resources\/fixtures\/harmony-utils\/src\/main\/ets/src\/test\/resources\/fixtures\/harmony-utils\/compiled}"
  output="${output/.ets/.js}"
  mkdir -p "$(dirname "$output")" 2>/dev/null
  mvn exec:java -Dexec.mainClass="com.ets2jsc.EtsCompiler" \
    -Dexec.args="$file $output" -q 2>&1 | grep -iE "(error|compiled)" || true
done
```

### 步骤 2：运行 es2abc 验证

```bash
cd "D:/code/ets-har-builder/ets2jsc"

# 创建输出目录
mkdir -p src/test/resources/fixtures/harmony-utils/abc-final

# 使用 es2abc 编译所有 JS 文件
for file in src/test/resources/fixtures/harmony-utils/compiled/**/*.js; do
  name=$(basename "$file" .js)
  output="src/test/resources/fixtures/harmony-utils/abc-final/$name.abc"
  "D:\env\ark\es2abc.exe" "$file" --output "$output" --module 2>&1 && \
    echo "✓ $name" || \
    echo "✗ $name - FAILED"
done
```

### 结果分析

| 结果 | 含义 | 可接受性 |
|------|------|----------|
| ✓ 成功 | JS 文件完全符合 es2abc 要求 | ✅ 优秀 |
| ⚠️ 警告 | 有 "Unimplemented code path" 但生成了 ABC | ⚠️ 可接受（页面文件） |
| ✗ 失败 | 语法错误或其他编译错误 | ❌ 不可接受 |

### 预期结果

- 非页面文件（服务类、工具类、Ability）：应完全通过 ✓
- 页面文件（包含复杂 UI）：可能有警告 ⚠️，但不应有语法错误 ✗

---

## 完整测试一键执行

### 运行完整测试套件

```bash
cd "D:/code/ets-har-builder/ets2jsc"

echo "==================================="
echo "   ETS 编译器完整测试流程"
echo "==================================="
echo ""

# 第一层：单元测试
echo "[1/3] 运行单元测试..."
mvn test -q
if [ $? -eq 0 ]; then
  echo "✓ 单元测试通过"
else
  echo "✗ 单元测试失败"
  exit 1
fi
echo ""

# 第二层：语法验证
echo "[2/3] 验证 JavaScript 语法..."
syntax_errors=0
for file in src/test/resources/fixtures/harmony-utils/compiled/**/*.js; do
  node -c "$file" 2>&1 >/dev/null || syntax_errors=$((syntax_errors + 1))
done
if [ $syntax_errors -eq 0 ]; then
  echo "✓ 所有文件语法正确"
else
  echo "✗ $syntax_errors 个文件有语法错误"
  exit 1
fi
echo ""

# 第三层：es2abc 验证
echo "[3/3] 验证 es2abc 编译..."
success=0
warnings=0
failures=0

for file in src/test/resources/fixtures/harmony-utils/compiled/**/*.js; do
  name=$(basename "$file" .js)
  output="src/test/resources/fixtures/harmony-utils/abc-final/$name.abc"

  result=$("D:\env\ark\es2abc.exe" "$file" --output "$output" --module 2>&1)

  if [ $? -eq 0 ]; then
    success=$((success + 1))
    echo "  ✓ $name"
  elif echo "$result" | grep -q "Unimplemented code path"; then
    warnings=$((warnings + 1))
    echo "  ⚠️  $name (有警告)"
  else
    failures=$((failures + 1))
    echo "  ✗ $name - 失败"
    echo "$result"
  fi
done

echo ""
echo "==================================="
echo "   测试结果汇总"
echo "==================================="
echo "✓ 成功: $success"
echo "⚠️  警告: $warnings"
echo "✗ 失败: $failures"
echo ""

if [ $failures -gt 0 ]; then
  echo "❌ 测试失败！请检查错误信息。"
  exit 1
else
  echo "✅ 测试完成！"
fi
```

---

## 常见问题诊断

### 问题 1：NewExpression 序列化为 JSON

**错误示例**：
```javascript
const iv = {"kind":215,"kindName":"NewExpression",...};
```

**正确输出**：
```javascript
const iv = new Uint8Array(16);
```

**诊断**：检查 `parse-ets.js` 中 `jsonToCodeString` 函数是否有 `NewExpression` 的处理分支：
```javascript
case 'NewExpression': {
    const expr = json.expression;
    const args = json.arguments || [];
    const argStr = args.map(arg => jsonToCodeString(arg)).join(', ');
    return 'new ' + jsonToCodeString(expr) + '(' + argStr + ')';
}
```

### 问题 2：对象字面量属性值缺失

**错误示例**：
```javascript
const ivParamsSpec = {algName: , iv: };
```

**正确输出**：
```javascript
const ivParamsSpec = {algName: "IvParamsSpec", iv: {data: iv}};
```

**诊断**：检查 `jsonToCodeString` 中 `PropertyAssignment` 是否使用了正确的字段名 `json.value` 而不是 `json.initializer`。

### 问题 3：字符串引号重复

**错误示例**：
```javascript
TextInput({placeholder: ""搜索关键词...""})
```

**正确输出**：
```javascript
TextInput({placeholder: "搜索关键词..."})
```

**诊断**：
1. `parse-ets.js` 中 StringLiteral 应添加引号：`result.text = '"' + node.text + '"'`
2. Java 代码 (`TypeScriptScriptParser.java`) 中应检查是否已有引号：
```java
case "StringLiteral":
    String strText = exprJson.has("text") ? exprJson.get("text").getAsString() : "";
    if (strText.startsWith("\"") || strText.startsWith("'")) {
        return strText;
    }
    return "\"" + strText + "\"";
```

### 问题 4：字符串被误认为正则表达式

**错误示例**：
```javascript
const currentPath__ = /data/storage/el2/base/haps/entry/files;
```

**正确输出**：
```javascript
const currentPath__ = "/data/storage/el2/base/haps/entry/files";
```

**诊断**：StringLiteral 的 `text` 字段必须包含引号。

### 问题 5：函数调用中的对象字面量被序列化为字符串

**错误示例**：
```javascript
convertKey("{data: keyData}")
```

**正确输出**：
```javascript
convertKey({data: keyData})
```

**诊断**：检查 `parse-ets.js` 中 CallExpression 参数处理，对 ObjectLiteralExpression 应存储 JSON 对象而不是字符串：
```javascript
if (argJson && argJson.kindName &&
    (argJson.kindName === 'ObjectLiteralExpression' ||
     argJson.kindName === 'ArrayLiteralExpression' ||
     argJson.kindName === 'ArrowFunction')) {
    result.arguments.push(argJson);
}
```

---

## 测试文件位置

| 类型 | 路径 |
|------|------|
| 测试源码 | `src/test/java/com/ets2jsc/` |
| ETS fixtures | `src/test/resources/fixtures/**/*.ets` |
| 编译输出 | `src/test/resources/fixtures/harmony-utils/compiled/` |
| ABC 输出 | `src/test/resources/fixtures/harmony-utils/abc-final/` |

## 相关命令

| 命令 | 说明 |
|------|------|
| `mvn test` | 运行所有单元测试 |
| `mvn test -Dtest <TestName>` | 运行特定测试类 |
| `node -c <file.js>` | 验证 JavaScript 语法 |
| `es2abc <input.js> --output <output.abc> --module` | 编译为 ABC 字节码 |
