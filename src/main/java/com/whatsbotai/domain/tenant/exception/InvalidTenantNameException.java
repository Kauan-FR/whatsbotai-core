package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when a tenant name fails domain validation rules.
 *
 * <p>Specific causes include:
 * <ul>
 *   <li>Null, empty, or blank input</li>
 *   <li>Length below the minimum allowed</li>
 *   <li>Length above the maximum allowed</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class InvalidTenantNameException extends DomainException{

    private InvalidTenantNameException(String message) {
        super(message);
    }

    /**
     * Creates an exception for null, empty, or blank input.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidTenantNameException forNullOrBlank() {
        return new InvalidTenantNameException("Tenant name must not be null or blank");
    }

    /**
     * Creates an exception for a name shorter than the minimum allowed length.
     *
     * @param rejectedValue the value that failed validation
     * @param minLength     the minimum required length
     * @return a new exception including the rejected value and the minimum length
     */
    public static InvalidTenantNameException forTooShort(String rejectedValue, int minLength) {
        return new InvalidTenantNameException(
            "Tenant name must have at least " + minLength + " characters: '" + rejectedValue + "'"
        );
    }

    /**
     * Creates an exception for a name longer than the maximum allowed length.
     *
     * @param rejectedValue the value that failed validation
     * @param maxLength     the maximum allowed length
     * @return a new exception including the rejected value and the maximum length
     */
    public static InvalidTenantNameException forTooLong(String rejectedValue, int maxLength) {
        return new InvalidTenantNameException(
            "Tenant name must have at most " + maxLength + " characters: '" + rejectedValue + "'"
        );
    }
}
