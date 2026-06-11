package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.tenant.vo.TenantId;

import java.util.Objects;
import java.util.Optional;

/**
 * Emitted when a {@code Tenant} transitions to the terminal {@code CANCELLED} state.
 *
 * <p>The {@code reason} is optional:
 * <ul>
 *   <li>When the tenant cancels voluntarily ({@code cancel()}), no reason is carried</li>
 *   <li>When an administrator force-cancels ({@code forceCancel(reason)}),
 *       the documented motive is included for compliance and auditing</li>
 * </ul>
 *
 * <p>Typical reactions: stopping all Baileys instances, removing recurring
 * billing, scheduling data retention/deletion per LGPD requirements,
 * notifying the tenant.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantCancelledEvent extends TenantEvent {

    private final String reason;

    public TenantCancelledEvent(TenantId tenantId) {
        super(tenantId);
        this.reason = null;
    }

    public TenantCancelledEvent(TenantId tenantId, String reason) {
        super(tenantId);
        this.reason = Objects.requireNonNull(reason, "reason must not be null when explicitly provided");
    }

    /**
     * Returns the documented reason for cancellation when present.
     *
     * @return an {@link Optional} containing the reason for an admin force-cancellation,
     *         or empty for a voluntary cancellation
     */
    public Optional<String> reason() {
        return Optional.ofNullable(reason);
    }
}
