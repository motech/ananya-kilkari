package org.motechproject.ananya.kilkari.request;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;

public class HelpWebRequestTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionOnInvalidStartDate() throws Exception {
        String startDate = "start";
        HelpWebRequest helpWebRequest = new HelpWebRequest(startDate, "12-12-2012 00:00:00", Channel.CONTACT_CENTER.name());

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Invalid start date : %s", startDate));

        helpWebRequest.validate();
    }

    @Test
    public void shouldThrowExceptionOnInvalidEndDate() throws Exception {
        String endDate = "end";
        HelpWebRequest helpWebRequest = new HelpWebRequest("12-12-2012 00:00:00", endDate, Channel.CONTACT_CENTER.name());

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Invalid end date : %s", endDate));

        helpWebRequest.validate();
    }

    @Test
    public void shouldThrowExceptionOnInvalidChannel() throws Exception {
        String channel = "Bad Channel";
        HelpWebRequest helpWebRequest = new HelpWebRequest("12-12-2012 00:00:00", "15-12-2012 00:00:00", channel);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Invalid channel : %s", channel));

        helpWebRequest.validate();
    }

    @Test
    public void shouldThrowExceptionIfStartDateIsAfterEndDate() throws Exception {
        String startDate = "16-12-2012 00:00:00";
        String endDate = "12-12-2012 00:00:00";
        HelpWebRequest helpWebRequest = new HelpWebRequest(startDate, endDate, Channel.CONTACT_CENTER.name());

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Start Date : %s is after End Date : %s", startDate, endDate));

        helpWebRequest.validate();
    }

    @Test
    public void shouldNotThrowExceptionIfEverythingIsValid() {
        HelpWebRequest helpWebRequest = new HelpWebRequest("12-12-2012 00:00:00", "15-12-2012 00:00:00", Channel.CONTACT_CENTER.name());

        helpWebRequest.validate();
    }
}
