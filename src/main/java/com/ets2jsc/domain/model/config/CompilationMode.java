package com.ets2jsc.domain.model.config;

/**
 * Base interface for compilation mode states.
 * <p>
 * This interface defines the State pattern for compilation modes,
 * allowing different compilation behaviors to be encapsulated in
 * separate state objects instead of using boolean flags.
 * </p>
 *
 * @since 1.0
 */
public interface CompilationMode {

    /**
     * Returns the render method name for this compilation mode.
     *
     * @return the render method name
     */
    String getRenderMethodName();

    /**
     * Returns true if this mode supports partial updates.
     *
     * @return true if partial update mode is enabled
     */
    boolean isPartialUpdateMode();

    /**
     * Returns a human-readable name for this mode.
     *
     * @return the mode name
     */
    String getName();

    /**
     * Partial update mode - uses initialRender method.
     */
    CompilationMode PARTIAL_UPDATE = new CompilationMode() {
        @Override
        public String getRenderMethodName() {
            return "initialRender";
        }

        @Override
        public boolean isPartialUpdateMode() {
            return true;
        }

        @Override
        public String getName() {
            return "PartialUpdate";
        }
    };

    /**
     * Full render mode - uses render method.
     */
    CompilationMode FULL_RENDER = new CompilationMode() {
        @Override
        public String getRenderMethodName() {
            return "render";
        }

        @Override
        public boolean isPartialUpdateMode() {
            return false;
        }

        @Override
        public String getName() {
            return "FullRender";
        }
    };

    /**
     * Returns the compilation mode based on a boolean flag.
     *
     * @param partialUpdate true for partial update mode, false for full render
     * @return the appropriate compilation mode
     */
    static CompilationMode fromBoolean(boolean partialUpdate) {
        return partialUpdate ? PARTIAL_UPDATE : FULL_RENDER;
    }
}
