package com.whatsbotai.domain.tenant.model;

import com.whatsbotai.domain.shared.event.DomainEvent;
import com.whatsbotai.domain.tenant.event.TenantCreatedEvent;
import com.whatsbotai.domain.tenant.exception.InvalidTenantNameException;
import com.whatsbotai.domain.tenant.vo.BaileysAppName;
import com.whatsbotai.domain.tenant.vo.Email;
import com.whatsbotai.domain.tenant.vo.PhoneNumber;
import com.whatsbotai.domain.tenant.vo.TaxId;
import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tenant — aggregate root: creation, reconstitution, and invariants")
class TenantTest {

    // === Helper factory methods to keep tests concise ===

    private static Email validEmail() {
        return Email.of("contato@pizzariadoze.com.br");
    }

    private static TaxId validTaxId() {
        return TaxId.of("11144477735");
    }

    private static PhoneNumber validPhone() {
        return PhoneNumber.of("5581987654321");
    }

    private static BaileysAppName validBaileysAppName() {
        return BaileysAppName.of("pizzaria-do-ze");
    }

    private static Tenant newTenant() {
        return Tenant.create(
                "Pizzaria do Zé",
                validEmail(),
                validTaxId(),
                validPhone(),
                validBaileysAppName()
        );
    }

    @Nested
    @DisplayName("create() — registering a new tenant")
    class Creation {

        @Test
        @DisplayName("should create a tenant with a generated TenantId")
        void shouldCreateWithGeneratedId() {
            Tenant tenant = newTenant();

            assertThat(tenant.id()).isNotNull();
            assertThat(tenant.id().value()).isNotNull();
        }

        @Test
        @DisplayName("should produce two tenants with different ids")
        void shouldProduceUniqueIds() {
            Tenant a = newTenant();
            Tenant b = newTenant();

            assertThat(a.id()).isNotEqualTo(b.id());
        }

        @Test
        @DisplayName("should start with status PENDING")
        void shouldStartWithStatusPending() {
            Tenant tenant = newTenant();

            assertThat(tenant.status()).isEqualTo(TenantStatus.PENDING);
        }

        @Test
        @DisplayName("should start with plan FREE")
        void shouldStartWithPlanFree() {
            Tenant tenant = newTenant();

            assertThat(tenant.plan()).isEqualTo(TenantPlan.FREE);
        }

        @Test
        @DisplayName("should start with email and phone NOT verified")
        void shouldStartUnverified() {
            Tenant tenant = newTenant();

            assertThat(tenant.isEmailVerified()).isFalse();
            assertThat(tenant.isPhoneNumberVerified()).isFalse();
        }

        @Test
        @DisplayName("should start with 2FA disabled")
        void shouldStart2faDisabled() {
            Tenant tenant = newTenant();

            assertThat(tenant.isTwoFactorEnabled()).isFalse();
        }

        @Test
        @DisplayName("should capture createdAt and updatedAt at creation time")
        void shouldCaptureTimestamps() {
            Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
            Tenant tenant = newTenant();
            Instant after = Instant.now().plus(1, ChronoUnit.SECONDS);

            assertThat(tenant.createdAt()).isBetween(before, after);
            assertThat(tenant.updatedAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("should have updatedAt equal to createdAt at creation time")
        void shouldHaveUpdatedAtEqualToCreatedAt() {
            Tenant tenant = newTenant();

            assertThat(tenant.updatedAt()).isEqualTo(tenant.createdAt());
        }

        @Test
        @DisplayName("should start with no last access recorded")
        void shouldStartWithNoLastAccess() {
            Tenant tenant = newTenant();

            assertThat(tenant.lastAccessAt()).isNull();
        }

        @Test
        @DisplayName("should start with version 0 for optimistic locking")
        void shouldStartWithVersionZero() {
            Tenant tenant = newTenant();

            assertThat(tenant.version()).isZero();
        }

        @Test
        @DisplayName("should publish a TenantCreatedEvent")
        void shouldPublishTenantCreatedEvent() {
            Tenant tenant = newTenant();

            List<DomainEvent> events = tenant.pullDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantCreatedEvent.class);

            TenantCreatedEvent event = (TenantCreatedEvent) events.get(0);
            assertThat(event.tenantId()).isEqualTo(tenant.id());
            assertThat(event.name()).isEqualTo("Pizzaria do Zé");
            assertThat(event.initialStatus()).isEqualTo(TenantStatus.PENDING);
            assertThat(event.initialPlan()).isEqualTo(TenantPlan.FREE);
        }
    }

    @Nested
    @DisplayName("create() — name validation")
    class NameValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should reject null, empty, or blank name")
        void shouldRejectNullOrBlankName(String invalid) {
            assertThatThrownBy(() ->
                    Tenant.create(invalid, validEmail(), validTaxId(), validPhone(), validBaileysAppName())
            )
                    .isInstanceOf(InvalidTenantNameException.class)
                    .hasMessageContaining("null or blank");
        }

        @Test
        @DisplayName("should reject name shorter than 2 characters")
        void shouldRejectTooShortName() {
            assertThatThrownBy(() ->
                    Tenant.create("A", validEmail(), validTaxId(), validPhone(), validBaileysAppName())
            )
                    .isInstanceOf(InvalidTenantNameException.class)
                    .hasMessageContaining("at least 2");
        }

        @Test
        @DisplayName("should reject name longer than 100 characters")
        void shouldRejectTooLongName() {
            String tooLong = "a".repeat(101);

            assertThatThrownBy(() ->
                    Tenant.create(tooLong, validEmail(), validTaxId(), validPhone(), validBaileysAppName())
            )
                    .isInstanceOf(InvalidTenantNameException.class)
                    .hasMessageContaining("at most 100");
        }

        @Test
        @DisplayName("should accept name at the lower boundary (2 characters)")
        void shouldAcceptMinimumLengthName() {
            Tenant tenant = Tenant.create(
                    "AB", validEmail(), validTaxId(), validPhone(), validBaileysAppName());

            assertThat(tenant.name()).isEqualTo("AB");
        }

        @Test
        @DisplayName("should accept name at the upper boundary (100 characters)")
        void shouldAcceptMaximumLengthName() {
            String hundred = "a".repeat(100);

            Tenant tenant = Tenant.create(
                    hundred, validEmail(), validTaxId(), validPhone(), validBaileysAppName());

            assertThat(tenant.name()).isEqualTo(hundred);
        }

        @Test
        @DisplayName("should trim leading and trailing whitespace before validation")
        void shouldTrimWhitespace() {
            Tenant tenant = Tenant.create(
                    "  Pizzaria do Zé  ", validEmail(), validTaxId(), validPhone(), validBaileysAppName());

            assertThat(tenant.name()).isEqualTo("Pizzaria do Zé");
        }
    }

    @Nested
    @DisplayName("create() — null safety for required fields")
    class NullSafety {

        @Test
        @DisplayName("should reject null email")
        void shouldRejectNullEmail() {
            assertThatThrownBy(() ->
                    Tenant.create("Name", null, validTaxId(), validPhone(), validBaileysAppName())
            )
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("should reject null taxId")
        void shouldRejectNullTaxId() {
            assertThatThrownBy(() ->
                    Tenant.create("Name", validEmail(), null, validPhone(), validBaileysAppName())
            )
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("taxId");
        }

        @Test
        @DisplayName("should reject null phoneNumber")
        void shouldRejectNullPhone() {
            assertThatThrownBy(() ->
                    Tenant.create("Name", validEmail(), validTaxId(), null, validBaileysAppName())
            )
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("phoneNumber");
        }

        @Test
        @DisplayName("should reject null baileysAppName")
        void shouldRejectNullBaileysAppName() {
            assertThatThrownBy(() ->
                    Tenant.create("Name", validEmail(), validTaxId(), validPhone(), null)
            )
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("baileysAppName");
        }
    }

    @Nested
    @DisplayName("reconstitute() — restoring from persistence")
    class Reconstitution {

        @Test
        @DisplayName("should restore all fields from the given values")
        void shouldRestoreAllFields() {
            TenantId id = TenantId.generate();
            Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
            Instant updatedAt = Instant.parse("2026-06-01T10:00:00Z");
            Instant lastAccess = Instant.parse("2026-06-10T10:00:00Z");

            Tenant tenant = Tenant.reconstitute(
                    id,
                    "Restored Tenant",
                    validEmail(),
                    validTaxId(),
                    validPhone(),
                    validBaileysAppName(),
                    TenantStatus.ACTIVE,
                    TenantPlan.PROFESSIONAL,
                    true,
                    true,
                    true,
                    createdAt,
                    updatedAt,
                    lastAccess,
                    42L
            );

            assertThat(tenant.id()).isEqualTo(id);
            assertThat(tenant.name()).isEqualTo("Restored Tenant");
            assertThat(tenant.status()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(tenant.plan()).isEqualTo(TenantPlan.PROFESSIONAL);
            assertThat(tenant.isEmailVerified()).isTrue();
            assertThat(tenant.isPhoneNumberVerified()).isTrue();
            assertThat(tenant.isTwoFactorEnabled()).isTrue();
            assertThat(tenant.createdAt()).isEqualTo(createdAt);
            assertThat(tenant.updatedAt()).isEqualTo(updatedAt);
            assertThat(tenant.lastAccessAt()).isEqualTo(lastAccess);
            assertThat(tenant.version()).isEqualTo(42L);
        }

        @Test
        @DisplayName("should NOT publish any event when reconstituting")
        void shouldNotPublishEventsOnReconstitution() {
            Tenant tenant = Tenant.reconstitute(
                    TenantId.generate(),
                    "Restored",
                    validEmail(),
                    validTaxId(),
                    validPhone(),
                    validBaileysAppName(),
                    TenantStatus.ACTIVE,
                    TenantPlan.FREE,
                    false,
                    false,
                    false,
                    Instant.now(),
                    Instant.now(),
                    null,
                    0L
            );

            assertThat(tenant.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should accept null lastAccessAt (tenant never accessed)")
        void shouldAcceptNullLastAccess() {
            Tenant tenant = Tenant.reconstitute(
                    TenantId.generate(),
                    "Restored",
                    validEmail(),
                    validTaxId(),
                    validPhone(),
                    validBaileysAppName(),
                    TenantStatus.PENDING,
                    TenantPlan.FREE,
                    false,
                    false,
                    false,
                    Instant.now(),
                    Instant.now(),
                    null,
                    0L
            );

            assertThat(tenant.lastAccessAt()).isNull();
        }
    }

    @Nested
    @DisplayName("reconstitute() — invariant: updatedAt >= createdAt")
    class UpdatedAtInvariant {

        @Test
        @DisplayName("should reject updatedAt earlier than createdAt")
        void shouldRejectUpdatedAtBeforeCreatedAt() {
            Instant createdAt = Instant.parse("2026-06-01T10:00:00Z");
            Instant updatedAt = Instant.parse("2026-05-01T10:00:00Z"); // before createdAt

            assertThatThrownBy(() ->
                    Tenant.reconstitute(
                            TenantId.generate(),
                            "Name",
                            validEmail(),
                            validTaxId(),
                            validPhone(),
                            validBaileysAppName(),
                            TenantStatus.ACTIVE,
                            TenantPlan.FREE,
                            false, false, false,
                            createdAt,
                            updatedAt,
                            null,
                            0L
                    )
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("updatedAt")
                    .hasMessageContaining("createdAt");
        }

        @Test
        @DisplayName("should accept updatedAt equal to createdAt")
        void shouldAcceptUpdatedAtEqualToCreatedAt() {
            Instant moment = Instant.parse("2026-06-01T10:00:00Z");

            Tenant tenant = Tenant.reconstitute(
                    TenantId.generate(),
                    "Name",
                    validEmail(),
                    validTaxId(),
                    validPhone(),
                    validBaileysAppName(),
                    TenantStatus.PENDING,
                    TenantPlan.FREE,
                    false, false, false,
                    moment,
                    moment,
                    null,
                    0L
            );

            assertThat(tenant.createdAt()).isEqualTo(tenant.updatedAt());
        }
    }

    @Nested
    @DisplayName("Identity and equality")
    class Identity {

        @Test
        @DisplayName("equals should be based solely on TenantId")
        void equalsShouldBeBasedOnId() {
            TenantId sharedId = TenantId.generate();
            Tenant a = Tenant.reconstitute(
                    sharedId, "Name A",
                    validEmail(), validTaxId(), validPhone(), validBaileysAppName(),
                    TenantStatus.PENDING, TenantPlan.FREE,
                    false, false, false,
                    Instant.now(), Instant.now(), null, 0L);

            Tenant b = Tenant.reconstitute(
                    sharedId, "Name B (different)",
                    validEmail(), validTaxId(), validPhone(), validBaileysAppName(),
                    TenantStatus.ACTIVE, TenantPlan.ENTERPRISE,
                    true, true, true,
                    Instant.now(), Instant.now(), null, 99L);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("two tenants with different ids should NOT be equal")
        void differentIdsShouldNotBeEqual() {
            Tenant a = newTenant();
            Tenant b = newTenant();

            assertThat(a).isNotEqualTo(b);
        }
    }

    @Nested
    @DisplayName("toString")
    class StringRepresentation {

        @Test
        @DisplayName("should include id, name, status, and plan")
        void shouldIncludeKeyFields() {
            Tenant tenant = newTenant();

            assertThat(tenant.toString())
                    .contains(tenant.id().toString())
                    .contains("Pizzaria do Zé")
                    .contains("PENDING")
                    .contains("FREE");
        }

        @Test
        @DisplayName("should NOT include sensitive data (email, taxId, phone)")
        void shouldNotIncludeSensitiveData() {
            Tenant tenant = newTenant();

            assertThat(tenant.toString())
                    .doesNotContain("contato@pizzariadoze.com.br")
                    .doesNotContain("11144477735")
                    .doesNotContain("5581987654321");
        }
    }
}
