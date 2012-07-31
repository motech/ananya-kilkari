package org.motechproject.ananya.kilkari.subscription.validators;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.common.domain.PhoneNumber;

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
        assertFalse(ValidationUtils.assertDateFormat("12-12-12"));
    }

    @Test
    public void assertDateShouldReturnFalseForInvalidDateStringWithYearDigitsGreaterThan4() {
        assertFalse(ValidationUtils.assertDateFormat("12-12-11232"));
    }

    @Test
    public void assertDateShouldReturnFalseForNullDateString() {
        assertFalse(ValidationUtils.assertDateFormat(null));
    }

    @Test
    public void assertDateShouldReturnFalseForEmptyDateString() {
        assertFalse(ValidationUtils.assertDateFormat(""));
    }

    @Test
    public void assertDateShouldNotReturnFalseForValidDateString() {
        assertTrue(ValidationUtils.assertDateFormat("21-01-2012"));
    }

    @Test
    public void assertDateShouldReturnFalseForInvalidDateTimeString() {
        assertFalse(ValidationUtils.assertDateTimeFormat("12-12-12 23.56.56"));
    }

    @Test
    public void assertDateShouldReturnFalseForInvalidDateTimeStringWithYearDigitsGreaterThan4() {
        assertFalse(ValidationUtils.assertDateTimeFormat("12-12-11232 23:56:56"));
    }

    @Test
    public void assertDateShouldReturnFalseForNullDateTimeString() {
        assertFalse(ValidationUtils.assertDateTimeFormat(null));
    }

    @Test
    public void assertDateShouldReturnFalseForEmptyDateTimeString() {
        assertFalse(ValidationUtils.assertDateTimeFormat(""));
    }

    @Test
    public void assertDateShouldNotReturnFalseForValidDateTimeString() {
        assertTrue(ValidationUtils.assertDateTimeFormat("21-01-2012 23-56-56"));
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
        assertFalse(PhoneNumber.isValid("12345"));
    }

    @Test
    public void shouldReturnFalseWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() {
        assertFalse(PhoneNumber.isValid("123456789a"));
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

    @Test
    public void shouldReturnFalseWhenInvalidReasonIsGivenToChangeCampaign() {
        assertFalse(ValidationUtils.assertCampaignChangeReason("Invalid-Reason"));
    }

    @Test
    public void shouldReturnTrueWhenValidReasonIsGivenToChangeCampaign() {
        assertTrue(ValidationUtils.assertCampaignChangeReason("INFANT_DEATH"));
    }

    @Test
    public void shouldReturnFalseIfDOBDateFormatIsInvalid() {
        assertFalse(ValidationUtils.assertDOB("25/11/1986", DateTime.now()));
    }

    @Test
    public void shouldReturnFalseIfDOBDateIsAfterCreatedAt() {
        assertFalse(ValidationUtils.assertDOB("25-11-1985", new DateTime(1984, 11, 25, 0, 0, 0)));
    }

    @Test
    public void shouldReturnTrueIfDOBDateIsValid() {
        assertTrue(ValidationUtils.assertDOB("25-11-1986", DateTime.now()));
    }

    @Test
    public void shouldReturnFalseIfEDDDateFormatIsInvalid() {
        assertFalse(ValidationUtils.assertEDD("25/11/1986", DateTime.now()));
    }

    @Test
    public void shouldReturnFalseIfEDDDateIsBeforeCreatedAt() {
        assertFalse(ValidationUtils.assertEDD("25-11-1984", new DateTime(1985, 11, 25, 0, 0, 0)));
    }

    @Test
    public void shouldReturnTrueIfEDDDateIsValid() {
        assertTrue(ValidationUtils.assertEDD("25-11-1985", new DateTime(1984, 11, 25, 0, 0, 0)));
    }

    @Test
    public void shouldReturnTrueIfBeneficiaryAgeIsValid() {
        assertTrue(ValidationUtils.assertAge("25"));
    }

    @Test
    public void shouldReturnFalseIfBeneficiaryAgeIsNotNumeric() {
        assertFalse(ValidationUtils.assertAge("25a"));
    }
}
