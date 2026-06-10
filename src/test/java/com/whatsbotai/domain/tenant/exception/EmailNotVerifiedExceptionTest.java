package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EmailNotVerifiedException — thrown when an operation requires a verified email but the tenant has none")
public class EmailNotVerifiedExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        EmailNotVerifiedException exception = EmailNotVerifiedException.forOperation("activate");

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should carry the attempted operation name in the message")
    void shouldCarryAttemptedOperation() {
        EmailNotVerifiedException exception = EmailNotVerifiedException.forOperation("enableTwoFactorAuth");

        assertThat(exception.getMessage())
                .containsIgnoringCase("email")
                .containsIgnoringCase("verified")
                .contains("enableTwoFactorAuth");
    }
}
