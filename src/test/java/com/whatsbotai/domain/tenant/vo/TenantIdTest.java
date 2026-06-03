package com.whatsbotai.domain.tenant.vo;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.whatsbotai.domain.tenant.exception.InvalidTenantIdException;

@DisplayName("TenantId — value object wrapping a UUID for type-safe tenant identity")
class TenantIdTest {

    @Nested
    @DisplayName("Generation")
    class Generation {

        @Test
        @DisplayName("generate() should produce a non-null TenantId")
        void generateShouldProduceNonNullTenantId() {
            TenantId id = TenantId.generate();

            assertThat(id).isNotNull();
            assertThat(id.value()).isNotNull();
        }

        @Test
        @DisplayName("generate() should produce a unique TenantId on each call")
        void generateShouldProduceUniqueIds() {
            TenantId a = TenantId.generate();
            TenantId b = TenantId.generate();

            assertThat(a).isNotEqualTo(b);
        }
    }

    @Nested
    @DisplayName("Creation from UUID")
    class CreationFromUuid {

        @Test
        @DisplayName("of(UUID) should wrap the given UUID")
        void shouldWrapGivenUuid() {
            UUID uuid = UUID.randomUUID();

            TenantId id = TenantId.of(uuid);

            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("of(UUID) should reject null")
        void shouldRejectNullUuid() {
            assertThatThrownBy(() -> TenantId.of((UUID) null))
                    .isInstanceOf(InvalidTenantIdException.class)
                    .hasMessageContaining("must not be null");
        }
    }

    @Nested
    @DisplayName("Creation from String")
    class CreationFromString {

        @Test
        @DisplayName("of(String) should parse a valid UUID string")
        void shouldParseValidUuidString() {
            UUID uuid = UUID.randomUUID();

            TenantId id = TenantId.of(uuid.toString());

            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("of(String) should accept uppercase UUID strings")
        void shouldAcceptUppercaseUuidString() {
            String upper = "550E8400-E29B-41D4-A716-446655440000";

            TenantId id = TenantId.of(upper);

            assertThat(id.value().toString()).isEqualToIgnoringCase(upper);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("of(String) should reject null, empty, or blank input")
        void shouldRejectNullEmptyOrBlankString(String invalid) {
            assertThatThrownBy(() -> TenantId.of(invalid))
                    .isInstanceOf(InvalidTenantIdException.class)
                    .hasMessageContaining("null or blank");
        }

        @ParameterizedTest(name = "should reject malformed UUID: \"{0}\"")
        @ValueSource(strings = {
                "not-a-uuid",
                "1234",
                "550e8400-e29b-41d4-a716",
                "zzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz",
                "550e8400e29b41d4a716446655440000"
        })
        @DisplayName("of(String) should reject malformed UUID strings")
        void shouldRejectMalformedUuidString(String invalid) {
            assertThatThrownBy(() -> TenantId.of(invalid))
                    .isInstanceOf(InvalidTenantIdException.class)
                    .hasMessageContaining("not a valid UUID");
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("two TenantIds wrapping the same UUID should be equal")
        void sameUuidShouldBeEqual() {
            UUID uuid = UUID.randomUUID();
            TenantId a = TenantId.of(uuid);
            TenantId b = TenantId.of(uuid);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("two TenantIds with different UUIDs should not be equal")
        void differentUuidsShouldNotBeEqual() {
            TenantId a = TenantId.generate();
            TenantId b = TenantId.generate();

            assertThat(a).isNotEqualTo(b);
        }
    }

    @Nested
    @DisplayName("Representation")
    class Representation {

        @Test
        @DisplayName("toString should return the UUID in canonical form")
        void toStringShouldReturnCanonicalUuid() {
            UUID uuid = UUID.randomUUID();
            TenantId id = TenantId.of(uuid);

            assertThat(id.toString()).isEqualTo(uuid.toString());
        }
    }
}
