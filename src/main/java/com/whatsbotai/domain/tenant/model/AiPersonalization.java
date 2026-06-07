package com.whatsbotai.domain.tenant.model;

/**
 * Level of AI personalization available to a {@code Tenant}.
 *
 * <p>Personalization is a separate dimension from {@link AiLevel}:
 * a tenant can have a powerful model ({@code AiLevel.PREMIUM}) configured
 * with no personalization, or a basic model with full RAG. The two
 * dimensions evolve independently.
 *
 * <p>Each value represents a strictly larger capability than the previous:
 * <ul>
 *   <li>{@link #NONE} — system uses a generic, fixed prompt</li>
 *   <li>{@link #CUSTOM_PROMPT} — tenant configures its own system prompt
 *       (tone of voice, persona, business context)</li>
 *   <li>{@link #RAG_DOCUMENTS} — custom prompt + retrieval-augmented
 *       generation over tenant-uploaded documents (FAQs, manuals, etc.)</li>
 *   <li>{@link #RAG_FULL} — RAG over documents <em>and</em> conversation
 *       history, allowing the AI to learn the tenant's writing style</li>
 * </ul>
 *
 * <p>Future levels (e.g. fine-tuned models) will be added as new enum
 * constants, preserving the ordering.
 *
 * @author Kauan
 * @since 1.0
 */
public enum AiPersonalization {

    /** Generic fixed prompt; no tenant-specific customization. */
    NONE,

    /** Tenant may define its own system prompt (tone, persona, business context). */
    CUSTOM_PROMPT,

    /** Tenant prompt plus retrieval-augmented generation over uploaded documents. */
    RAG_DOCUMENTS,

    /** Full RAG over both documents and conversation history. */
    RAG_FULL
}