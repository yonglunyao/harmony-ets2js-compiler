package com.ets2jsc.constant;

/**
 * Constants for all ETS decorators.
 * Used for decorator recognition and transformation.
 */
public final class Decorators {

    // Component decorators
    public static final String COMPONENT = "Component";
    public static final String ENTRY = "Entry";
    public static final String PREVIEW = "Preview";
    public static final String CUSTOM_DIALOG = "CustomDialog";
    public static final String REUSABLE = "Reusable";
    public static final String COMPONENT_V2 = "ComponentV2";

    // State decorators
    public static final String STATE = "State";
    public static final String PROP = "Prop";
    public static final String LINK = "Link";
    public static final String PROVIDE = "Provide";
    public static final String CONSUME = "Consume";
    public static final String OBJECT_LINK = "ObjectLink";
    public static final String STORAGE_PROP = "StorageProp";
    public static final String STORAGE_LINK = "StorageLink";
    public static final String LOCAL_STORAGE_PROP = "LocalStorageProp";
    public static final String LOCAL_STORAGE_LINK = "LocalStorageLink";

    // V2 state decorators
    public static final String LOCAL = "Local";
    public static final String PARAM = "Param";
    public static final String ONCE = "Once";
    public static final String EVENT = "Event";
    public static final String CONSUMER_V2 = "Consumer";
    public static final String PROVIDER_V2 = "Provider";

    // Observer decorators
    public static final String OBSERVED = "Observed";
    public static final String OBSERVED_V2 = "ObservedV2";
    public static final String TRACK = "Track";

    // Method decorators
    public static final String BUILDER = "Builder";
    public static final String LOCAL_BUILDER = "LocalBuilder";
    public static final String EXTEND = "Extend";
    public static final String STYLES = "Styles";
    public static final String ANIMATABLE_EXTEND = "AnimatableExtend";
    public static final String WATCH = "Watch";
    public static final String BUILDER_PARAM = "BuilderParam";
    public static final String REQUIRE = "Require";
    public static final String ENV = "Env";

    // Other decorators
    public static final String SENDABLE = "Sendable";

    private Decorators() {
        // Prevent instantiation
    }
}
