package com.whatsbotai.domain.tenant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantPlan — commercial plan with limits and capabilities")
class TenantPlanTest {

    @Nested
    @DisplayName("Initial plan")
    class InitialPlan {

        @Test
        @DisplayName("FREE should be the initial plan for new tenants")
        void freeShouldBeInitial() {
            assertThat(TenantPlan.initial()).isEqualTo(TenantPlan.FREE);
        }
    }

    @Nested
    @DisplayName("Tier ordering")
    class TierOrdering {

        @ParameterizedTest(name = "tier of {0} should be {1}")
        @CsvSource({
                "FREE,         0",
                "STARTER,      1",
                "PROFESSIONAL, 2",
                "ENTERPRISE,   3"
        })
        @DisplayName("each plan should have its expected tier value")
        void shouldHaveExpectedTier(TenantPlan plan, int expectedTier) {
            assertThat(plan.tier()).isEqualTo(expectedTier);
        }

        @Test
        @DisplayName("higher tier plan should be an upgrade from a lower one")
        void higherTierShouldBeUpgrade() {
            assertThat(TenantPlan.PROFESSIONAL.isUpgradeFrom(TenantPlan.STARTER)).isTrue();
            assertThat(TenantPlan.ENTERPRISE.isUpgradeFrom(TenantPlan.FREE)).isTrue();
        }

        @Test
        @DisplayName("lower tier plan should be a downgrade from a higher one")
        void lowerTierShouldBeDowngrade() {
            assertThat(TenantPlan.STARTER.isDowngradeFrom(TenantPlan.PROFESSIONAL)).isTrue();
        }

        @Test
        @DisplayName("comparison against null should return false")
        void comparisonAgainstNullShouldReturnFalse() {
            assertThat(TenantPlan.STARTER.isUpgradeFrom(null)).isFalse();
            assertThat(TenantPlan.STARTER.isDowngradeFrom(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Quantitative limits — monthly messages")
    class MonthlyMessages {

        @ParameterizedTest(name = "{0} should allow {1} monthly messages")
        @CsvSource({
                "FREE,         500",
                "STARTER,      5000",
                "PROFESSIONAL, 50000"
        })
        @DisplayName("non-unlimited plans should expose a positive limit")
        void shouldExposeMessageLimits(TenantPlan plan, int expected) {
            assertThat(plan.maxMonthlyMessages()).isEqualTo(expected);
            assertThat(plan.isMonthlyMessagesUnlimited()).isFalse();
        }

        @Test
        @DisplayName("ENTERPRISE should have unlimited monthly messages")
        void enterpriseShouldHaveUnlimitedMessages() {
            assertThat(TenantPlan.ENTERPRISE.isMonthlyMessagesUnlimited()).isTrue();
        }
    }

    @Nested
    @DisplayName("Quantitative limits — instances")
    class Instances {

        @ParameterizedTest(name = "{0} should allow {1} WhatsApp instances")
        @CsvSource({
                "FREE,         1",
                "STARTER,      1",
                "PROFESSIONAL, 5"
        })
        @DisplayName("non-unlimited plans should expose a positive instance limit")
        void shouldExposeInstanceLimits(TenantPlan plan, int expected) {
            assertThat(plan.maxWhatsAppInstances()).isEqualTo(expected);
            assertThat(plan.isInstancesUnlimited()).isFalse();
        }

        @Test
        @DisplayName("ENTERPRISE should have unlimited instances")
        void enterpriseShouldHaveUnlimitedInstances() {
            assertThat(TenantPlan.ENTERPRISE.isInstancesUnlimited()).isTrue();
        }
    }

    @Nested
    @DisplayName("Quantitative limits — panel users")
    class PanelUsers {

        @ParameterizedTest(name = "{0} should allow {1} panel users")
        @CsvSource({
                "FREE,         1",
                "STARTER,      2",
                "PROFESSIONAL, 10"
        })
        @DisplayName("non-unlimited plans should expose a positive panel user limit")
        void shouldExposePanelUserLimits(TenantPlan plan, int expected) {
            assertThat(plan.maxPanelUsers()).isEqualTo(expected);
            assertThat(plan.isPanelUsersUnlimited()).isFalse();
        }

        @Test
        @DisplayName("ENTERPRISE should have unlimited panel users")
        void enterpriseShouldHaveUnlimitedPanelUsers() {
            assertThat(TenantPlan.ENTERPRISE.isPanelUsersUnlimited()).isTrue();
        }
    }

    @Nested
    @DisplayName("Quantitative limits — daily media")
    class DailyMediaLimits {

        @Test
        @DisplayName("FREE should not support any non-text media")
        void freeShouldNotSupportMedia() {
            assertThat(TenantPlan.FREE.supportsAudio()).isFalse();
            assertThat(TenantPlan.FREE.supportsImage()).isFalse();
            assertThat(TenantPlan.FREE.supportsVideo()).isFalse();
        }

        @ParameterizedTest(name = "{0} should allow {1} audio, {2} images, {3} videos per day")
        @CsvSource({
                "STARTER,      100, 5,  1",
                "PROFESSIONAL, -1,  30, 5"
        })
        @DisplayName("intermediate plans should have specific daily media limits")
        void shouldExposeDailyMediaLimits(TenantPlan plan, int audio, int image, int video) {
            assertThat(plan.maxDailyAudioMessages()).isEqualTo(audio);
            assertThat(plan.maxDailyImageMessages()).isEqualTo(image);
            assertThat(plan.maxDailyVideoMessages()).isEqualTo(video);
        }

        @Test
        @DisplayName("ENTERPRISE should have unlimited media of all types")
        void enterpriseShouldHaveUnlimitedMedia() {
            assertThat(TenantPlan.ENTERPRISE.isAudioUnlimited()).isTrue();
            assertThat(TenantPlan.ENTERPRISE.isImageUnlimited()).isTrue();
            assertThat(TenantPlan.ENTERPRISE.isVideoUnlimited()).isTrue();
        }

        @Test
        @DisplayName("PROFESSIONAL should have unlimited audio but capped image/video")
        void professionalShouldHaveUnlimitedAudioOnly() {
            assertThat(TenantPlan.PROFESSIONAL.isAudioUnlimited()).isTrue();
            assertThat(TenantPlan.PROFESSIONAL.isImageUnlimited()).isFalse();
            assertThat(TenantPlan.PROFESSIONAL.isVideoUnlimited()).isFalse();
        }
    }

    @Nested
    @DisplayName("Quantitative limits — RAG documents")
    class RagDocuments {

        @ParameterizedTest(name = "{0} should allow {1} RAG documents")
        @CsvSource({
                "FREE,         0",
                "STARTER,      0",
                "PROFESSIONAL, 500"
        })
        @DisplayName("non-unlimited plans should expose their document limit")
        void shouldExposeRagLimits(TenantPlan plan, int expected) {
            assertThat(plan.maxRagDocuments()).isEqualTo(expected);
        }

        @Test
        @DisplayName("ENTERPRISE should have unlimited RAG documents")
        void enterpriseShouldHaveUnlimitedRag() {
            assertThat(TenantPlan.ENTERPRISE.isRagDocumentsUnlimited()).isTrue();
        }
    }

    @Nested
    @DisplayName("Quantitative limits — history retention")
    class HistoryRetention {

        @ParameterizedTest(name = "{0} should retain history for {1} days")
        @CsvSource({
                "FREE,         7",
                "STARTER,      30",
                "PROFESSIONAL, 180",
                "ENTERPRISE,   365"
        })
        @DisplayName("each plan should have its expected history retention")
        void shouldExposeHistoryRetention(TenantPlan plan, int expectedDays) {
            assertThat(plan.historyRetentionDays()).isEqualTo(expectedDays);
        }
    }

    @Nested
    @DisplayName("AI personalization")
    class Personalization {

        @ParameterizedTest(name = "{0} should expose personalization {1}")
        @CsvSource({
                "FREE,         NONE",
                "STARTER,      CUSTOM_PROMPT",
                "PROFESSIONAL, RAG_DOCUMENTS",
                "ENTERPRISE,   RAG_FULL"
        })
        @DisplayName("each plan should expose its expected personalization tier")
        void shouldExposePersonalization(TenantPlan plan, AiPersonalization expected) {
            assertThat(plan.aiPersonalization()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("AI level")
    class AiLevelMapping {

        @ParameterizedTest(name = "{0} should expose AI level {1}")
        @CsvSource({
                "FREE,         BASIC",
                "STARTER,      STANDARD",
                "PROFESSIONAL, ADVANCED",
                "ENTERPRISE,   PREMIUM"
        })
        @DisplayName("each plan should expose its expected AI level")
        void shouldExposeAiLevel(TenantPlan plan, AiLevel expected) {
            assertThat(plan.aiLevel()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Support level")
    class SupportLevelMapping {

        @ParameterizedTest(name = "{0} should expose support level {1}")
        @CsvSource({
                "FREE,         COMMUNITY",
                "STARTER,      EMAIL",
                "PROFESSIONAL, PRIORITY",
                "ENTERPRISE,   DEDICATED"
        })
        @DisplayName("each plan should expose its expected support level")
        void shouldExposeSupportLevel(TenantPlan plan, SupportLevel expected) {
            assertThat(plan.supportLevel()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Analytics")
    class Analytics {

        @ParameterizedTest
        @EnumSource(value = TenantPlan.class, names = {"FREE", "STARTER"})
        @DisplayName("lower plans should not support analytics")
        void lowerPlansShouldNotSupportAnalytics(TenantPlan plan) {
            assertThat(plan.supportsAnalytics()).isFalse();
        }

        @ParameterizedTest
        @EnumSource(value = TenantPlan.class, names = {"PROFESSIONAL", "ENTERPRISE"})
        @DisplayName("higher plans should support analytics")
        void higherPlansShouldSupportAnalytics(TenantPlan plan) {
            assertThat(plan.supportsAnalytics()).isTrue();
        }
    }
}