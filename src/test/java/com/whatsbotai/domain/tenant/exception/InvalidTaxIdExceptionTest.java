package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InvalidTaxIdException — thrown when a CPF/CNPJ violates domain rules")
class InvalidTaxIdExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        InvalidTaxIdException exception = InvalidTaxIdException.forNullOrBlank();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should produce a clear message for null or blank input")
    void shouldProduceClearMessageForNullOrBlank() {
        InvalidTaxIdException exception = InvalidTaxIdException.forNullOrBlank();

        assertThat(exception.getMessage())
                .containsIgnoringCase("tax id")
                .containsAnyOf("null", "blank", "empty");
    }

    @Test
    @DisplayName("should carry the rejected value for invalid length")
    void shouldCarryRejectedValueForInvalidLength() {
        String rejected = "123";

        InvalidTaxIdException exception = InvalidTaxIdException.forInvalidlength(rejected);

        assertThat(exception.getMessage()).contains(rejected);
    }

    @Test
    @DisplayName("should carry the rejected value for invalid check digits")
    void shouldCarryRejectedValueForInvalidCheckDigits() {
        String rejected = "11144477700";

        InvalidTaxIdException exception = InvalidTaxIdException.forInvalidCheckDigits(rejected);

        assertThat(exception.getMessage()).contains(rejected);
    }
}
