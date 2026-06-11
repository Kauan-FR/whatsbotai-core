package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.model.TenantPlan;
import com.whatsbotai.domain.tenant.model.TenantStatus;
import com.whatsbotai.domain.tenant.vo.BaileysAppName;
import com.whatsbotai.domain.tenant.vo.Email;
import com.whatsbotai.domain.tenant.vo.PhoneNumber;
import com.whatsbotai.domain.tenant.vo.TaxId;
import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;

/**
 * Emitted when a new {@code Tenant} is registered in the system.
 *
 * <p>Carries a snapshot of the tenant's initial state so downstream handlers
 * can react without needing to query the aggregate (which may not yet be
 * persisted at the moment the event is consumed).
 *
 * <p>Typical reactions: sending welcome email, provisioning the Baileys
 * WhatsApp session, registering the tenant in billing systems, notifying
 * internal administrators.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantCreatedEvent extends TenantEvent {

    private final String name;
    private final Email email;
    private final TaxId taxId;
    private final PhoneNumber phoneNumber;
    private final BaileysAppName baileysAppName;
    private final TenantStatus initialStatus;
    private final TenantPlan initialPlan;

    public TenantCreatedEvent(TenantId tenantId,
                              String name,
                              Email email,
                              TaxId taxId,
                              PhoneNumber phoneNumber,
                              BaileysAppName baileysAppName,
                              TenantStatus initialStatus,
                              TenantPlan initialPlan) {

        super(tenantId);

        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.taxId = Objects.requireNonNull(taxId, "taxId must not be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber must not be null");
        this.baileysAppName = Objects.requireNonNull(baileysAppName, "baileysAppName must not be null");
        this.initialStatus = Objects.requireNonNull(initialStatus, "initialStatus must not be null");
        this.initialPlan = Objects.requireNonNull(initialPlan, "initialPlan must not be null");
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public TaxId taxId() {
        return taxId;
    }

    public PhoneNumber phoneNumber() {
        return phoneNumber;
    }

    public BaileysAppName baileysAppName() {
        return baileysAppName;
    }

    public TenantStatus initialStatus() {
        return initialStatus;
    }

    public TenantPlan initialPlan() {
        return initialPlan;
    }
}
