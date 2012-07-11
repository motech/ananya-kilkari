package org.motechproject.ananya.kilkari.validation;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;

import static org.hamcrest.CoreMatchers.is;

public class ValidationUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void assertNumericShouldThrowValidationExceptionForAlphaNumericStrings() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid string 234a"));

        ValidationUtils.assertNumeric("234a", "Invalid string %s");
    }

    @Test
    public void assertNumericShouldThrowValidationExceptionForNull() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid string null"));

        ValidationUtils.assertNumeric(null, "Invalid string %s");
    }

    @Test
    public void assertNumericShouldThrowValidationExceptionForEmptyString() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid string "));

        ValidationUtils.assertNumeric("", "Invalid string %s");
    }

    @Test
    public void assertNumericShouldNotThrowValidationExceptionForNumericString() {
        ValidationUtils.assertNumeric("1123", "Invalid string %s");
    }

    @Test
    public void assertDateShouldThrowValidationExceptionForInvalidDateString() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid date format 12-12-1 dd-MM-yyyy"));

        ValidationUtils.assertDateFormat("12-12-1", "dd-MM-yyyy", "Invalid date format %s %s");
    }

    @Test
    public void assertDateShouldThrowValidationExceptionForNullDateString() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid date format null dd-MM-yyyy"));

        ValidationUtils.assertDateFormat(null, "dd-MM-yyyy", "Invalid date format %s %s");
    }

    @Test
    public void assertDateShouldThrowValidationExceptionForEmptyDateString() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid date format  dd-MM-yyyy"));

        ValidationUtils.assertDateFormat("", "dd-MM-yyyy", "Invalid date format %s %s");
    }

    @Test
    public void assertDateShouldNotThrowValidationExceptionForValidDateString() {
        ValidationUtils.assertDateFormat("21-01-2012", "dd-MM-yyyy", "Invalid date format %s %s");
    }

    @Test
    public void shouldThrowExceptionWhenInvalidPackIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid subscription pack Invalid-Pack"));

        ValidationUtils.assertPack("Invalid-Pack");
    }

    @Test
    public void shouldThrowExceptionWhenInvalidChannelIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid channel Invalid-Channel"));

        ValidationUtils.assertChannel("Invalid-Channel");
    }

    @Test
    public void shouldThrowExceptionWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid msisdn 12345"));

        ValidationUtils.assertMsisdn("12345");
    }

    @Test
    public void shouldThrowExceptionWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid msisdn 123456789a"));

        ValidationUtils.assertMsisdn("123456789a");
    }

    @Test
    public void shouldThrowExceptionWhenAssertingNullForNotNull() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("invalid");

        ValidationUtils.assertNotNull(null, "invalid");
    }

    @Test
    public void shouldNotThrowExceptionWhenAssertingNotNullForNotNull() {
        try {
            ValidationUtils.assertNotNull(new Object(), "invalid");
        } catch (ValidationException e) {
            Assert.fail("ValidationException not expected");
        }
    }

    @Test
    public void shouldThrowExceptionWhenAssertingNotNullForNull() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("invalid");

        ValidationUtils.assertNull(new Object(), "invalid");
    }

    @Test
    public void shouldNotThrowExceptionWhenAssertingNullForNull() {
        try {
            ValidationUtils.assertNull(null, "invalid");
        } catch (ValidationException e) {
            Assert.fail("ValidationException not expected");
        }
    }
}
