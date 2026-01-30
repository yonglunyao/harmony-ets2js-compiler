package com.ets2jsc.ast;

import java.util.HashSet;
import java.util.Set;

/**
 * Component registry for identifying built-in UI components.
 * Based on component_map.ts from the original implementation.
 */
public class ComponentRegistry {

    private static final Set<String> BUILTIN_COMPONENTS = new HashSet<>();
    private static final Set<String> CONTAINER_COMPONENTS = new HashSet<>();

    static {
        // Initialize built-in components from ArkTS
        // Basic components
        addComponent("Text", false);
        addComponent("Button", false);
        addComponent("Image", false);
        addComponent("TextInput", false);
        addComponent("TextArea", false);
        addComponent("TextField", false);
        addComponent("Search", false);
        addComponent("Checkbox", false);
        addComponent("Radio", false);
        addComponent("Toggle", false);
        addComponent("Switch", false);
        addComponent("Slider", false);
        addComponent("Progress", false);
        addComponent("Rating", false);
        addComponent("Stepper", false);
        addComponent("TextArea", false);
        addComponent("XComponent", false);

        // Layout components (containers)
        addComponent("Column", true);
        addComponent("Row", true);
        addComponent("Stack", true);
        addComponent("Flex", true);
        addComponent("Grid", true);
        addComponent("GridRow", true);
        addComponent("GridCol", true);
        addComponent("List", true);
        addComponent("ListItem", true);
        addComponent("ListItemGroup", true);
        addComponent("Navigation", true);
        addComponent("Tabs", true);
        addComponent("TabContent", true);
        addComponent("RelativeContainer", true);
        addComponent("AlphabetIndexer", true);
        addComponent("WaterFlow", true);
        addComponent("FlowItem", true);
        addComponent("SideBarContainer", true);
        addComponent("Panel", true);

        // Advanced components
        addComponent("ForEach", false);
        addComponent("LazyForEach", false);
        addComponent("If", false);
        addComponent("Else", false);
        addComponent("Scroll", true);
        addComponent("Swiper", true);
        addComponent("GridRow", false);
        addComponent("GridCol", false);

        // Web components
        addComponent("Web", false);

        // Canvas components
        addComponent("Canvas", false);

        // Media components
        addComponent("Video", false);
        addComponent("VideoSlider", false);

        // Map components
        addComponent("Map", false);

        // Other components
        addComponent("Divider", false);
        addComponent("Blank", false);
        addComponent("Badge", false);
        addComponent("Circle", false);
        addComponent("Ellipse", false);
        addComponent("Line", false);
        addComponent("Polyline", false);
        addComponent("Path", false);
        addComponent("Polygon", false);
        addComponent("Rect", false);
        addComponent("Shape", false);

        // Custom components base
        addComponent("CustomDialog", false);
        addComponent("Popover", false);
        addComponent("Overlay", false);
    }

    private static void addComponent(String name, boolean isContainer) {
        BUILTIN_COMPONENTS.add(name);
        if (isContainer) {
            CONTAINER_COMPONENTS.add(name);
        }
    }

    /**
     * Check if a component name is a built-in component.
     */
    public static boolean isBuiltinComponent(String name) {
        return BUILTIN_COMPONENTS.contains(name);
    }

    /**
     * Check if a component is a container component (can have children).
     */
    public static boolean isContainerComponent(String name) {
        return CONTAINER_COMPONENTS.contains(name);
    }

    /**
     * Check if a component needs create/pop transformation.
     * All built-in components need this transformation.
     */
    public static boolean needsCreatePopTransformation(String name) {
        return BUILTIN_COMPONENTS.contains(name);
    }

    /**
     * Get all built-in component names.
     */
    public static Set<String> getAllBuiltinComponents() {
        return new HashSet<>(BUILTIN_COMPONENTS);
    }
}
