package com.ets2jsc.config;

/**
 * Full render compilation mode state.
 * <p>
 * In this mode, components use the render method which performs
 * a complete UI rendering without partial update optimizations.
 * </p>
 *
 * @since 1.0
 */
public final class FullRenderMode implements CompilationMode {

    private static final FullRenderMode INSTANCE = new FullRenderMode();

    private static final String RENDER_METHOD_NAME = "render";

    private FullRenderMode() {
        // Private constructor for singleton
    }

    /**
     * Returns the singleton instance of full render mode.
     *
     * @return the full render mode instance
     */
    public static FullRenderMode getInstance() {
        return INSTANCE;
    }

    @Override
    public String getRenderMethodName() {
        return RENDER_METHOD_NAME;
    }

    @Override
    public boolean isPartialUpdateMode() {
        return false;
    }

    @Override
    public String getName() {
        return "FullRender";
    }
}
