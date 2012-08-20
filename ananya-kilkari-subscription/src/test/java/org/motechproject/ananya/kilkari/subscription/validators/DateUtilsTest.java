package org.motechproject.ananya.kilkari.subscription.validators;


import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldParseDateTime(){
        DateTime dateTime = DateUtils.parseDateTime("13-12-2012 23-56-56");
        DateTime expectedDateTime = new DateTime(2012, 12, 13, 23, 56, 56);
        assertEquals(expectedDateTime, dateTime);
    }

    @Test
    public void shouldReturnNullForEmptyDateTimeString(){
        assertNull(DateUtils.parseDateTime(null));
        assertNull(DateUtils.parseDateTime(""));
    }

    @Test
    public void shouldThrowExceptionForInvalidDateTimeFormat(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Invalid format: \"%s\"","1234-343"));
        assertNull(DateUtils.parseDateTime("1234-343"));
    }

    @Test
    public void shouldParseDate(){
        DateTime dateTime = DateUtils.parseDate("13-12-2012");
        DateTime expectedDate = new DateTime(2012, 12, 13, 0, 0, 0);
        assertEquals(expectedDate, dateTime);
    }

    @Test
         public void shouldReturnNullForEmptyDateString(){
        assertNull(DateUtils.parseDate(null));
        assertNull(DateUtils.parseDate(""));
    }

    @Test
    public void shouldThrowExceptionForInvalidDateFormat(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Invalid format: \"%s\"","1234-343"));
        assertNull(DateUtils.parseDate("1234-343"));
    }

    @Test
    public void shouldFormatDateToString() {
        DateTime dateTime =  new DateTime(2012, 12, 13, 0, 0, 0);
        String formattedDate = DateUtils.formatDate(dateTime);

        assertEquals("13-12-2012", formattedDate);
    }

    @Test
    public void shouldFormatDateTimeToString() {
        DateTime dateTime =  new DateTime(2012, 12, 13, 23, 3, 56);
        String formattedDate = DateUtils.formatDateTime(dateTime);

        assertEquals("13-12-2012 23-03-56", formattedDate);
    }
}
