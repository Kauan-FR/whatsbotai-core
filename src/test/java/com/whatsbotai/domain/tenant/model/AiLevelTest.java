package com.whatsbotai.domain.tenant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AiLevel — abstract quality tier of the AI used by a Tenant")
class AiLevelTest {

    @Nested
    @DisplayName("Tier ordering")
    class TierOrdering {

        @ParameterizedTest(name = "tier of {0} should be {1}")
        @CsvSource({
                "BASIC,    0",
                "STANDARD, 1",
                "ADVANCED, 2",
                "PREMIUM,  3"
        })
        @DisplayName("each level should expose its expected tier value")
        void shouldExposeExpectedTier(AiLevel level, int expectedTier) {
            assertThat(level.tier()).isEqualTo(expectedTier);
        }

        @Test
        @DisplayName("levels should be naturally orderable by tier")
        void shouldBeOrderableByTier() {
            assertThat(AiLevel.BASIC.tier()).isLessThan(AiLevel.STANDARD.tier());
            assertThat(AiLevel.STANDARD.tier()).isLessThan(AiLevel.ADVANCED.tier());
            assertThat(AiLevel.ADVANCED.tier()).isLessThan(AiLevel.PREMIUM.tier());
        }
    }

    @Nested
    @DisplayName("Comparison")
    class Comparison {

        @Test
        @DisplayName("higher tier level should be at least as capable as a lower one")
        void higherTierShouldBeAtLeastAsCapable() {
            assertThat(AiLevel.ADVANCED.isAtLeast(AiLevel.STANDARD)).isTrue();
            assertThat(AiLevel.PREMIUM.isAtLeast(AiLevel.BASIC)).isTrue();
        }

        @Test
        @DisplayName("same level should be considered at least as capable as itself")
        void sameLevelShouldBeAtLeastAsCapable() {
            assertThat(AiLevel.STANDARD.isAtLeast(AiLevel.STANDARD)).isTrue();
        }

        @Test
        @DisplayName("lower tier level should NOT be at least as capable as a higher one")
        void lowerTierShouldNotBeAtLeastAsCapable() {
            assertThat(AiLevel.BASIC.isAtLeast(AiLevel.PREMIUM)).isFalse();
            assertThat(AiLevel.STANDARD.isAtLeast(AiLevel.ADVANCED)).isFalse();
        }

        @Test
        @DisplayName("comparison against null should return false")
        void comparisonAgainstNullShouldReturnFalse() {
            assertThat(AiLevel.STANDARD.isAtLeast(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Defaults")
    class Defaults {

        @Test
        @DisplayName("BASIC should be the lowest level")
        void basicShouldBeLowest() {
            assertThat(AiLevel.lowest()).isEqualTo(AiLevel.BASIC);
        }

        @Test
        @DisplayName("PREMIUM should be the highest level")
        void premiumShouldBeHighest() {
            assertThat(AiLevel.highest()).isEqualTo(AiLevel.PREMIUM);
        }
    }

    @Nested
    @DisplayName("Exhaustiveness")
    class Exhaustiveness {

        @ParameterizedTest
        @EnumSource(AiLevel.class)
        @DisplayName("every level should have a non-negative tier")
        void everyLevelShouldHaveNonNegativeTier(AiLevel level) {
            assertThat(level.tier()).isGreaterThanOrEqualTo(0);
        }
    }
}