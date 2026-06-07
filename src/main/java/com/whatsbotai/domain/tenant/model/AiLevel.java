package com.whatsbotai.domain.tenant.model;

/**
 * Abstract quality tier of the AI model used to serve a {@code Tenant}.
 *
 * <p><strong>Design note — separation of domain and infrastructure:</strong>
 * This enum intentionally describes a <em>promise of quality</em>
 * (BASIC, STANDARD, ADVANCED, PREMIUM) rather than naming a concrete AI
 * provider or model (e.g. Gemini, Claude, GPT). The reason is twofold:
 *
 * <ol>
 *   <li><strong>Independence from vendor decisions.</strong> The mapping from
 *       {@code AiLevel} to a concrete model lives in the infrastructure layer
 *       (configuration files, environment variables, or an {@code AiService}
 *       implementation). Changing the underlying provider — from Gemini to
 *       Claude, from one model version to another, or adding a fallback chain —
 *       requires no changes to the domain.</li>
 *   <li><strong>Commercial flexibility.</strong> Early-stage products typically
 *       map every level to the cheapest viable model to validate the market;
 *       higher levels are upgraded as revenue allows. With this enum, that
 *       evolution is a configuration change rather than a code change.</li>
 * </ol>
 *
 * <p><strong>Tier semantics:</strong> levels are totally ordered by their
 * {@link #tier()} value. Use {@link #isAtLeast(AiLevel)} to check capability
 * requirements (e.g. a feature that needs at least {@link #STANDARD}).
 *
 * @author Kauan
 * @since 1.0
 */
public enum AiLevel {

    /** Lowest tier. Fast, cheap, good enough for short conversational replies. */
    BASIC(0),

    /** Mid-low tier. Balanced quality and cost for typical customer support. */
    STANDARD(1),

    /** Mid-high tier. Stronger reasoning and longer-context handling. */
    ADVANCED(2),

    /** Highest tier. Top-of-the-line model for complex tasks and enterprise. */
    PREMIUM(3);

    private final int tier;

    AiLevel(int tier) {
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
     * Returns the lowest available {@code AiLevel}.
     *
     * @return {@link #BASIC}
     */
    public static AiLevel lowest() {
        return BASIC;
    }

    /**
     * Returns the highest available {@code AiLevel}.
     *
     * @return {@link #PREMIUM}
     */
    public static AiLevel highest() {
        return PREMIUM;
    }

    /**
     * Indicates whether this level is at least as capable as the given other level
     * (i.e. has a tier value greater than or equal to the other's tier).
     *
     * @param other the level to compare against (may be {@code null})
     * @return {@code true} if this level's tier is &ge; {@code other}'s tier
     */
    public boolean isAtLeast(AiLevel other) {
        if (other == null) {
            return false;
        }
        return this.tier >= other.tier;
    }
}