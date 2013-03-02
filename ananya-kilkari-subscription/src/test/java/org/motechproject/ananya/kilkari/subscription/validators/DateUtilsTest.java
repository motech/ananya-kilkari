package org.motechproject.ananya.kilkari.subscription.validators;


import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class DateUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldParseDateTime() {
        DateTime dateTime = DateUtils.parseDateTime("13-12-2012 23-56-56");
        DateTime expectedDateTime = new DateTime(2012, 12, 13, 23, 56, 56);
        assertEquals(expectedDateTime, dateTime);
    }

    @Test
    public void shouldReturnNullForEmptyDateTimeString() {
        assertNull(DateUtils.parseDateTime(null));
        assertNull(DateUtils.parseDateTime(""));
    }

    @Test
    public void shouldThrowExceptionForInvalidDateTimeFormat() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Invalid format: \"%s\"", "1234-343"));
        assertNull(DateUtils.parseDateTime("1234-343"));
    }

    @Test
    public void shouldParseDate() {
        DateTime dateTime = DateUtils.parseDate("13-12-2012");
        DateTime expectedDate = new DateTime(2012, 12, 13, 0, 0, 0);
        assertEquals(expectedDate, dateTime);
    }

    @Test
    public void shouldReturnNullForEmptyDateString() {
        assertNull(DateUtils.parseDate(null));
        assertNull(DateUtils.parseDate(""));
    }

    @Test
    public void shouldThrowExceptionForInvalidDateFormat() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Invalid format: \"%s\"", "1234-343"));
        assertNull(DateUtils.parseDate("1234-343"));
    }

    @Test
    public void shouldFormatDateToString() {
        DateTime dateTime = new DateTime(2012, 12, 13, 0, 0, 0);

        String formattedDate = DateUtils.formatDate(dateTime, DateUtils.ISTTimeZone);

        assertEquals("13-12-2012", formattedDate);
    }

    @Test
    public void shouldFormatNullDateToNull() {
        String formattedDate = DateUtils.formatDate(null, DateUtils.ISTTimeZone);

        assertNull(formattedDate);
    }

    @Test
    public void shouldFormatDateTimeToString() {
        DateTime dateTime = new DateTime(2012, 12, 13, 23, 3, 56);

        String formattedDate = DateUtils.formatDateTime(dateTime);

        assertEquals("13-12-2012 23-03-56", formattedDate);
    }

    @Test
    public void shouldFormatNullDateTimeToNull() {
        String formattedDate = DateUtils.formatDateTime(null);

        assertNull(formattedDate);
    }

    @Test
    public void shouldFormatDateTimeToStringInCCFormat() {
        DateTime dateTime = new DateTime(2012, 12, 13, 23, 3, 56);

        String formattedDateForCC = DateUtils.formatDateTimeForCC(dateTime, DateUtils.ISTTimeZone);

        assertEquals("13-12-2012 23:03:56", formattedDateForCC);
    }

    @Test
    public void shouldFormatNulDateTimeToNullStringInCCFormat() {
        String formattedDateForCC = DateUtils.formatDateTimeForCC(null, DateUtils.ISTTimeZone);

        assertNull(formattedDateForCC);
    }

    @Test
    public void shouldParseDateFromStringInCCFormat() {
        DateTime dateTime = DateUtils.parseDateTimeForCC("13-12-2012 23:03:56");

        assertEquals(new DateTime(2012, 12, 13, 23, 3, 56), dateTime);
    }

    @Test
    public void shouldParseToNullDateFromEmptyStringInCCFormat() {
        DateTime dateTime = DateUtils.parseDateTimeForCC("");

        assertNull(dateTime);
    }

    @Test
    public void shouldFormatTimeToString() {
        String time = DateUtils.formatTime(new DateTime(2012, 12, 13, 23, 3, 56), DateUtils.ISTTimeZone);

        assertEquals("23:03:56", time);
    }

    @Test
    public void shouldFormatNullDateTimeToNullString() {
        String time = DateUtils.formatTime(null, DateUtils.ISTTimeZone);

        assertNull(time);
    }

    @Test
    public void shouldValidateACorrectDateStringForCC() {
        boolean isValid = DateUtils.isValidForCC("13-12-2012 23:03:56");

        assertTrue(isValid);
    }

    @Test
    public void shouldInValidateAWrongDateStringForCC() {
        boolean isValid = DateUtils.isValidForCC("spiderman");

        assertFalse(isValid);
    }

    @Test
    public void shouldInValidateANullOrEmptyDateStringForCC() {
        boolean isValid = DateUtils.isValidForCC("");
        assertFalse(isValid);

        isValid = DateUtils.isValidForCC(null);
        assertFalse(isValid);
    }
}
