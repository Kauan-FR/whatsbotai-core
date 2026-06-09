package com.whatsbotai.domain.shared.event;

import java.time.Instant;
import java.util.UUID;

/*
 *
 * <p>A domain event represents a fact that occurred inside the domain and
 * that other parts of the system may want to react to (e.g., send an email,
 * publish to a message queue, update a read model). Events are immutable
 * by nature: once something has happened, it cannot be undone.
 *
 * <p>Every domain event carries two mandatory pieces of metadata:
 * <ul>
 *   <li>{@link #eventId()} — a unique identifier used for idempotency
 *       (handlers must not process the same event twice)</li>
 *   <li>{@link #occurredOn()} — the absolute moment in time when the
 *       event occurred, expressed in UTC via {@link Instant}</li>
 * </ul>
 *
 * <p>Concrete events should extend {@link AbstractDomainEvent} to inherit
 * the default implementation of these metadata fields.
 *
 * @author Kauan
 * @since 1.0
 */
public interface DomainEvent {

    /**
     * Returns the unique identifier of this event.
     *
     * @return the event id (never {@code null})
     */
    UUID eventId();

    /**
     * Returns the moment in time when this event occurred, in UTC.
     *
     * @return the event occurrence instant (never {@code null})
     */
    Instant occurredOn();
}
