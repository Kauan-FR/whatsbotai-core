package com.whatsbotai.domain.tenant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SupportLevel — abstract tier of customer support promised to a Tenant")
class SupportLevelTest {

    @Nested
    @DisplayName("Tier ordering")
    class TierOrdering {

        @ParameterizedTest(name = "tier of {0} should be {1}")
        @CsvSource({
                "COMMUNITY, 0",
                "EMAIL,     1",
                "PRIORITY,  2",
                "DEDICATED, 3"
        })
        @DisplayName("each level should expose its expected tier value")
        void shouldExposeExpectedTier(SupportLevel level, int expectedTier) {
            assertThat(level.tier()).isEqualTo(expectedTier);
        }
    }

    @Nested
    @DisplayName("Comparison")
    class Comparison {

        @Test
        @DisplayName("higher tier should be at least as capable as a lower one")
        void higherShouldBeAtLeast() {
            assertThat(SupportLevel.DEDICATED.isAtLeast(SupportLevel.COMMUNITY)).isTrue();
            assertThat(SupportLevel.PRIORITY.isAtLeast(SupportLevel.EMAIL)).isTrue();
        }

        @Test
        @DisplayName("same tier should be at least as capable as itself")
        void sameTierShouldBeAtLeast() {
            assertThat(SupportLevel.EMAIL.isAtLeast(SupportLevel.EMAIL)).isTrue();
        }

        @Test
        @DisplayName("lower tier should NOT be at least as capable as a higher one")
        void lowerShouldNotBeAtLeast() {
            assertThat(SupportLevel.COMMUNITY.isAtLeast(SupportLevel.DEDICATED)).isFalse();
        }

        @Test
        @DisplayName("comparison against null should return false")
        void comparisonAgainstNullShouldReturnFalse() {
            assertThat(SupportLevel.EMAIL.isAtLeast(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Defaults")
    class Defaults {

        @Test
        @DisplayName("COMMUNITY should be the lowest level")
        void communityShouldBeLowest() {
            assertThat(SupportLevel.lowest()).isEqualTo(SupportLevel.COMMUNITY);
        }
    }

    @Nested
    @DisplayName("Exhaustiveness")
    class Exhaustiveness {

        @ParameterizedTest
        @EnumSource(SupportLevel.class)
        @DisplayName("every level should have a non-negative tier")
        void everyLevelShouldHaveNonNegativeTier(SupportLevel level) {
            assertThat(level.tier()).isGreaterThanOrEqualTo(0);
        }
    }
}