package com.whatsbotai.domain.tenant.vo;

import com.whatsbotai.domain.tenant.exception.InvalidTaxIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TaxId — value object for Brazilian CPF and CNPJ")
public class TaxIdTest {

    @Nested
    @DisplayName("CPF creation")
    class CpfCreation {

        @ParameterizedTest(name = "should accept valid CPF: \"{0}\"")
        @ValueSource(strings = {
                "11144477735",
                "111.444.777-35",
                "52998224725",
                "529.982.247-25"
        })
        @DisplayName("should create a TaxId for valid CPF (masked or unmasked)")
        void shouldCreateForValidCpf(String validCpf) {
            TaxId taxId = TaxId.of(validCpf);

            assertThat(taxId).isNotNull();
            assertThat(taxId.type()).isEqualTo(TaxIdType.CPF);
        }

        @Test
        @DisplayName("should store CPF as digits only")
        void shouldStoreCpfAsDigitsOnly() {
            TaxId taxId = TaxId.of("111.444.777-35");

            assertThat(taxId.value()).isEqualTo("11144477735");
        }

        @ParameterizedTest(name = "should reject CPF with invalid check digits: \"{0}\"")
        @ValueSource(strings = {
                "11144477700",
                "12345678901",
                "111.444.777-00"
        })
        @DisplayName("should reject CPF with invalid check digits")
        void shouldRejectCpfWithInvalidCheckDigits(String invalidCpf) {
            assertThatThrownBy(() -> TaxId.of(invalidCpf))
                    .isInstanceOf(InvalidTaxIdException.class)
                    .hasMessageContaining("check digits");
        }

        @ParameterizedTest(name = "should reject all-repeated-digit CPF: \"{0}\"")
        @ValueSource(strings = {
                "00000000000",
                "11111111111",
                "99999999999"
        })
        @DisplayName("should reject CPF made of all repeated digits")
        void shouldRejectRepeatedDigitCpf(String repeated) {
            assertThatThrownBy(() -> TaxId.of(repeated))
                    .isInstanceOf(InvalidTaxIdException.class)
                    .hasMessageContaining("repeated digits");
        }
    }

    @Nested
    @DisplayName("CNPJ creation")
    class CnpjCreation {

        @ParameterizedTest(name = "should accept valid CNPJ: \"{0}\"")
        @ValueSource(strings = {
                "11222333000181",
                "11.222.333/0001-81",
                "04252011000110",
                "04.252.011/0001-10"
        })
        @DisplayName("should create a TaxId for valid CNPJ (masked or unmasked)")
        void shouldCreateForValidCnpj(String validCnpj) {
            TaxId taxId = TaxId.of(validCnpj);

            assertThat(taxId).isNotNull();
            assertThat(taxId.type()).isEqualTo(TaxIdType.CNPJ);
        }

        @Test
        @DisplayName("should store CNPJ as digits only")
        void shouldStoreCnpjAsDigitsOnly() {
            TaxId taxId = TaxId.of("11.222.333/0001-81");

            assertThat(taxId.value()).isEqualTo("11222333000181");
        }

        @ParameterizedTest(name = "should reject CNPJ with invalid check digits: \"{0}\"")
        @ValueSource(strings = {
                "11222333000100",
                "11.222.333/0001-00"
        })
        @DisplayName("should reject CNPJ with invalid check digits")
        void shouldRejectCnpjWithInvalidCheckDigits(String invalidCnpj) {
            assertThatThrownBy(() -> TaxId.of(invalidCnpj))
                    .isInstanceOf(InvalidTaxIdException.class)
                    .hasMessageContaining("check digits");
        }

        @ParameterizedTest(name = "should reject all-repeated-digit CNPJ: \"{0}\"")
        @ValueSource(strings = {
                "00000000000000",
                "11111111111111"
        })
        @DisplayName("should reject CNPJ made of all repeated digits")
        void shouldRejectRepeatedDigitCnpj(String repeated) {
            assertThatThrownBy(() -> TaxId.of(repeated))
                    .isInstanceOf(InvalidTaxIdException.class)
                    .hasMessageContaining("repeated digits");
        }
    }

    @Nested
    @DisplayName("Invalid input")
    class InvalidInput {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should reject null, empty, or blank input")
        void shouldRejectNullEmptyOrBlank(String invalid) {
            assertThatThrownBy(() -> TaxId.of(invalid))
                    .isInstanceOf(InvalidTaxIdException.class)
                    .hasMessageContaining("null or blank");
        }

        @ParameterizedTest(name = "should reject wrong-length input: \"{0}\"")
        @ValueSource(strings = {
                "123",
                "1234567890",
                "123456789012",
                "123456789012345",
                "abcdefghijk"
        })
        @DisplayName("should reject input that is neither 11 nor 14 digits")
        void shouldRejectInvalidLength(String invalid) {
            assertThatThrownBy(() -> TaxId.of(invalid))
                    .isInstanceOf(InvalidTaxIdException.class);
        }
    }

    @Nested
    @DisplayName("Formatting")
    class Formatting {

        @Test
        @DisplayName("should format CPF with standard mask")
        void shouldFormartCpf() {
            TaxId taxId = TaxId.of("11144477735");

            assertThat(taxId.formatted()).isEqualTo("111.444.777-35");
        }

        @Test
        @DisplayName("should format CNPJ with standard mask")
        void shouldFormatCnpj() {
            TaxId taxId = TaxId.of("11222333000181");

            assertThat(taxId.formatted()).isEqualTo("11.222.333/0001-81");
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("same document with different masks should be equal")
        void sameDocumentDifferentMasksShouldBeEqual() {
            TaxId masked = TaxId.of("111.444.777-35");
            TaxId unmasked = TaxId.of("11144477735");

            assertThat(masked).isEqualTo(unmasked);
            assertThat(masked.hashCode()).isEqualTo(unmasked.hashCode());
        }

        @Test
        @DisplayName("different documents should not be equal")
        void differentDocumentsShouldNotBeEqual() {
            TaxId cpf = TaxId.of("11144477735");
            TaxId cnpj = TaxId.of("11222333000181");

            assertThat(cpf).isNotEqualTo(cnpj);
        }
    }
}
