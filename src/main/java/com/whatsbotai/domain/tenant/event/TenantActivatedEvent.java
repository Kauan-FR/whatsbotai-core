package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

/**
 * Emitted when a {@code Tenant} transitions from {@code PENDING} to {@code ACTIVE}.
 *
 * <p>Typical reactions: provisioning Baileys WhatsApp session, sending
 * "your account is now active" notification, starting metered usage counters.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantActivatedEvent extends TenantEvent {
    public TenantActivatedEvent(TenantId tenantId) {
        super(tenantId);
    }
}
