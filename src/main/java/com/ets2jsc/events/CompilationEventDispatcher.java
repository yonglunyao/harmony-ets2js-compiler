package com.ets2jsc.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event dispatcher for compilation events.
 * <p>
 * This class manages the notification of compilation events to registered
 * listeners. It is thread-safe and supports dynamic listener registration.
 * </p>
 *
 * @since 1.0
 */
public class CompilationEventDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompilationEventDispatcher.class);

    private final List<CompilationListener> listeners;

    /**
     * Creates a new event dispatcher.
     */
    public CompilationEventDispatcher() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a listener to receive compilation events.
     *
     * @param listener the listener to add
     * @throws IllegalArgumentException if listener is null
     */
    public void addListener(CompilationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        listeners.add(listener);
        LOGGER.debug("Added compilation listener: {}", listener.getClass().getSimpleName());
    }

    /**
     * Removes a listener from receiving compilation events.
     *
     * @param listener the listener to remove
     * @return true if the listener was removed, false otherwise
     */
    public boolean removeListener(CompilationListener listener) {
        boolean removed = listeners.remove(listener);
        if (removed) {
            LOGGER.debug("Removed compilation listener: {}", listener.getClass().getSimpleName());
        }
        return removed;
    }

    /**
     * Removes all listeners.
     */
    public void clearListeners() {
        int size = listeners.size();
        listeners.clear();
        LOGGER.debug("Cleared {} compilation listeners", size);
    }

    /**
     * Fires a compilation start event.
     *
     * @param event the compilation event
     */
    public void fireCompilationStart(CompilationEvent event) {
        LOGGER.debug("Firing compilation start event");
        for (CompilationListener listener : listeners) {
            try {
                listener.onCompilationStart(event);
            } catch (Exception e) {
                LOGGER.error("Listener error in onCompilationStart", e);
            }
        }
    }

    /**
     * Fires a file compilation start event.
     *
     * @param event the compilation event
     */
    public void fireFileCompilationStart(CompilationEvent event) {
        LOGGER.debug("Firing file compilation start event: {}", event.getSourcePath());
        for (CompilationListener listener : listeners) {
            try {
                listener.onFileCompilationStart(event);
            } catch (Exception e) {
                LOGGER.error("Listener error in onFileCompilationStart", e);
            }
        }
    }

    /**
     * Fires a file compilation success event.
     *
     * @param event the compilation event
     */
    public void fireFileCompilationSuccess(CompilationEvent event) {
        LOGGER.debug("Firing file compilation success event: {}", event.getSourcePath());
        for (CompilationListener listener : listeners) {
            try {
                listener.onFileCompilationSuccess(event);
            } catch (Exception e) {
                LOGGER.error("Listener error in onFileCompilationSuccess", e);
            }
        }
    }

    /**
     * Fires a file compilation failure event.
     *
     * @param event the compilation event
     */
    public void fireFileCompilationFailure(CompilationEvent event) {
        LOGGER.debug("Firing file compilation failure event: {}", event.getSourcePath());
        for (CompilationListener listener : listeners) {
            try {
                listener.onFileCompilationFailure(event);
            } catch (Exception e) {
                LOGGER.error("Listener error in onFileCompilationFailure", e);
            }
        }
    }

    /**
     * Fires a compilation complete event.
     *
     * @param event the compilation event
     */
    public void fireCompilationComplete(CompilationEvent event) {
        LOGGER.debug("Firing compilation complete event");
        for (CompilationListener listener : listeners) {
            try {
                listener.onCompilationComplete(event);
            } catch (Exception e) {
                LOGGER.error("Listener error in onCompilationComplete", e);
            }
        }
    }

    /**
     * Fires a compilation failure event.
     *
     * @param event the compilation event
     */
    public void fireCompilationFailure(CompilationEvent event) {
        LOGGER.debug("Firing compilation failure event");
        for (CompilationListener listener : listeners) {
            try {
                listener.onCompilationFailure(event);
            } catch (Exception e) {
                LOGGER.error("Listener error in onCompilationFailure", e);
            }
        }
    }

    /**
     * Returns the number of registered listeners.
     *
     * @return the listener count
     */
    public int getListenerCount() {
        return listeners.size();
    }
}
