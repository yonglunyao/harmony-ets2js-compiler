package com.ets2jsc.application.di;

/**
 * Service locator interface for dependency injection.
 * <p>
 * This interface provides a lightweight dependency injection mechanism
 * that allows services to be registered and retrieved without requiring
 * a heavyweight DI framework. It follows the Service Locator pattern
 * to enable testability and loose coupling.
 * </p>
 *
 * @param <T> the type of service
 * @since 1.0
 */
public interface ServiceLocator<T> {

    /**
     * Registers a service factory for the given service type.
     *
     * @param serviceType the class type of the service
     * @param factory the factory that creates instances of the service
     * @param <S> the specific service type
     * @throws IllegalArgumentException if serviceType or factory is null
     */
    <S extends T> void registerService(Class<S> serviceType, ServiceFactory<S> factory);

    /**
     * Retrieves a service instance for the given service type.
     *
     * @param serviceType the class type of the service to retrieve
     * @param <S> the specific service type
     * @return an instance of the requested service
     * @throws ServiceResolutionException if the service cannot be resolved
     * @throws IllegalArgumentException if serviceType is null
     */
    <S extends T> S getService(Class<S> serviceType);

    /**
     * Checks if a service is registered for the given type.
     *
     * @param serviceType the class type of the service to check
     * @param <S> the specific service type
     * @return true if a service is registered, false otherwise
     * @throws IllegalArgumentException if serviceType is null
     */
    <S extends T> boolean hasService(Class<S> serviceType);

    /**
     * Removes a service registration for the given type.
     *
     * @param serviceType the class type of the service to remove
     * @param <S> the specific service type
     * @throws IllegalArgumentException if serviceType is null
     */
    <S extends T> void unregisterService(Class<S> serviceType);

    /**
     * Clears all service registrations.
     */
    void clear();
}
