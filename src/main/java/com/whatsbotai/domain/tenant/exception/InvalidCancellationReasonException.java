package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when an administrative force-cancellation is attempted without
 * providing a valid reason.
 *
 * <p>Recording a reason is required for two purposes:
 * <ul>
 *   <li><strong>Compliance (LGPD/GDPR):</strong> the tenant has the right
 *       to know why their account was terminated</li>
 *   <li><strong>Internal auditing:</strong> distinguishing terms-of-service
 *       violations, payment failures, and operational decisions requires
 *       a documented motive</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class InvalidCancellationReasonException extends DomainException{

    private InvalidCancellationReasonException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a null, empty, or blank cancellation reason.
     *
     * @return a new exception with a standardized message
     */
    public static InvalidCancellationReasonException forNullOrBlank() {
        return new InvalidCancellationReasonException("Cancellation reason must not be null or blank");
    }
}
