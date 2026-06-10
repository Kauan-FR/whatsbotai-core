package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when an operation that requires the tenant's email to be verified
 * is attempted before verification has occurred.
 *
 * <p>Operations that require a verified email include (but are not limited to):
 * <ul>
 *   <li>{@code activate()} — preventing account squatting / typosquatting</li>
 *   <li>{@code enableTwoFactorAuth()} — ensuring the recovery channel is trusted</li>
 * </ul>
 *
 * @author Kauan
 * @since 1.0
 */
public final class EmailNotVerifiedException extends DomainException{

    private EmailNotVerifiedException(String message) {
        super(message);
    }

    /**
     * Creates an exception describing which operation was rejected due to an unverified email.
     *
     * @param operationName the name of the operation that was rejected
     * @return a new exception with the operation included in the message
     */
    public static EmailNotVerifiedException forOperation(String operationName) {
        return new EmailNotVerifiedException(
            "Operation '" + operationName + "' requires the tenant's email to be verified first"

        );
    }
}
