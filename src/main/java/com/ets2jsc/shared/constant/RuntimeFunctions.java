package com.ets2jsc.shared.constant;

/**
 * Constants for runtime function names in the generated JavaScript.
 * These functions are part of the HarmonyOS ArkTS runtime.
 */
public final class RuntimeFunctions {

    // Component lifecycle functions
    public static final String COMPONENT_CREATE = "create";
    public static final String COMPONENT_POP = "pop";
    public static final String COMPONENT_RENDER = "render";
    public static final String COMPONENT_INITIAL_RENDER = "initialRender";

    // State management functions
    public static final String CREATE_STATE = "createState";
    public static final String CREATE_PROP = "createProp";
    public static final String CREATE_LINK = "createLink";
    public static final String INITIALIZE_CONSUME_V2 = "initializeConsumeV2";
    public static final String INITIALIZE_PROVIDE = "initializeProvide";

    // View stack functions
    public static final String VIEWSTACKPROCESSOR = "ViewStackProcessor";
    public static final String START_GET_ACCESS_RECORDING_FOR = "startGetAccessRecordingFor";
    public static final String STOP_GET_ACCESS_RECORDING = "stopGetAccessRecording";

    // Component creation functions
    public static final String OBSERVE_COMPONENT_CREATION = "observeComponentCreation";
    public static final String UPDATE_FUNC_BY_ELMT_ID = "updateFuncByElmtId";

    // Property access functions
    public static final String GET = "get";
    public static final String SET = "set";

    // ForEach functions
    public static final String ITEM_GENERATOR = "itemGenerator";
    public static final String KEY_GENERATOR = "keyGenerator";

    // If branch functions
    public static final String BRANCH_ID = "branchId";

    // Resource functions
    public static final String GET_RESOURCE_ID = "__getResourceId__";
    public static final String GET_RAW_FILE_ID = "__getRawFileId__";

    // Resource type IDs (from HarmonyOS resource system)
    public static final int RESOURCE_TYPE_COLOR = 10001;
    public static final int RESOURCE_TYPE_FLOAT = 10002;
    public static final int RESOURCE_TYPE_STRING = 10003;
    public static final int RESOURCE_TYPE_PLURAL = 10004;
    public static final int RESOURCE_TYPE_BOOLEAN = 10005;
    public static final int RESOURCE_TYPE_INTARRAY = 10006;
    public static final int RESOURCE_TYPE_INTEGER = 10007;
    public static final int RESOURCE_TYPE_PATTERN = 10008;
    public static final int RESOURCE_TYPE_STRARRAY = 10009;
    public static final int RESOURCE_TYPE_MEDIA = 10010;
    public static final int RESOURCE_TYPE_FONT = 10011;
    public static final int RESOURCE_TYPE_PROFILE = 10012;
    public static final int RESOURCE_TYPE_DEFAULT = RESOURCE_TYPE_STRING;

    // Type names
    public static final String OBSERVED_PROPERTY_SIMPLE = "ObservedPropertySimple";
    public static final String OBSERVED_PROPERTY_SIMPLE_ONE_WAY = "ObservedPropertySimpleOneWay";
    public static final String OBSERVED_PROPERTY_SIMPLE_TWO_WAY = "ObservedPropertySimpleTwoWay";
    public static final String VIEW = "View";
    public static final String BUILDER_PARAM = "BuilderParam";

    private RuntimeFunctions() {
        // Prevent instantiation
    }
}
