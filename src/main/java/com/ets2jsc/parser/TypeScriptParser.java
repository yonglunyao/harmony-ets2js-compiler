package com.ets2jsc.parser;

import com.ets2jsc.ast.SourceFile;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * TypeScript/ETS parser using GraalVM JavaScript engine.
 * Uses TypeScript Compiler API to parse source code into AST.
 */
public class TypeScriptParser {

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    private Context context;
    private Value typescript;
    private Value ts;
    private boolean graalvmDisabled = false;

    private final TypeScriptAstConverter astConverter;
    private final SimpleParser simpleParser;

    public TypeScriptParser() {
        // Don't create context in constructor to avoid classpath issues on Windows
        this.context = null;
        this.typescript = null;
        this.ts = null;

        // On Windows, GraalVM has classpath issues, so disable it
        if (IS_WINDOWS) {
            this.graalvmDisabled = true;
        }

        this.astConverter = new TypeScriptAstConverter();
        this.simpleParser = new SimpleParser();
    }

    /**
     * Parses ETS/TypeScript source code into a SourceFile AST node.
     * CC: 4 (early returns + try-catch)
     */
    public SourceFile parse(String fileName, String sourceCode) {
        if (!graalvmDisabled) {
            SourceFile result = tryParseWithTypeScript(fileName, sourceCode);
            if (result != null) {
                return result;
            }
        }

        return simpleParser.parse(fileName, sourceCode);
    }

    /**
     * Tries to parse using TypeScript Compiler API.
     * Returns null if parsing fails.
     * CC: 3 (null check + catch block)
     */
    private SourceFile tryParseWithTypeScript(String fileName, String sourceCode) {
        try {
            initContext();

            if (ts == null || ts.isNull()) {
                return null;
            }

            return parseWithTypeScript(fileName, sourceCode);
        } catch (Throwable t) {
            // GraalVM or TypeScript parsing failed, disable it
            disableGraalVM();
            return null;
        }
    }

    /**
     * Initializes the GraalVM context if needed.
     * CC: 2 (null check + catch block)
     */
    private void initContext() {
        if (context != null || graalvmDisabled) {
            return;
        }

        try {
            createContext();
            loadTypeScript();
        } catch (Throwable e) {
            disableGraalVM();
        }
    }

    /**
     * Creates GraalVM context with JavaScript.
     */
    private void createContext() {
        this.context = Context.newBuilder("js")
                .allowAllAccess(true)
                .option("engine.WarnInterpreterOnly", "false")
                .allowHostClassLoading(false)
                .build();
    }

    /**
     * Loads TypeScript from node_modules or global installation.
     * CC: 2 (null checks)
     */
    private void loadTypeScript() {
        Value typescriptValue = context.eval("js",
            "try {\n" +
            "  require('typescript');\n" +
            "} catch (e) {\n" +
            "  null;\n" +
            "}"
        );

        if (typescriptValue != null && !typescriptValue.isNull()) {
            this.ts = typescriptValue;
        }

        this.typescript = typescriptValue;
    }

    /**
     * Parses using TypeScript Compiler API.
     * CC: 1
     */
    private SourceFile parseWithTypeScript(String fileName, String sourceCode) {
        Value sourceFile = ts.invokeMember("createSourceFile",
            fileName,
            sourceCode,
            ts.getMember("ScriptTarget").getMember("Latest"),
            true
        );

        return astConverter.convertSourceFile(sourceFile);
    }

    /**
     * Disables GraalVM and clears resources.
     * CC: 1
     */
    private void disableGraalVM() {
        this.graalvmDisabled = true;
        this.ts = null;
        this.typescript = null;
        this.context = null;
    }

    /**
     * Closes the parser and releases resources.
     */
    public void close() {
        if (context != null) {
            context.close();
        }
    }
}
