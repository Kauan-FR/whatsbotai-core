package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when a Baileys app name fails domain validation rules.
 *
 * <p>Specific causes include:
 * <ul>
 *   <li>Null, empty, or blank input</li>
 *   <li>Length outside the allowed range (3 to 50 characters)</li>
 *   <li>Invalid format (must be a kebab-case slug:
 *       lowercase letters, digits, and single hyphens)</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class InvalidBaileysAppNameException extends DomainException{

    private InvalidBaileysAppNameException(String message) {
        super(message);
    }

    /**
     * Creates an exception for null, empty, or blank input.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidBaileysAppNameException forNullOrBlank() {
        return new InvalidBaileysAppNameException("Baileys app name must not be null or blank");
    }

    /**
     * Creates an exception for input whose length is outside the allowed range.
     *
     * @param rejectedValue the value that failed validation
     * @param minLength     the minimum allowed length
     * @param maxLength     the maximum allowed length
     * @return a new exception including the rejected value and length bounds
     */
    public static InvalidBaileysAppNameException forLengthOutOfRange(String rejectedValue, int minLength, int maxLength) {
        return new InvalidBaileysAppNameException(
            "Baileys app name length must be between " + minLength + " and " + maxLength + " characters: '" + rejectedValue + "'"
        );
    }

    /**
     * Creates an exception for input that does not match the required slug format.
     *
     * @param rejectedValue the value that failed validation
     * @return a new exception including the rejected value
     */
    public static InvalidBaileysAppNameException forInvalidFormat(String rejectedValue) {
        return new InvalidBaileysAppNameException(
             "Baileys app name must be a kebab-case slug "
                        + "(lowercase letters, digits, and single hyphens, not starting or ending with hyphen): '"
                        + rejectedValue + "'"
        );
    }
}
