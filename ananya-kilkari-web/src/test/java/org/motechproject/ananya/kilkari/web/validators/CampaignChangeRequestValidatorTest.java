package org.motechproject.ananya.kilkari.web.validators;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.request.CampaignChangeRequest;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


public class CampaignChangeRequestValidatorTest {

    private CampaignChangeRequestValidator campaignChangeRequestValidator;

    @Before
    public void setup() {
        initMocks(this);
        campaignChangeRequestValidator = new CampaignChangeRequestValidator();
    }

    @Test
    public void shouldReturnValidIfCampaignChangeRequestDetailsAreCorrect() {
        String subscriptionId = "abcd1234";
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        campaignChangeRequest.setSubscriptionId(subscriptionId);
        campaignChangeRequest.setReason("MISCARRIAGE");

        assertTrue(campaignChangeRequestValidator.validate(campaignChangeRequest).hasNoErrors());
    }

    @Test
    public void shouldReturnInvalidForInvalidReason(){
        String reason = "InvalidReason";
        String subscriptionId = "subscriptionId";
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        campaignChangeRequest.setReason(reason);
        campaignChangeRequest.setSubscriptionId(subscriptionId);

        Errors errors = campaignChangeRequestValidator.validate(campaignChangeRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid reason " + reason));
    }
}