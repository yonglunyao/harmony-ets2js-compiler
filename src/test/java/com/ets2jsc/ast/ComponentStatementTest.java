package com.ets2jsc.ast;

import com.ets2jsc.ast.ComponentStatement.ComponentPart;
import com.ets2jsc.ast.ComponentStatement.PartKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ComponentStatement ComponentStatement
 */
@DisplayName("Component StatementTest")
class ComponentStatementTest {

    @Test
    @DisplayName("Test Component StatementCreation")
    void testComponentStatementCreation() {
        ComponentStatement stmt = new ComponentStatement("Text");

        assertEquals("ComponentStatement", stmt.getType());
        assertEquals("Text", stmt.getComponentName());
        assertNotNull(stmt.getParts());
        assertTrue(stmt.getParts().isEmpty());
    }

    @Test
    @DisplayName("TestGetComponentName")
    void testGetComponentName() {
        ComponentStatement stmt1 = new ComponentStatement("Text");
        ComponentStatement stmt2 = new ComponentStatement("Column");
        ComponentStatement stmt3 = new ComponentStatement("Row");

        assertEquals("Text", stmt1.getComponentName());
        assertEquals("Column", stmt2.getComponentName());
        assertEquals("Row", stmt3.getComponentName());
    }

    @Test
    @DisplayName("TestAddComponent部分")
    void testAddPart() {
        ComponentStatement stmt = new ComponentStatement("Text");

        stmt.addPart(new ComponentPart(PartKind.CREATE, "'Hello'"));
        stmt.addPart(new ComponentPart(PartKind.METHOD, "fontSize(16)"));
        stmt.addPart(new ComponentPart(PartKind.POP, ""));

        assertEquals(3, stmt.getParts().size());
    }

    @Test
    @DisplayName("TestGetComponent部分")
    void testGetParts() {
        ComponentStatement stmt = new ComponentStatement("Text");

        ComponentPart part1 = new ComponentPart(PartKind.CREATE, "'World'");
        ComponentPart part2 = new ComponentPart(PartKind.POP, "");
        stmt.addPart(part1);
        stmt.addPart(part2);

        assertEquals(2, stmt.getParts().size());
        assertEquals(part1, stmt.getParts().get(0));
        assertEquals(part2, stmt.getParts().get(1));
    }

    @Test
    @DisplayName("Test子节点初始is null")
    void testChildrenInitiallyNull() {
        ComponentStatement stmt = new ComponentStatement("Column");

        assertFalse(stmt.hasChildren());
        assertNull(stmt.getChildren());
    }

    @Test
    @DisplayName("TestSet子节点")
    void testSetChildren() {
        ComponentStatement stmt = new ComponentStatement("Column");
        Block children = new Block();
        children.addStatement(new ExpressionStatement("Text.create('Child');"));

        stmt.setChildren(children);

        assertTrue(stmt.hasChildren());
        assertNotNull(stmt.getChildren());
        assertEquals(1, stmt.getChildren().getStatements().size());
    }

    @Test
    @DisplayName("Test有子节点")
    void testHasChildren() {
        ComponentStatement stmt = new ComponentStatement("Column");
        Block children = new Block();
        children.addStatement(new ExpressionStatement("Text.create('Child');"));

        stmt.setChildren(children);

        assertTrue(stmt.hasChildren());
    }

    @Test
    @DisplayName("Test没有子节点")
    void testNoChildren() {
        ComponentStatement stmt = new ComponentStatement("Text");

        assertFalse(stmt.hasChildren());
    }

    @Test
    @DisplayName("Test ComponentPart - CREATE Class型")
    void testComponentPartCreate() {
        ComponentPart part = new ComponentPart(PartKind.CREATE, "'Hello'");

        assertEquals(PartKind.CREATE, part.kind());
        assertEquals("'Hello'", part.code());
    }

    @Test
    @DisplayName("Test ComponentPart - METHOD Class型")
    void testComponentPartMethod() {
        ComponentPart part = new ComponentPart(PartKind.METHOD, "fontSize(16)");

        assertEquals(PartKind.METHOD, part.kind());
        assertEquals("fontSize(16)", part.code());
    }

    @Test
    @DisplayName("Test ComponentPart - POP Class型")
    void testComponentPartPop() {
        ComponentPart part = new ComponentPart(PartKind.POP, "");

        assertEquals(PartKind.POP, part.kind());
        assertEquals("", part.code());
    }

    @Test
    @DisplayName("TestMultiple METHOD 部分")
    void testMultipleMethodParts() {
        ComponentStatement stmt = new ComponentStatement("Text");

        stmt.addPart(new ComponentPart(PartKind.CREATE, "'Test'"));
        stmt.addPart(new ComponentPart(PartKind.METHOD, "fontSize(16)"));
        stmt.addPart(new ComponentPart(PartKind.METHOD, "fontColor(Color.Red)"));
        stmt.addPart(new ComponentPart(PartKind.METHOD, "fontWeight(FontWeight.Bold)"));
        stmt.addPart(new ComponentPart(PartKind.POP, ""));

        assertEquals(5, stmt.getParts().size());
        assertEquals(PartKind.METHOD, stmt.getParts().get(1).kind());
        assertEquals(PartKind.METHOD, stmt.getParts().get(2).kind());
        assertEquals(PartKind.METHOD, stmt.getParts().get(3).kind());
    }

    @Test
    @DisplayName("TestNestedComponent（子节点是 Block）")
    void testNestedComponents() {
        ComponentStatement parent = new ComponentStatement("Column");

        Block children = new Block();
        ComponentStatement child1 = new ComponentStatement("Text");
        child1.addPart(new ComponentPart(PartKind.CREATE, "'Child 1'"));
        child1.addPart(new ComponentPart(PartKind.POP, ""));

        ComponentStatement child2 = new ComponentStatement("Text");
        child2.addPart(new ComponentPart(PartKind.CREATE, "'Child 2'"));
        child2.addPart(new ComponentPart(PartKind.POP, ""));

        children.addStatement(child1);
        children.addStatement(child2);

        parent.setChildren(children);

        assertTrue(parent.hasChildren());
        assertEquals(2, parent.getChildren().getStatements().size());
    }

    @Test
    @DisplayName("Test accept Method")
    void testAcceptMethod() {
        ComponentStatement stmt = new ComponentStatement("Text");

        assertNotNull(stmt);
        assertEquals("ComponentStatement", stmt.getType());
    }

    @Test
    @DisplayName("TestdifferentComponentName")
    void testDifferentComponentNames() {
        ComponentStatement text = new ComponentStatement("Text");
        ComponentStatement button = new ComponentStatement("Button");
        ComponentStatement image = new ComponentStatement("Image");
        ComponentStatement column = new ComponentStatement("Column");
        ComponentStatement row = new ComponentStatement("Row");
        ComponentStatement stack = new ComponentStatement("Stack");
        ComponentStatement grid = new ComponentStatement("Grid");
        ComponentStatement list = new ComponentStatement("List");

        assertEquals("Text", text.getComponentName());
        assertEquals("Button", button.getComponentName());
        assertEquals("Image", image.getComponentName());
        assertEquals("Column", column.getComponentName());
        assertEquals("Row", row.getComponentName());
        assertEquals("Stack", stack.getComponentName());
        assertEquals("Grid", grid.getComponentName());
        assertEquals("List", list.getComponentName());
    }

    @Test
    @DisplayName("TestComponent部分代码内容")
    void testComponentPartCode() {
        ComponentStatement stmt = new ComponentStatement("Text");

        stmt.addPart(new ComponentPart(PartKind.CREATE, "'Hello World'"));
        stmt.addPart(new ComponentPart(PartKind.METHOD, "width('100%')"));
        stmt.addPart(new ComponentPart(PartKind.METHOD, "height(50)"));
        stmt.addPart(new ComponentPart(PartKind.METHOD, "textAlign(TextAlign.Center)"));
        stmt.addPart(new ComponentPart(PartKind.POP, ""));

        assertEquals("'Hello World'", stmt.getParts().get(0).code());
        assertEquals("width('100%')", stmt.getParts().get(1).code());
        assertEquals("height(50)", stmt.getParts().get(2).code());
        assertEquals("textAlign(TextAlign.Center)", stmt.getParts().get(3).code());
        assertEquals("", stmt.getParts().get(4).code());
    }

    @Test
    @DisplayName("TestEmpty子Block")
    void testEmptyChildrenBlock() {
        ComponentStatement stmt = new ComponentStatement("Column");
        Block emptyChildren = new Block();

        stmt.setChildren(emptyChildren);

        assertTrue(stmt.hasChildren());
        assertTrue(stmt.getChildren().getStatements().isEmpty());
    }

    @Test
    @DisplayName("Test PartKind 枚举值")
    void testPartKindEnum() {
        assertEquals(PartKind.CREATE, PartKind.valueOf("CREATE"));
        assertEquals(PartKind.METHOD, PartKind.valueOf("METHOD"));
        assertEquals(PartKind.POP, PartKind.valueOf("POP"));

        assertEquals(3, PartKind.values().length);
    }
}
