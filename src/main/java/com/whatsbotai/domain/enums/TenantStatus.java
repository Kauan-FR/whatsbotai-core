package com.whatsbotai.domain.enums;

/**
 * Represents the lifecycle status of a Tenant in the WhatsBotAI platform.
 *
 * <p>Tenant status drives access control: only {@link #TRIAL} and {@link #ACTIVE}
 * tenants can use the bot. Suspended tenants are blocked due to payment issues
 * but their data is preserved. Cancelled and expired tenants are scheduled for
 * data deletion after a retention period.
 *
 * <p>Transition rules:
 * <ul>
 *   <li>TRIAL → ACTIVE (after successful first payment)</li>
 *   <li>TRIAL → EXPIRED (after trial period ends without payment)</li>
 *   <li>ACTIVE → SUSPENDED (payment failure)</li>
 *   <li>SUSPENDED → ACTIVE (payment recovered)</li>
 *   <li>ANY → CANCELLED (manual cancellation by tenant or admin)</li>
 * </ul>
 *
 * @author Kauan Santos Ferreira
 * @version 1.0
 * @since 2026
 * @see com.whatsbotai.domain.entity.Tenant
 */

public enum TenantStatus {

    /** Tenant is in free trial period (default for new signups). */
    TRIAL,

    /** Tenant has active paid subscription. Full access to the bot. */
    ACTIVE,

    /** Tenant subscription failed payment; access blocked but data preserved. */
    SUSPENDED,

    /** Tenant manually cancelled; data scheduled for deletion after retention period. */
    CANCELLED,

    /** Trial period ended without subscription; data scheduled for deletion. */
    EXPIRED
}
