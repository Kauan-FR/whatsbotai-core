package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.model.TenantPlan;
import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tenant plan change events — emitted on upgrade and downgrade")
public class TenantPlanChangeEventsTest {

    @Test
    @DisplayName("TenantPlanUpgradedEvent should carry the old and new plans")
    void upgradeEventShouldCarryBothPlans() {
        TenantPlanUpgradedEvent event = new TenantPlanUpgradedEvent(
            TenantId.generate(), TenantPlan.STARTER, TenantPlan.PROFESSIONAL);

        assertThat(event.oldPlan()).isEqualTo(TenantPlan.STARTER);
        assertThat(event.newPlan()).isEqualTo(TenantPlan.PROFESSIONAL);
    }

    @Test
    @DisplayName("TenantPlanDowngradedEvent should carry the old and new plans")
    void downgradeEventShouldCarryBothPlans() {
        TenantPlanDowngradedEvent event = new TenantPlanDowngradedEvent(
            TenantId.generate(), TenantPlan.PROFESSIONAL, TenantPlan.STARTER);

        assertThat(event.oldPlan()).isEqualTo(TenantPlan.PROFESSIONAL);
        assertThat(event.newPlan()).isEqualTo(TenantPlan.STARTER);
    }
}
