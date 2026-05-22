package com.whatsbotai.domain.enums;

/**
 * Represents the subscription plan tier of a Tenant.
 *
 * <p>Each plan defines feature access, message limits, and pricing. Plans are
 * checked at runtime to enforce quotas (e.g., max messages per month, available
 * features like RAG knowledge base or WhatsApp Business API).
 *
 * <p>Pricing (BRL, monthly):
 * <ul>
 *   <li>{@link #STARTER}: R$ 79 — 500 messages/month, basic features</li>
 *   <li>{@link #PRO}: R$ 149 — 2000 messages/month, RAG knowledge base</li>
 *   <li>{@link #BUSINESS}: R$ 299 — unlimited, WhatsApp Business API official</li>
 * </ul>
 *
 * @author Kauan Santos Ferreira
 * @version 1.0
 * @since 2026
 * @see com.whatsbotai.domain.entity.Tenant
 */

public enum TenantPlan {

    /** Entry-level plan: 500 messages/month, single WhatsApp number, basic dashboard. */
    STARTED,

    /** Mid-tier plan: 2000 messages/month, RAG knowledge base, advanced metrics. */
    PRO,

    /** Top-tier plan: unlimited messages, WhatsApp Business API official, priority support. */
    BUSINESS
}
