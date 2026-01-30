package com.ets2jsc;

import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.ExportStatement;
import com.ets2jsc.ast.ImportStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 ImportStatement 和 ExportStatement
 */
@DisplayName("导入/导出语句测试")
class ImportExportTest {

    @Test
    @DisplayName("测试默认导入")
    void testDefaultImport() {
        ImportStatement importStmt = new ImportStatement("module");
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
            "defaultName", "defaultName",
            ImportStatement.ImportSpecifier.SpecifierKind.DEFAULT));

        String code = importStmt.toString();

        assertTrue(code.contains("import"));
        assertTrue(code.contains("defaultName"));
        assertTrue(code.contains("from 'module'"));
    }

    @Test
    @DisplayName("测试命名导入")
    void testNamedImport() {
        ImportStatement importStmt = new ImportStatement("library");

        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
            "originalName", "importedName",
            ImportStatement.ImportSpecifier.SpecifierKind.NAMED));

        String code = importStmt.toString();

        assertTrue(code.contains("{ originalName as importedName }"));
    }

    @Test
    @DisplayName("测试多个命名导入")
    void testMultipleNamedImports() {
        ImportStatement importStmt = new ImportStatement("utils");

        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
            "A", "A", ImportStatement.ImportSpecifier.SpecifierKind.NAMED));
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
            "B", "B", ImportStatement.ImportSpecifier.SpecifierKind.NAMED));
        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
            "C", "C", ImportStatement.ImportSpecifier.SpecifierKind.NAMED));

        String code = importStmt.toString();

        assertTrue(code.contains("{ A, B, C }"));
    }

    @Test
    @DisplayName("测试命名空间导入")
    void testNamespaceImport() {
        ImportStatement importStmt = new ImportStatement("library");

        importStmt.addSpecifier(new ImportStatement.ImportSpecifier(
            "*", "Lib",
            ImportStatement.ImportSpecifier.SpecifierKind.NAMESPACE));

        String code = importStmt.toString();

        assertTrue(code.contains("* as Lib"));
    }

    @Test
 @DisplayName("测试导入模块路径")
    void testImportModulePath() {
        ImportStatement import1 = new ImportStatement("./local");
        ImportStatement import2 = new ImportStatement("npm/package");
        ImportStatement import3 = new ImportStatement("@scope/package");

        assertEquals("./local", import1.getModule());
        assertEquals("npm/package", import2.getModule());
        assertEquals("@scope/package", import3.getModule());
    }

    @Test
    @DisplayName("测试导入说明符类型")
    void testImportSpecifierKind() {
        ImportStatement.ImportSpecifier defaultSpec =
            new ImportStatement.ImportSpecifier("x", "x",
                ImportStatement.ImportSpecifier.SpecifierKind.DEFAULT);
        assertTrue(defaultSpec.isDefault());
        assertFalse(defaultSpec.isNamespace());
        assertFalse(defaultSpec.isNamed());  // DEFAULT is not NAMED

        ImportStatement.ImportSpecifier namedSpec =
            new ImportStatement.ImportSpecifier("y", "y",
                ImportStatement.ImportSpecifier.SpecifierKind.NAMED);
        assertFalse(namedSpec.isDefault());
        assertFalse(namedSpec.isNamespace());
        assertTrue(namedSpec.isNamed());

        ImportStatement.ImportSpecifier namespaceSpec =
            new ImportStatement.ImportSpecifier("*", "NS",
                ImportStatement.ImportSpecifier.SpecifierKind.NAMESPACE);
        assertFalse(namespaceSpec.isDefault());
        assertTrue(namespaceSpec.isNamespace());
        assertFalse(namespaceSpec.isNamed());
    }

    @Test
    @DisplayName("测试导出语句类型")
    void testExportStatementType() {
        ClassDeclaration classDecl = new ClassDeclaration("TestClass");
        ExportStatement exportStmt = new ExportStatement(classDecl, false, "class TestClass {}");

        assertEquals("ExportStatement", exportStmt.getType());
    }

    @Test
    @DisplayName("测试导入语句类型")
    void testImportStatementType() {
        ImportStatement importStmt = new ImportStatement("module");

        assertEquals("ImportStatement", importStmt.getType());
    }

    @Test
    @DisplayName("测试导入说明符字段")
    void testImportSpecifierFields() {
        ImportStatement.ImportSpecifier spec =
            new ImportStatement.ImportSpecifier("imported", "local",
                ImportStatement.ImportSpecifier.SpecifierKind.NAMED);

        assertEquals("imported", spec.getImportedName());
        assertEquals("local", spec.getLocalName());
    }
}
