package org.motechproject.ananya.kilkari.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDetailsWebRequestTest {

    @Mock
    private CallDurationWebRequest callDurationWebRequest;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateInValidRequestWithAllFieldsNull() {
        CallDetailsWebRequest callDetailsWebRequest = new CallDetailsWebRequest();
        assertEquals("Invalid msisdn null,Invalid campaign id null,Null call duration", callDetailsWebRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateInValidRequestWithInvalidCallDuration() {
        CallDetailsWebRequest callDetailsWebRequest = new CallDetailsWebRequest();
        callDetailsWebRequest.setCampaignId("WEEK12");
        callDetailsWebRequest.setMsisdn("1234567890");
        callDetailsWebRequest.setCallDurationWebRequest(callDurationWebRequest);
        when(callDurationWebRequest.validate()).thenReturn(new Errors(){{ add("invalid call duration for something");}});

        assertEquals("invalid call duration for something", callDetailsWebRequest.validate().allMessages());
    }


    @Test
    public void shouldValidateInValidRequestWithEmptyFields() {
        CallDetailsWebRequest callDetailsWebRequest = new CallDetailsWebRequest();
        callDetailsWebRequest.setCampaignId("");
        callDetailsWebRequest.setMsisdn("");
        callDetailsWebRequest.setCallDurationWebRequest(callDurationWebRequest);
        when(callDurationWebRequest.validate()).thenReturn(new Errors());

        assertEquals("Invalid msisdn ,Invalid campaign id ", callDetailsWebRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateInValidRequestWithInvalidFields() {
        CallDetailsWebRequest callDetailsWebRequest = new CallDetailsWebRequest();
        callDetailsWebRequest.setCampaignId("invalid");
        callDetailsWebRequest.setMsisdn("invalid");
        callDetailsWebRequest.setCallDurationWebRequest(callDurationWebRequest);
        when(callDurationWebRequest.validate()).thenReturn(new Errors());

        assertEquals("Invalid msisdn invalid,Invalid campaign id invalid", callDetailsWebRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateValidRequest() {
        CallDetailsWebRequest callDetailsWebRequest = new CallDetailsWebRequest();
        callDetailsWebRequest.setCampaignId("WEEK12");
        callDetailsWebRequest.setMsisdn("1234567890");
        callDetailsWebRequest.setCallDurationWebRequest(callDurationWebRequest);
        when(callDurationWebRequest.validate()).thenReturn(new Errors());
        assertTrue(callDetailsWebRequest.validate().hasNoErrors());
    }
}