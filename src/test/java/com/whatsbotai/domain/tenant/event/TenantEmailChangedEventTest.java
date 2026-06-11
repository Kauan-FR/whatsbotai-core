package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.Email;
import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantEmailChangedEvent — emitted when the contact email is changed")
public class TenantEmailChangedEventTest {

    @Test
    @DisplayName("should carry the old and new email")
    void shouldCarryOldAndNewEmail() {
        Email oldEmail = Email.of("old@example.com");
        Email newEmail = Email.of("new@example.com");

        TenantEmailChangedEvent event = new TenantEmailChangedEvent(TenantId.generate(), oldEmail, newEmail);

        assertThat(event.oldEmail()).isEqualTo(oldEmail);
        assertThat(event.newEmail()).isEqualTo(newEmail);
    }
}
