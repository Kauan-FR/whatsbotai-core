package com.whatsbotai.domain.tenant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantStatus — lifecycle state machine for a Tenant aggregate")
class TenantStatusTest {

    @Nested
    @DisplayName("Valid transitions")
    class ValidTransitions {

        @ParameterizedTest(name = "{0} should be allowed to transition to {1}")
        @CsvSource({
                "PENDING,   ACTIVE",
                "PENDING,   CANCELLED",
                "ACTIVE,    SUSPENDED",
                "ACTIVE,    CANCELLED",
                "SUSPENDED, ACTIVE",
                "SUSPENDED, CANCELLED"
        })
        @DisplayName("should allow defined transitions")
        void shouldAllowDefinedTransitions(TenantStatus from, TenantStatus to) {
            assertThat(from.canTransitionTo(to)).isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid transitions")
    class InvalidTransitions {

        @ParameterizedTest(name = "{0} should NOT be allowed to transition to {1}")
        @CsvSource({
                "PENDING,   PENDING",
                "PENDING,   SUSPENDED",
                "ACTIVE,    PENDING",
                "ACTIVE,    ACTIVE",
                "SUSPENDED, PENDING",
                "SUSPENDED, SUSPENDED",
                "CANCELLED, PENDING",
                "CANCELLED, ACTIVE",
                "CANCELLED, SUSPENDED",
                "CANCELLED, CANCELLED"
        })
        @DisplayName("should reject undefined transitions")
        void shouldRejectUndefinedTransitions(TenantStatus from, TenantStatus to) {
            assertThat(from.canTransitionTo(to)).isFalse();
        }
    }

    @Nested
    @DisplayName("Terminal state")
    class TerminalState {

        @Test
        @DisplayName("CANCELLED should be terminal (no outgoing transitions)")
        void cancelledShouldBeTerminal() {
            assertThat(TenantStatus.CANCELLED.isTerminal()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(value = TenantStatus.class, names = {"PENDING", "ACTIVE", "SUSPENDED"})
        @DisplayName("non-terminal statuses should report not terminal")
        void nonTerminalStatusesShouldReportNotTerminal(TenantStatus status) {
            assertThat(status.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("Initial state")
    class InitialState {

        @Test
        @DisplayName("PENDING should be the initial status for new tenants")
        void pendingShouldBeInitial() {
            assertThat(TenantStatus.initial()).isEqualTo(TenantStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("Capabilities")
    class Capabilities {

        @Test
        @DisplayName("only ACTIVE should be operational")
        void onlyActiveShouldBeOperational() {
            assertThat(TenantStatus.ACTIVE.isOperational()).isTrue();
            assertThat(TenantStatus.PENDING.isOperational()).isFalse();
            assertThat(TenantStatus.SUSPENDED.isOperational()).isFalse();
            assertThat(TenantStatus.CANCELLED.isOperational()).isFalse();
        }
    }

    @Nested
    @DisplayName("Null safety")
    class NullSafety {

        @Test
        @DisplayName("canTransitionTo(null) should return false")
        void shouldReturnFalseForNullTarget() {
            assertThat(TenantStatus.ACTIVE.canTransitionTo(null)).isFalse();
        }
    }
}