package com.ets2jsc.transformer.chain;

import com.ets2jsc.domain.model.ast.AstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of {@link TransformationChain}.
 * <p>
 * This implementation maintains an ordered list of handlers and
 * processes nodes through each handler in sequence. It is thread-safe
 * and supports dynamic handler registration.
 * </p>
 *
 * @since 1.0
 */
public class DefaultTransformationChain implements TransformationChain {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTransformationChain.class);

    private final List<TransformationHandler> handlers;
    private final List<TransformationHandler> handlersView;
    private int currentIndex;

    /**
     * Creates a new transformation chain.
     */
    public DefaultTransformationChain() {
        this.handlers = new CopyOnWriteArrayList<>();
        this.handlersView = List.copyOf(handlers);
        this.currentIndex = 0;
    }

    /**
     * Adds a handler to the end of the chain.
     *
     * @param handler the handler to add
     * @throws IllegalArgumentException if handler is null
     */
    public void addHandler(TransformationHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        handlers.add(handler);
        LOGGER.debug("Added transformation handler: {}", handler.getClass().getSimpleName());
    }

    /**
     * Adds a handler at a specific position in the chain.
     *
     * @param index the position to insert the handler
     * @param handler the handler to add
     * @throws IllegalArgumentException if handler is null
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public void addHandler(int index, TransformationHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        handlers.add(index, handler);
        LOGGER.debug("Added transformation handler at index {}: {}", index, handler.getClass().getSimpleName());
    }

    /**
     * Removes a handler from the chain.
     *
     * @param handler the handler to remove
     * @return true if the handler was removed, false otherwise
     */
    public boolean removeHandler(TransformationHandler handler) {
        boolean removed = handlers.remove(handler);
        if (removed) {
            LOGGER.debug("Removed transformation handler: {}", handler.getClass().getSimpleName());
        }
        return removed;
    }

    /**
     * Processes a node through the entire chain starting from the first handler.
     *
     * @param node the AST node to process
     * @return the transformed node
     * @throws Exception if any handler in the chain fails
     */
    public AstNode process(AstNode node) throws Exception {
        currentIndex = 0;
        return proceed(node);
    }

    @Override
    public AstNode proceed(AstNode node) throws Exception {
        if (currentIndex >= handlers.size()) {
            LOGGER.debug("Reached end of transformation chain");
            return node;
        }

        TransformationHandler handler = handlers.get(currentIndex);
        currentIndex++;

        if (handler.canHandle(node)) {
            LOGGER.debug("Processing with handler {}: {}", currentIndex - 1, handler.getClass().getSimpleName());
            return handler.handle(node, this);
        } else {
            LOGGER.debug("Handler {} skipped, proceeding to next", currentIndex - 1);
            return proceed(node);
        }
    }

    /**
     * Returns the number of handlers in the chain.
     *
     * @return the handler count
     */
    public int getHandlerCount() {
        return handlers.size();
    }

    /**
     * Clears all handlers from the chain.
     */
    public void clear() {
        int size = handlers.size();
        handlers.clear();
        LOGGER.debug("Cleared {} transformation handlers", size);
    }
}
