package com.whatsbotai.domain.tenant.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.whatsbotai.domain.tenant.event.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.whatsbotai.domain.shared.event.DomainEvent;
import com.whatsbotai.domain.tenant.exception.EmailNotVerifiedException;
import com.whatsbotai.domain.tenant.exception.InvalidCancellationReasonException;
import com.whatsbotai.domain.tenant.exception.InvalidTenantNameException;
import com.whatsbotai.domain.tenant.exception.TenantAlreadyCancelledException;
import com.whatsbotai.domain.tenant.exception.TenantStatusTransitionException;
import com.whatsbotai.domain.tenant.vo.BaileysAppName;
import com.whatsbotai.domain.tenant.vo.Email;
import com.whatsbotai.domain.tenant.vo.PhoneNumber;
import com.whatsbotai.domain.tenant.vo.TaxId;
import com.whatsbotai.domain.tenant.vo.TenantId;

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

    /**
     * Helper to reconstitute a tenant in a specific state for testing
     * lifecycle operations that require non-default starting conditions.
     */
    private static Tenant tenantInStatus(TenantStatus status, boolean emailVerified) {
        return Tenant.reconstitute(
                TenantId.generate(),
                "Test Tenant",
                validEmail(),
                validTaxId(),
                validPhone(),
                validBaileysAppName(),
                status,
                TenantPlan.FREE,
                emailVerified,
                false,
                false,
                Instant.now().minusSeconds(3600),
                Instant.now().minusSeconds(3600),
                null,
                0L
        );
    }

    @Nested
    @DisplayName("activate() — PENDING → ACTIVE")
    class Activation {

        @Test
        @DisplayName("should transition from PENDING to ACTIVE when email is verified")
        void shouldActivateWhenEmailVerified() {
            Tenant tenant = tenantInStatus(TenantStatus.PENDING, true);

            tenant.activate();

            assertThat(tenant.status()).isEqualTo(TenantStatus.ACTIVE);
        }

        @Test
        @DisplayName("should publish a TenantActivatedEvent")
        void shouldPublishActivatedEvent() {
            Tenant tenant = tenantInStatus(TenantStatus.PENDING, true);

            tenant.activate();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantActivatedEvent.class);
            assertThat(((TenantActivatedEvent) events.get(0)).tenantId()).isEqualTo(tenant.id());
        }

        @Test
        @DisplayName("should update updatedAt")
        void shouldUpdateUpdatedAt() {
            Tenant tenant = tenantInStatus(TenantStatus.PENDING, true);
            Instant before = tenant.updatedAt();

            tenant.activate();

            assertThat(tenant.updatedAt()).isAfterOrEqualTo(before);
        }

        @Test
        @DisplayName("should reject activation when email is NOT verified")
        void shouldRejectWhenEmailNotVerified() {
            Tenant tenant = tenantInStatus(TenantStatus.PENDING, false);

            assertThatThrownBy(tenant::activate)
                    .isInstanceOf(EmailNotVerifiedException.class)
                    .hasMessageContaining("activate");
        }

        @ParameterizedTest
        @EnumSource(value = TenantStatus.class, names = {"ACTIVE", "SUSPENDED"})
        @DisplayName("should reject activation when status is not PENDING")
        void shouldRejectWhenNotPending(TenantStatus status) {
            Tenant tenant = tenantInStatus(status, true);

            assertThatThrownBy(tenant::activate)
                    .isInstanceOf(TenantStatusTransitionException.class);
        }

        @Test
        @DisplayName("should reject activation on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(tenant::activate)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("activate");
        }
    }

    @Nested
    @DisplayName("suspend() — ACTIVE → SUSPENDED")
    class Suspension {

        @Test
        @DisplayName("should transition from ACTIVE to SUSPENDED")
        void shouldSuspendFromActive() {
            Tenant tenant = tenantInStatus(TenantStatus.ACTIVE, true);

            tenant.suspend();

            assertThat(tenant.status()).isEqualTo(TenantStatus.SUSPENDED);
        }

        @Test
        @DisplayName("should publish a TenantSuspendedEvent")
        void shouldPublishSuspendedEvent() {
            Tenant tenant = tenantInStatus(TenantStatus.ACTIVE, true);

            tenant.suspend();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantSuspendedEvent.class);
        }

        @ParameterizedTest
        @EnumSource(value = TenantStatus.class, names = {"PENDING", "SUSPENDED"})
        @DisplayName("should reject suspension when status is not ACTIVE")
        void shouldRejectWhenNotActive(TenantStatus status) {
            Tenant tenant = tenantInStatus(status, true);

            assertThatThrownBy(tenant::suspend)
                    .isInstanceOf(TenantStatusTransitionException.class);
        }

        @Test
        @DisplayName("should reject suspension on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(tenant::suspend)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("suspend");
        }
    }

    @Nested
    @DisplayName("reactivate() — SUSPENDED → ACTIVE")
    class Reactivation {

        @Test
        @DisplayName("should transition from SUSPENDED to ACTIVE")
        void shouldReactivateFromSuspended() {
            Tenant tenant = tenantInStatus(TenantStatus.SUSPENDED, true);

            tenant.reactivate();

            assertThat(tenant.status()).isEqualTo(TenantStatus.ACTIVE);
        }

        @Test
        @DisplayName("should publish a TenantReactivatedEvent")
        void shouldPublishReactivatedEvent() {
            Tenant tenant = tenantInStatus(TenantStatus.SUSPENDED, true);

            tenant.reactivate();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantReactivatedEvent.class);
        }

        @ParameterizedTest
        @EnumSource(value = TenantStatus.class, names = {"PENDING", "ACTIVE"})
        @DisplayName("should reject reactivation when status is not SUSPENDED")
        void shouldRejectWhenNotSuspended(TenantStatus status) {
            Tenant tenant = tenantInStatus(status, true);

            assertThatThrownBy(tenant::reactivate)
                    .isInstanceOf(TenantStatusTransitionException.class);
        }

        @Test
        @DisplayName("should reject reactivation on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(tenant::reactivate)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("reactivate");
        }
    }

    @Nested
    @DisplayName("cancel() — voluntary cancellation")
    class VoluntaryCancellation {

        @ParameterizedTest
        @EnumSource(value = TenantStatus.class, names = {"PENDING", "ACTIVE", "SUSPENDED"})
        @DisplayName("should transition any non-terminal status to CANCELLED")
        void shouldCancelFromAnyNonTerminalStatus(TenantStatus startingStatus) {
            Tenant tenant = tenantInStatus(startingStatus, true);

            tenant.cancel();

            assertThat(tenant.status()).isEqualTo(TenantStatus.CANCELLED);
        }

        @Test
        @DisplayName("should publish a TenantCancelledEvent with empty reason")
        void shouldPublishCancelledEventWithoutReason() {
            Tenant tenant = tenantInStatus(TenantStatus.ACTIVE, true);

            tenant.cancel();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantCancelledEvent.class);
            assertThat(((TenantCancelledEvent) events.get(0)).reason()).isEmpty();
        }

        @Test
        @DisplayName("should reject voluntary cancellation on already cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(tenant::cancel)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("cancel");
        }
    }

    @Nested
    @DisplayName("forceCancel(reason) — admin force-cancellation")
    class ForceCancellation {

        @Test
        @DisplayName("should transition to CANCELLED with the documented reason")
        void shouldForceCancelWithReason() {
            Tenant tenant = tenantInStatus(TenantStatus.ACTIVE, true);

            tenant.forceCancel("Repeated terms of service violations");

            assertThat(tenant.status()).isEqualTo(TenantStatus.CANCELLED);
        }

        @Test
        @DisplayName("should publish a TenantCancelledEvent with the reason")
        void shouldPublishCancelledEventWithReason() {
            Tenant tenant = tenantInStatus(TenantStatus.ACTIVE, true);
            String reason = "Payment method rejected for 3 months";

            tenant.forceCancel(reason);

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            TenantCancelledEvent event = (TenantCancelledEvent) events.get(0);
            assertThat(event.reason()).contains(reason);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should reject null, empty, or blank reason")
        void shouldRejectBlankReason(String invalidReason) {
            Tenant tenant = tenantInStatus(TenantStatus.ACTIVE, true);

            assertThatThrownBy(() -> tenant.forceCancel(invalidReason))
                    .isInstanceOf(InvalidCancellationReasonException.class)
                    .hasMessageContaining("null or blank");
        }

        @Test
        @DisplayName("should reject force cancellation on already cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(() -> tenant.forceCancel("Any reason"))
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("forceCancel");
        }
    }

    /**
     * Helper to reconstitute a fully configured tenant for testing
     * operations that depend on verified-contact / 2FA state.
     */
    private static Tenant fullyConfiguredTenant() {
        return Tenant.reconstitute(
                TenantId.generate(),
                "Pizzaria do Zé",
                validEmail(),
                validTaxId(),
                validPhone(),
                validBaileysAppName(),
                TenantStatus.ACTIVE,
                TenantPlan.FREE,
                true, // emailVerified
                true, // phoneNumberVerified
                false, // twoFactorEnabled
                Instant.now().minusSeconds(3600),
                Instant.now().minusSeconds(3600),
                null,
                0L
        );
    }

    @Nested
    @DisplayName("changeEmail(newEmail)")
    class ChangeEmail {

        @Test
        @DisplayName("should update the contact email and reset verification")
        void shouldUpdateAndResetVerification() {
            Tenant tenant = fullyConfiguredTenant();
            Email newEmail = Email.of("novo@pizzariadoze.com.br");

            tenant.changeEmail(newEmail);

            assertThat(tenant.email()).isEqualTo(newEmail);
            assertThat(tenant.isEmailVerified()).isFalse();
        }

        @Test
        @DisplayName("should publish TenantEmailChangedEvent with old and new emails")
        void shouldPublishEvent() {
            Tenant tenant = fullyConfiguredTenant();
            Email oldEmail = tenant.email();
            Email newEmail = Email.of("novo@pizzariadoze.com.br");

            tenant.changeEmail(newEmail);

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            TenantEmailChangedEvent event = (TenantEmailChangedEvent) events.get(0);
            assertThat(event.oldEmail()).isEqualTo(oldEmail);
            assertThat(event.newEmail()).isEqualTo(newEmail);
        }

        @Test
        @DisplayName("should be a no-op when the new email equals the current one")
        void shouldBeNoOpWhenSameEmail() {
            Tenant tenant = fullyConfiguredTenant();
            Email sameEmail = tenant.email();

            tenant.changeEmail(sameEmail);

            assertThat(tenant.pullDomainEvents()).isEmpty();
            assertThat(tenant.isEmailVerified()).isTrue();
        }

        @Test
        @DisplayName("should reject null email")
        void shouldRejectNull() {
            Tenant tenant = fullyConfiguredTenant();

            assertThatThrownBy(() -> tenant.changeEmail(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("should reject change on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(() -> tenant.changeEmail(Email.of("any@example.com")))
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("changeEmail");
        }
    }

    @Nested
    @DisplayName("changePhoneNumber(newPhone)")
    class ChangePhone {

        @Test
        @DisplayName("should update the phone number and reset verification")
        void shouldUpdateAndResetVerification() {
            Tenant tenant = fullyConfiguredTenant();
            PhoneNumber newPhone = PhoneNumber.of("5581912345678");

            tenant.changePhone(newPhone);

            assertThat(tenant.phoneNumber()).isEqualTo(newPhone);
            assertThat(tenant.isPhoneNumberVerified()).isFalse();
        }

        @Test
        @DisplayName("should publish TenantPhoneNumberChangedEvent")
        void shouldPublishEvent() {
            Tenant tenant = fullyConfiguredTenant();
            PhoneNumber oldPhone = tenant.phoneNumber();
            PhoneNumber newPhone = PhoneNumber.of("5581912345678");

            tenant.changePhone(newPhone);

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            TenantPhoneNumberChangedEvent event = (TenantPhoneNumberChangedEvent) events.get(0);
            assertThat(event.oldPhoneNumber()).isEqualTo(oldPhone);
            assertThat(event.newPhoneNumber()).isEqualTo(newPhone);
        }

        @Test
        @DisplayName("should be a no-op when the new phone equals the current one")
        void shouldBeNoOpWhenSamePhone() {
            Tenant tenant = fullyConfiguredTenant();
            PhoneNumber samePhone = tenant.phoneNumber();

            tenant.changePhone(samePhone);

            assertThat(tenant.pullDomainEvents()).isEmpty();
            assertThat(tenant.isPhoneNumberVerified()).isTrue();
        }

        @Test
        @DisplayName("should reject null phone")
        void shouldRejectNull() {
            Tenant tenant = fullyConfiguredTenant();

            assertThatThrownBy(() -> tenant.changePhone(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("phoneNumber");
        }

        @Test
        @DisplayName("should reject change on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(() -> tenant.changePhone(PhoneNumber.of("5581912345678")))
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("changePhoneNumber");
        }
    }

    @Nested
    @DisplayName("rename(newName)")
    class  Rename {

        @Test
        @DisplayName("should update the tenant name and publish TenantRenamedEvent")
        void shouldRenameAndPublishEvent() {
            Tenant tenant = fullyConfiguredTenant();
            String oldName = tenant.name();

            tenant.rename("Pizzaria do Zé Premium");

            assertThat(tenant.name()).isEqualTo("Pizzaria do Zé Premium");

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            TenantRenamedEvent event = (TenantRenamedEvent) events.get(0);
            assertThat(event.oldName()).isEqualTo(oldName);
            assertThat(event.newName()).isEqualTo("Pizzaria do Zé Premium");
        }

        @Test
        @DisplayName("should trim whitespace from the new name")
        void shouldTrimNewName() {
            Tenant tenant = fullyConfiguredTenant();

            tenant.rename("  New Name  ");

            assertThat(tenant.name()).isEqualTo("New Name");
        }

        @Test
        @DisplayName("should be a no-op when the new name equals the current one (after trimming)")
        void shouldBeNoOpWhenSameName() {
            Tenant tenant = fullyConfiguredTenant();
            String sameName = tenant.name();

            tenant.rename(sameName);

            assertThat(tenant.pullDomainEvents()).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "A"})
        @DisplayName("should reject invalid new names")
        void shouldRejectInvalidNames(String invalid) {
            Tenant tenant = fullyConfiguredTenant();

            assertThatThrownBy(() -> tenant.rename(invalid))
                    .isInstanceOf(InvalidTenantNameException.class);
        }

        @Test
        @DisplayName("should reject rename on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(() -> tenant.rename("New Name"))
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("rename");
        }
    }

    @Nested
    @DisplayName("markEmailAsVerified()")
    class MarkEmailAsVerified {

        @Test
        @DisplayName("should mark email as verified and publish TenantEmailVerifiedEvent")
        void shouldMarkAndPublish() {
            Tenant tenant = tenantInStatus(TenantStatus.PENDING, false);

            tenant.markEmailAsVerified();

            assertThat(tenant.isEmailVerified()).isTrue();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantEmailVerifiedEvent.class);
        }

        @Test
        @DisplayName("should be idempotent: no-op when already verified")
        void shouldBeIdempotent() {
            Tenant tenant = fullyConfiguredTenant();

            tenant.markEmailAsVerified();

            assertThat(tenant.isEmailVerified()).isTrue();
            assertThat(tenant.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should reject on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, false);

            assertThatThrownBy(tenant::markEmailAsVerified)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("markEmailAsVerified");
        }
    }

    @Nested
    @DisplayName("markPhoneNumberAsVerified()")
    class MarkPhoneVerified {

        @Test
        @DisplayName("should mark phone as verified and publish TenantPhoneNumberVerifiedEvent")
        void shouldMarkAndPublish() {
            Tenant tenant = tenantInStatus(TenantStatus.PENDING, false);

            tenant.markPhoneNumberAsVerified();

            assertThat(tenant.isPhoneNumberVerified()).isTrue();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantPhoneNumberVerifiedEvent.class);
        }

        @Test
        @DisplayName("should be idempotent: no-op when already verified")
        void shouldBeIdempotent() {
            Tenant tenant = fullyConfiguredTenant();

            tenant.markPhoneNumberAsVerified();

            assertThat(tenant.isPhoneNumberVerified()).isTrue();
            assertThat(tenant.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should reject on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, false);

            assertThatThrownBy(tenant::markPhoneNumberAsVerified)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("markPhoneNumberAsVerified");
        }
    }

    @Nested
    @DisplayName("enableTwoFactorAuth()")
    class EnableTwoFactor {

        @Test
        @DisplayName("should enable 2FA when email is verified and publish event")
        void shouldEnableWhenEmailVerified() {
            Tenant tenant = fullyConfiguredTenant();

            tenant.enableTwoFactorAuth();

            assertThat(tenant.isTwoFactorEnabled()).isTrue();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantTwoFactorAuthEnabledEvent.class);
        }

        @Test
        @DisplayName("should reject when email is NOT verified")
        void shouldRejectWhenEmailNotVerified() {
            Tenant tenant = tenantInStatus(TenantStatus.ACTIVE, false);

            assertThatThrownBy(tenant::enableTwoFactorAuth)
                    .isInstanceOf(EmailNotVerifiedException.class)
                    .hasMessageContaining("enableTwoFactorAuth");
        }

        @Test
        @DisplayName("should be idempotent: no-op when already enabled")
        void shouldBeIdempotent() {
            Tenant tenant = Tenant.reconstitute(
                    TenantId.generate(),
                    "Test",
                    validEmail(),
                    validTaxId(),
                    validPhone(),
                    validBaileysAppName(),
                    TenantStatus.ACTIVE,
                    TenantPlan.FREE,
                    true,
                    true,
                    true,
                    Instant.now().minusSeconds(3600),
                    Instant.now().minusSeconds(3600),
                    null,
                    0L
            );
            tenant.enableTwoFactorAuth();

            assertThat(tenant.isTwoFactorEnabled()).isTrue();
            assertThat(tenant.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should reject on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(tenant::enableTwoFactorAuth)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("enableTwoFactorAuth");
        }
    }

    @Nested
    @DisplayName("disableTwoFactorAuth()")
    class DisableTwoFactor {

        @Test
        @DisplayName("should disable 2FA and publish event")
        void shouldDisable() {
            Tenant tenant = Tenant.reconstitute(
                    TenantId.generate(),
                    "Test",
                    validEmail(),
                    validTaxId(),
                    validPhone(),
                    validBaileysAppName(),
                    TenantStatus.ACTIVE,
                    TenantPlan.FREE,
                    true,
                    true,
                    true,
                    Instant.now().minusSeconds(3600),
                    Instant.now().minusSeconds(3600),
                    null,
                    0L
            );

            tenant.disableTwoFactorAuth();

            assertThat(tenant.isTwoFactorEnabled()).isFalse();

            List<DomainEvent> events = tenant.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TenantTwoFactorAuthDisabledEvent.class);
        }

        @Test
        @DisplayName("should be idempotent: no-op when already disabled")
        void shouldBeIdempotent() {
            Tenant tenant = fullyConfiguredTenant();

            tenant.disableTwoFactorAuth();

            assertThat(tenant.isTwoFactorEnabled()).isFalse();
            assertThat(tenant.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should reject on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(tenant::disableTwoFactorAuth)
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("disableTwoFactorAuth");
        }
    }

    @Nested
    @DisplayName("recordLastAccess(when)")
    class RecordLastAccess {

        @Test
        @DisplayName("should update lastAccessAt to the given instant")
        void shouldUpdateLastAccess() {
            Tenant  tenant = fullyConfiguredTenant();
            Instant accessMoment = Instant.parse("2026-06-10T15:30:00Z");

            tenant.recordLastAccess(accessMoment);

            assertThat(tenant.lastAccessAt()).isEqualTo(accessMoment);
        }

        @Test
        @DisplayName("should NOT publish any domain event")
        void shouldNotPublishEvent() {
            Tenant tenant = fullyConfiguredTenant();

            tenant.recordLastAccess(Instant.now());

            assertThat(tenant.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should NOT update updatedAt (it is a separate metric)")
        void shouldNotUpdateUpdatedAt() {
            Tenant  tenant = fullyConfiguredTenant();
            Instant beforeUpdatedAt = tenant.updatedAt();

            tenant.recordLastAccess(Instant.now());

            assertThat(tenant.updatedAt()).isEqualTo(beforeUpdatedAt);
        }

        @Test
        @DisplayName("should reject null instant")
        void shouldRejectNull() {
            Tenant tenant = fullyConfiguredTenant();

            assertThatThrownBy(() -> tenant.recordLastAccess(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("lastAccessAt");
        }

        @Test
        @DisplayName("should reject on a cancelled tenant")
        void shouldRejectOnCancelled() {
            Tenant tenant = tenantInStatus(TenantStatus.CANCELLED, true);

            assertThatThrownBy(() -> tenant.recordLastAccess(Instant.now()))
                    .isInstanceOf(TenantAlreadyCancelledException.class)
                    .hasMessageContaining("recordLastAccess");
        }
    }
}
