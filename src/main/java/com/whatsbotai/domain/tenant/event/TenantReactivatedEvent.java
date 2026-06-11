package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

/**
 * Emitted when a {@code Tenant} transitions from {@code SUSPENDED} back to {@code ACTIVE}.
 *
 * <p>Typical reactions: re-enabling Baileys WhatsApp instances, resuming
 * message processing, notifying the tenant that the account is operational again.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantReactivatedEvent extends TenantEvent {
    public TenantReactivatedEvent(TenantId tenantId) {
        super(tenantId);
    }
}
