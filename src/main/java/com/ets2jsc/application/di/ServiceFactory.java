package com.ets2jsc.application.di;

/**
 * Factory interface for creating service instances.
 * <p>
 * Implementations of this interface are responsible for creating
 * instances of services. This enables lazy initialization and
 * custom creation logic for services registered in the
 * {@link ServiceLocator}.
 * </p>
 *
 * @param <T> the type of service this factory creates
 * @since 1.0
 */
@FunctionalInterface
public interface ServiceFactory<T> {

    /**
     * Creates a new instance of the service.
     *
     * @return a new service instance
     * @throws Exception if the service cannot be created
     */
    T create() throws Exception;
}
