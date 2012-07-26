package org.motechproject.ananya.kilkari.web.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.request.CampaignChangeRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class CampaignChangeRequestValidatorTest {

    private CampaignChangeRequestValidator campaignChangeRequestValidator;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setup() {
        initMocks(this);
        campaignChangeRequestValidator = new CampaignChangeRequestValidator(subscriptionService);
    }

    @Test
    public void shouldReturnValidIfCampaignChangeRequestDetailsAreCorrect() {
        String subscriptionId = "abcd1234";
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        campaignChangeRequest.setSubscriptionId(subscriptionId);
        campaignChangeRequest.setReason("MISCARRIAGE");

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        assertTrue(campaignChangeRequestValidator.validate(campaignChangeRequest).hasNoErrors());
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionDoesNotExist() {
        String subscriptionId = "abcd1234";
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        campaignChangeRequest.setSubscriptionId(subscriptionId);
        campaignChangeRequest.setReason("MISCARRIAGE");

        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(null);

        Errors errors = campaignChangeRequestValidator.validate(campaignChangeRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid subscriptionId " + subscriptionId));
    }

    @Test
    public void shouldReturnInvalidForInvalidReason(){
        String reason = "InvalidReason";
        String subscriptionId = "subscriptionId";
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        campaignChangeRequest.setReason(reason);
        campaignChangeRequest.setSubscriptionId(subscriptionId);

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        Errors errors = campaignChangeRequestValidator.validate(campaignChangeRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid reason " + reason));

    }
}