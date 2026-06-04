package com.whatsbotai.domain.tenant.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Lifecycle status of a {@code Tenant} aggregate, modeled as a state machine.
 *
 * <p>The valid transitions are encoded inside this enum so the rule lives in
 * the domain itself, rather than being spread across services or controllers.
 * Each status declares which target statuses it may transition to.
 *
 * <p><strong>State machine:</strong>
 * <pre>
 *   PENDING   → ACTIVE | CANCELLED
 *   ACTIVE    → SUSPENDED | CANCELLED
 *   SUSPENDED → ACTIVE | CANCELLED
 *   CANCELLED → (terminal — no outgoing transitions)
 * </pre>
 *
 * @author Kauan
 * @since 1.0
 */
public enum TenantStatus {

    /** Tenant created but not yet operational (awaiting confirmation). */
    PENDING,

    /** Tenant is operational and may use the system. */
    ACTIVE,

    /** Tenant is temporarily blocked (may be reactivated). */
    SUSPENDED,

    /** Tenant has been definitively cancelled; terminal state. */
    CANCELLED;

    private static final Map<TenantStatus, Set<TenantStatus>> ALLOWED_TRANSITIONS = Map.of(
            PENDING,   EnumSet.of(ACTIVE, CANCELLED),
            ACTIVE,    EnumSet.of(SUSPENDED, CANCELLED),
            SUSPENDED, EnumSet.of(ACTIVE, CANCELLED),
            CANCELLED, EnumSet.noneOf(TenantStatus.class)
    );

    /**
     * Returns the initial status assigned to a newly created {@code Tenant}.
     *
     * @return {@link #PENDING}
     */
    public static TenantStatus initial() {
        return PENDING;
    }

    /**
     * Checks whether this status may transition to the given target.
     *
     * @param target the candidate target status (may be {@code null})
     * @return {@code true} if the transition is defined as valid; {@code false} otherwise
     */
    public boolean canTransitionTo(TenantStatus target) {
        if (target == null) {
            return false;
        }
        return ALLOWED_TRANSITIONS.get(this).contains(target);
    }

    /**
     * Indicates whether this is a terminal status (no outgoing transitions).
     *
     * @return {@code true} if the status is terminal
     */
    public boolean isTerminal() {
        return ALLOWED_TRANSITIONS.get(this).isEmpty();
    }

    /**
     * Indicates whether the tenant is operational in this status, i.e.
     * may actually use the system.
     *
     * @return {@code true} only for {@link #ACTIVE}
     */
    public boolean isOperational() {
        return this == ACTIVE;
    }
}