package com.whatsbotai.domain.tenant.vo;

import com.whatsbotai.domain.tenant.exception.InvalidEmailException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a syntactically valid email address.
 *
 * <p>An {@code Email} is immutable, self-validating, and compared by value.
 * It cannot exist in an invalid state — any attempt to create one with
 * invalid input throws {@link InvalidEmailException}.
 *
 * <p><strong>Validation rules:</strong>
 * <ul>
 *   <li>Must not be null, empty, or blank</li>
 *   <li>Must match a pragmatic email pattern (OWASP-recommended regex)</li>
 *   <li>Must not exceed 254 characters (RFC 5321)</li>
 * </ul>
 *
 * <p><strong>Normalization:</strong>
 * Input is trimmed and lowercased before validation, so
 * {@code Email.of("  User@Example.COM  ")} yields the value
 * {@code "user@example.com"}.
 *
 * @author Kauan
 * @since 1.0
 */

public final class Email {

    /**
     * Maximum allowed length per RFC 5321 (section 4.5.3.1.3).
     */
    private static final int MAX_LENGTH = 254;

    /**
     * Pragmatic email validation pattern (OWASP-recommended).
     * Covers the vast majority of real-world valid emails while rejecting
     * common malformed inputs.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    /**
     * Creates a new {@code Email} from the given raw input.
     *
     * <p>The input is trimmed and lowercased before validation.
     *
     * @param rawInput the raw email string (may contain surrounding whitespace or mixed case)
     * @return a validated, normalized {@code Email}
     * @throws InvalidEmailException if the input is null, blank, malformed, or exceeds the maximum length
     */
    public static Email of(String rawInput) {

        if (rawInput == null || rawInput.isBlank()) {
            throw InvalidEmailException.forNullOrBlank();
        }

        String normalized = rawInput.trim().toLowerCase();

        if (normalized.length() > MAX_LENGTH) {
            throw InvalidEmailException.forExceedingmaxLength(normalized, MAX_LENGTH);
        }

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw InvalidEmailException.forValue(normalized);
        }

        return new Email(normalized);
    }

    /**
     * Returns the underlying normalized email string.
     *
     * @return the email value (lowercased, trimmed)
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Email that)) return false;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
