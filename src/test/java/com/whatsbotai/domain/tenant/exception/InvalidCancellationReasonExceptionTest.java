package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InvalidCancellationReasonException — thrown when an admin force-cancels without a reason")
public class InvalidCancellationReasonExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        InvalidCancellationReasonException exception =
                InvalidCancellationReasonException.forNullOrBlank();

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should produce a clear message for null or blank reason")
    void shouldProduceClearMessageForNullOrBlank() {
        InvalidCancellationReasonException exception =
                InvalidCancellationReasonException.forNullOrBlank();

        assertThat(exception.getMessage())
                .containsIgnoringCase("reason")
                .containsAnyOf("null", "blank", "empty");
    }
}
