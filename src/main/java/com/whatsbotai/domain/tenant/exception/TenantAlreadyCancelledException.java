package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;

/**
 * Thrown when a write operation is attempted on a tenant that is already in
 * the terminal {@code CANCELLED} state.
 *
 * <p>A cancelled tenant is read-only from the domain's perspective: its data
 * is preserved for historical and compliance reasons, but no further
 * mutations are accepted.
 *
 * <p>The attempted operation's name is included in the message to make
 * auditing and debugging straightforward.
 *
 * @author Kauan
 * @since 1.0
 */
public final class TenantAlreadyCancelledException extends DomainException{

    private TenantAlreadyCancelledException(String message) {
        super(message);
    }

    /**
     * Creates an exception describing which operation was attempted on a cancelled tenant.
     *
     * @param operationName the name of the operation that was rejected
     *                      (e.g. {@code "changeEmail"}, {@code "upgradeTo"})
     * @return a new exception with the operation included in the message
     */
    public static TenantAlreadyCancelledException forOperation(String operationName) {
        return new TenantAlreadyCancelledException(
            "Operation '" + operationName + "' is not allowed: tenant is already cancelled"

        );
    }
}
