package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberTest {
    @Test
    public void shouldFailValidationForNullOrEmpty() {
        assertFalse(PhoneNumber.isValid(""));
        assertFalse(PhoneNumber.isValid(null));
    }

    @Test
    public void shouldFailValidationForPhoneNumberLessThanOrGreaterThan10Digits() {
        assertFalse(PhoneNumber.isValid("12"));
        assertFalse(PhoneNumber.isValid("123456789012312"));
    }

    @Test
    public void shouldFailValidationIfTheNumberContainsInvalidCharacters() {
        assertFalse(PhoneNumber.isValid("test1123123-"));
    }

    @Test
    public void shouldPassValidationFor10DigitPhoneNumber() {
        assertTrue(PhoneNumber.isValid("1234567890"));
    }
}