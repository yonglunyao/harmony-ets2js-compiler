package com.ets2jsc.shared.constant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants for built-in HarmonyOS UI components.
 */
public final class Components {

    // Container components
    public static final String COLUMN = "Column";
    public static final String ROW = "Row";
    public static final String STACK = "Stack";
    public static final String FLEX = "Flex";
    public static final String GRID = "Grid";
    public static final String LIST = "List";
    public static final String SCROLL = "Scroll";
    public static final String SWIPER = "Swiper";
    public static final String TABS = "Tabs";
    public static final String NAVIGATOR = "Navigator";

    // Control flow components
    public static final String FOR_EACH = "ForEach";
    public static final String LAZY_FOR_EACH = "LazyForEach";
    public static final String IF = "If";

    // Atomic components
    public static final String TEXT = "Text";
    public static final String IMAGE = "Image";
    public static final String TEXT_INPUT = "TextInput";
    public static final String TEXT_AREA = "TextArea";
    public static final String BUTTON = "Button";
    public static final String TOGGLE = "Toggle";
    public static final String CHECK_BOX = "CheckBox";
    public static final String RADIO = "Radio";
    public static final String SLIDER = "Slider";
    public static final String PROGRESS = "Progress";
    public static final String DIVIDER = "Divider";
    public static final String BLANK = "Blank";
    public static final String SPAN = "Span";

    // Form components
    public static final String SELECT = "Select";
    public static final String PICKER = "Picker";
    public static final String DATA_PICKER = "DatePicker";
    public static final String TIME_PICKER = "TimePicker";

    // Layout components
    public static final String GRID_ROW = "GridRow";
    public static final String GRID_COL = "GridCol";
    public static final String RELATIVE_CONTAINER = "RelativeContainer";
    public static final String ABSOLUTE_CONTAINER = "AbsoluteContainer";

    // All built-in component names (immutable)
    public static final Set<String> ALL_COMPONENTS;

    // Container component names (immutable)
    public static final Set<String> CONTAINER_COMPONENTS;

    // Atomic component names (immutable)
    public static final Set<String> ATOMIC_COMPONENTS;

    static {
        // Initialize mutable sets
        Set<String> allComponents = new HashSet<>();
        Set<String> containerComponents = new HashSet<>();
        Set<String> atomicComponents = new HashSet<>();

        // Container components
        containerComponents.add(COLUMN);
        containerComponents.add(ROW);
        containerComponents.add(STACK);
        containerComponents.add(FLEX);
        containerComponents.add(GRID);
        containerComponents.add(LIST);
        containerComponents.add(SCROLL);
        containerComponents.add(SWIPER);
        containerComponents.add(TABS);
        containerComponents.add(NAVIGATOR);
        containerComponents.add(FOR_EACH);
        containerComponents.add(LAZY_FOR_EACH);
        containerComponents.add(IF);
        containerComponents.add(GRID_ROW);
        containerComponents.add(GRID_COL);
        containerComponents.add(RELATIVE_CONTAINER);
        containerComponents.add(ABSOLUTE_CONTAINER);

        // Atomic components
        atomicComponents.add(TEXT);
        atomicComponents.add(IMAGE);
        atomicComponents.add(TEXT_INPUT);
        atomicComponents.add(TEXT_AREA);
        atomicComponents.add(BUTTON);
        atomicComponents.add(TOGGLE);
        atomicComponents.add(CHECK_BOX);
        atomicComponents.add(RADIO);
        atomicComponents.add(SLIDER);
        atomicComponents.add(PROGRESS);
        atomicComponents.add(DIVIDER);
        atomicComponents.add(BLANK);
        atomicComponents.add(SPAN);
        atomicComponents.add(SELECT);
        atomicComponents.add(PICKER);
        atomicComponents.add(DATA_PICKER);
        atomicComponents.add(TIME_PICKER);

        allComponents.addAll(containerComponents);
        allComponents.addAll(atomicComponents);

        // Make sets immutable to prevent runtime modification
        ALL_COMPONENTS = Collections.unmodifiableSet(allComponents);
        CONTAINER_COMPONENTS = Collections.unmodifiableSet(containerComponents);
        ATOMIC_COMPONENTS = Collections.unmodifiableSet(atomicComponents);
    }

    /**
     * Returns true if the given name is a built-in component.
     */
    public static boolean isBuiltinComponent(String name) {
        return ALL_COMPONENTS.contains(name);
    }

    /**
     * Returns true if the given name is a container component.
     */
    public static boolean isContainerComponent(String name) {
        return CONTAINER_COMPONENTS.contains(name);
    }

    /**
     * Returns true if the given name is an atomic component.
     */
    public static boolean isAtomicComponent(String name) {
        return ATOMIC_COMPONENTS.contains(name);
    }

    private Components() {
        // Prevent instantiation
    }
}
