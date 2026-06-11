package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.PhoneNumber;
import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;

/**
 * Emitted when a {@code Tenant} changes its contact phone number.
 *
 * <p>Carries both the previous and the new number so handlers can react:
 * notify the old number that the contact was changed, send a verification
 * challenge to the new number, update WhatsApp Business records.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantPhoneNumberChangedEvent extends TenantEvent {

    private final PhoneNumber oldPhoneNumber;
    private final PhoneNumber newPhoneNumber;

    public TenantPhoneNumberChangedEvent(TenantId tenantId,
                                         PhoneNumber oldPhoneNumber,
                                         PhoneNumber newPhoneNumber) {

        super(tenantId);
        this.oldPhoneNumber = Objects.requireNonNull(oldPhoneNumber, "oldPhoneNumber must not be null");
        this.newPhoneNumber = Objects.requireNonNull(newPhoneNumber, "newPhoneNumber must not be null");
    }

    public PhoneNumber oldPhoneNumber() {
        return oldPhoneNumber;
    }

    public PhoneNumber newPhoneNumber() {
        return newPhoneNumber;
    }
}
