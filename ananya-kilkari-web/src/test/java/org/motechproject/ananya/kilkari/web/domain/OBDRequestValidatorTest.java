package org.motechproject.ananya.kilkari.web.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.OBDRequest;
import org.motechproject.ananya.kilkari.domain.ServiceOption;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.validators.OBDRequestValidator;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDRequestValidatorTest {

    @Mock
    private SubscriptionService subscriptionService;

    private OBDRequestValidator obdRequestValidator;

    @Before
    public void setUp() {
        initMocks(this);
        obdRequestValidator = new OBDRequestValidator(subscriptionService);
    }

    @Test
    public void shouldValidateWithoutAnyErrors() {
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("1234567890");
        obdRequest.setCampaignId("WEEK1");
        obdRequest.setServiceOption(ServiceOption.HELP.name());
        when(subscriptionService.findBySubscriptionId(Matchers.anyString())).thenReturn(new Subscription());

        List<String> validationErrors = obdRequestValidator.validate(obdRequest, "subscriptionId");

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    public void shouldValidateInvalidMsisdn() {
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("123456789");
        obdRequest.setCampaignId("WEEK1");
        obdRequest.setServiceOption(ServiceOption.HELP.name());
        when(subscriptionService.findBySubscriptionId(Matchers.anyString())).thenReturn(new Subscription());

        List<String> validationErrors = obdRequestValidator.validate(obdRequest, "subscriptionId");

        assertEquals(1, validationErrors.size());
        assertEquals("Invalid msisdn 123456789", validationErrors.get(0));
    }

    @Test
    public void shouldValidateInvalidNonNumericMsisdn() {
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("123456789a");
        obdRequest.setCampaignId("WEEK1");
        obdRequest.setServiceOption(ServiceOption.HELP.name());
        when(subscriptionService.findBySubscriptionId(Matchers.anyString())).thenReturn(new Subscription());

        List<String> validationErrors = obdRequestValidator.validate(obdRequest, "subscriptionId");

        assertEquals(1, validationErrors.size());
        assertEquals("Invalid msisdn 123456789a", validationErrors.get(0));
    }

    @Test
    public void shouldValidateInvalidCampaignName() {
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("1234567890");
        obdRequest.setCampaignId("WEEKS1");
        obdRequest.setServiceOption(ServiceOption.HELP.name());
        when(subscriptionService.findBySubscriptionId(Matchers.anyString())).thenReturn(new Subscription());

        List<String> validationErrors = obdRequestValidator.validate(obdRequest, "subscriptionId");

        assertEquals(1, validationErrors.size());
        assertEquals("Invalid campaign id WEEKS1", validationErrors.get(0));
    }

    @Test
    public void shouldValidateInvalidServiceOption() {
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("1234567890");
        obdRequest.setCampaignId("WEEK1");
        obdRequest.setServiceOption("RANDOM_OPTION");
        when(subscriptionService.findBySubscriptionId(Matchers.anyString())).thenReturn(new Subscription());

        List<String> validationErrors = obdRequestValidator.validate(obdRequest, "subscriptionId");

        assertEquals(1, validationErrors.size());
        assertEquals("Invalid service option RANDOM_OPTION", validationErrors.get(0));
    }

    @Test
    public void shouldValidateInvalidSubscription() {
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("1234567890");
        obdRequest.setCampaignId("WEEK1");
        obdRequest.setServiceOption(ServiceOption.HELP.name());
        when(subscriptionService.findBySubscriptionId(Matchers.anyString())).thenReturn(null);

        List<String> validationErrors = obdRequestValidator.validate(obdRequest, "subscriptionId");

        assertEquals(1, validationErrors.size());
        assertEquals("Invalid subscription id subscriptionId", validationErrors.get(0));
    }
}
