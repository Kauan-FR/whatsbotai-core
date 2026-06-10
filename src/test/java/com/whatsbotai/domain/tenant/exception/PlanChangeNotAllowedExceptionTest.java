package com.whatsbotai.domain.tenant.exception;

import com.whatsbotai.domain.shared.exception.DomainException;
import com.whatsbotai.domain.tenant.model.TenantPlan;
import com.whatsbotai.domain.tenant.model.TenantStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlanChangeNotAllowedException — thrown for invalid plan transitions")
public class PlanChangeNotAllowedExceptionTest {

    @Test
    @DisplayName("should extend DomainException")
    void shouldExtendDomainException() {
        PlanChangeNotAllowedException exception =
                PlanChangeNotAllowedException.becauseTenantIsNotActive(TenantStatus.PENDING);

        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should carry the current status when tenant is not active")
    void shouldCarryCurrentStatusWhenNotActive() {
        PlanChangeNotAllowedException exception =
                PlanChangeNotAllowedException.becauseTenantIsNotActive(TenantStatus.SUSPENDED);

        assertThat(exception.getMessage())
                .containsIgnoringCase("active")
                .contains("SUSPENDED");
    }

    @Test
    @DisplayName("should carry both plans when target is not a real upgrade")
    void shouldCarryBothPlansWhenNotAnUpgrade() {
        PlanChangeNotAllowedException exception =
                PlanChangeNotAllowedException.becauseTargetIsNotUpgrade(TenantPlan.PROFESSIONAL, TenantPlan.STARTER);

        assertThat(exception.getMessage())
                .containsIgnoringCase("upgrade")
                .contains("PROFESSIONAL")
                .contains("STARTER");
    }

    @Test
    @DisplayName("should carry both plans when target is not a real downgrade")
    void shouldCarryBothPlansWhenNotADowngrade() {
        PlanChangeNotAllowedException exception =
                PlanChangeNotAllowedException.becauseTargetIsNotDowngrade(TenantPlan.STARTER, TenantPlan.PROFESSIONAL);

        assertThat(exception.getMessage())
                .containsIgnoringCase("downgrade")
                .contains("STARTER")
                .contains("PROFESSIONAL");
    }
}
