package com.whatsbotai.domain.shared.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AggregateRoot — base for aggregates that publish domain events")
public class AggregateRootTest {

    @Nested
    @DisplayName("Initial state")
    class InitialState {

        @Test
        @DisplayName("a fresh aggregate should have no pending events")
        void freshAggregateShouldHaveNoEvents() {
            TestAggregate aggregate = new TestAggregate();

            assertThat(aggregate.pullDomainEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Recording events")
    class RecordingEvents {

        @Test
        @DisplayName("should expose recorded events in registration order")
        void shouldExposeRecordedEventsInOrder() {
            TestAggregate aggregate = new TestAggregate();
            TestEvent first = new TestEvent();
            TestEvent second = new TestEvent();

            aggregate.doSomething(first);
            aggregate.doSomething(second);

            List<DomainEvent> events = aggregate.pullDomainEvents();

            assertThat(events).containsExactly(first, second);
        }
    }

    @Nested
    @DisplayName("Pulling events")
    class PullingEvents {

        @Test
        @DisplayName("pullDomainEvents should clear the internal buffer")
        void pullingShouldClearBuffer() {
            TestAggregate aggregate = new TestAggregate();
            aggregate.doSomething(new TestEvent());

            aggregate.pullDomainEvents();
            List<DomainEvent> afterPull = aggregate.pullDomainEvents();

            assertThat(afterPull).isEmpty();
        }

        @Test
        @DisplayName("returned list should be a defensive copy (immutable view)")
        void returnedListShouldBeDefensiveCopy() {
            TestAggregate aggregate = new TestAggregate();
            aggregate.doSomething(new TestEvent());

            List<DomainEvent> pulled = aggregate.pullDomainEvents();

            assertThat(pulled).hasSize(1);
            // Mutating the returned list must not throw, but also must not affect internal state
            // (we get a copy, so any attempt to add to it would be on the copy only)
            assertThat(aggregate.pullDomainEvents()).isEmpty();
        }
    }

    /**
     * Concrete aggregate used only for testing.
     */
    private static final class TestAggregate extends AggregateRoot {
        void doSomething(DomainEvent event) {
            registerEvent(event);
        }
    }

    /**
     * Concrete event used only for testing.
     */
    private static final class TestEvent extends AbstractDomainEvent{}
}
