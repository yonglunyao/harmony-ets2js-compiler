package com.ets2jsc.generator.strategy;

import com.ets2jsc.domain.model.ast.AstNode;
import com.ets2jsc.generator.context.GenerationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Registry for code generation strategies.
 * <p>
 * This class manages all registered code generation strategies
 * and provides methods to find and execute the appropriate strategy
 * for a given AST node.
 * </p>
 *
 * @since 1.0
 */
public class CodeGenerationStrategyRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenerationStrategyRegistry.class);

    private final List<CodeGenerationStrategy> strategies;

    /**
     * Creates a new strategy registry.
     */
    public CodeGenerationStrategyRegistry() {
        this.strategies = new ArrayList<>();
    }

    /**
     * Registers a code generation strategy.
     *
     * @param strategy the strategy to register
     * @throws IllegalArgumentException if strategy is null
     */
    public void registerStrategy(CodeGenerationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        strategies.add(strategy);
        strategies.sort(Comparator.comparingInt(CodeGenerationStrategy::getPriority).reversed());
        LOGGER.debug("Registered code generation strategy: {}", strategy.getClass().getSimpleName());
    }

    /**
     * Generates code for the given AST node using the appropriate strategy.
     *
     * @param node the AST node to generate code for
     * @param context the generation context
     * @return the generated code
     * @throws IllegalArgumentException if no strategy can handle the node
     */
    public String generate(AstNode node, GenerationContext context) {
        if (node == null) {
            throw new IllegalArgumentException("AST node cannot be null");
        }

        for (CodeGenerationStrategy strategy : strategies) {
            if (strategy.canHandle(node)) {
                LOGGER.debug("Using strategy {} for node: {}",
                        strategy.getClass().getSimpleName(), node.getClass().getSimpleName());
                return strategy.generate(node, context);
            }
        }

        LOGGER.warn("No strategy found for node type: {}", node.getClass().getSimpleName());
        return "";
    }

    /**
     * Checks if there is a strategy that can handle the given node.
     *
     * @param node the AST node to check
     * @return true if a strategy can handle the node
     */
    public boolean canHandle(AstNode node) {
        for (CodeGenerationStrategy strategy : strategies) {
            if (strategy.canHandle(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of registered strategies.
     *
     * @return the strategy count
     */
    public int getStrategyCount() {
        return strategies.size();
    }

    /**
     * Clears all registered strategies.
     */
    public void clear() {
        strategies.clear();
    }
}
