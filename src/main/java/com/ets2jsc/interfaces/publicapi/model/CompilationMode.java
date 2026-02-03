package com.ets2jsc.interfaces.publicapi.model;

/**
 * Compilation mode for batch operations.
 * <p>
 * This enum defines how multiple source files are compiled.
 *
 * @see com.ets2jsc.interfaces.publicapi.EtsCompiler
 */
public enum CompilationMode {

    /**
     * Sequential compilation mode.
     * <p>
     * Files are compiled one after another in order.
     * This mode uses less memory and is predictable.
     */
    SEQUENTIAL,

    /**
     * Parallel compilation mode.
     * <p>
     * Multiple files are compiled concurrently using a thread pool.
     * This mode can be faster for multi-core systems but uses more memory.
     */
    PARALLEL
}
