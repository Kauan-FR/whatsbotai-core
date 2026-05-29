package com.whatsbotai.domain.tenant.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.whatsbotai.domain.shared.exception.DomainException;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InvalidBaileysAppNameException — thrown when a Baileys app name violates domain rules")
public class InvalidBaileysAppNameExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        InvalidBaileysAppNameException exception = InvalidBaileysAppNameException.forNullOrBlank();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should produce a clear message for null or blank input")
    void shouldProduceClearMessageForNullOrBlank() {
        InvalidBaileysAppNameException exception = InvalidBaileysAppNameException.forNullOrBlank();

        assertThat(exception.getMessage())
                .containsIgnoringCase("baileys app name")
                .containsAnyOf("null", "blank", "empty");
    }

    @Test
    @DisplayName("should carry the rejected value for invalid format")
    void shouldCarryRejectedValueForInvalidFormat() {
        String rejected = "Invalid_Name!";

        InvalidBaileysAppNameException exception = InvalidBaileysAppNameException.forInvalidFormat(rejected);

        assertThat(exception.getMessage()).contains(rejected);
    }

    @Test
    @DisplayName("should carry length information when out of range")
    void shouldCarryLengthInfoWhenOutOfRange() {
        String rejected = "ab";

        InvalidBaileysAppNameException exception = InvalidBaileysAppNameException.forLengthOutOfRange(rejected, 3, 50);

        assertThat(exception.getMessage())
            .contains(rejected)
            .contains("3")
            .contains("50");
    }
}
