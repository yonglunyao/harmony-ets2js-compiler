package com.ets2jsc.core.factory;

import com.ets2jsc.parser.TypeScriptParser;
import com.ets2jsc.parser.internal.ProcessExecutor;

/**
 * Factory interface for creating parser instances.
 * <p>
 * Implementations of this interface are responsible for creating
 * configured parser instances. This enables dependency injection
 * and makes testing easier.
 */
public interface ParserFactory {

    /**
     * Creates a new parser instance.
     *
     * @return the parser
     */
    TypeScriptParser createParser();

    /**
     * Creates a parser with a specific script path.
     *
     * @param scriptPath the path to the parser script
     * @return the parser
     */
    TypeScriptParser createParser(String scriptPath);

    /**
     * Creates a parser with a custom process executor.
     *
     * @param processExecutor the process executor to use
     * @return the parser
     */
    TypeScriptParser createParser(ProcessExecutor processExecutor);
}
