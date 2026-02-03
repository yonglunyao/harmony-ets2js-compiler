package com.ets2jsc.application.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link ServiceLocator}.
 * <p>
 * This implementation uses a thread-safe concurrent map to store
 * service registrations, making it suitable for multi-threaded
 * compilation scenarios.
 * </p>
 *
 * @param <T> the base type for all services in this locator
 * @since 1.0
 */
public class DefaultServiceLocator<T> implements ServiceLocator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceLocator.class);

    private final Map<Class<?>, ServiceFactory<?>> serviceFactories;

    /**
     * Creates a new default service locator.
     */
    public DefaultServiceLocator() {
        this.serviceFactories = new ConcurrentHashMap<>();
    }

    @Override
    public <S extends T> void registerService(Class<S> serviceType, ServiceFactory<S> factory) {
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        if (factory == null) {
            throw new IllegalArgumentException("Service factory cannot be null");
        }

        serviceFactories.put(serviceType, factory);
        LOGGER.debug("Registered service: {}", serviceType.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S getService(Class<S> serviceType) {
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }

        ServiceFactory<?> factory = serviceFactories.get(serviceType);
        if (factory == null) {
            LOGGER.error("Service not registered: {}", serviceType.getName());
            throw ServiceResolutionException.notRegistered(serviceType);
        }

        try {
            S service = (S) factory.create();
            LOGGER.debug("Created service instance: {}", serviceType.getName());
            return service;
        } catch (Exception e) {
            LOGGER.error("Failed to instantiate service: {}", serviceType.getName(), e);
            throw ServiceResolutionException.instantiationFailed(serviceType, e);
        }
    }

    @Override
    public <S extends T> boolean hasService(Class<S> serviceType) {
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        return serviceFactories.containsKey(serviceType);
    }

    @Override
    public <S extends T> void unregisterService(Class<S> serviceType) {
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }

        ServiceFactory<?> removed = serviceFactories.remove(serviceType);
        if (removed != null) {
            LOGGER.debug("Unregistered service: {}", serviceType.getName());
        }
    }

    @Override
    public void clear() {
        int size = serviceFactories.size();
        serviceFactories.clear();
        LOGGER.debug("Cleared {} service registrations", size);
    }
}
