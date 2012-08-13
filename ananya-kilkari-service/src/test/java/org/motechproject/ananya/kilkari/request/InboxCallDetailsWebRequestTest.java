package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class InboxCallDetailsWebRequestTest {
    @Test
    public void shouldValidateInvalidRequest() {
        String startTime = DateTime.now().minusDays(1).toString("dd-MM-yyyy HH-mm-ss");
        String endTime = DateTime.now().toString("dd-MM-yyyy HH-mm-ss");
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest(startTime, endTime);
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest("1234567890", "WEEK", callDurationWebRequest, "invalid_pack", "subscriptionId");

        Errors errors = inboxCallDetailsWebRequest.validate();

        assertTrue(errors.hasErrors());
        assertEquals("Invalid campaign id WEEK,Invalid subscription pack invalid_pack", errors.allMessages());
    }

    @Test
    public void shouldValidateValidRequest() {
        String startTime = DateTime.now().minusDays(1).toString("dd-MM-yyyy HH-mm-ss");
        String endTime = DateTime.now().toString("dd-MM-yyyy HH-mm-ss");
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest(startTime, endTime);
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest("1234567890", "WEEK12", callDurationWebRequest, "choti_kilkari", "subscriptionId");

        Errors errors = inboxCallDetailsWebRequest.validate();

        assertTrue("Should be a valid request", errors.hasNoErrors());
    }
}
