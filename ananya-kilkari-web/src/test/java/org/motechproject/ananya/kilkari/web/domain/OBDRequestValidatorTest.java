package org.motechproject.ananya.kilkari.web.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.request.OBDRequest;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.validators.OBDRequestValidator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDRequestValidatorTest {

    private OBDRequestValidator obdRequestValidator;

    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp(){
        initMocks(this);
        obdRequestValidator = new OBDRequestValidator(subscriptionService);
    }

    @Test
    public void shouldValidateValidRequest() {
        String subscriptionId = "sub001";
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setCampaignId("WEEK33");
        obdRequest.setMsisdn("9876543211");
        obdRequest.setServiceOption("UNSUBSCRIBE");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 23:56:56");
        callDetailRecord.setEndTime("25-12-2012 23:57:56");
        obdRequest.setCallDetailRecord(callDetailRecord);

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());
        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldValidateInvalidRequest() {
        String subscriptionId = "sub001";
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setCampaignId("");
        obdRequest.setMsisdn("123");
        obdRequest.setServiceOption("invalid");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012");
        callDetailRecord.setEndTime("27-12-2012");
        obdRequest.setCallDetailRecord(callDetailRecord);
        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

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
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setCampaignId("WEEK13");
        obdRequest.setMsisdn("1234567890");
        obdRequest.setServiceOption("");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 20:20:20");
        callDetailRecord.setEndTime("27-12-2012 20:25:20");
        obdRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldNotThrowErrorForNoServiceOption() {
        String subscriptionId = "sub001";
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setCampaignId("WEEK13");
        obdRequest.setMsisdn("1234567890");
        obdRequest.setServiceOption(null);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 20:20:20");
        callDetailRecord.setEndTime("27-12-2012 20:25:20");
        obdRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldThrowErrorIfStartTimeNotBeforeEndTime() {
        String subscriptionId = "sub001";
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setCampaignId("WEEK13");
        obdRequest.setMsisdn("1234567890");
        obdRequest.setServiceOption(null);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("27-12-2012 21:20:20");
        callDetailRecord.setEndTime("27-12-2012 20:20:20");
        obdRequest.setCallDetailRecord(callDetailRecord);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

        assertEquals(1,errors.size());
        assertEquals("Start DateTime[27-12-2012 21:20:20] should not be greater than End DateTime[27-12-2012 20:20:20]",errors.get(0));
    }

    @Test
    public void shouldReturnErrorForInvalidCampaignId() {
        String subscriptionId = "sub001";
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setCampaignId("WEEK33WEEK");
        obdRequest.setMsisdn("9876543211");
        obdRequest.setServiceOption("UNSUBSCRIBE");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("25-12-2012 23:56:56");
        callDetailRecord.setEndTime("25-12-2012 23:57:56");
        obdRequest.setCallDetailRecord(callDetailRecord);

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());
        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("Invalid campaign id WEEK33WEEK"));
    }
}
