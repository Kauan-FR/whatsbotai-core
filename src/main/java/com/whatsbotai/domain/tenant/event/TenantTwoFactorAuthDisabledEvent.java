package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

/**
 * Emitted when a {@code Tenant} disables two-factor authentication.
 *
 * <p>Typical reactions: notifying the tenant (warn about reduced security),
 * updating audit log, possibly flagging the account for additional monitoring.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantTwoFactorAuthDisabledEvent extends TenantEvent {
    public TenantTwoFactorAuthDisabledEvent(TenantId tenantId) {
        super(tenantId);
    }
}
