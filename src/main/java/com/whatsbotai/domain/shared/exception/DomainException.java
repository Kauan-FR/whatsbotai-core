/**
 * Base class for all domain exceptions in the WhatsBot AI system.
 *
 * <p>Domain exceptions signal violations of business invariants or
 * invalid input at the domain layer. They are intentionally unchecked
 * (extending {@link RuntimeException}) to avoid polluting method
 * signatures with {@code throws} clauses across the application.
 *
 * <p>Each bounded context defines its own specific subclasses
 * (e.g. {@code InvalidEmailException}, {@code TenantAlreadySuspendedException})
 * to enable granular exception handling in the application and
 * presentation layers.
 *
 * <p>This class is abstract to enforce that callers create
 * domain-specific exceptions rather than throwing the generic base.
 *
 * @author Kauan
 * @since 1.0
 */

package com.whatsbotai.domain.shared.exception;

public abstract class DomainException extends RuntimeException {

    /**
     * Creates a new domain exception with the given message.
     *
     * @param message the detail message describing the domain rule violation
     */
    protected DomainException(String message){
        super(message);
    }

    /**
     * Creates a new domain exception with the given message and cause.
     *
     * @param message the detail message describing the domain rule violation
     * @param cause   the underlying cause of this exception
     */
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
