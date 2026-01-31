package com.ets2jsc.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ExpressionStatement ExpressionStatement
 */
@DisplayName("ExpressionStatementTest")
class ExpressionStatementTest {

    @Test
    @DisplayName("Test基本ExpressionStatement")
    void testBasicExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement("x + y");

        assertEquals("x + y", stmt.getExpression());
        assertEquals("ExpressionStatement", stmt.getType());
    }

    @Test
    @DisplayName("Test带分号 ofExpressionStatement")
    void testExpressionWithSemicolon() {
        ExpressionStatement stmt = new ExpressionStatement("console.log('test');");

        assertEquals("console.log('test');", stmt.getExpression());
    }

    @Test
    @DisplayName("TestEmptyExpressionStatement")
    void testEmptyExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement("");

        assertEquals("", stmt.getExpression());
    }

    @Test
    @DisplayName("Test null ExpressionStatement")
    void testNullExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement(null);

        assertNull(stmt.getExpression());
    }

    @Test
    @DisplayName("TestMethodCallExpression")
    void testMethodCallExpression() {
        ExpressionStatement stmt = new ExpressionStatement("obj.method(param1, param2)");

        String expr = stmt.getExpression();

        assertTrue(expr.contains("obj.method("));
        assertTrue(expr.contains("param1"));
        assertTrue(expr.contains("param2"));
    }

    @Test
    @DisplayName("Test赋值Expression")
    void testAssignmentExpression() {
        ExpressionStatement stmt = new ExpressionStatement("x = 42");

        assertEquals("x = 42", stmt.getExpression());
    }

    @Test
    @DisplayName("Test复合赋值Expression")
    void testCompoundAssignmentExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("x += 5");
        ExpressionStatement stmt2 = new ExpressionStatement("x -= 3");
        ExpressionStatement stmt3 = new ExpressionStatement("x *= 2");

        assertEquals("x += 5", stmt1.getExpression());
        assertEquals("x -= 3", stmt2.getExpression());
        assertEquals("x *= 2", stmt3.getExpression());
    }

    @Test
    @DisplayName("Test一元Expression")
    void testUnaryExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("++x");
        ExpressionStatement stmt2 = new ExpressionStatement("--y");
        ExpressionStatement stmt3 = new ExpressionStatement("!flag");
        ExpressionStatement stmt4 = new ExpressionStatement("typeof x");

        assertEquals("++x", stmt1.getExpression());
        assertEquals("--y", stmt2.getExpression());
        assertEquals("!flag", stmt3.getExpression());
        assertEquals("typeof x", stmt4.getExpression());
    }

    @Test
    @DisplayName("Test二元Expression")
    void testBinaryExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("a + b");
        ExpressionStatement stmt2 = new ExpressionStatement("x > y");
        ExpressionStatement stmt3 = new ExpressionStatement("obj === null");
        ExpressionStatement stmt4 = new ExpressionStatement("str && str2");

        assertEquals("a + b", stmt1.getExpression());
        assertEquals("x > y", stmt2.getExpression());
        assertEquals("obj === null", stmt3.getExpression());
        assertEquals("str && str2", stmt4.getExpression());
    }

    @Test
    @DisplayName("Test三元Expression")
    void testTernaryExpression() {
        ExpressionStatement stmt = new ExpressionStatement("condition ? value1 : value2");

        assertEquals("condition ? value1 : value2", stmt.getExpression());
    }

    @Test
    @DisplayName("TestObjectMethodCallExpression")
    void testObjectMethodCallExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("this.myMethod()");
        ExpressionStatement stmt2 = new ExpressionStatement("obj.prop.method()");

        assertEquals("this.myMethod()", stmt1.getExpression());
        assertEquals("obj.prop.method()", stmt2.getExpression());
    }

    @Test
    @DisplayName("Test成员访问Expression")
    void testMemberAccessExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("obj.property");
        ExpressionStatement stmt2 = new ExpressionStatement("this.value");
        ExpressionStatement stmt3 = new ExpressionStatement("module.submodule.prop");

        assertEquals("obj.property", stmt1.getExpression());
        assertEquals("this.value", stmt2.getExpression());
        assertEquals("module.submodule.prop", stmt3.getExpression());
    }

    @Test
    @DisplayName("TestArray访问Expression")
    void testArrayAccessExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("arr[0]");
        ExpressionStatement stmt2 = new ExpressionStatement("items[index]");
        ExpressionStatement stmt3 = new ExpressionStatement("matrix[i][j]");

        assertEquals("arr[0]", stmt1.getExpression());
        assertEquals("items[index]", stmt2.getExpression());
        assertEquals("matrix[i][j]", stmt3.getExpression());
    }

    @Test
    @DisplayName("Test new Expression")
    void testNewExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("new Object()");
        ExpressionStatement stmt2 = new ExpressionStatement("new Array(10)");
        ExpressionStatement stmt3 = new ExpressionStatement("new MyClass()");

        assertEquals("new Object()", stmt1.getExpression());
        assertEquals("new Array(10)", stmt2.getExpression());
        assertEquals("new MyClass()", stmt3.getExpression());
    }

    @Test
    @DisplayName("Test模板字符串Expression")
    void testTemplateStringExpression() {
        ExpressionStatement stmt = new ExpressionStatement("`Hello ${name}!`");

        assertEquals("`Hello ${name}!`", stmt.getExpression());
    }

    @Test
    @DisplayName("TestArray字面量Expression")
    void testArrayLiteralExpression() {
        ExpressionStatement stmt = new ExpressionStatement("[1, 2, 3]");

        assertEquals("[1, 2, 3]", stmt.getExpression());
    }

    @Test
    @DisplayName("TestObject字面量Expression")
    void testObjectLiteralExpression() {
        ExpressionStatement stmt = new ExpressionStatement("{ key: 'value', num: 42 }");

        assertEquals("{ key: 'value', num: 42 }", stmt.getExpression());
    }

    @Test
    @DisplayName("Test箭头FunctionExpression")
    void testArrowFunctionExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("() => {}");
        ExpressionStatement stmt2 = new ExpressionStatement("(x) => x * 2");
        ExpressionStatement stmt3 = new ExpressionStatement("(x, y) => { return x + y; }");

        assertEquals("() => {}", stmt1.getExpression());
        assertEquals("(x) => x * 2", stmt2.getExpression());
        assertEquals("(x, y) => { return x + y; }", stmt3.getExpression());
    }

    @Test
    @DisplayName("Test async FunctionCallExpression")
    void testAsyncFunctionCallExpression() {
        ExpressionStatement stmt = new ExpressionStatement("await myAsyncFunction()");

        assertEquals("await myAsyncFunction()", stmt.getExpression());
    }

    @Test
    @DisplayName("Test return Statement")
    void testReturnStatement() {
        ExpressionStatement stmt1 = new ExpressionStatement("return");
        ExpressionStatement stmt2 = new ExpressionStatement("return value");
        ExpressionStatement stmt3 = new ExpressionStatement("return x + y");

        assertEquals("return", stmt1.getExpression());
        assertEquals("return value", stmt2.getExpression());
        assertEquals("return x + y", stmt3.getExpression());
    }

    @Test
    @DisplayName("Test break 和 continue Statement")
    void testBreakContinueStatements() {
        ExpressionStatement breakStmt = new ExpressionStatement("break");
        ExpressionStatement continueStmt = new ExpressionStatement("continue");

        assertEquals("break", breakStmt.getExpression());
        assertEquals("continue", continueStmt.getExpression());
    }

    @Test
    @DisplayName("Test变量DeclarationExpression")
    void testVariableDeclarationExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("const x = 1");
        ExpressionStatement stmt2 = new ExpressionStatement("let y = 2");
        ExpressionStatement stmt3 = new ExpressionStatement("var z = 3");

        assertEquals("const x = 1", stmt1.getExpression());
        assertEquals("let y = 2", stmt2.getExpression());
        assertEquals("var z = 3", stmt3.getExpression());
    }

    @Test
    @DisplayName("Test复杂NestedExpression")
    void testComplexNestedExpression() {
        ExpressionStatement stmt = new ExpressionStatement(
            "obj.items.find(item => item.id === target)?.name"
        );

        assertEquals("obj.items.find(item => item.id === target)?.name", stmt.getExpression());
    }

    @Test
    @DisplayName("Test可选链Expression")
    void testOptionalChainingExpression() {
        ExpressionStatement stmt1 = new ExpressionStatement("obj?.prop");
        ExpressionStatement stmt2 = new ExpressionStatement("obj?.method?.()");
        ExpressionStatement stmt3 = new ExpressionStatement("arr?.[0]");

        assertEquals("obj?.prop", stmt1.getExpression());
        assertEquals("obj?.method?.()", stmt2.getExpression());
        assertEquals("arr?.[0]", stmt3.getExpression());
    }

    @Test
    @DisplayName("TestEmpty值合并Expression")
    void testNullishCoalescingExpression() {
        ExpressionStatement stmt = new ExpressionStatement("value ?? defaultValue");

        assertEquals("value ?? defaultValue", stmt.getExpression());
    }
}
