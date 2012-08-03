package org.motechproject.ananya.kilkari.request;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CallDurationWebRequestTest {

    @Test
    public void shouldValidateInvalidCallDurationWithNullStartEndTimes(){
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest();
        assertEquals("Invalid start time format null,Invalid end time format null", callDurationWebRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateInvalidCallDurationWithEmptyStartEndTimes(){
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest();
        callDurationWebRequest.setEndTime("");
        callDurationWebRequest.setStartTime("");
        assertEquals("Invalid start time format ,Invalid end time format ", callDurationWebRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateInvalidCallDurationWithInvalidStartEndTimes(){
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest();
        callDurationWebRequest.setEndTime("12/12/12 5:6:7");
        callDurationWebRequest.setStartTime("12-12-12 T 5:6:7");
        assertEquals("Invalid start time format 12-12-12 T 5:6:7,Invalid end time format 12/12/12 5:6:7", callDurationWebRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateIfStartTimeNotBeforeEndTime() {
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest();
        callDurationWebRequest.setStartTime("27-12-2012 21-20-20");
        callDurationWebRequest.setEndTime("27-12-2012 20-20-20");
        assertEquals("Start DateTime[27-12-2012 21-20-20] should not be greater than End DateTime[27-12-2012 20-20-20]", callDurationWebRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateValidCallDurationRequest() {
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest();
        callDurationWebRequest.setEndTime("27-12-2012 21-20-20");
        callDurationWebRequest.setStartTime("27-12-2012 20-20-20");
        assertTrue(callDurationWebRequest.validate().hasNoErrors());
    }

}
