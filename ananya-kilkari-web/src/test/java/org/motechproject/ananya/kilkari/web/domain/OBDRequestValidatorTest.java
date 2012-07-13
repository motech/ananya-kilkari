package org.motechproject.ananya.kilkari.web.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequest;
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
        obdRequest.setCallDetailRecord(new CallDetailRecord());

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
        obdRequest.setCallDetailRecord(new CallDetailRecord());
        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

        assertEquals(4, errors.size());
        assertTrue(errors.contains("Invalid msisdn 123"));
        assertTrue(errors.contains("Invalid service option invalid"));
        assertTrue(errors.contains("Invalid campaign id "));
        assertTrue(errors.contains("Invalid subscription id sub001"));
    }

    @Test
    public void shouldReturnErrorForInvalidCampaignId() {
        String subscriptionId = "sub001";
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setCampaignId("WEEK33WEEK");
        obdRequest.setMsisdn("9876543211");
        obdRequest.setServiceOption("UNSUBSCRIBE");
        obdRequest.setCallDetailRecord(new CallDetailRecord());

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());
        List<String> errors = obdRequestValidator.validate(obdRequest, subscriptionId);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("Invalid campaign id WEEK33WEEK"));
    }
}
