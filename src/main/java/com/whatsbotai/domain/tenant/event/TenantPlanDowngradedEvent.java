package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.model.TenantPlan;
import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;

/**
 * Emitted when a {@code Tenant} downgrades from a higher-tier plan to a lower-tier plan.
 *
 * <p>Typical reactions: enforcing the new (lower) limits, scheduling the
 * removal of premium features at the next billing cycle, notifying the
 * tenant about lost capabilities.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantPlanDowngradedEvent extends TenantEvent {

    private final TenantPlan oldPlan;
    private final TenantPlan newPlan;

    public TenantPlanDowngradedEvent(TenantId tenantId, TenantPlan oldPlan, TenantPlan newPlan) {
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
