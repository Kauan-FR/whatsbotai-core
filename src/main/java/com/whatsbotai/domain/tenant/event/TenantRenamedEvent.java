package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;

/**
 * Emitted when a {@code Tenant} is renamed.
 *
 * <p>Typical reactions: updating dashboards, refreshing display caches,
 * informing audit logs.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantRenamedEvent extends TenantEvent{

    private final String oldName;
    private final String newName;

    public TenantRenamedEvent(TenantId tenantId, String oldName, String newName) {
        super(tenantId);
        this.oldName = Objects.requireNonNull(oldName, "oldName must not be null");
        this.newName = Objects.requireNonNull(newName, "newName must not be null");
    }

    public String oldName() {
        return oldName;
    }

    public String newName() {
        return newName;
    }
}
