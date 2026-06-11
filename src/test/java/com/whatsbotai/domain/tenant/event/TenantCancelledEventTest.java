package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantCancelledEvent — emitted when a tenant transitions to CANCELLED")
public class TenantCancelledEventTest {

    @Test
    @DisplayName("voluntary cancellation should carry no reason")
    void voluntaryCancellationShouldHaveNoReason() {
        TenantCancelledEvent event = new TenantCancelledEvent(TenantId.generate());

        assertThat(event.reason()).isEmpty();
    }

    @Test
    @DisplayName("force cancellation should carry the documented reason")
    void forceCancellationShouldCarryReason() {
        String reason = "Repeated terms of service violations";

        TenantCancelledEvent event = new TenantCancelledEvent(TenantId.generate(), reason);

        assertThat(event.reason()).contains(reason);
    }
}
