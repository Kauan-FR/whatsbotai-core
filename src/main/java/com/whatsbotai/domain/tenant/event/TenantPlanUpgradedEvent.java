package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.model.TenantPlan;
import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;

/**
 * Emitted when a {@code Tenant} upgrades from a lower-tier plan to a higher-tier plan.
 *
 * <p>Typical reactions: applying new limits and features, prorating
 * billing, unlocking premium AI levels, sending a "thank you for upgrading"
 * notification.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantPlanUpgradedEvent extends TenantEvent {

    private final TenantPlan oldPlan;
    private final TenantPlan newPlan;

    public TenantPlanUpgradedEvent(TenantId tenantId, TenantPlan oldPlan, TenantPlan newPlan) {
        super(tenantId);
        this.oldPlan = Objects.requireNonNull(oldPlan, "oldPlan must not be null");
        this.newPlan = Objects.requireNonNull(newPlan, "newPlan must not be null");
    }

    public TenantPlan oldPlan() {
        return oldPlan;
    }

    public TenantPlan newPlan() {
        return newPlan;
    }
}
