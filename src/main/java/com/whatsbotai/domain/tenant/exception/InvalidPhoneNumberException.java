package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when a phone number fails domain validation rules.
 *
 * <p>Specific causes include:
 * <ul>
 *   <li>Null, empty, or blank input</li>
 *   <li>Invalid Brazilian mobile structure (wrong length, invalid area code,
 *       or missing the mandatory leading 9 of the subscriber number)</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class InvalidPhoneNumberException extends DomainException {

    private InvalidPhoneNumberException(String message) {
        super(message);
    }

    /**
     * Creates an exception for null, empty, or blank input.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidPhoneNumberException forNullOrBlanck() {

        return new InvalidPhoneNumberException("Phone number must not be null or blanck");
    }

    /**
     * Creates an exception for a structurally invalid Brazilian mobile number.
     *
     * @param rejectedValue the value that failed validation
     * @return a new exception including the rejected value
     */
    public static InvalidPhoneNumberException forValue(String rejectedValue) {
        return new InvalidPhoneNumberException("Invalid Brazilian mobile phone number: '" + rejectedValue + "'");
    }
}
