package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("InvalidPhoneNumberException — thrown when a phone number violates domain rules")
public class InvalidPhoneNumberExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        InvalidPhoneNumberException exception = InvalidPhoneNumberException.forNullOrBlanck();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should produce a clear message for null or blank input")
    void shouldProduceClearMessageForNullOrBlank() {
        InvalidPhoneNumberException exception = InvalidPhoneNumberException.forNullOrBlanck();

        assertThat(exception.getMessage())
                .containsIgnoringCase("phone")
                .containsAnyOf("null", "blank", "empty");
    }

    @Test
    @DisplayName("should carry the rejected value in the message")
    void shouldCarryRejectedValue() {
        String rejected = "123";

        InvalidPhoneNumberException exception = InvalidPhoneNumberException.forValue(rejected);

        assertThat(exception.getMessage()).contains(rejected);
    }
}
