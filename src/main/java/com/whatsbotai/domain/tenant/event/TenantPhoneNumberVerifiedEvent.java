package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

/**
 * Emitted when a {@code Tenant}'s contact phone number is marked as verified.
 *
 * <p>Typical reactions: unlocking features that require a verified phone,
 * updating WhatsApp messaging trust signals.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantPhoneNumberVerifiedEvent extends TenantEvent {
    public TenantPhoneNumberVerifiedEvent(TenantId tenantId) {
        super(tenantId);
    }
}
