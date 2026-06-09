package com.whatsbotai.domain.shared.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base implementation of {@link DomainEvent} carrying the mandatory metadata
 * (event id and occurrence instant) so concrete events do not need to
 * reimplement them.
 *
 * <p><strong>Two constructors are provided:</strong>
 * <ul>
 *   <li>{@link #AbstractDomainEvent()} — used when a brand-new event is
 *       being created; generates a fresh UUID and captures
 *       {@code Instant.now()} automatically.</li>
 *   <li>{@link #AbstractDomainEvent(UUID, Instant)} — used when an event
 *       is being reconstituted from persistence (event sourcing, replay),
 *       so the original metadata is preserved.</li>
 * </ul>
 *
 * <p>Event equality is defined purely by {@link #eventId()}: two events
 * with the same id are the same event, regardless of when they are observed.
 *
 * @author Kauan
 * @since 1.0
 */
public abstract class AbstractDomainEvent implements DomainEvent{

    private final UUID eventId;
    private final Instant occurredOn;

    /**
     * Creates a new domain event with a fresh id and the current instant.
     * Use when a brand-new event is being raised by an aggregate.
     */
    protected AbstractDomainEvent() {
        this(UUID.randomUUID(), Instant.now());
    }

    /**
     * Creates a domain event with the given metadata.
     * Use when reconstituting an event from persistence.
     *
     * @param eventId    the event identifier (must not be {@code null})
     * @param occurredOn the moment the event occurred (must not be {@code null})
     */
    protected AbstractDomainEvent(UUID eventId, Instant occurredOn) {
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

        @Override
    public final UUID eventId() {
        return eventId;
    }

    @Override
    public final Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractDomainEvent that)) return false;
        return Objects.equals(this.eventId, that.eventId);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(eventId);
    }
}
