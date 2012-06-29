package org.motechproject.ananya.kilkari.validation;

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
}
