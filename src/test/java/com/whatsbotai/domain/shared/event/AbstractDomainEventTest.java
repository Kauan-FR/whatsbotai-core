package com.whatsbotai.domain.shared.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AbstractDomainEvent — base class for all domain events")
class AbstractDomainEventTest {

    @Nested
    @DisplayName("Contract")
    class Contract {

        @Test
        @DisplayName("should implement DomainEvent")
        void shouldImplementDomainEvent() {
            TestDomainEvent event = new TestDomainEvent();

            assertThat(event).isInstanceOf(DomainEvent.class);
        }

        @Test
        @DisplayName("should be abstract — cannot be instantiated directly")
        void shouldBeAbstract() {
            assertThat(java.lang.reflect.Modifier.isAbstract(AbstractDomainEvent.class.getModifiers()))
                    .as("AbstractDomainEvent must be abstract")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Metadata")
    class Metadata {

        @Test
        @DisplayName("should generate a non-null event id automatically")
        void shouldGenerateEventId() {
            TestDomainEvent event = new TestDomainEvent();

            assertThat(event.eventId()).isNotNull();
        }

        @Test
        @DisplayName("two events should have different ids")
        void twoEventsShouldHaveDifferentIds() {
            TestDomainEvent a = new TestDomainEvent();
            TestDomainEvent b = new TestDomainEvent();

            assertThat(a.eventId()).isNotEqualTo(b.eventId());
        }

        @Test
        @DisplayName("should record the moment of occurrence automatically")
        void shouldRecordOccurrenceMoment() {
            Instant before = Instant.now();

            TestDomainEvent event = new TestDomainEvent();

            Instant after = Instant.now();

            assertThat(event.occurredOn())
                    .isNotNull()
                    .isBetween(before, after);
        }

        @Nested
        @DisplayName("Equality")
        class Equality {

            @Test
            @DisplayName("events with the same eventId should be equal")
            void sameIdShouldBeEqual() {
                UUID sharedId = UUID.randomUUID();
                TestDomainEvent a = new TestDomainEvent(sharedId, Instant.now());
                TestDomainEvent b = new TestDomainEvent(sharedId, Instant.now().plusSeconds(60));

                assertThat(a).isEqualTo(b);
                assertThat(a.hashCode()).isEqualTo(b.hashCode());
            }

            @Test
            @DisplayName("events with different eventIds should not be equal")
            void differentIdsShouldNotBeEqual() {
                TestDomainEvent a = new TestDomainEvent();
                TestDomainEvent b = new TestDomainEvent();

                assertThat(a).isNotEqualTo(b);
            }
        }
    }

    /**
     * Concrete subclass used only for testing the abstract base.
     */
    private static final class TestDomainEvent extends AbstractDomainEvent {
        TestDomainEvent() {
            super();
        }

        TestDomainEvent(UUID eventId, Instant occurredOn) {
            super(eventId, occurredOn);
        }
    }
}
