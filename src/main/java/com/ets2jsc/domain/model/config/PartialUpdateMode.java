package com.ets2jsc.domain.model.config;

/**
 * Partial update compilation mode state.
 * <p>
 * In this mode, components use the initialRender method which is optimized
 * for partial UI updates in ArkUI applications.
 * </p>
 *
 * @since 1.0
 */
public final class PartialUpdateMode implements CompilationMode {

    private static final PartialUpdateMode INSTANCE = new PartialUpdateMode();

    private static final String RENDER_METHOD_NAME = "initialRender";

    private PartialUpdateMode() {
        // Private constructor for singleton
    }

    /**
     * Returns the singleton instance of partial update mode.
     *
     * @return the partial update mode instance
     */
    public static PartialUpdateMode getInstance() {
        return INSTANCE;
    }

    @Override
    public String getRenderMethodName() {
        return RENDER_METHOD_NAME;
    }

    @Override
    public boolean isPartialUpdateMode() {
        return true;
    }

    @Override
    public String getName() {
        return "PartialUpdate";
    }
}
