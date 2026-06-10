package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantAlreadyCancelledException — thrown when a write is attempted on a cancelled tenant")
public class TenantAlreadyCancelledExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        TenantAlreadyCancelledException exception = TenantAlreadyCancelledException.forOperation("changeEmail");

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should carry the attempted operation name in the message")
    void shouldCarryAttemptedOperation() {
        TenantAlreadyCancelledException exception = TenantAlreadyCancelledException.forOperation("upgradeTo");

        assertThat(exception.getMessage())
                .containsIgnoringCase("cancelled")
                .contains("upgradeTo");
    }
}
