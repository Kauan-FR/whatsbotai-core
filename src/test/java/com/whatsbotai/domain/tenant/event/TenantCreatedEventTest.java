package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.shared.event.AbstractDomainEvent;
import com.whatsbotai.domain.shared.event.DomainEvent;
import com.whatsbotai.domain.tenant.model.TenantPlan;
import com.whatsbotai.domain.tenant.model.TenantStatus;
import com.whatsbotai.domain.tenant.vo.BaileysAppName;
import com.whatsbotai.domain.tenant.vo.Email;
import com.whatsbotai.domain.tenant.vo.PhoneNumber;
import com.whatsbotai.domain.tenant.vo.TaxId;
import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantCreatedEvent — emitted when a new tenant is registered in the system")
public class TenantCreatedEventTest {

    @Nested
    @DisplayName("Contract")
    class Contract {

        @Test
        @DisplayName("should be a DomainEvent")
        void shouldBeADomainEvent() {
            TenantCreatedEvent event = buildEvent();

            assertThat(event)
                    .isInstanceOf(DomainEvent.class)
                    .isInstanceOf(AbstractDomainEvent.class);
        }

        @Test
        @DisplayName("should have auto-generated eventId and occurredOn")
        void shouldHaveAutomaticMetadata() {
            TenantCreatedEvent event = buildEvent();

            assertThat(event.eventId()).isNotNull();
            assertThat(event.occurredOn()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Payload")
    class PayLoad {

        @Test
        @DisplayName("should carry the tenant id")
        void shouldCarryTenantId() {
            TenantId tenantId = TenantId.generate();

            TenantCreatedEvent event = new TenantCreatedEvent(
                 tenantId,
                    "Pizzaria do Zé",
                    Email.of("contato@pizzariadoze.com.br"),
                    TaxId.of("11144477735"),
                    PhoneNumber.of("5581987654321"),
                    BaileysAppName.of("pizzaria-do-ze"),
                    TenantStatus.PENDING,
                    TenantPlan.FREE
            );

            assertThat(event.tenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("should carry all snapshot fields of the freshly created tenant")
        void shouldCarrySnapshot() {
            TenantCreatedEvent event = buildEvent();

            assertThat(event.name()).isEqualTo("Pizzaria do Zé");
            assertThat(event.email().value()).isEqualTo("contato@pizzariadoze.com.br");
            assertThat(event.taxId().value()).isEqualTo("11144477735");
            assertThat(event.phoneNumber().value()).isEqualTo("5581987654321");
            assertThat(event.baileysAppName().value()).isEqualTo("pizzaria-do-ze");
            assertThat(event.initialStatus()).isEqualTo(TenantStatus.PENDING);
            assertThat(event.initialPlan()).isEqualTo(TenantPlan.FREE);
        }
    }

    private TenantCreatedEvent buildEvent() {
        return new TenantCreatedEvent(
            TenantId.generate(),
            "Pizzaria do Zé",
            Email.of("contato@pizzariadoze.com.br"),
            TaxId.of("11144477735"),
            PhoneNumber.of("5581987654321"),
            BaileysAppName.of("pizzaria-do-ze"),
            TenantStatus.PENDING,
            TenantPlan.FREE
        );
    }
}
