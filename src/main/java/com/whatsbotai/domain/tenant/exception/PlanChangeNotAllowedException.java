package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import com.whatsbotai.domain.tenant.model.TenantPlan;
import com.whatsbotai.domain.tenant.model.TenantStatus;

/**
 * Thrown when a tenant plan change is rejected by the domain.
 *
 * <p>Specific causes include:
 * <ul>
 *   <li>The tenant is not in the {@link TenantStatus#ACTIVE} state — plan
 *       changes are blocked while the tenant is pending, suspended, or
 *       cancelled, to prevent fraud and enforce the lifecycle invariants</li>
 *   <li>{@code upgradeTo(target)} was called with a {@code target} that is
 *       not strictly higher than the current plan</li>
 *   <li>{@code downgradeTo(target)} was called with a {@code target} that
 *       is not strictly lower than the current plan</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class PlanChangeNotAllowedException extends DomainException{

    private PlanChangeNotAllowedException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a plan change attempted while the tenant is
     * not in the ACTIVE status.
     *
     * @param currentStatus the tenant's current status
     * @return a new exception including the current status
     */
    public static PlanChangeNotAllowedException becauseTenantIsNotActive(TenantStatus currentStatus) {
        return new PlanChangeNotAllowedException(
            "Plan change is only allowed for ACTIVE tenants; current status is " + currentStatus
        );
    }

    /**
     * Creates an exception for an {@code upgradeTo} call whose target is not
     * strictly higher than the current plan.
     *
     * @param currentPlan the current plan
     * @param targetPlan  the requested target plan
     * @return a new exception including both plans
     */
    public static PlanChangeNotAllowedException becauseTargetIsNotUpgrade(TenantPlan currentPlan, TenantPlan targetPlan) {
        return new PlanChangeNotAllowedException(
            "Target plan " + targetPlan + " is not an upgrade from current plan " + currentPlan
        );
    }

    /**
     * Creates an exception for a {@code downgradeTo} call whose target is not
     * strictly lower than the current plan.
     *
     * @param currentPlan the current plan
     * @param targetPlan  the requested target plan
     * @return a new exception including both plans
     */
    public static PlanChangeNotAllowedException becauseTargetIsNotDowngrade(TenantPlan currentPlan, TenantPlan targetPlan) {
        return new PlanChangeNotAllowedException(
            "Target plan " + targetPlan + " is not a downgrade from current plan " + currentPlan

        );
    }
}
