package com.ets2jsc.shared.events;

/**
 * Enumeration of compilation event types.
 *
 * @since 1.0
 */
public enum CompilationEventType {

    /**
     * Event fired when the overall compilation process starts.
     */
    COMPILATION_START,

    /**
     * Event fired when compilation of a single file starts.
     */
    FILE_COMPILATION_START,

    /**
     * Event fired when compilation of a single file succeeds.
     */
    FILE_COMPILATION_SUCCESS,

    /**
     * Event fired when compilation of a single file fails.
     */
    FILE_COMPILATION_FAILURE,

    /**
     * Event fired when the overall compilation process completes successfully.
     */
    COMPILATION_COMPLETE,

    /**
     * Event fired when the overall compilation process fails.
     */
    COMPILATION_FAILURE
}
