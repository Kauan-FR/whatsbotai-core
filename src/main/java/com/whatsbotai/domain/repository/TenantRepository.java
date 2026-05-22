package com.whatsbotai.domain.repository;

import com.whatsbotai.domain.entity.Tenant;
import com.whatsbotai.domain.enums.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository interface for {@link Tenant} entity persistence operations.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD operations (save,
 * findById, findAll, delete, count) plus pagination and sorting capabilities.
 * Custom queries below cover authentication, billing webhooks, and admin views.
 *
 * <p>All methods are transactional by default and managed by Spring Data JPA.
 * Method names follow Spring Data query derivation conventions: prefix +
 * property name auto-generates the underlying JPQL.
 *
 * @author Kauan Santos Ferreira
 * @version 1.0
 * @since 2026
 * @see Tenant
 * @see TenantStatus
 */

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Finds a tenant by the owner's email address.
     *
     * <p>Used during authentication (login flow) to locate the tenant associated
     * with the provided credentials.
     *
     * @param ownerEmail the email address to search for
     * @return an {@link Optional} containing the tenant if found, empty otherwise
     */
    Optional<Tenant> findByOwnerEmail(String ownerEmail);

    /**
     * Finds a tenant by the AbacatePay subscription identifier.
     *
     * <p>Used in billing webhook handlers to correlate incoming events
     * (subscription.completed, subscription.cancelled, etc.) to the corresponding
     * tenant record.
     *
     * @param subscriptionId the AbacatePay subscription ID
     * @return an {@link Optional} containing the tenant if found, empty otherwise
     */
    Optional<Tenant> findBySubscriptionId(String subscriptionId);

    /**
     * Checks whether a tenant exists with the given owner email.
     *
     * <p>Used during signup to validate that the email is not already registered,
     * avoiding unnecessary entity loading just to check existence.
     *
     * @param ownerEmail the email address to check
     * @return {@code true} if a tenant with this email exists, {@code false} otherwise
     */
    boolean existsByOwnerEmail(String ownerEmail);

    /**
     * Retrieves all tenants currently in a given status.
     *
     * <p>Used by background jobs (e.g., expire trials, suspend overdue accounts)
     * and admin dashboards.
     *
     * @param status the lifecycle status to filter by
     * @return list of tenants matching the status (may be empty)
     */
    List<Tenant> findAllByStatus(TenantStatus status);
}
