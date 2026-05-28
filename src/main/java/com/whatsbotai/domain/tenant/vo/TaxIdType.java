package com.whatsbotai.domain.tenant.vo;

/**
 * Represents the type of a Brazilian tax identifier.
 *
 * <ul>
 *   <li>{@link #CPF} — 11 digits, used for natural persons</li>
 *   <li>{@link #CNPJ} — 14 digits, used for legal entities</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */

public enum TaxIdType {

    /** Cadastro de Pessoa Física — 11 digits. */
    CPF(11),

    /** Cadastro Nacional da Pessoa Jurídica — 14 digits. */
    CNPJ(14);

    private final int length;

    private TaxIdType(int length) {
        this.length = length;
    }

    /**
     * Returns the exact number of digits for this tax id type.
     *
     * @return 11 for CPF, 14 for CNPJ
     */
    public int length() {
        return length();
    }
}
