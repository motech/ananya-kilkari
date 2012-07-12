package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SubscriberCareDocTest {
    @Test
    public void shouldReturn10DigitPhoneNumberWhenGivenANumber() {
        String expectedNumber = "1234567890";
        assertEquals(expectedNumber, new SubscriberCareDoc("1234567890", null, null, null).getMsisdn());
        assertEquals(expectedNumber, new SubscriberCareDoc("911234567890", null, null, null).getMsisdn());
        assertEquals(expectedNumber, new SubscriberCareDoc("001234567890", null, null, null).getMsisdn());
    }

}
