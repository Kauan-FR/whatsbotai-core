package com.whatsbotai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Setup smoke test — validates JUnit 5 + AssertJ are wired correctly")
public class SetupSmokeTest {

    @Test
    @DisplayName("AssertJ fluent assertion works")
    public void assertjFluentAssertionWorks() {
        String greeting = "WhatsBot AI";

        assertThat(greeting)
                .isNotNull()
                .isNotBlank()
                .startsWith("WhatsBot")
                .hasSize(11);
    }

    @Test
    @DisplayName("JUnit 5 lifecycle works")
    public void junit5LifecycleWorks() {
        int result = 2 + 2;

        assertThat(result).isEqualTo(4);
    }
}
