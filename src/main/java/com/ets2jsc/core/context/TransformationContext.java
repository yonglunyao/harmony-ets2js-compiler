package com.ets2jsc.core.context;

import com.ets2jsc.ast.AstNode;
import com.ets2jsc.ast.ClassDeclaration;
import com.ets2jsc.ast.MethodDeclaration;

import java.util.HashMap;
import java.util.Map;

/**
 * Context object for AST transformation.
 * Holds state and utilities used during the transformation phase.
 * <p>
 * This context is created for each transformation pass and maintains
 * state specific to that transformation (e.g., current class, current method).
 */
public class TransformationContext {

    private final CompilationContext compilationContext;
    private final Map<String, Object> attributes;
    private final Map<String, Integer> counters;
    private ClassDeclaration currentClass;
    private MethodDeclaration currentMethod;
    private int insideComponentClassDepth;

    /**
     * Creates a new transformation context.
     *
     * @param compilationContext the parent compilation context
     */
    public TransformationContext(CompilationContext compilationContext) {
        this.compilationContext = compilationContext;
        this.attributes = new HashMap<>();
        this.counters = new HashMap<>();
        this.insideComponentClassDepth = 0;
    }

    /**
     * Gets the parent compilation context.
     *
     * @return the compilation context
     */
    public CompilationContext getCompilationContext() {
        return compilationContext;
    }

    /**
     * Gets the compiler configuration.
     *
     * @return the compiler configuration
     */
    public com.ets2jsc.config.CompilerConfig getConfig() {
        return compilationContext.getConfig();
    }

    /**
     * Checks if pure JavaScript mode is enabled.
     *
     * @return true if pure JavaScript mode is enabled
     */
    public boolean isPureJavaScript() {
        return compilationContext.isPureJavaScript();
    }

    /**
     * Checks if partial update mode is enabled.
     *
     * @return true if partial update mode is enabled
     */
    public boolean isPartialUpdateMode() {
        return compilationContext.isPartialUpdateMode();
    }

    /**
     * Gets the currently being transformed class.
     *
     * @return the current class, or null if not inside a class
     */
    public ClassDeclaration getCurrentClass() {
        return currentClass;
    }

    /**
     * Sets the currently being transformed class.
     *
     * @param currentClass the current class
     */
    public void setCurrentClass(ClassDeclaration currentClass) {
        this.currentClass = currentClass;
    }

    /**
     * Checks if we are currently inside a class transformation.
     *
     * @return true if inside a class transformation
     */
    public boolean isInsideClass() {
        return currentClass != null;
    }

    /**
     * Gets the currently being transformed method.
     *
     * @return the current method, or null if not inside a method
     */
    public MethodDeclaration getCurrentMethod() {
        return currentMethod;
    }

    /**
     * Sets the currently being transformed method.
     *
     * @param currentMethod the current method
     */
    public void setCurrentMethod(MethodDeclaration currentMethod) {
        this.currentMethod = currentMethod;
    }

    /**
     * Checks if we are currently inside a method transformation.
     *
     * @return true if inside a method transformation
     */
    public boolean isInsideMethod() {
        return currentMethod != null;
    }

    /**
     * Checks if we are currently inside a component class.
     *
     * @return true if inside a component class
     */
    public boolean isInsideComponentClass() {
        return insideComponentClassDepth > 0;
    }

    /**
     * Enters a component class context.
     */
    public void enterComponentClass() {
        insideComponentClassDepth++;
    }

    /**
     * Exits a component class context.
     */
    public void exitComponentClass() {
        if (insideComponentClassDepth > 0) {
            insideComponentClassDepth--;
        }
    }

    /**
     * Sets an attribute value in the context.
     *
     * @param key the attribute key
     * @param value the attribute value
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Gets an attribute value from the context.
     *
     * @param key the attribute key
     * @return the attribute value, or null if not found
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Gets an attribute value from the context with a default value.
     *
     * @param key the attribute key
     * @param defaultValue the default value
     * @return the attribute value, or the default if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        Object value = attributes.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    /**
     * Checks if an attribute exists in the context.
     *
     * @param key the attribute key
     * @return true if the attribute exists
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * Removes an attribute from the context.
     *
     * @param key the attribute key
     * @return the removed value, or null if not found
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    /**
     * Gets or creates a counter with the given name.
     *
     * @param name the counter name
     * @return the current counter value
     */
    public int getCounter(String name) {
        return counters.getOrDefault(name, 0);
    }

    /**
     * Increments a counter and returns the new value.
     *
     * @param name the counter name
     * @return the incremented counter value
     */
    public int incrementCounter(String name) {
        int value = counters.getOrDefault(name, 0) + 1;
        counters.put(name, value);
        return value;
    }

    /**
     * Resets a counter to zero.
     *
     * @param name the counter name
     */
    public void resetCounter(String name) {
        counters.put(name, 0);
    }

    /**
     * Clears all counters.
     */
    public void clearCounters() {
        counters.clear();
    }

    /**
     * Generates a unique identifier for temporary variables.
     *
     * @param prefix the prefix for the identifier
     * @return a unique identifier
     */
    public String generateUniqueId(String prefix) {
        int count = incrementCounter(prefix);
        return "__" + prefix + "_" + count + "__";
    }

    /**
     * Pushes a node onto the transformation stack.
     *
     * @param node the AST node
     */
    public void pushNode(AstNode node) {
        if (node instanceof ClassDeclaration) {
            setCurrentClass((ClassDeclaration) node);
        } else if (node instanceof MethodDeclaration) {
            setCurrentMethod((MethodDeclaration) node);
        }
    }

    /**
     * Pops a node from the transformation stack.
     *
     * @param node the AST node
     */
    public void popNode(AstNode node) {
        if (node instanceof ClassDeclaration && currentClass == node) {
            setCurrentClass(null);
        } else if (node instanceof MethodDeclaration && currentMethod == node) {
            setCurrentMethod(null);
        }
    }

    /**
     * Clears all attributes.
     */
    public void clearAttributes() {
        attributes.clear();
    }

    @Override
    public String toString() {
        return "TransformationContext{" +
                "currentClass=" + (currentClass != null ? currentClass.getName() : "null") +
                ", currentMethod=" + (currentMethod != null ? currentMethod.getName() : "null") +
                ", insideComponentClass=" + insideComponentClassDepth +
                '}';
    }
}
