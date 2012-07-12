package org.motechproject.ananya.kilkari.validators;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void assertNumericShouldReturnFalseForAlphaNumericStrings() {

        assertFalse(ValidationUtils.assertNumeric("234a"));
    }

    @Test
    public void assertNumericShouldReturnFalseForNull() {

        assertFalse(ValidationUtils.assertNumeric(null));
    }

    @Test
    public void assertNumericShouldReturnFalseForEmptyString() {
        assertFalse(ValidationUtils.assertNumeric(""));
    }

    @Test
    public void assertNumericShouldNotReturnFalseForNumericString() {
        assertTrue(ValidationUtils.assertNumeric("1123"));
    }

    @Test
    public void assertDateShouldReturnFalseForInvalidDateString() {

        assertFalse(ValidationUtils.assertDateFormat("12-12-1", "dd-MM-yyyy"));
    }

    @Test
    public void assertDateShouldReturnFalseForNullDateString() {

        assertFalse(ValidationUtils.assertDateFormat(null, "dd-MM-yyyy"));
    }

    @Test
    public void assertDateShouldReturnFalseForEmptyDateString() {

        assertFalse(ValidationUtils.assertDateFormat("", "dd-MM-yyyy"));
    }

    @Test
    public void assertDateShouldNotReturnFalseForValidDateString() {

        assertTrue(ValidationUtils.assertDateFormat("21-01-2012", "dd-MM-yyyy"));
    }

    @Test
    public void shouldReturnFalseWhenInvalidPackIsGivenToCreateNewSubscription() {

        assertFalse(ValidationUtils.assertPack("Invalid-Pack"));
    }

    @Test
    public void shouldReturnFalseWhenInvalidChannelIsGivenToCreateNewSubscription() {

        assertFalse(ValidationUtils.assertChannel("Invalid-Channel"));
    }

    @Test
    public void shouldReturnFalseWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription() {

        assertFalse(ValidationUtils.assertMsisdn("12345"));
    }

    @Test
    public void shouldReturnFalseWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() {

        assertFalse(ValidationUtils.assertMsisdn("123456789a"));
    }

    @Test
    public void shouldReturnFalseWhenAssertingNullForNotNull() {

        assertFalse(ValidationUtils.assertNotNull(null));
    }

    @Test
    public void shouldNotReturnFalseWhenAssertingNotNullForNotNull() {
        assertTrue(ValidationUtils.assertNotNull(new Object()));
    }

    @Test
    public void shouldReturnFalseWhenAssertingNotNullForNull() {

        assertFalse(ValidationUtils.assertNull(new Object()));
    }

    @Test
    public void shouldNotReturnFalseWhenAssertingNullForNull() {
        assertTrue(ValidationUtils.assertNull(null));
    }

    @Test
    public void shouldReturnFalseWhenDateNotBeforeNow() {

        assertFalse(ValidationUtils.assertDateBefore(DateTime.now().plusWeeks(3), DateTime.now()));
    }

    @Test
    public void shouldNotReturnFalseWhenDateBeforeNow() {
        assertTrue(ValidationUtils.assertDateBefore(DateTime.now().minusDays(4), DateTime.now()));
    }
}
