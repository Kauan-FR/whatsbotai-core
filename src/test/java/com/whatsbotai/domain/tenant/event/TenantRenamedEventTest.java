package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantRenamedEvent — emitted when the tenant name is changed")
public class TenantRenamedEventTest {

    @Test
    @DisplayName("should carry the old and new name")
    void shouldCarryOldAndNewName() {
        TenantRenamedEvent event = new TenantRenamedEvent(TenantId.generate(), "Old Name", "New Name");

        assertThat(event.oldName()).isEqualTo("Old Name");
        assertThat(event.newName()).isEqualTo("New Name");
    }
}
