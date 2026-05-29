package com.whatsbotai.domain.tenant.vo;

import com.whatsbotai.domain.tenant.exception.InvalidBaileysAppNameException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing the unique slug that identifies a tenant's
 * Baileys WhatsApp session within the system.
 *
 * <p>This name is used as a filesystem-safe and URL-safe identifier for the
 * tenant's WhatsApp session (e.g., as a credentials folder name, in logs,
 * and across multi-tenant infrastructure). It is a {@code kebab-case} slug.
 *
 * <p><strong>Validation rules:</strong>
 * <ul>
 *   <li>Must not be null, empty, or blank (after trimming)</li>
 *   <li>Length between 3 and 50 characters</li>
 *   <li>Allowed characters: lowercase letters {@code a-z}, digits {@code 0-9},
 *       and the hyphen {@code -}</li>
 *   <li>Must not start or end with a hyphen</li>
 *   <li>Must not contain consecutive hyphens</li>
 *   <li>Must contain at least one letter (cannot be digits or hyphens only)</li>
 * </ul>
 *
 * <p><strong>No silent normalization:</strong>
 * Input is only trimmed of surrounding whitespace; case and other formatting
 * are not corrected silently. Invalid input is rejected explicitly so the
 * caller knows exactly what was wrong.
 *
 * @author Kauan
 * @since 1.0
 */
public final class BaileysAppName {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    /**
     * Kebab-case slug pattern:
     * <ul>
     *   <li>Starts with a lowercase letter or digit</li>
     *   <li>May contain lowercase letters, digits, or single hyphens</li>
     *   <li>Ends with a lowercase letter or digit</li>
     * </ul>
     * Consecutive hyphens are forbidden by requiring an alphanumeric character
     * after every hyphen.
     */
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    /**
     * Used to enforce that at least one letter is present
     * (prevents purely numeric slugs like {@code "12345"}).
     */
    private static final Pattern CONTAINS_LETTER = Pattern.compile(".*[a-z].*");

    private final String value;

    private BaileysAppName(String value) {
        this.value = value;
    }

    /**
     * Creates a new {@code BaileysAppName} from raw input.
     *
     * <p>The input is trimmed of surrounding whitespace but is otherwise
     * validated as-is; no case correction is performed.
     *
     * @param rawInput the raw slug string
     * @return a validated {@code BaileysAppName}
     * @throws InvalidBaileysAppNameException if the input is null, blank,
     *         outside the length range, or does not match the slug format
     */
    public static BaileysAppName of(String rawInput) {

        if (rawInput == null || rawInput.isBlank()) {
            throw InvalidBaileysAppNameException.forNullOrBlank();
        }

        String trimmed = rawInput.trim();

        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw InvalidBaileysAppNameException.forLengthOutOfRange(trimmed, MIN_LENGTH, MAX_LENGTH);
        }

        if (!SLUG_PATTERN.matcher(trimmed).matches() || !CONTAINS_LETTER.matcher(trimmed).matches()) {
            throw InvalidBaileysAppNameException.forInvalidFormat(trimmed);
        }

        return new BaileysAppName(trimmed);
    }

    /**
     * Returns the underlying slug value.
     *
     * @return the slug
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BaileysAppName that)) return false;
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
