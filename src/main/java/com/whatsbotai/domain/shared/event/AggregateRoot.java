package com.whatsbotai.domain.shared.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for aggregate roots that publish {@link DomainEvent}s as part
 * of their behavior.
 *
 * <p>Aggregates accumulate events in an internal buffer during business
 * operations (for example, a {@code Tenant} suspends itself and records
 * a {@code TenantSuspendedEvent}). The application layer is responsible
 * for draining this buffer via {@link #pullDomainEvents()} after the
 * aggregate has been persisted, and for publishing the drained events to
 * the appropriate handlers (event bus, message queue, in-process listeners).
 *
 * <p><strong>Why pull instead of push:</strong> the domain does not know
 * what to do with events (that is the application layer's job). It only
 * declares that something happened. This keeps the domain free of any
 * messaging or infrastructure dependency.
 *
 * <p><strong>Thread-safety:</strong> aggregates are intended to be used
 * by a single thread within a unit of work; this class is not thread-safe.
 *
 * @author Kauan
 * @since 1.0
 */
public abstract class AggregateRoot {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Registers a new domain event raised by this aggregate.
     *
     * <p>Intended to be called from within domain methods of concrete
     * subclasses (for example, inside {@code tenant.suspend()}).
     *
     * @param event the event to record (must not be {@code null})
     */
    protected final void registerEvent(DomainEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Domain event must not be null");
        }
        domainEvents.add(event);
    }

    /**
     * Returns the events currently pending publication and clears the
     * internal buffer in the same operation.
     *
     * <p>The returned list is an immutable snapshot; callers cannot
     * mutate the aggregate's internal state through it.
     *
     * @return an unmodifiable snapshot of pending events (possibly empty,
     *         never {@code null})
     */
    public final List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> snapshot = List.copyOf(domainEvents);
        domainEvents.clear();
        return snapshot;
    }
}
