package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when an email value fails domain validation rules.
 *
 * <p>Specific causes include:
 * <ul>
 *   <li>Null, empty, or blank input</li>
 *   <li>Malformed structure (missing {@code @}, invalid characters, etc.)</li>
 *   <li>Exceeding the maximum length of 254 characters (RFC 5321)</li>
 * </ul>
 *
 * <p>Use the static factory methods to construct contextualized
 * exceptions rather than passing raw messages.
 *
 * @author Kauan
 * @since 1.0
 */

public final class InvalidEmailException extends DomainException{

    InvalidEmailException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a null, empty, or blank email input.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidEmailException forNullOrBlank() {
        return new InvalidEmailException("Email must not be null or blank");
    }

    /**
     * Creates an exception for a structurally invalid email value.
     *
     * @param rejectedValue the value that failed validation
     * @return a new exception that includes the rejected value in the message
     */
    public static InvalidEmailException forValue(String rejectedValue) {
        return new InvalidEmailException("Invalid email format: '" + rejectedValue + "'");
    }

    /**
     * Creates an exception for an email that exceeds the maximum allowed length.
     *
     * @param rejectedValue the value that exceeded the limit
     * @param maxLength     the maximum allowed length
     * @return a new exception that includes both the value and the limit
     */
    public static InvalidEmailException forExceedingmaxLength(String rejectedValue, int maxLength) {
        return new InvalidEmailException("Email exceeds maximum length of " + maxLength + " characters: '" + rejectedValue + "'");
    }
}
