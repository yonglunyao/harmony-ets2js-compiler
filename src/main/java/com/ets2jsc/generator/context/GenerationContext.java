package com.ets2jsc.generator.context;

import com.ets2jsc.config.CompilerConfig;
import com.ets2jsc.generator.IndentationManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Context for code generation operations.
 * <p>
 * Provides shared state for code generation, including indentation,
 * compilation context tracking, and helper utilities.
 * </p>
 *
 * @since 1.0
 */
public class GenerationContext {

    private final CompilerConfig config;
    private final IndentationManager indentation;
    private final StringBuilder output;
    private final Deque<Boolean> insideComponentStack;
    private final Set<String> builderMethods;
    private final Set<String> importedModules;

    private GenerationContext(CompilerConfig config) {
        this.config = config != null ? config : new CompilerConfig();
        this.indentation = new IndentationManager();
        this.output = new StringBuilder();
        this.insideComponentStack = new ArrayDeque<>();
        this.builderMethods = new HashSet<>();
        this.importedModules = new HashSet<>();
    }

    /**
     * Creates a new generation context.
     *
     * @param config the compiler configuration
     * @return a new generation context
     */
    public static GenerationContext create(CompilerConfig config) {
        return new GenerationContext(config);
    }

    /**
     * Creates a new generation context with default configuration.
     *
     * @return a new generation context
     */
    public static GenerationContext create() {
        return new GenerationContext(null);
    }

    /**
     * Gets the compiler configuration.
     *
     * @return the compiler configuration
     */
    public CompilerConfig getConfig() {
        return config;
    }

    /**
     * Gets the indentation manager.
     *
     * @return the indentation manager
     */
    public IndentationManager getIndentation() {
        return indentation;
    }

    /**
     * Gets the output string builder.
     *
     * @return the output builder
     */
    public StringBuilder getOutput() {
        return output;
    }

    /**
     * Checks if currently inside a component class.
     *
     * @return true if inside a component class
     */
    public boolean isInsideComponentClass() {
        return !insideComponentStack.isEmpty() && insideComponentStack.peek();
    }

    /**
     * Pushes a new component class context.
     *
     * @param insideComponent whether inside a component class
     */
    public void pushComponentContext(boolean insideComponent) {
        insideComponentStack.push(insideComponent);
    }

    /**
     * Pops the current component class context.
     *
     * @return the previous inside component value
     */
    public boolean popComponentContext() {
        return insideComponentStack.isEmpty() ? false : insideComponentStack.pop();
    }

    /**
     * Adds a builder method name.
     *
     * @param methodName the builder method name
     */
    public void addBuilderMethod(String methodName) {
        builderMethods.add(methodName);
    }

    /**
     * Checks if a method is a builder method.
     *
     * @param methodName the method name to check
     * @return true if the method is a builder method
     */
    public boolean isBuilderMethod(String methodName) {
        return builderMethods.contains(methodName);
    }

    /**
     * Clears all builder methods.
     */
    public void clearBuilderMethods() {
        builderMethods.clear();
    }

    /**
     * Adds an imported module.
     *
     * @param moduleName the module name
     */
    public void addImportedModule(String moduleName) {
        importedModules.add(moduleName);
    }

    /**
     * Checks if a module has been imported.
     *
     * @param moduleName the module name to check
     * @return true if the module has been imported
     */
    public boolean isImportedModule(String moduleName) {
        return importedModules.contains(moduleName);
    }

    /**
     * Clears all imported modules.
     */
    public void clearImportedModules() {
        importedModules.clear();
    }

    /**
     * Resets the context for reuse.
     */
    public void reset() {
        output.setLength(0);
        indentation.reset();
        insideComponentStack.clear();
        builderMethods.clear();
        importedModules.clear();
    }
}
