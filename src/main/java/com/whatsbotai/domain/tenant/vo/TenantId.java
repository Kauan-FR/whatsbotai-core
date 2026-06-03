package com.whatsbotai.domain.tenant.vo;

import java.util.Objects;
import java.util.UUID;

import com.whatsbotai.domain.tenant.exception.InvalidTenantIdException;

/**
 * Value object representing the unique identifier of a {@code Tenant} aggregate.
 *
 * <p>{@code TenantId} wraps a {@link UUID} to provide compile-time type safety:
 * a method declaring {@code TenantId} as a parameter will not accept any other
 * identifier (e.g. {@code UserId}), even when both are UUIDs underneath.
 *
 * <p>Internally uses {@link UUID#randomUUID()} (UUID v4) for generation. The
 * wrapper isolates the concrete identifier format from the rest of the domain,
 * so migrating to another format (UUID v7, KSUID, ULID, etc.) in the future
 * would be a localized change.
 *
 * <p><strong>Factory methods:</strong>
 * <ul>
 *   <li>{@link #generate()} — produce a new identifier (use when creating a new aggregate)</li>
 *   <li>{@link #of(UUID)} — wrap an existing UUID (use when reconstituting from persistence)</li>
 *   <li>{@link #of(String)} — parse and wrap a UUID string (use when receiving from DTOs/APIs)</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantId {

    private final UUID value;

    private TenantId(UUID value) {
        this.value = value;
    }

    /**
     * Generates a new random {@code TenantId} backed by a UUID v4.
     *
     * @return a new, unique tenant identifier
     */
    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }

    /**
     * Wraps an existing {@link UUID} into a {@code TenantId}.
     *
     * @param uuid the underlying UUID (must not be null)
     * @return a {@code TenantId} backed by the given UUID
     * @throws InvalidTenantIdException if {@code uuid} is null
     */
    public static TenantId of(UUID uuid) {
        if (uuid == null) {
            throw InvalidTenantIdException.forNullValue();
        }
        return new TenantId(uuid);
    }

    /**
     * Parses a UUID string into a {@code TenantId}.
     *
     * <p>Accepts the canonical UUID textual representation (with hyphens),
     * in any case.
     *
     * @param uuidString the canonical UUID string
     * @return a {@code TenantId} backed by the parsed UUID
     * @throws InvalidTenantIdException if the input is null, blank, or not a valid UUID
     */
    public static TenantId of(String uuidString) {
        if (uuidString == null || uuidString.isBlank()) {
            throw InvalidTenantIdException.forNullOrBlankString();
        }
        try {
            return new TenantId(UUID.fromString(uuidString.trim()));
        } catch (IllegalArgumentException e) {
            throw InvalidTenantIdException.forMalformedUuid(uuidString);
        }
    }

    /**
     * Returns the underlying UUID.
     *
     * @return the wrapped UUID
     */
    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TenantId that)) return false;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
