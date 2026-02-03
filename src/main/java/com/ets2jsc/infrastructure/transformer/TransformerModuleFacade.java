package com.ets2jsc.infrastructure.transformer;

import com.ets2jsc.domain.service.TransformerService;
import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.domain.model.ast.SourceFile;
import com.ets2jsc.domain.model.config.CompilerConfig;
import com.ets2jsc.infrastructure.factory.DefaultTransformerFactory;
import com.ets2jsc.infrastructure.factory.TransformerFactory;
import com.ets2jsc.infrastructure.transformer.AstTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Facade for the TransformerModule.
 * <p>
 * This class provides a single entry point for all AST transformation operations,
 * internally managing a chain of transformers through the TransformerFactory.
 */
public class TransformerModuleFacade implements TransformerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformerModuleFacade.class);

    private final TransformerFactory transformerFactory;
    private final CompilerConfig config;
    private List<AstTransformer> transformers;

    /**
     * Creates a new transformer module facade with the given configuration.
     *
     * @param config the compiler configuration
     */
    public TransformerModuleFacade(CompilerConfig config) {
        this(config, new DefaultTransformerFactory());
    }

    /**
     * Creates a new transformer module facade with the given configuration and factory.
     * This constructor enables dependency injection for testing.
     *
     * @param config the compiler configuration
     * @param transformerFactory the transformer factory to use
     */
    public TransformerModuleFacade(CompilerConfig config, TransformerFactory transformerFactory) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        if (transformerFactory == null) {
            throw new IllegalArgumentException("TransformerFactory cannot be null");
        }

        this.config = config;
        this.transformerFactory = transformerFactory;
        this.transformers = transformerFactory.createTransformers(config);
    }

    @Override
    public SourceFile transform(SourceFile sourceFile) throws com.ets2jsc.shared.exception.CompilationException {
        if (sourceFile == null) {
            throw new IllegalArgumentException("SourceFile cannot be null");
        }

        // Transform all statements in the source file
        for (int i = 0; i < sourceFile.getStatements().size(); i++) {
            sourceFile.getStatements().set(i, transformNode(sourceFile.getStatements().get(i)));
        }
        return sourceFile;
    }

    @Override
    public AstNode transformNode(AstNode node) throws com.ets2jsc.shared.exception.CompilationException {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        AstNode current = node;
        for (AstTransformer transformer : transformers) {
            if (transformer.canTransform(current)) {
                current = transformer.transform(current);
            }
        }
        return current;
    }

    @Override
    public boolean canTransform(AstNode node) {
        if (node == null) {
            return false;
        }

        return transformers.stream().anyMatch(t -> t.canTransform(node));
    }

    @Override
    public void reconfigure(CompilerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        // Recreate transformers with new configuration
        this.transformers = transformerFactory.createTransformers(config);
    }

    @Override
    public void close() {
        // Clean up resources if needed
        transformers.clear();
    }

    /**
     * Gets the list of active transformers.
     * This method is primarily for testing and debugging.
     *
     * @return the list of active transformers
     */
    List<AstTransformer> getTransformers() {
        return List.copyOf(transformers);
    }
}
