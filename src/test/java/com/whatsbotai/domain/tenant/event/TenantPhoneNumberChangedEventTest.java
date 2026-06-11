package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.PhoneNumber;
import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantPhoneNumberChangedEvent — emitted when the contact phone number is changed")
public class TenantPhoneNumberChangedEventTest {

    @Test
    @DisplayName("should carry the old and new phone number")
    void shouldCarryOldAndNewPhone() {
        PhoneNumber oldPhone = PhoneNumber.of("5581987654321");
        PhoneNumber newPhone = PhoneNumber.of("5581998765432");

        TenantPhoneNumberChangedEvent event = new TenantPhoneNumberChangedEvent(TenantId.generate(), oldPhone, newPhone);

        assertThat(event.oldPhoneNumber()).isEqualTo(oldPhone);
        assertThat(event.newPhoneNumber()).isEqualTo(newPhone);
    }
}
