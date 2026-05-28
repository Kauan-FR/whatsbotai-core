package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InvalidEmailException - thrown when an email value violates domain rules")
public class InvalidEmailExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    public void shouldExtendDomaindException() {
        InvalidEmailException exception = new InvalidEmailException("any message");

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should carry the rejected value in the message")
    public void shouldCarryRejectedValueInMessage() {
        String rejected = "not-an-email";

        InvalidEmailException exception = InvalidEmailException.forValue(rejected);

        assertThat(exception.getMessage()).contains(rejected);
    }

    @Test
    @DisplayName("should produce a clear message for null input")
    private void shouldProduceClearMessageForNull() {
        InvalidEmailException exception = InvalidEmailException.forNullOrBlank();

        assertThat(exception.getMessage())
                .containsIgnoringCase("email")
                .containsAnyOf("null", "blank", "empty");
    }
}
