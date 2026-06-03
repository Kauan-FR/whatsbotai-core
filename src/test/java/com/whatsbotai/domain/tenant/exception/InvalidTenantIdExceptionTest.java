package com.whatsbotai.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.whatsbotai.domain.shared.exception.DomainException;

@DisplayName("InvalidTenantIdException — thrown when a TenantId value violates domain rules")
class InvalidTenantIdExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        InvalidTenantIdException exception = InvalidTenantIdException.forNullValue();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should produce a clear message for null input")
    void shouldProduceClearMessageForNull() {
        InvalidTenantIdException exception = InvalidTenantIdException.forNullValue();

        assertThat(exception.getMessage())
                .containsIgnoringCase("tenant id")
                .containsIgnoringCase("null");
    }

    @Test
    @DisplayName("should carry the rejected value for malformed UUID strings")
    void shouldCarryRejectedValueForMalformedUuid() {
        String rejected = "not-a-uuid";

        InvalidTenantIdException exception = InvalidTenantIdException.forMalformedUuid(rejected);

        assertThat(exception.getMessage()).contains(rejected);
    }
}
