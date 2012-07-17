package org.motechproject.ananya.kilkari.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SuccessfulCallRequestValidatorTest {

    private OBDSuccessfulCallRequestValidator successfulCallRequestValidator;

    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp(){
        initMocks(this);
        successfulCallRequestValidator = new OBDSuccessfulCallRequestValidator(subscriptionService);
    }

    @Test
    public void shouldValidateValidRequest() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setCampaignId("WEEK33");
        successfulCallRequest.setMsisdn("9876543211");
        successfulCallRequest.setServiceOption("UNSUBSCRIBE");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 23:56:56");
        callDetailRecord.setEndTime("25-12-2012 23:57:56");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = successfulCallRequestValidator.validate(new OBDSuccessfulCallRequestWrapper(successfulCallRequest, subscriptionId, null, null));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldValidateInvalidRequest() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setCampaignId("");
        successfulCallRequest.setMsisdn("123");
        successfulCallRequest.setServiceOption("invalid");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012");
        callDetailRecord.setEndTime("27-12-2012");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);

        List<String> errors = successfulCallRequestValidator.validate(new OBDSuccessfulCallRequestWrapper(successfulCallRequest, subscriptionId, null, null));

        assertEquals(6, errors.size());
        assertTrue(errors.contains("Invalid msisdn 123"));
        assertTrue(errors.contains("Invalid service option invalid"));
        assertTrue(errors.contains("Invalid campaign id "));
        assertTrue(errors.contains("Invalid start time format 25-12-2012"));
        assertTrue(errors.contains("Invalid end time format 27-12-2012"));
        assertTrue(errors.contains("Invalid campaign id "));
        assertTrue(errors.contains("Invalid subscription id sub001"));
    }

    @Test
    public void shouldNotThrowErrorForEmptyServiceOption() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setCampaignId("WEEK13");
        successfulCallRequest.setMsisdn("1234567890");
        successfulCallRequest.setServiceOption("");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 20:20:20");
        callDetailRecord.setEndTime("27-12-2012 20:25:20");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = successfulCallRequestValidator.validate(new OBDSuccessfulCallRequestWrapper(successfulCallRequest, subscriptionId, null, null));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldNotThrowErrorForNoServiceOption() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setCampaignId("WEEK13");
        successfulCallRequest.setMsisdn("1234567890");
        successfulCallRequest.setServiceOption(null);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 20:20:20");
        callDetailRecord.setEndTime("27-12-2012 20:25:20");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = successfulCallRequestValidator.validate(new OBDSuccessfulCallRequestWrapper(successfulCallRequest, subscriptionId, null, null));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldThrowErrorIfStartTimeNotBeforeEndTime() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setCampaignId("WEEK13");
        successfulCallRequest.setMsisdn("1234567890");
        successfulCallRequest.setServiceOption(null);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("27-12-2012 21:20:20");
        callDetailRecord.setEndTime("27-12-2012 20:20:20");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = successfulCallRequestValidator.validate(new OBDSuccessfulCallRequestWrapper(successfulCallRequest, subscriptionId, null, null));

        assertEquals(1,errors.size());
        assertEquals("Start DateTime[27-12-2012 21:20:20] should not be greater than End DateTime[27-12-2012 20:20:20]",errors.get(0));
    }

    @Test
    public void shouldReturnErrorForInvalidCampaignId() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setCampaignId("WEEK33WEEK");
        successfulCallRequest.setMsisdn("9876543211");
        successfulCallRequest.setServiceOption("UNSUBSCRIBE");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 23:56:56");
        callDetailRecord.setEndTime("25-12-2012 23:57:56");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = successfulCallRequestValidator.validate(new OBDSuccessfulCallRequestWrapper(successfulCallRequest, subscriptionId, null, null));

        assertEquals(1, errors.size());
        assertTrue(errors.contains("Invalid campaign id WEEK33WEEK"));
    }
}
