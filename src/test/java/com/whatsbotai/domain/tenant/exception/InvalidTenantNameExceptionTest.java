package com.whatsbotai.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.whatsbotai.domain.shared.exception.DomainException;

@DisplayName("InvalidTenantNameException — thrown when a tenant name violates domain rules")
public class InvalidTenantNameExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        InvalidTenantNameException exception = InvalidTenantNameException.forNullOrBlank();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should produce a clear message for null or blank input")
    void shouldProduceClearMessageForNullOrBlank() {
        InvalidTenantNameException exception = InvalidTenantNameException.forNullOrBlank();

        assertThat(exception.getMessage())
                .containsIgnoringCase("tenant name")
                .containsAnyOf("null", "blank", "empty");
    }

    @Test
    @DisplayName("should carry rejected value and minimum length when too short")
    void shouldCarryRejectedValueAndMinimumLengthWhenTooShort() {
        String rejected = "A";

        InvalidTenantNameException exception = InvalidTenantNameException.forTooShort(rejected, 2);

        assertThat(exception.getMessage())
                .contains(rejected)
                .contains("2");
    }

    @Test
    @DisplayName("should carry rejected value and maximum length when too long")
    void shouldCarryRejectedValueAndMaximumLengthWhenTooLong() {
        String rejected = "very-long-name-that-exceeds-the-limit";

        InvalidTenantNameException exception = InvalidTenantNameException.forTooLong(rejected, 100);

        assertThat(exception.getMessage())
                .contains(rejected)
                .contains("100");
    }
}
