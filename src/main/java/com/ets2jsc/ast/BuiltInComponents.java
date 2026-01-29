package com.ets2jsc.ast;

import java.util.HashSet;
import java.util.Set;

/**
 * Registry of built-in HarmonyOS components.
 */
public class BuiltInComponents {
    private static final Set<String> CONTAINER_COMPONENTS = new HashSet<>();
    private static final Set<String> ATOMIC_COMPONENTS = new HashSet<>();
    private static final Set<String> ALL_COMPONENTS = new HashSet<>();

    static {
        // Container components
        CONTAINER_COMPONENTS.add("Column");
        CONTAINER_COMPONENTS.add("Row");
        CONTAINER_COMPONENTS.add("Stack");
        CONTAINER_COMPONENTS.add("Flex");
        CONTAINER_COMPONENTS.add("Grid");
        CONTAINER_COMPONENTS.add("List");
        CONTAINER_COMPONENTS.add("Scroll");
        CONTAINER_COMPONENTS.add("Swiper");
        CONTAINER_COMPONENTS.add("Tabs");
        CONTAINER_COMPONENTS.add("Navigator");
        CONTAINER_COMPONENTS.add("ForEach");
        CONTAINER_COMPONENTS.add("LazyForEach");
        CONTAINER_COMPONENTS.add("If");

        // Atomic components (leaf nodes)
        ATOMIC_COMPONENTS.add("Text");
        ATOMIC_COMPONENTS.add("Image");
        ATOMIC_COMPONENTS.add("TextInput");
        ATOMIC_COMPONENTS.add("TextArea");
        ATOMIC_COMPONENTS.add("Button");
        ATOMIC_COMPONENTS.add("Toggle");
        ATOMIC_COMPONENTS.add("CheckBox");
        ATOMIC_COMPONENTS.add("Radio");
        ATOMIC_COMPONENTS.add("Slider");
        ATOMIC_COMPONENTS.add("Progress");
        ATOMIC_COMPONENTS.add("Divider");
        ATOMIC_COMPONENTS.add("Blank");
        ATOMIC_COMPONENTS.add("Span");

        ALL_COMPONENTS.addAll(CONTAINER_COMPONENTS);
        ALL_COMPONENTS.addAll(ATOMIC_COMPONENTS);
    }

    /**
     * Returns true if the component name is a built-in component.
     */
    public static boolean isBuiltin(String componentName) {
        return ALL_COMPONENTS.contains(componentName);
    }

    /**
     * Returns true if the component is a container (can have children).
     */
    public static boolean isContainer(String componentName) {
        return CONTAINER_COMPONENTS.contains(componentName);
    }

    /**
     * Returns true if the component is atomic (leaf node).
     */
    public static boolean isAtomic(String componentName) {
        return ATOMIC_COMPONENTS.contains(componentName);
    }

    /**
     * Returns all built-in component names.
     */
    public static Set<String> getAllComponents() {
        return new HashSet<>(ALL_COMPONENTS);
    }
}
