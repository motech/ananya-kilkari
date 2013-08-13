package org.motechproject.ananya.kilkari.request;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HelpWebRequestTest {

    @Test
    public void shouldInvalidateTheRequest() throws Exception {
        String startDate = "start";
        String endDate = "end";
        String channel = "invalid_channel";
        HelpWebRequest helpWebRequest = new HelpWebRequest(startDate, endDate, channel);

        Errors errors = helpWebRequest.validate();

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage(String.format("Invalid start time %s", startDate)));
        assertTrue(errors.hasMessage(String.format("Invalid end time %s", endDate)));
        assertTrue(errors.hasMessage(String.format("Invalid channel %s", channel)));
    }

    @Test
    public void shouldInvalidateTheRequestIfEndDateIsBeforeStartDate() throws Exception {
        String startDate = "12-12-2012 00:00:00";
        String endDate = "12-12-2011 00:00:00";
        String channel = "invalid_channel";
        HelpWebRequest helpWebRequest = new HelpWebRequest(startDate, endDate, channel);

        Errors errors = helpWebRequest.validate();

        assertEquals(2, errors.getCount());
        assertTrue(errors.hasMessage(String.format("Invalid channel %s", channel)));
        assertTrue(errors.hasMessage(String.format("Start time %s is after end time %s", startDate, endDate)));
    }

    @Test
    public void shouldNotInvalidateIfAllFieldsAreCorrect() {
        HelpWebRequest helpWebRequest = new HelpWebRequest("12-12-2012 00:00:00", "15-12-2012 00:00:00", Channel.CONTACT_CENTER.name());

        Errors errors = helpWebRequest.validate();
        assertFalse(errors.hasErrors()) ;
    }
}
