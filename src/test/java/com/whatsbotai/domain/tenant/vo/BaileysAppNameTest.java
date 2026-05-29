package com.whatsbotai.domain.tenant.vo;

import com.whatsbotai.domain.tenant.exception.InvalidBaileysAppNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BaileysAppName — value object for the WhatsApp session identifier slug")
public class BaileysAppNameTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @ParameterizedTest(name = "should accept valid slug: \"{0}\"")
        @ValueSource(strings = {
                "abc",
                "minha-empresa",
                "tenant-01",
                "la-bodega-mineira",
                "app1",
                "a-b-c",
                "tenant-with-many-segments-still-valid"
        })
        @DisplayName("should create a BaileysAppName for valid kebab-case slugs")
        void shouldCreateForValidSlug(String validSlug) {
            BaileysAppName name = BaileysAppName.of(validSlug);

            assertThat(name).isNotNull();
            assertThat(name.value()).isEqualTo(validSlug);
        }

        @Test
        @DisplayName("should trim leading and trailing whitespace before validation")
        void shouldTrimWhitespace() {
            BaileysAppName name = BaileysAppName.of("   minha-empresa   ");

            assertThat(name.value()).isEqualTo("minha-empresa");
        }
    }

    @Nested
    @DisplayName("Invalid input — null and blank")
    class NullAndBlank {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should reject null, empty, or blank input")
        void shouldRejectNullEmptyOrBlank(String invalid) {
            assertThatThrownBy(() -> BaileysAppName.of(invalid))
                    .isInstanceOf(InvalidBaileysAppNameException.class)
                    .hasMessageContaining("null or blank");
        }
    }

    @Nested
    @DisplayName("Invalid input — length")
    class Length {

        @ParameterizedTest(name = "should reject too short: \"{0}\"")
        @ValueSource(strings = {"a", "ab"})
        @DisplayName("should reject input shorter than 3 characters")
        void shouldRejectTooShort(String invalid) {
            assertThatThrownBy(() -> BaileysAppName.of(invalid))
                    .isInstanceOf(InvalidBaileysAppNameException.class)
                    .hasMessageContaining("between 3 and 50");
        }

        @Test
        @DisplayName("should reject input longer than 50 characters")
        void shouldRejectTooLong() {
            String tooLong = "a".repeat(51);

            assertThatThrownBy(() -> BaileysAppName.of(tooLong))
                    .isInstanceOf(InvalidBaileysAppNameException.class)
                    .hasMessageContaining("between 3 and 50");
        }

        @Test
        @DisplayName("should accept exactly 3 characters (lower boundary)")
        void shouldAcceptMinimumLength() {
            BaileysAppName name = BaileysAppName.of("abc");

            assertThat(name.value()).isEqualTo("abc");
        }

        @Test
        @DisplayName("should accept exactly 50 characters (upper boundary)")
        void shouldAcceptMaximumLength() {
            String fifty = "a".repeat(50);

            BaileysAppName name = BaileysAppName.of(fifty);

            assertThat(name.value()).isEqualTo(fifty);
        }
    }

    @Nested
    @DisplayName("Invalid input — format")
    class Format {

        @ParameterizedTest(name = "should reject invalid format: \"{0}\"")
        @ValueSource(strings = {
                "MinhaEmpresa",         // uppercase
                "minha_empresa",        // underscore
                "minha empresa",        // space
                "minha.empresa",        // dot
                "minha@empresa",        // special char
                "empresa!",             // exclamation
                "-minha-empresa",       // starts with hyphen
                "minha-empresa-",       // ends with hyphen
                "minha--empresa",       // consecutive hyphens
                "12345",                // digits only
                "---",                  // hyphens only
                "café-bar",             // accented character
                "ção"                   // accented character
        })
        @DisplayName("should reject input that does not match kebab-case slug")
        void shouldRejectInvalidFormat(String invalid) {
            assertThatThrownBy(() -> BaileysAppName.of(invalid))
                    .isInstanceOf(InvalidBaileysAppNameException.class);
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("same slug should be equal")
        void sameSlugShouldBeEqual() {
            BaileysAppName a = BaileysAppName.of("minha-empresa");
            BaileysAppName b = BaileysAppName.of("minha-empresa");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("different slugs should not be equal")
        void differentSlugsShouldNotBeEqual() {
            BaileysAppName a = BaileysAppName.of("empresa-um");
            BaileysAppName b = BaileysAppName.of("empresa-dois");

            assertThat(a).isNotEqualTo(b);
        }
    }

    @Nested
    @DisplayName("Representation")
    class Representation {

        @Test
        @DisplayName("toString should return the slug value")
        void toStringShouldReturnValue() {
            BaileysAppName name = BaileysAppName.of("minha-empresa");

            assertThat(name.toString()).isEqualTo("minha-empresa");
        }
    }
}
