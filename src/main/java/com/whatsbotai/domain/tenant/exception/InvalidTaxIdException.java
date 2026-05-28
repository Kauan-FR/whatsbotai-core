package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when a tax identifier (CPF or CNPJ) fails domain validation.
 *
 * <p>Specific causes include:
 * <ul>
 *   <li>Null, empty, or blank input</li>
 *   <li>Invalid length after stripping non-digit characters
 *       (not 11 digits for CPF, nor 14 for CNPJ)</li>
 *   <li>All-repeated digits (e.g. {@code 11111111111})</li>
 *   <li>Invalid check digits (failed modulo-11 verification)</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class InvalidTaxIdException extends DomainException {

    private InvalidTaxIdException(String message) {
        super(message);
    }

    /**
     * Creates an exception for null, empty, or blank input.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidTaxIdException forNullOrBlank() {
        return new InvalidTaxIdException("Tax ID must not be null or blank");
    }

    /**
     * Creates an exception for input whose digit count matches neither CPF (11) nor CNPJ (14).
     *
     * @param rejectedValue the value that failed validation
     * @return a new exception including the rejected value
     */
    public static InvalidTaxIdException forInvalidlength(String rejectedValue) {
        return new InvalidTaxIdException("Tax ID must have 11 digits (CPF) or 14 digits (CNPJ): '" + rejectedValue + "'");
    }

    /**
     * Creates an exception for input made entirely of repeated digits.
     *
     * @param rejectedValue the value that failed validation
     * @return a new exception including the rejected value
     */
    public static InvalidTaxIdException forRepeatedDigits(String rejectedValue) {
        return new InvalidTaxIdException("Tax ID must not consist of all repeated digits: '" + rejectedValue + "'");
    }

    /**
     * Creates an exception for input that fails modulo-11 check digit verification.
     *
     * @param rejectedValue the value that failed validation
     * @return a new exception including the rejected value
     */
    public static InvalidTaxIdException forInvalidCheckDigits(String rejectedValue) {
        return new InvalidTaxIdException("Tax ID has invalid check digits: '" + rejectedValue + "'");
    }
}
