package com.whatsbotai.domain.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DomainException - base of all domain exception")
public class DomainExceptionTest {

    @Test
    @DisplayName("should be a RuntimeException (unchecked)")
    public void shouldBeRuntimeException() {
        DomainException exception = new TestDomainException("test message");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("should be abstract — cannot be instantiated directly")
    public void shouldBeAbstract() {
        Class<DomainException> clazz = DomainException.class;

        assertThat(java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()))
                .as("DomainException must be abstract to force domain-specific subclasses")
                .isTrue();
    }

    @Test
    @DisplayName("should carry the message passed in constructor")
    public void shouldCarryMessage() {
        String expectedMessage = "tenant cannot be suspended twice";

        DomainException exception = new TestDomainException(expectedMessage);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("should carry the cause when provided")
    public void shouldCarryCause() {
        Throwable cause = new IllegalStateException("underlying cause");

        DomainException exception = new TestDomainException("test message", cause);

        assertThat(exception.getCause()).isEqualTo(cause);
    }

    /**
     * Concrete subclass used only for testing the abstract base.
     * Real domain exceptions will live in each bounded context's exception package.
     */
    private static final class TestDomainException extends DomainException {

        public TestDomainException(String message) {
            super(message);
        }

        public TestDomainException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
