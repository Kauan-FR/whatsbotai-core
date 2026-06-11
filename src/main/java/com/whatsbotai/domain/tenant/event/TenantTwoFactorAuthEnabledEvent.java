package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

/**
 * Emitted when a {@code Tenant} enables two-factor authentication.
 *
 * <p>Typical reactions: notifying the tenant of the security change,
 * updating audit log, requiring 2FA on subsequent logins.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantTwoFactorAuthEnabledEvent extends TenantEvent {
    public TenantTwoFactorAuthEnabledEvent(TenantId tenantId) {
        super(tenantId);
    }
}
