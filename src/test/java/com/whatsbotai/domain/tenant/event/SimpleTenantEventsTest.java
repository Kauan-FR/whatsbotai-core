package com.whatsbotai.domain.tenant.event;

import com.whatsbotai.domain.shared.event.DomainEvent;
import com.whatsbotai.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Simple tenant events — events that carry only the tenant id")
public class SimpleTenantEventsTest {

    private static Stream<Arguments> simpleEventFactories() {
            return Stream.of(
                Arguments.of("TenantActivatedEvent",
                        (Function<TenantId, TenantEvent>) TenantActivatedEvent::new),
                Arguments.of("TenantSuspendedEvent",
                        (Function<TenantId, TenantEvent>) TenantSuspendedEvent::new),
                Arguments.of("TenantReactivatedEvent",
                        (Function<TenantId, TenantEvent>) TenantReactivatedEvent::new),
                Arguments.of("TenantEmailVerifiedEvent",
                        (Function<TenantId, TenantEvent>) TenantEmailVerifiedEvent::new),
                Arguments.of("TenantPhoneNumberVerifiedEvent",
                        (Function<TenantId, TenantEvent>) TenantPhoneNumberVerifiedEvent::new),
                Arguments.of("TenantTwoFactorAuthEnabledEvent",
                        (Function<TenantId, TenantEvent>) TenantTwoFactorAuthEnabledEvent::new),
                Arguments.of("TenantTwoFactorAuthDisabledEvent",
                        (Function<TenantId, TenantEvent>) TenantTwoFactorAuthDisabledEvent::new)
        );
    }

    @Nested
    @DisplayName("Contract")
    class Contract {

        @ParameterizedTest(name = "{0} should implement DomainEvent and TenantEvent contracts")
        @MethodSource("com.whatsbotai.domain.tenant.event.SimpleTenantEventsTest#simpleEventFactories")
        @DisplayName("each simple event should be a TenantEvent and a DomainEvent")
        void shouldBeDomainAndTenantEvent(String displayName, Function<TenantId, TenantEvent> factory) {
            TenantEvent event = factory.apply(TenantId.generate());

            assertThat(event)
                    .isInstanceOf(DomainEvent.class)
                    .isInstanceOf(TenantEvent.class);
        }

        @ParameterizedTest(name = "{0} should carry the given tenant id")
        @MethodSource("com.whatsbotai.domain.tenant.event.SimpleTenantEventsTest#simpleEventFactories")
        @DisplayName("each simple event should preserve the tenant id passed to the constructor")
        void shouldCarryTenantId(String displayName, Function<TenantId, TenantEvent> factory) {
            TenantId tenantId = TenantId.generate();

            TenantEvent event = factory.apply(tenantId);

            assertThat(event.tenantId()).isEqualTo(tenantId);
        }

        @ParameterizedTest(name = "{0} should auto-generate eventId and occurredOn")
        @MethodSource("com.whatsbotai.domain.tenant.event.SimpleTenantEventsTest#simpleEventFactories")
        @DisplayName("each simple event should populate metadata automatically")
        void shouldPopulateMetadata(String displayName, Function<TenantId, TenantEvent> factory) {
            TenantEvent event = factory.apply(TenantId.generate());

            assertThat(event.eventId()).isNotNull();
            assertThat(event.occurredOn()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Null safety")
    class NullSafety {

        @Test
        @DisplayName("creating a TenantActivatedEvent with null tenantId should throw")
        void shouldRejectNullTenantId() {
            org.assertj.core.api.Assertions
                    .assertThatThrownBy(() -> new TenantActivatedEvent(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("tenantId");
        }
    }
}
