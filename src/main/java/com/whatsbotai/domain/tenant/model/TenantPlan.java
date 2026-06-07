package com.whatsbotai.domain.tenant.model;

/**
 * Commercial plan of a {@code Tenant}, encoding limits and capabilities
 * across multiple dimensions of the product.
 *
 * <p><strong>Dimensions modeled:</strong>
 * <ul>
 *   <li><strong>Tier ordering</strong> — ordered position from {@link #FREE}
 *       (tier 0) to {@link #ENTERPRISE} (tier 3); powers
 *       {@link #isUpgradeFrom(TenantPlan)} and {@link #isDowngradeFrom(TenantPlan)}.</li>
 *   <li><strong>Quantitative limits</strong> — monthly messages, WhatsApp
 *       instances, panel users, daily media limits per type (audio, image,
 *       video), RAG documents, and history retention in days.</li>
 *   <li><strong>AI level</strong> — abstract quality tier of the model used
 *       to serve this plan (see {@link AiLevel}).</li>
 *   <li><strong>AI personalization</strong> — how much the AI can be tuned
 *       to the tenant (see {@link AiPersonalization}).</li>
 *   <li><strong>Support level</strong> — promised customer support tier
 *       (see {@link SupportLevel}).</li>
 *   <li><strong>Operational features</strong> — analytics availability.</li>
 * </ul>
 *
 * <p><strong>Unlimited limits</strong> are represented by the sentinel
 * value {@link #UNLIMITED} ({@code -1}). Callers should not test the
 * sentinel directly; prefer the {@code isXxxUnlimited()} helpers, which
 * keep the sentinel as an internal detail.
 *
 * <p><strong>Design note — separation of domain and infrastructure:</strong>
 * The values declared here are the <em>current</em> product configuration.
 * The mapping from each plan to concrete operational artifacts (AI provider,
 * pricing tier, billing logic, support ticketing system) lives in the
 * infrastructure or application layers. This separation lets product/pricing
 * decisions evolve without touching the domain.
 *
 * @author Kauan
 * @since 1.0
 */
public enum TenantPlan {

    /** Free tier for product validation; text-only, minimal limits. */
    FREE(
            /* tier */                     0,
            /* maxMonthlyMessages */       500,
            /* maxWhatsAppInstances */     1,
            /* maxPanelUsers */            1,
            /* maxDailyAudioMessages */    0,
            /* maxDailyImageMessages */    0,
            /* maxDailyVideoMessages */    0,
            /* maxRagDocuments */          0,
            /* historyRetentionDays */     7,
            /* supportsAnalytics */        false,
            /* aiLevel */                  AiLevel.BASIC,
            /* aiPersonalization */        AiPersonalization.NONE,
            /* supportLevel */             SupportLevel.COMMUNITY
    ),

    /** Entry-level paid plan with limited media support and custom prompts. */
    STARTER(
            /* tier */                     1,
            /* maxMonthlyMessages */       5_000,
            /* maxWhatsAppInstances */     1,
            /* maxPanelUsers */            2,
            /* maxDailyAudioMessages */    100,
            /* maxDailyImageMessages */    5,
            /* maxDailyVideoMessages */    1,
            /* maxRagDocuments */          0,
            /* historyRetentionDays */     30,
            /* supportsAnalytics */        false,
            /* aiLevel */                  AiLevel.STANDARD,
            /* aiPersonalization */        AiPersonalization.CUSTOM_PROMPT,
            /* supportLevel */             SupportLevel.EMAIL
    ),

    /** Mid-tier plan with unlimited audio, capped image/video, and document RAG. */
    PROFESSIONAL(
            /* tier */                     2,
            /* maxMonthlyMessages */       50_000,
            /* maxWhatsAppInstances */     5,
            /* maxPanelUsers */            10,
            /* maxDailyAudioMessages */    -1,
            /* maxDailyImageMessages */    30,
            /* maxDailyVideoMessages */    5,
            /* maxRagDocuments */          500,
            /* historyRetentionDays */     180,
            /* supportsAnalytics */        true,
            /* aiLevel */                  AiLevel.ADVANCED,
            /* aiPersonalization */        AiPersonalization.RAG_DOCUMENTS,
            /* supportLevel */             SupportLevel.PRIORITY
    ),

    /** Top-tier plan with unlimited everything and full RAG. */
    ENTERPRISE(
            /* tier */                     3,
            /* maxMonthlyMessages */       -1,
            /* maxWhatsAppInstances */     -1,
            /* maxPanelUsers */            -1,
            /* maxDailyAudioMessages */    -1,
            /* maxDailyImageMessages */    -1,
            /* maxDailyVideoMessages */    -1,
            /* maxRagDocuments */          -1,
            /* historyRetentionDays */     365,
            /* supportsAnalytics */        true,
            /* aiLevel */                  AiLevel.PREMIUM,
            /* aiPersonalization */        AiPersonalization.RAG_FULL,
            /* supportLevel */             SupportLevel.DEDICATED
    );

    /**
     * Sentinel value used to indicate an unlimited limit.
     * Callers should not test against this directly; prefer the
     * {@code isXxxUnlimited()} helper methods.
     */
    public static final int UNLIMITED = -1;

    private final int tier;
    private final int maxMonthlyMessages;
    private final int maxWhatsAppInstances;
    private final int maxPanelUsers;
    private final int maxDailyAudioMessages;
    private final int maxDailyImageMessages;
    private final int maxDailyVideoMessages;
    private final int maxRagDocuments;
    private final int historyRetentionDays;
    private final boolean supportsAnalytics;
    private final AiLevel aiLevel;
    private final AiPersonalization aiPersonalization;
    private final SupportLevel supportLevel;

    TenantPlan(int tier,
               int maxMonthlyMessages,
               int maxWhatsAppInstances,
               int maxPanelUsers,
               int maxDailyAudioMessages,
               int maxDailyImageMessages,
               int maxDailyVideoMessages,
               int maxRagDocuments,
               int historyRetentionDays,
               boolean supportsAnalytics,
               AiLevel aiLevel,
               AiPersonalization aiPersonalization,
               SupportLevel supportLevel) {
        this.tier = tier;
        this.maxMonthlyMessages = maxMonthlyMessages;
        this.maxWhatsAppInstances = maxWhatsAppInstances;
        this.maxPanelUsers = maxPanelUsers;
        this.maxDailyAudioMessages = maxDailyAudioMessages;
        this.maxDailyImageMessages = maxDailyImageMessages;
        this.maxDailyVideoMessages = maxDailyVideoMessages;
        this.maxRagDocuments = maxRagDocuments;
        this.historyRetentionDays = historyRetentionDays;
        this.supportsAnalytics = supportsAnalytics;
        this.aiLevel = aiLevel;
        this.aiPersonalization = aiPersonalization;
        this.supportLevel = supportLevel;
    }

    /**
     * Returns the initial plan assigned to a newly created {@code Tenant}.
     *
     * @return {@link #FREE}
     */
    public static TenantPlan initial() {
        return FREE;
    }

    /** @return the relative tier of this plan (0 = lowest, 3 = highest) */
    public int tier() {
        return tier;
    }

    /** @return the maximum number of messages allowed per month, or {@link #UNLIMITED} */
    public int maxMonthlyMessages() {
        return maxMonthlyMessages;
    }

    /** @return {@code true} if this plan has no cap on monthly messages */
    public boolean isMonthlyMessagesUnlimited() {
        return maxMonthlyMessages == UNLIMITED;
    }

    /** @return the maximum number of concurrent WhatsApp instances, or {@link #UNLIMITED} */
    public int maxWhatsAppInstances() {
        return maxWhatsAppInstances;
    }

    /** @return {@code true} if this plan has no cap on WhatsApp instances */
    public boolean isInstancesUnlimited() {
        return maxWhatsAppInstances == UNLIMITED;
    }

    /** @return the maximum number of users allowed on the admin panel, or {@link #UNLIMITED} */
    public int maxPanelUsers() {
        return maxPanelUsers;
    }

    /** @return {@code true} if this plan has no cap on panel users */
    public boolean isPanelUsersUnlimited() {
        return maxPanelUsers == UNLIMITED;
    }

    /** @return the maximum number of audio messages allowed per day, or {@link #UNLIMITED} */
    public int maxDailyAudioMessages() {
        return maxDailyAudioMessages;
    }

    /** @return {@code true} if audio messages are supported at all in this plan */
    public boolean supportsAudio() {
        return maxDailyAudioMessages != 0;
    }

    /** @return {@code true} if this plan has no cap on daily audio messages */
    public boolean isAudioUnlimited() {
        return maxDailyAudioMessages == UNLIMITED;
    }

    /** @return the maximum number of image messages allowed per day, or {@link #UNLIMITED} */
    public int maxDailyImageMessages() {
        return maxDailyImageMessages;
    }

    /** @return {@code true} if image messages are supported at all in this plan */
    public boolean supportsImage() {
        return maxDailyImageMessages != 0;
    }

    /** @return {@code true} if this plan has no cap on daily image messages */
    public boolean isImageUnlimited() {
        return maxDailyImageMessages == UNLIMITED;
    }

    /** @return the maximum number of video messages allowed per day, or {@link #UNLIMITED} */
    public int maxDailyVideoMessages() {
        return maxDailyVideoMessages;
    }

    /** @return {@code true} if video messages are supported at all in this plan */
    public boolean supportsVideo() {
        return maxDailyVideoMessages != 0;
    }

    /** @return {@code true} if this plan has no cap on daily video messages */
    public boolean isVideoUnlimited() {
        return maxDailyVideoMessages == UNLIMITED;
    }

    /** @return the maximum number of RAG documents allowed, or {@link #UNLIMITED} */
    public int maxRagDocuments() {
        return maxRagDocuments;
    }

    /** @return {@code true} if this plan has no cap on RAG documents */
    public boolean isRagDocumentsUnlimited() {
        return maxRagDocuments == UNLIMITED;
    }

    /** @return the number of days the system retains conversation history */
    public int historyRetentionDays() {
        return historyRetentionDays;
    }

    /** @return {@code true} if this plan grants access to analytics dashboards */
    public boolean supportsAnalytics() {
        return supportsAnalytics;
    }

    /** @return the AI level associated with this plan (see {@link AiLevel}) */
    public AiLevel aiLevel() {
        return aiLevel;
    }

    /** @return the AI personalization level associated with this plan */
    public AiPersonalization aiPersonalization() {
        return aiPersonalization;
    }

    /** @return the support level associated with this plan */
    public SupportLevel supportLevel() {
        return supportLevel;
    }

    /**
     * Indicates whether this plan represents an upgrade compared to the given other plan.
     *
     * @param other the plan to compare against (may be {@code null})
     * @return {@code true} if this plan has a strictly higher tier than {@code other}
     */
    public boolean isUpgradeFrom(TenantPlan other) {
        if (other == null) {
            return false;
        }
        return this.tier > other.tier;
    }

    /**
     * Indicates whether this plan represents a downgrade compared to the given other plan.
     *
     * @param other the plan to compare against (may be {@code null})
     * @return {@code true} if this plan has a strictly lower tier than {@code other}
     */
    public boolean isDowngradeFrom(TenantPlan other) {
        if (other == null) {
            return false;
        }
        return this.tier < other.tier;
    }
}