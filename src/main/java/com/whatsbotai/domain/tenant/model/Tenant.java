package com.whatsbotai.domain.tenant.model;

import java.time.Instant;
import java.util.Objects;

import com.whatsbotai.domain.shared.event.AggregateRoot;
import com.whatsbotai.domain.tenant.event.*;
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
/**
 * Aggregate root representing a tenant in the WhatsBot AI multi-tenant SaaS.
 *
 * <p>A {@code Tenant} encapsulates all identifying data, lifecycle state,
 * commercial plan, verification status, security configuration, and
 * temporal metadata of a customer account. It is the only entry point
 * for mutating tenant state — the domain enforces every invariant here
 * so the tenant is never in an inconsistent state from the outside.
 *
 * <p><strong>Construction:</strong>
 * <ul>
 *   <li>{@link #create(String, Email, TaxId, PhoneNumber, BaileysAppName)}
 *       — registers a brand-new tenant; generates the id, sets initial
 *       status to {@link TenantStatus#PENDING} and initial plan to
 *       {@link TenantPlan#FREE}, captures creation timestamps, and
 *       publishes a {@code TenantCreatedEvent}</li>
 *   <li>{@link #reconstitute(...)} — restores an existing tenant from
 *       persistence with all fields supplied; does NOT publish events</li>
 * </ul>
 *
 * <p><strong>Identity:</strong> two {@code Tenant} instances are considered
 * equal if and only if they share the same {@link TenantId}, regardless
 * of internal state. This allows aggregates to be compared safely across
 * different snapshots in time.
 *
 * <p><strong>Optimistic locking:</strong> the {@link #version()} field
 * tracks the persistence version used by the infrastructure layer (JPA
 * {@code @Version}) to detect concurrent modifications. A freshly created
 * tenant starts at version {@code 0}.
 *
 * @author Kauan
 * @since 1.0
 */
public final class Tenant extends AggregateRoot {

    /** Minimum allowed length of the tenant name (after trimming). */
    private static final int NAME_MIN_LENGTH = 2;

    /** Maximum allowed length of the tenant name (after trimming). */
    private static final int NAME_MAX_LENGTH = 100;

    /**
     * Number of days a {@code PENDING} tenant may remain unactivated before
     * being considered expired. Exposed publicly so the application layer
     * can use the same constant for UI messages and cleanup jobs.
     */
    public static final int PENDING_EXPIRATION_DAYS = 30;

    private final TenantId id;
    private String name;
    private Email email;
    private TaxId taxId;
    private PhoneNumber phoneNumber;
    private BaileysAppName baileysAppName;
    private TenantStatus status;
    private TenantPlan plan;
    private boolean emailVerified;
    private boolean phoneNumberVerified;
    private boolean twoFactorEnabled;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastAccessAt;
    private long version;

    private Tenant(TenantId id,
                   String name,
                   Email email,
                   TaxId taxId,
                   PhoneNumber phoneNumber,
                   BaileysAppName baileysAppName,
                   TenantStatus status,
                   TenantPlan plan,
                   boolean emailVerified,
                   boolean phoneNumberVerified,
                   boolean twoFactorEnabled,
                   Instant createdAt,
                   Instant updatedAt,
                   Instant lastAccessAt,
                   long version) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.taxId = taxId;
        this.phoneNumber = phoneNumber;
        this.baileysAppName = baileysAppName;
        this.status = status;
        this.plan = plan;
        this.emailVerified = emailVerified;
        this.phoneNumberVerified = phoneNumberVerified;
        this.twoFactorEnabled = twoFactorEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastAccessAt = lastAccessAt;
        this.version = version;
    }

    /**
     * Registers a brand-new tenant.
     *
     * <p>Generates a new {@link TenantId}, sets the initial status to
     * {@link TenantStatus#PENDING} and the initial plan to
     * {@link TenantPlan#FREE}, captures the current instant as both
     * {@code createdAt} and {@code updatedAt}, and publishes a
     * {@link TenantCreatedEvent}.
     *
     * @param name           the commercial display name (2 to 100 characters, trimmed)
     * @param email          the contact email (must not be null)
     * @param taxId          the Brazilian tax identifier (CPF or CNPJ, must not be null)
     * @param phoneNumber    the contact phone number (must not be null)
     * @param baileysAppName the WhatsApp session slug (must not be null)
     * @return a new {@code Tenant} ready to be persisted
     * @throws InvalidTenantNameException if the name is null, blank, or out of range
     * @throws NullPointerException       if any other required argument is null
     */
    public static Tenant create(String name,
                                Email email,
                                TaxId taxId,
                                PhoneNumber phoneNumber,
                                BaileysAppName baileysAppName) {

        String validatedName = validateName(name);
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(taxId, "taxId must not be null");
        Objects.requireNonNull(phoneNumber, "phoneNumber must not be null");
        Objects.requireNonNull(baileysAppName, "baileysAppName must not be null");

        TenantId id = TenantId.generate();
        Instant now = Instant.now();
        TenantStatus initialStatus = TenantStatus.initial();
        TenantPlan initialPlan = TenantPlan.initial();

        Tenant tenant = new Tenant(
            id,
            validatedName,
            email,
            taxId,
            phoneNumber,
            baileysAppName,
            initialStatus,
            initialPlan,
            false,
            false,
            false,
            now,
            now,
            null,
            0L
        );

        tenant.registerEvent(new TenantCreatedEvent(
            id,
            validatedName,
            email,
            taxId,
            phoneNumber,
            baileysAppName,
            initialStatus,
            initialPlan
        ));

        return tenant;
    }

    /**
     * Restores an existing tenant from persistence.
     *
     * <p>All fields are supplied externally, including the id, timestamps,
     * version, and verification flags. <strong>No domain events are
     * published</strong>: reconstitution is a technical operation, not a
     * business fact.
     *
     * <p>Enforces the invariant {@code updatedAt &gt;= createdAt} as a
     * sanity check against corrupted persistence state.
     *
     * @return a {@code Tenant} reflecting the persisted state
     * @throws IllegalArgumentException if {@code updatedAt} is earlier than {@code createdAt}
     * @throws NullPointerException     if any required argument is null
     */
    public static Tenant reconstitute(TenantId id,
                                      String name,
                                      Email email,
                                      TaxId taxId,
                                      PhoneNumber phoneNumber,
                                      BaileysAppName baileysAppName,
                                      TenantStatus status,
                                      TenantPlan plan,
                                      boolean emailVerified,
                                      boolean phoneNumberVerified,
                                      boolean twoFactorEnabled,
                                      Instant createdAt,
                                      Instant updatedAt,
                                      Instant lastAccessAt,
                                      long version) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(taxId, "taxId must not be null");
        Objects.requireNonNull(phoneNumber, "phoneNumber must not be null");
        Objects.requireNonNull(baileysAppName, "baileysAppName must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(plan, "plan must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException(
                    "updatedAt (" + updatedAt + ") must not be earlier than createdAt (" + createdAt + ")"
            );
        }

        return new Tenant(
                id,
                name,
                email,
                taxId,
                phoneNumber,
                baileysAppName,
                status,
                plan,
                emailVerified,
                phoneNumberVerified,
                twoFactorEnabled,
                createdAt,
                updatedAt,
                lastAccessAt,
                version
        );
    }

    /**
     * Validates and normalizes a tenant name.
     *
     * <p>The name is trimmed of surrounding whitespace, then validated
     * against the length boundaries {@link #NAME_MIN_LENGTH} and
     * {@link #NAME_MAX_LENGTH}.
     *
     * @return the trimmed, validated name
     * @throws InvalidTenantNameException if the name is null, blank, or out of range
     */
    private static String validateName(String rawName) {

        if (rawName == null || rawName.isBlank()) {
            throw InvalidTenantNameException.forNullOrBlank();
        }
        String trimmed = rawName.trim();
        if (trimmed.length() < NAME_MIN_LENGTH) {
            throw InvalidTenantNameException.forTooShort(trimmed, NAME_MIN_LENGTH);
        }
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw InvalidTenantNameException.forTooLong(trimmed, NAME_MAX_LENGTH);
        }
        return trimmed;
    }

   // === Lifecycle operations ===

    /**
     * Activates a {@code PENDING} tenant, transitioning it to {@code ACTIVE}.
     *
     * <p><strong>Pre-conditions:</strong>
     * <ul>
     *   <li>The tenant must not be {@code CANCELLED}</li>
     *   <li>The current status must be {@code PENDING}</li>
     *   <li>The contact email must be verified (anti-squatting safeguard)</li>
     * </ul>
     *
     * <p>Publishes a {@link TenantActivatedEvent} on success.
     *
     * @throws TenantAlreadyCancelledException  if the tenant is in CANCELLED status
     * @throws TenantStatusTransitionException  if the current status is not PENDING
     * @throws EmailNotVerifiedException        if the contact email has not been verified
     */
    public void activate() {
        ensureNotCancelled("activate");

        if (status != TenantStatus.PENDING) {
            throw TenantStatusTransitionException.between(status, TenantStatus.ACTIVE);
        }

        if (!emailVerified) {
            throw EmailNotVerifiedException.forOperation("activate");
        }

        this.status = TenantStatus.ACTIVE;
        touch();
        registerEvent(new TenantActivatedEvent(id));
    }

    /**
     * Suspends an {@code ACTIVE} tenant, transitioning it to {@code SUSPENDED}.
     *
     * <p>A suspended tenant cannot use the system but is not terminated;
     * it can be reactivated later via {@link #reactivate()}.
     *
     * <p>Publishes a {@link TenantSuspendedEvent} on success.
     *
     * @throws TenantAlreadyCancelledException  if the tenant is in CANCELLED status
     * @throws TenantStatusTransitionException  if the current status is not ACTIVE
     */
    public void suspend() {
        ensureNotCancelled("suspend");

        transitionTo(TenantStatus.SUSPENDED);
        touch();
        registerEvent(new TenantSuspendedEvent(id));
    }

    /**
     * Reactivates a {@code SUSPENDED} tenant, transitioning it back to {@code ACTIVE}.
     *
     * <p>Publishes a {@link TenantReactivatedEvent} on success.
     *
     * @throws TenantAlreadyCancelledException  if the tenant is in CANCELLED status
     * @throws TenantStatusTransitionException  if the current status is not SUSPENDED
     */
    public void reactivate() {
        ensureNotCancelled("reactivate");

        if (status != TenantStatus.SUSPENDED) {
            throw TenantStatusTransitionException.between(status, TenantStatus.ACTIVE);
        }

        this.status = TenantStatus.ACTIVE;
        touch();
        registerEvent(new TenantReactivatedEvent(id));
    }

    /**
     * Voluntarily cancels this tenant, transitioning it to the terminal
     * {@code CANCELLED} state.
     *
     * <p>Used when the customer themselves requests termination. For
     * administrative force-cancellation (terms violations, fraud, etc.),
     * use {@link #forceCancel(String)} instead.
     *
     * <p>Publishes a {@link TenantCancelledEvent} with an empty reason.
     *
     * @throws TenantAlreadyCancelledException if the tenant is already cancelled
     * @throws TenantStatusTransitionException if the current status cannot transition to CANCELLED
     */
    public void cancel() {
        ensureNotCancelled("cancel");

        transitionTo(TenantStatus.CANCELLED);
        touch();
        registerEvent(new TenantCancelledEvent(id));
    }

    /**
     * Administratively cancels this tenant with a documented reason.
     *
     * <p>Used by administrators for terms-of-service violations, payment
     * failures, or other operational decisions. The reason is mandatory
     * for compliance (LGPD/GDPR) and internal auditing.
     *
     * <p>Publishes a {@link TenantCancelledEvent} carrying the reason.
     *
     * @param reason a non-blank explanation for the cancellation
     * @throws TenantAlreadyCancelledException     if the tenant is already cancelled
     * @throws InvalidCancellationReasonException  if the reason is null or blank
     * @throws TenantStatusTransitionException     if the current status cannot transition to CANCELLED
     */
    public void forceCancel(String reason) {
        ensureNotCancelled("forceCancel");

        if (reason == null || reason.isBlank()) {
            throw InvalidCancellationReasonException.forNullOrBlank();
        }

        String trimmedReason = reason.trim();
        transitionTo(TenantStatus.CANCELLED);
        touch();
        registerEvent(new TenantCancelledEvent(id, trimmedReason));
    }

    // === Internal helpers ===

    /**
     * Guard that prevents any write operation on a cancelled tenant.
     * Cancelled is the terminal state — data is preserved but immutable
     * from the domain's perspective.
     *
     * @param operationName the name of the operation being attempted,
     *                      used for diagnostic messages
     * @throws TenantAlreadyCancelledException if the tenant is in CANCELLED status
     */
    private void ensureNotCancelled(String operationName) {
        if (status == TenantStatus.CANCELLED) {
            throw TenantAlreadyCancelledException.forOperation(operationName);
        }
    }

    /**
     * Performs a status transition, validating it against the state machine
     * declared in {@link TenantStatus}.
     *
     * @param target the desired status
     * @throws TenantStatusTransitionException if the transition is not allowed
     */
    private void transitionTo(TenantStatus target) {
        if (!status.canTransitionTo(target)) {
            throw TenantStatusTransitionException.between(status, target);
        }
        this.status = target;
    }

    /**
     * Updates {@code updatedAt} to the current instant. Called by every
     * write operation to keep the timestamp in sync.
     */
    private void touch() {
        this.updatedAt = Instant.now();
    }

    // === Data change operations ===
    /**
     * Changes the tenant's contact email.
     *
     * <p><strong>Security note:</strong> changing the email automatically
     * resets the verification flag to {@code false}. The new address must
     * be re-verified before any operation that requires a verified email
     * (such as {@link #activate()} or {@link #enableTwoFactorAuth()}).
     * This prevents an attacker from inheriting verification status by
     * changing the address to one they control.
     *
     * <p>If the provided email equals the current one, this method is a
     * no-op: verification is preserved and no event is published.
     *
     * <p>Publishes {@link TenantEmailChangedEvent} on actual change.
     *
     * @param newEmail the new contact email (must not be null)
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     * @throws NullPointerException            if {@code newEmail} is null
     */
    public void changeEmail(Email newEmail) {
        ensureNotCancelled("changeEmail");
        Objects.requireNonNull(newEmail, "email must not be null");

        if (this.email.equals(newEmail)) {
            return;
        }

        Email oldEmail = this.email;
        this.email = newEmail;
        this.emailVerified = false;
        touch();
        registerEvent(new TenantEmailChangedEvent(id, oldEmail, newEmail));
    }

    /**
     * Changes the tenant's contact phone number.
     *
     * <p><strong>Security note:</strong> changing the phone number
     * automatically resets the verification flag to {@code false}. The
     * new number must be re-verified before features that rely on a
     * verified phone (such as WhatsApp messaging trust signals).
     *
     * <p>If the provided phone equals the current one, this method is a
     * no-op: verification is preserved and no event is published.
     *
     * <p>Publishes {@link TenantPhoneNumberChangedEvent} on actual change.
     *
     * @param newPhoneNumber the new phone number (must not be null)
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     * @throws NullPointerException            if {@code newPhoneNumber} is null
     */
    public void changePhone(PhoneNumber newPhoneNumber) {
        ensureNotCancelled("changePhoneNumber");
        Objects.requireNonNull(newPhoneNumber, "phoneNumber must not be null");

        if (this.phoneNumber.equals(newPhoneNumber)) {
            return;
        }

        PhoneNumber oldPhone = this.phoneNumber;
        this.phoneNumber = newPhoneNumber;
        this.phoneNumberVerified = false;
        touch();
        registerEvent(new TenantPhoneNumberChangedEvent(id, oldPhone, newPhoneNumber));
    }

    /**
     * Renames the tenant.
     *
     * <p>The input is trimmed and validated against the same rules as
     * {@link #create(String, Email, TaxId, PhoneNumber, BaileysAppName)}.
     *
     * <p>If the trimmed new name equals the current one, this method is
     * a no-op: no event is published.
     *
     * <p>Publishes {@link TenantRenamedEvent} on actual change.
     *
     * @param newName the new display name (2 to 100 characters, trimmed)
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     * @throws InvalidTenantNameException      if the name is null, blank, or out of range
     */
    public void rename(String newName) {
        ensureNotCancelled("rename");
        String validatedName = validateName(newName);

        if (this.name.equals(validatedName)) {
            return;
        }

        String oldName = this.name;
        this.name = validatedName;
        touch();
        registerEvent(new TenantRenamedEvent(id, oldName, validatedName));
    }

    // === Verification operations ===

    /**
     * Marks the contact email as verified.
     *
     * <p>This method is idempotent: calling it on an already-verified
     * tenant is a no-op (no event published). This matches the common
     * UX pattern where the user may click the verification link multiple
     * times.
     *
     * <p>Publishes {@link TenantEmailVerifiedEvent} on actual change.
     *
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     */
    public void markEmailAsVerified() {
        ensureNotCancelled("markEmailAsVerified");

        if (this.emailVerified) {
            return;
        }
        this.emailVerified = true;
        touch();
        registerEvent(new TenantEmailVerifiedEvent(id));
    }

    /**
     * Marks the contact phone number as verified.
     *
     * <p>Idempotent: no-op when the phone is already verified.
     *
     * <p>Publishes {@link TenantPhoneNumberVerifiedEvent} on actual change.
     *
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     */
    public void markPhoneNumberAsVerified() {
        ensureNotCancelled("markPhoneNumberAsVerified");

        if (this.phoneNumberVerified) {
            return;
        }

        this.phoneNumberVerified = true;
        touch();
        registerEvent(new TenantPhoneNumberVerifiedEvent(id));
    }

    // === Security operations ===

    /**
     * Enables two-factor authentication for this tenant.
     *
     * <p>Requires a verified email, because the recovery channel for 2FA
     * is the contact email. Enabling 2FA on an unverified address would
     * leave a back door open.
     *
     * <p>Idempotent: no-op when 2FA is already enabled.
     *
     * <p>Publishes {@link TenantTwoFactorAuthEnabledEvent} on actual change.
     *
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     * @throws EmailNotVerifiedException       if the contact email is not verified
     */
    public void enableTwoFactorAuth() {
        ensureNotCancelled("enableTwoFactorAuth");

        if (!emailVerified) {
            throw EmailNotVerifiedException.forOperation("enableTwoFactorAuth");
        }
        if (this.twoFactorEnabled) {
            return;
        }

        this.twoFactorEnabled = true;
        touch();
        registerEvent(new TenantTwoFactorAuthEnabledEvent(id));
    }

    /**
     * Disables two-factor authentication for this tenant.
     *
     * <p>Idempotent: no-op when 2FA is already disabled.
     *
     * <p>Publishes {@link TenantTwoFactorAuthDisabledEvent} on actual change.
     *
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     */
    public void disableTwoFactorAuth() {
        ensureNotCancelled("disableTwoFactorAuth");

        if (!this.twoFactorEnabled) {
            return;
        }
        this.twoFactorEnabled = false;
        touch();
        registerEvent(new TenantTwoFactorAuthDisabledEvent(id));
    }

    // === Activity tracking ===

    /**
     * Records the moment of the tenant's last access.
     *
     * <p>Unlike business operations, this method does <strong>not</strong>
     * update {@link #updatedAt()} (since access happens frequently and
     * tracking it would defeat the purpose of {@code updatedAt} as a
     * change indicator) and does <strong>not</strong> publish any domain
     * event (it is operational metric, not a business fact).
     *
     * @param when the moment of access (typically {@code Instant.now()})
     * @throws TenantAlreadyCancelledException if the tenant is cancelled
     * @throws NullPointerException            if {@code when} is null
     */
    public void recordLastAccess(Instant when) {
        ensureNotCancelled("recordLastAccess");
        Objects.requireNonNull(when, "lastAccessAt must not be null");

        this.lastAccessAt = when;
    }

    // === Accessors ===
    public TenantId id() {
        return id;
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

    public TenantStatus status() {
        return status;
    }

    public TenantPlan plan() {
        return plan;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant lastAccessAt() {
        return lastAccessAt;
    }

    public long version() {
        return version;
    }

        // === Identity ===

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Tenant that)) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", plan=" + plan +
                '}';
    }
}
