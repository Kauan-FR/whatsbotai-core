package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.shared.event.AbstractDomainEvent;
import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;

/**
 * Intermediate base class for all domain events emitted by the
 * {@code Tenant} aggregate. Carries the {@link TenantId} that every
 * tenant-scoped event must reference, so handlers can filter and
 * route events by tenant without inspecting subclass-specific payload.
 *
 * @author Kauan
 * @since 1.0
 */
public abstract class TenantEvent extends AbstractDomainEvent {

    private final TenantId tenantId;

    protected TenantEvent(TenantId tenantId) {
        super();
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
    }

    /**
     * Returns the identifier of the tenant that emitted this event.
     *
     * @return the tenant id (never {@code null})
     */
    public final TenantId tenantId() {
        return tenantId;
    }
}
