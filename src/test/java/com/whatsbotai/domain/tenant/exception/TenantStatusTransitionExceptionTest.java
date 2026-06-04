package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import com.whatsbotai.domain.tenant.model.TenantStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantStatusTransitionException — thrown for invalid tenant status transitions")
class TenantStatusTransitionExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        TenantStatusTransitionException exception =
                TenantStatusTransitionException.between(TenantStatus.CANCELLED, TenantStatus.ACTIVE);

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should carry both source and target status in the message")
    void shouldCarryBothStatusesInMessage() {
        TenantStatusTransitionException exception =
                TenantStatusTransitionException.between(TenantStatus.CANCELLED, TenantStatus.ACTIVE);

        assertThat(exception.getMessage())
                .contains("CANCELLED")
                .contains("ACTIVE");
    }
}