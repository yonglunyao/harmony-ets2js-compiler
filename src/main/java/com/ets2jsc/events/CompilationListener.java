package com.ets2jsc.events;

/**
 * Listener interface for compilation events.
 * <p>
 * Implementations of this interface can be registered with the
 * {@link CompilationEventDispatcher} to receive notifications
 * about compilation lifecycle events.
 * </p>
 *
 * @since 1.0
 */
public interface CompilationListener {

    /**
     * Called when compilation starts.
     *
     * @param event the compilation event
     */
    default void onCompilationStart(CompilationEvent event) {
        // Default implementation does nothing
    }

    /**
     * Called when a single file compilation starts.
     *
     * @param event the compilation event
     */
    default void onFileCompilationStart(CompilationEvent event) {
        // Default implementation does nothing
    }

    /**
     * Called when a single file compilation completes successfully.
     *
     * @param event the compilation event
     */
    default void onFileCompilationSuccess(CompilationEvent event) {
        // Default implementation does nothing
    }

    /**
     * Called when a single file compilation fails.
     *
     * @param event the compilation event
     */
    default void onFileCompilationFailure(CompilationEvent event) {
        // Default implementation does nothing
    }

    /**
     * Called when compilation completes.
     *
     * @param event the compilation event
     */
    default void onCompilationComplete(CompilationEvent event) {
        // Default implementation does nothing
    }

    /**
     * Called when compilation fails.
     *
     * @param event the compilation event
     */
    default void onCompilationFailure(CompilationEvent event) {
        // Default implementation does nothing
    }
}
