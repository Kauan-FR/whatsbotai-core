package com.whatsbotai.domain.tenant.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import com.whatsbotai.domain.tenant.exception.InvalidPhoneNumberException;

/**
 * Value object representing a valid Brazilian mobile phone number, stored in
 * E.164 digit form (country code + area code + subscriber number, no symbols).
 *
 * <p>A {@code PhoneNumber} is immutable, self-validating, and compared by value.
 * Input may be provided masked or unmasked, with or without the {@code +55}
 * country code; it is always normalized to {@code 55} + DDD + 9-digit number,
 * e.g. {@code "5581987654321"}.
 *
 * <p><strong>Validation rules (Brazilian mobile):</strong>
 * <ul>
 *   <li>Must not be null, empty, or blank</li>
 *   <li>Area code (DDD) must be in the range 11–99</li>
 *   <li>Subscriber number must have 9 digits and start with 9</li>
 * </ul>
 *
 * <p>Fixed-line numbers are intentionally rejected, since this domain targets
 * WhatsApp messaging, which requires a mobile number.
 *
 * @author Kauan
 * @since 1.0
 */
public final class PhoneNumber {

    private static final String BRAZIL_COUNTRY_CODE = "55";
    private static final String WHATSAPP_JID_SUFFIX = "@s.whatsapp.net";

    /**
     * Matches a normalized Brazilian mobile: country code 55, DDD 11–99,
     * mandatory leading 9, then 8 more digits.
     */
    private static final Pattern E164_BR_MOBILE = Pattern.compile("^55[1-9][1-9]9\\d{8}$");

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    /**
     * Creates a new {@code PhoneNumber} from raw input.
     *
     * <p>Accepts masked or unmasked input, with or without the {@code +55}
     * country code. National numbers (11 digits) are assumed Brazilian and
     * prefixed with {@code 55}.
     *
     * @param rawInput the raw phone string
     * @return a validated {@code PhoneNumber} in E.164 digit form
     * @throws InvalidPhoneNumberException if the input is null, blank, or not a valid Brazilian mobile
     */
    public static PhoneNumber of(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw InvalidPhoneNumberException.forNullOrBlanck();
        }

        String digits = rawInput.replaceAll("\\D", "");
        String normalized = withCountryCode(digits);

        if (!E164_BR_MOBILE.matcher(normalized).matches()) {
            throw InvalidPhoneNumberException.forValue(rawInput);
        }

        return new PhoneNumber(normalized);
    }

    private static String withCountryCode(String digits) {

        // National format: DDD (2) + mobile (9) = 11 digits, prepend country code.
        if (digits.length() == 11) {
            return BRAZIL_COUNTRY_CODE + digits;
        }
        return digits;
    }

    /**
     * Returns the phone number in E.164 digit form (no symbols), e.g. {@code "5581987654321"}.
     *
     * @return the normalized phone value
     */
    public String value() {
        return value;
    }

    /**
     * Returns the two-digit area code (DDD), e.g. {@code "81"}.
     *
     * @return the area code
     */
    public String areaCode() {
        return value.substring(2, 4);
    }

    /**
     * Returns the 9-digit subscriber number, e.g. {@code "987654321"}.
     *
     * @return the subscriber number
     */
    public String subscriberNumber() {
        return value.substring(4);
    }

    /**
     * Returns the number formatted for display, e.g. {@code "+55 (81) 98765-4321"}.
     *
     * @return the formatted representation
     */
    public String formatted() {
        return String.format("+%s (%s) %s-%s",
            BRAZIL_COUNTRY_CODE,
            areaCode(),
            subscriberNumber().substring(0, 5),
            subscriberNumber().substring(5)
        );
    }

    /**
     * Returns the WhatsApp JID for this number, e.g. {@code "5581987654321@s.whatsapp.net"}.
     *
     * @return the WhatsApp JID
     */
        public String toWhatsAppJid() {
        return value + WHATSAPP_JID_SUFFIX;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PhoneNumber that)) return false;
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
