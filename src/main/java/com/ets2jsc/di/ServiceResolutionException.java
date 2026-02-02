package com.ets2jsc.di;

/**
 * Exception thrown when a service cannot be resolved from the service locator.
 * <p>
 * This exception indicates that a requested service is not registered
 * in the service locator or cannot be instantiated for some reason.
 * </p>
 *
 * @since 1.0
 */
public class ServiceResolutionException extends RuntimeException {

    /**
     * Creates a new service resolution exception with the specified message.
     *
     * @param message the error message
     */
    public ServiceResolutionException(String message) {
        super(message);
    }

    /**
     * Creates a new service resolution exception with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of the resolution failure
     */
    public ServiceResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new service resolution exception for a service type that is not registered.
     *
     * @param serviceType the type of service that could not be resolved
     * @return a new exception instance
     */
    public static ServiceResolutionException notRegistered(Class<?> serviceType) {
        return new ServiceResolutionException(
                "Service not registered: " + serviceType.getName());
    }

    /**
     * Creates a new service resolution exception for a service that failed during instantiation.
     *
     * @param serviceType the type of service that failed to instantiate
     * @param cause the cause of the instantiation failure
     * @return a new exception instance
     */
    public static ServiceResolutionException instantiationFailed(Class<?> serviceType, Throwable cause) {
        return new ServiceResolutionException(
                "Failed to instantiate service: " + serviceType.getName(), cause);
    }
}
