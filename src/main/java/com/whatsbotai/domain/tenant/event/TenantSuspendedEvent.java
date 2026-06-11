package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

/**
 * Emitted when a {@code Tenant} transitions from {@code ACTIVE} to {@code SUSPENDED}.
 *
 * <p>Typical reactions: stopping Baileys WhatsApp instances, halting message
 * processing, notifying the tenant about the suspension and any remediation steps.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantSuspendedEvent extends TenantEvent {
    public TenantSuspendedEvent(TenantId tenantId) {
        super(tenantId);
    }
}
