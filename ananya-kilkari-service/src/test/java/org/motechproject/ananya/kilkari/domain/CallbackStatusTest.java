package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CallbackStatusTest {
    @Test
    public void shouldMapTheStatusInStringToEnum() {
        assertEquals("SUCCESS", CallbackStatus.SUCCESS.getStatus());
        assertEquals("FAILURE", CallbackStatus.FAILURE.getStatus());
        assertEquals("ERROR", CallbackStatus.ERROR.getStatus());
        assertEquals("BAL-LOW", CallbackStatus.BAL_LOW.getStatus());
        assertEquals("GRACE", CallbackStatus.GRACE.getStatus());
        assertEquals("SUS", CallbackStatus.SUS.getStatus());
    }

    @Test
    public void shouldGetAValidStatusForAGivenString() {
        assertEquals(CallbackStatus.SUCCESS, CallbackStatus.getFor("sUcceSS "));
    }
}
