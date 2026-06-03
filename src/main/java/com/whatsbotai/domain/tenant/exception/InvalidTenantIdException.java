package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when a tenant identifier value violates domain rules.
 *
 * <p>Specific causes include:
 * <ul>
 *   <li>Null UUID input</li>
 *   <li>Null, empty, or blank string input</li>
 *   <li>Malformed UUID string that cannot be parsed</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class InvalidTenantIdException extends DomainException {

    private InvalidTenantIdException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a null UUID input.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidTenantIdException forNullValue() {
        return new InvalidTenantIdException("Tenant id must not be null");
    }

    /**
     * Creates an exception for null, empty, or blank string input.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidTenantIdException forNullOrBlankString() {
        return new InvalidTenantIdException("Tenant id string must not be null or blank");
    }

    /**
     * Creates an exception for a string that cannot be parsed as a UUID.
     *
     * @param rejectedValue the value that failed parsing
     * @return a new exception including the rejected value
     */
    public static InvalidTenantIdException forMalformedUuid(String rejectedValue) {
        return new InvalidTenantIdException("Tenant id is not a valid UUID: '" + rejectedValue + "'");
    }
}
