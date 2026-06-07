package com.whatsbotai.domain.tenant.model;

/**
 * Abstract tier of customer support promised to a {@code Tenant}.
 *
 * <p>Like {@link AiLevel}, this enum describes a <em>promise</em> of service
 * rather than a concrete operational channel. The mapping from a
 * {@code SupportLevel} to actual support behavior (response SLA, routing
 * destination, escalation rules) lives in the infrastructure or
 * application layer, so it can evolve independently of the domain.
 *
 * <p><strong>Tier semantics:</strong> levels are totally ordered by their
 * {@link #tier()} value. Use {@link #isAtLeast(SupportLevel)} to check
 * capability requirements.
 *
 * @author Kauan
 * @since 1.0
 */
public enum SupportLevel {

    /** Lowest tier. Self-service via public documentation and community forums. */
    COMMUNITY(0),

    /** Email-based support with a best-effort response time. */
    EMAIL(1),

    /** Faster response times and priority routing. */
    PRIORITY(2),

    /** Dedicated account manager and contractual SLAs. */
    DEDICATED(3);

    private final int tier;

    SupportLevel(int tier) {
        this.tier = tier;
    }

    /**
     * Returns the relative tier of this level (0 = lowest, 3 = highest).
     *
     * @return the tier value
     */
    public int tier() {
        return tier;
    }

    /**
     * Returns the lowest available {@code SupportLevel}.
     *
     * @return {@link #COMMUNITY}
     */
    public static SupportLevel lowest() {
        return COMMUNITY;
    }

    /**
     * Indicates whether this level is at least as capable as the given other level.
     *
     * @param other the level to compare against (may be {@code null})
     * @return {@code true} if this level's tier is &ge; {@code other}'s tier
     */
    public boolean isAtLeast(SupportLevel other) {
        if (other == null) {
            return false;
        }
        return this.tier >= other.tier;
    }
}