package com.ets2jsc.constant;

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

    // All built-in component names
    public static final Set<String> ALL_COMPONENTS = new HashSet<>();

    // Container component names
    public static final Set<String> CONTAINER_COMPONENTS = new HashSet<>();

    // Atomic component names
    public static final Set<String> ATOMIC_COMPONENTS = new HashSet<>();

    static {
        // Container components
        CONTAINER_COMPONENTS.add(COLUMN);
        CONTAINER_COMPONENTS.add(ROW);
        CONTAINER_COMPONENTS.add(STACK);
        CONTAINER_COMPONENTS.add(FLEX);
        CONTAINER_COMPONENTS.add(GRID);
        CONTAINER_COMPONENTS.add(LIST);
        CONTAINER_COMPONENTS.add(SCROLL);
        CONTAINER_COMPONENTS.add(SWIPER);
        CONTAINER_COMPONENTS.add(TABS);
        CONTAINER_COMPONENTS.add(NAVIGATOR);
        CONTAINER_COMPONENTS.add(FOR_EACH);
        CONTAINER_COMPONENTS.add(LAZY_FOR_EACH);
        CONTAINER_COMPONENTS.add(IF);
        CONTAINER_COMPONENTS.add(GRID_ROW);
        CONTAINER_COMPONENTS.add(GRID_COL);
        CONTAINER_COMPONENTS.add(RELATIVE_CONTAINER);
        CONTAINER_COMPONENTS.add(ABSOLUTE_CONTAINER);

        // Atomic components
        ATOMIC_COMPONENTS.add(TEXT);
        ATOMIC_COMPONENTS.add(IMAGE);
        ATOMIC_COMPONENTS.add(TEXT_INPUT);
        ATOMIC_COMPONENTS.add(TEXT_AREA);
        ATOMIC_COMPONENTS.add(BUTTON);
        ATOMIC_COMPONENTS.add(TOGGLE);
        ATOMIC_COMPONENTS.add(CHECK_BOX);
        ATOMIC_COMPONENTS.add(RADIO);
        ATOMIC_COMPONENTS.add(SLIDER);
        ATOMIC_COMPONENTS.add(PROGRESS);
        ATOMIC_COMPONENTS.add(DIVIDER);
        ATOMIC_COMPONENTS.add(BLANK);
        ATOMIC_COMPONENTS.add(SPAN);
        ATOMIC_COMPONENTS.add(SELECT);
        ATOMIC_COMPONENTS.add(PICKER);
        ATOMIC_COMPONENTS.add(DATA_PICKER);
        ATOMIC_COMPONENTS.add(TIME_PICKER);

        ALL_COMPONENTS.addAll(CONTAINER_COMPONENTS);
        ALL_COMPONENTS.addAll(ATOMIC_COMPONENTS);
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
