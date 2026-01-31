package com.ets2jsc.core.factory;

import com.ets2jsc.parser.TypeScriptParser;

/**
 * Default implementation of ParserFactory.
 * <p>
 * Creates standard TypeScriptParser instances.
 */
public class DefaultParserFactory implements ParserFactory {

    @Override
    public TypeScriptParser createParser() {
        return new TypeScriptParser();
    }

    @Override
    public TypeScriptParser createParser(String scriptPath) {
        // Note: TypeScriptParser currently only has default constructor
        // scriptPath parameter is ignored for now
        return new TypeScriptParser();
    }

    @Override
    public TypeScriptParser createParser(com.ets2jsc.parser.internal.ProcessExecutor processExecutor) {
        // Note: TypeScriptParser currently only has default constructor
        // processExecutor parameter is ignored for now
        return new TypeScriptParser();
    }
}
