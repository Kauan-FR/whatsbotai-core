package com.whatsbotai.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.whatsbotai.domain.tenant.exception.InvalidEmailException;

@DisplayName("Email - value object representing a valid email address")
public class EmailTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @ParameterizedTest(name = "should accept valid email: \"{0}\"")
        @ValueSource(strings = {
                "user@example.com",
                "kauan.dev@whatsbotai.com.br",
                "first.last+tag@gmail.com",
                "a@b.co",
                "user_name@sub.domain.org"
        })
        @DisplayName("should create an Email for valid input")
        void shouldCreateEmailForValidInput(String validInput) {
            Email email = Email.of(validInput);

            assertThat(email).isNotNull();
            assertThat(email.value()).isEqualTo(validInput.toLowerCase());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("should reject null, empty, or blank input")
        void shouldRejectNullEmptyOrBlank(String invalidInput) {
            assertThatThrownBy(() -> Email.of(invalidInput))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("null or blank");
        }

        @ParameterizedTest(name = "should reject malformed email: \"{0}\"")
        @ValueSource(strings = {
                "not-an-email",
                "missing-at-sign.com",
                "@no-local-part.com",
                "no-domain@",
                "spaces in@email.com",
                "double@@at.com",
                "trailing-dot@domain.com.",
                "user@domain"
        })
        @DisplayName("should reject structurally invalid emails")
        void shouldRejectMalformedEmails(String invalidInput) {
            assertThatThrownBy(() -> Email.of(invalidInput))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("Invalid email format");
        }

        @Test
        @DisplayName("should reject email exceeding 254 characters (RFC 5321)")
        void shouldRejectEmailExceedingMaxLength() {
            String longLocalPart = "a".repeat(250);
            String tooLongEmail = longLocalPart + "@x.co";

            assertThatThrownBy(() -> Email.of(tooLongEmail))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessageContaining("maximum length");
        }
    }

    @Nested
    @DisplayName("Normalization")
    class Normalization {

        @Test
        @DisplayName("should normalize the entire email to lowercase")
        void shouldNormalizeToLowercase() {
            Email email = Email.of("Kauan.DEV@WhatsBotAI.COM.BR");

            assertThat(email.value()).isEqualTo("kauan.dev@whatsbotai.com.br");
        }

        @Test
        @DisplayName("should trim leading and trailing whitespace")
        void shouldTrimWhitespace() {
            Email email = Email.of("  user@example.com  ");

            assertThat(email.value()).isEqualTo("user@example.com");
        }
    }

    @Nested
    @DisplayName("Equality and identity")
    class Equality{

        @Test
        @DisplayName("two emails with the same value should be equal")
        void twoEmailsWithSameValueShouldBeEqual() {
            Email a = Email.of("user@example.com");
            Email b = Email.of("user@example.com");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("two emails that differ only by case should be equal (normalization)")
        void emailsDifferingOnlyByCaseShouldBeEqual() {
            Email a = Email.of("USER@example.com");
            Email b = Email.of("user@EXAMPLE.com");

            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("emails with different values should not be equal")
        void differentEmailsShouldNotBeEqual() {
            Email a = Email.of("a@example.com");
            Email b = Email.of("b@example.com");

            assertThat(a).isNotEqualTo(b);
        }
    }

    @Nested
    @DisplayName("Representation")
    class Representation {

        @Test
        @DisplayName("toString should return the email value")
        void toStringShouldReturnValue() {
            Email email = Email.of("user@example.com");

            assertThat(email.toString()).isEqualTo("user@example.com");
        }
    }

}
