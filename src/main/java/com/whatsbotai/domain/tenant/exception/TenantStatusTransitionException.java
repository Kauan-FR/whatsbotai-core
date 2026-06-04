package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import com.whatsbotai.domain.tenant.model.TenantStatus;

/**
 * Thrown when an invalid transition between two {@link TenantStatus} values
 * is attempted on a {@code Tenant} aggregate.
 *
 * <p>The valid transitions are defined inside {@link TenantStatus}; any
 * attempt outside that set results in this exception.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantStatusTransitionException extends DomainException {

    private TenantStatusTransitionException(String message) {
        super(message);
    }

    /**
     * Creates an exception describing an invalid transition between two statuses.
     *
     * @param from the current status
     * @param to   the attempted target status
     * @return a new exception with both statuses included in the message
     */
    public static TenantStatusTransitionException between(TenantStatus from, TenantStatus to) {
        return new TenantStatusTransitionException(
                "Invalid tenant status transition from " + from + " to " + to
        );
    }
}