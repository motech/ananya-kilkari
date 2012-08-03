package org.motechproject.ananya.kilkari.request;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDSuccessfulCallDetailsRequestTest {

    @Mock
    private CallDurationWebRequest callDurationWebRequest;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateInvalidCallDetailsRequest(){
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();
        assertEquals("Invalid msisdn null,Invalid campaign id null,Null call duration", obdSuccessfulCallDetailsRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateInvalidServiceOption(){
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("WEEK12");
        obdSuccessfulCallDetailsRequest.setMsisdn("1234567890");
        obdSuccessfulCallDetailsRequest.setCallDurationWebRequest(callDurationWebRequest);
        obdSuccessfulCallDetailsRequest.setServiceOption("Invalid");

        when(callDurationWebRequest.validate()).thenReturn(new Errors());

        assertEquals("Invalid service option Invalid", obdSuccessfulCallDetailsRequest.validate().allMessages());
    }

    @Test
    public void shouldValidateValidRequest(){
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("WEEK12");
        obdSuccessfulCallDetailsRequest.setMsisdn("1234567890");
        obdSuccessfulCallDetailsRequest.setCallDurationWebRequest(callDurationWebRequest);
        obdSuccessfulCallDetailsRequest.setServiceOption("Unsubscribe");

        when(callDurationWebRequest.validate()).thenReturn(new Errors());

        assertTrue(obdSuccessfulCallDetailsRequest.validate().hasNoErrors());
    }
}
