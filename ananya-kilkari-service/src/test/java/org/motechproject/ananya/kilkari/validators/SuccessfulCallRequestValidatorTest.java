package org.motechproject.ananya.kilkari.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

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
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("WEEK33");
        obdSuccessfulCallDetailsRequest.setMsisdn("9876543211");
        obdSuccessfulCallDetailsRequest.setServiceOption("UNSUBSCRIBE");
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 23-56-56");
        callDetailRecord.setEndTime("25-12-2012 23-57-56");
        obdSuccessfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        Errors errors = successfulCallRequestValidator.validate(obdSuccessfulCallDetailsRequest);

        assertTrue(errors.hasNoErrors());
    }

    @Test
    public void shouldValidateInvalidRequest() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("");
        obdSuccessfulCallDetailsRequest.setMsisdn("123");
        obdSuccessfulCallDetailsRequest.setServiceOption("invalid");
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012");
        callDetailRecord.setEndTime("27-12-2012");
        obdSuccessfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);

        Errors errors = successfulCallRequestValidator.validate(obdSuccessfulCallDetailsRequest);

        assertEquals(6, errors.getCount());
        assertTrue(errors.hasMessage("Invalid msisdn 123"));
        assertTrue(errors.hasMessage("Invalid service option invalid"));
        assertTrue(errors.hasMessage("Invalid campaign id "));
        assertTrue(errors.hasMessage("Invalid start time format 25-12-2012"));
        assertTrue(errors.hasMessage("Invalid end time format 27-12-2012"));
        assertTrue(errors.hasMessage("Invalid campaign id "));
        assertTrue(errors.hasMessage("Invalid subscription id sub001"));
    }

    @Test
    public void shouldNotThrowErrorForEmptyServiceOption() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("WEEK13");
        obdSuccessfulCallDetailsRequest.setMsisdn("1234567890");
        obdSuccessfulCallDetailsRequest.setServiceOption("");
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 20-20-20");
        callDetailRecord.setEndTime("27-12-2012 20-25-20");
        obdSuccessfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        Errors errors = successfulCallRequestValidator.validate(obdSuccessfulCallDetailsRequest);

        assertTrue(errors.hasNoErrors());
    }

    @Test
    public void shouldNotThrowErrorForNoServiceOption() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("WEEK13");
        obdSuccessfulCallDetailsRequest.setMsisdn("1234567890");
        obdSuccessfulCallDetailsRequest.setServiceOption(null);
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 20-20-20");
        callDetailRecord.setEndTime("27-12-2012 20-25-20");
        obdSuccessfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        Errors errors = successfulCallRequestValidator.validate(obdSuccessfulCallDetailsRequest);

        assertTrue(errors.hasNoErrors());
    }

    @Test
    public void shouldThrowErrorIfStartTimeNotBeforeEndTime() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("WEEK13");
        obdSuccessfulCallDetailsRequest.setMsisdn("1234567890");
        obdSuccessfulCallDetailsRequest.setServiceOption(null);
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("27-12-2012 21-20-20");
        callDetailRecord.setEndTime("27-12-2012 20-20-20");
        obdSuccessfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        Errors errors = successfulCallRequestValidator.validate(obdSuccessfulCallDetailsRequest);

        assertEquals(1,errors.getCount());
        assertTrue(errors.hasMessage("Start DateTime[27-12-2012 21-20-20] should not be greater than End DateTime[27-12-2012 20-20-20]"));
    }

    @Test
    public void shouldReturnErrorForInvalidCampaignId() {
        String subscriptionId = "sub001";
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setCampaignId("WEEK33WEEK");
        obdSuccessfulCallDetailsRequest.setMsisdn("9876543211");
        obdSuccessfulCallDetailsRequest.setServiceOption("UNSUBSCRIBE");
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 23-56-56");
        callDetailRecord.setEndTime("25-12-2012 23-57-56");
        obdSuccessfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        Errors errors = successfulCallRequestValidator.validate(obdSuccessfulCallDetailsRequest);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid campaign id WEEK33WEEK"));
    }
}