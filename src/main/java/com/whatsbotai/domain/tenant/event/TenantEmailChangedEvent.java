package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.Email;
import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;

/**
 * Emitted when a {@code Tenant} changes its contact email.
 *
 * <p>Carries both the previous and the new email so handlers can react
 * appropriately: revoke any tokens sent to the old address, send a
 * confirmation challenge to the new address, update CRM contact data, etc.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantEmailChangedEvent extends TenantEvent{

    private final Email oldEmail;
    private final Email newEmail;

    public TenantEmailChangedEvent(TenantId tenantId, Email oldEmail, Email newEmail) {
        super(tenantId);
        this.oldEmail = Objects.requireNonNull(oldEmail, "oldEmail must not be null");
        this.newEmail = Objects.requireNonNull(newEmail, "newEmail must not be null");
    }

    public Email oldEmail() {
        return oldEmail;
    }

    public Email newEmail() {
        return newEmail;
    }
}
