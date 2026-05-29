package com.whatsbotai.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.whatsbotai.domain.tenant.exception.InvalidPhoneNumberException;

@DisplayName("PhoneNumber — value object for Brazilian mobile numbers in E.164")
public class PhoneNumberTest {

    @Nested
    @DisplayName("Creation and normalization")
    class CreationAndNormalization {

        @ParameterizedTest(name = "input \"{0}\" should normalize to \"{1}\"")
        @CsvSource({
                "5581987654321,        5581987654321",
                "81987654321,          5581987654321",
                "'+55 81 98765-4321',  5581987654321",
                "'(81) 98765-4321',    5581987654321",
                "'+5581987654321',     5581987654321"
        })
        @DisplayName("should accept valid inputs and normalize to E.164 digits")
        void shouldNormalizeValidInputs(String input, String expected) {
            PhoneNumber phone = PhoneNumber.of(input);

            assertThat(phone.value()).isEqualTo(expected);
        }

        @Test
        @DisplayName("should expose area code and subscriber number")
        void shouldExposeParts() {
            PhoneNumber phone = PhoneNumber.of("5581987654321");

            assertThat(phone.areaCode()).isEqualTo("81");
            assertThat(phone.subscriberNumber()).isEqualTo("987654321");
        }
    }

    @Nested
    @DisplayName("Invalid input")
    class InvalidInput {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should reject null, empty, or blank input")
        void shouldRejectNullEmptyOrBlank(String invalid) {
            assertThatThrownBy(() -> PhoneNumber.of(invalid))
                    .isInstanceOf(InvalidPhoneNumberException.class)
                    .hasMessageContaining("null or blank");
        }

        @ParameterizedTest(name = "should reject invalid number: \"{0}\"")
        @ValueSource(strings = {
                "123",                  // too short
                "8198765432",           // 10 digits — missing 9th digit
                "1198765432100",        // too long
                "5501987654321",        // invalid area code (01)
                "5581887654321",        // subscriber not starting with 9
                "0081987654321",        // invalid country/area
                "abcdefghijk"           // non-numeric
        })
        @DisplayName("should reject structurally invalid Brazilian mobile numbers")
        void shouldRejectInvalidNumbers(String invalid) {
            assertThatThrownBy(() -> PhoneNumber.of(invalid))
                    .isInstanceOf(InvalidPhoneNumberException.class);
        }
    }

    @Nested
    @DisplayName("Formatting")
    class Formatting {

        @Test
        @DisplayName("should format with international mask")
        void shouldFormat() {
            PhoneNumber phone = PhoneNumber.of("5581987654321");

            assertThat(phone.formatted()).isEqualTo("+55 (81) 98765-4321");
        }
    }

    @Nested
    @DisplayName("WhatsApp integration")
    class WhatsAppIntegration {

        @Test
        @DisplayName("should produce a WhatsApp JID")
        void shouldProduceWhatsAppJid() {
            PhoneNumber phone = PhoneNumber.of("5581987654321");

            assertThat(phone.toWhatsAppJid()).isEqualTo("5581987654321@s.whatsapp.net");
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("same number from different input formats should be equal")
        void sameNumberDifferentFormatsShouldBeEqual() {
            PhoneNumber a = PhoneNumber.of("+55 81 98765-4321");
            PhoneNumber b = PhoneNumber.of("81987654321");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("different numbers should not be equal")
        void differentNumbersShouldNotBeEqual() {
            PhoneNumber a = PhoneNumber.of("5581987654321");
            PhoneNumber b = PhoneNumber.of("5581912345678");

            assertThat(a).isNotEqualTo(b);
        }
    }
}
