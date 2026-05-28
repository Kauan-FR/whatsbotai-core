package com.whatsbotai.domain.tenant.vo;

import com.whatsbotai.domain.tenant.exception.InvalidTaxIdException;

import java.util.Objects;

/**
 * Value object representing a valid Brazilian tax identifier (CPF or CNPJ).
 *
 * <p>A {@code TaxId} is immutable, self-validating, and compared by value.
 * It is stored internally as digits only (no mask), regardless of whether
 * the input was provided masked ({@code "111.444.777-35"}) or unmasked
 * ({@code "11144477735"}).
 *
 * <p><strong>Validation rules:</strong>
 * <ul>
 *   <li>Must not be null, empty, or blank</li>
 *   <li>After stripping non-digits, must have 11 (CPF) or 14 (CNPJ) digits</li>
 *   <li>Must not consist entirely of repeated digits</li>
 *   <li>Must pass modulo-11 check digit verification</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class TaxId {

    private static final int CPF_LENGTH = 11;
    private static final int CNPJ_LENGTH = 14;

    /** Weights for the CNPJ first check digit (12 base digits). */
    private static final int[] CNPJ_DV1_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6 ,5, 4, 3 ,2};

    /** Weights for the CNPJ second check digit (13 digits). */
    private static final int[] CNPJ_DV2_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private final String value;
    private final TaxIdType type;

    private TaxId(String value, TaxIdType type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Creates a new {@code TaxId} from raw input that may be masked or unmasked.
     *
     * @param rawInput the raw CPF or CNPJ string
     * @return a validated {@code TaxId} stored as digits only
     * @throws InvalidTaxIdException if the input is null, blank, has an invalid length,
     *                               consists of repeated digits, or fails check digit verification
     */
    public static TaxId of(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw InvalidTaxIdException.forNullOrBlank();
        }

        String digits = rawInput.replaceAll("\\D", "");

        TaxIdType resolvedType = resolveType(digits);

        if (hasAllRepeatedDigits(digits)) {
            throw InvalidTaxIdException.forRepeatedDigits(digits);
        }

        boolean valid = resolvedType == TaxIdType.CPF
                ? isValidCpf(digits)
                : isValidCnpj(digits);

        if (!valid) {
            throw InvalidTaxIdException.forInvalidCheckDigits(digits);
        }

        return new TaxId(digits, resolvedType);
    }

    private static TaxIdType resolveType(String digits) {
        if (digits.length() == CPF_LENGTH) {
            return TaxIdType.CPF;
        }
        if (digits.length() == CNPJ_LENGTH) {
            return TaxIdType.CNPJ;
        }
        throw InvalidTaxIdException.forInvalidlength(digits);
    }

    private static boolean hasAllRepeatedDigits(String digits) {
        return digits.chars().distinct().count() == 1;
    }

    private static boolean isValidCpf(String cpf) {
        int dv1 = cumputeCpfCheckDigit(cpf, 9, 10);
        int dv2 = cumputeCpfCheckDigit(cpf, 10, 11);

        return dv1 == charToInt(cpf, 9) && dv2 == charToInt(cpf, 10);
    }

    /**
     * Computes a CPF check digit using modulo 11.
     *
     * @param cpf          the digits string
     * @param length       how many leading digits to weigh
     * @param startWeight  the starting (highest) weight, decremented per digit
     * @return the computed check digit (0-9)
     */
    private static int cumputeCpfCheckDigit(String cpf, int length, int startWeight) {
        int sum = 0;
        int weight = startWeight;
        for (int i = 0; i < length; i++) {
            sum += charToInt(cpf, i) * weight;
            weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean isValidCnpj(String cnpj) {
        int dv1 = cumputeCnpjCheckDigit(cnpj, CNPJ_DV1_WEIGHTS);
        int dv2 = cumputeCnpjCheckDigit(cnpj, CNPJ_DV2_WEIGHTS);

        return dv1 == charToInt(cnpj, 12) && dv2 == charToInt(cnpj, 13);
    }

    /**
     * Computes a CNPJ check digit using modulo 11 with the given weight array.
     *
     * @param cnpj    the digits string
     * @param weights the weight array (length determines how many digits are weighed)
     * @return the computed check digit (0-9)
     */
    private static int cumputeCnpjCheckDigit(String cnpj, int[] weight) {
        int sum = 0;
        for (int i = 0; i < weight.length; i++) {
            sum += charToInt(cnpj, i) * weight[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static int charToInt(String digits, int index) {
        return digits.charAt(index) - '0';
    }

    /**
     * Returns the tax id as digits only, without any mask.
     *
     * @return the unmasked tax id value
     */
    public String value() {
        return value;
    }

    /**
     * Returns the type of this tax id.
     *
     * @return {@link TaxIdType#CPF} or {@link TaxIdType#CNPJ}
     */
    public TaxIdType type() {
        return type;
    }

    /**
     * Returns the tax id formatted with the standard Brazilian mask.
     *
     * <p>CPF: {@code 000.000.000-00} — CNPJ: {@code 00.000.000/0000-00}
     *
     * @return the masked representation
     */
    public String formatted() {
        return type == TaxIdType.CPF
                ? value.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4")
                : value.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TaxId that)) return false;
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
