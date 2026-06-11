package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

/**
 * Emitted when a {@code Tenant}'s contact email is marked as verified.
 *
 * <p>Typical reactions: unlocking features that require a verified email
 * (e.g. {@code activate()}, {@code enableTwoFactorAuth()}), updating CRM
 * status, sending a confirmation notification.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantEmailVerifiedEvent extends TenantEvent {
    public TenantEmailVerifiedEvent(TenantId tenantId) {
        super(tenantId);
    }
}
