package org.motechproject.ananya.kilkari.web.validators;

import org.motechproject.ananya.kilkari.request.CampaignChangeRequest;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CampaignChangeRequestValidator {

    private final SubscriptionService subscriptionService;

    @Autowired
    public CampaignChangeRequestValidator(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public Errors validate(CampaignChangeRequest campaignChangeRequest) {
        Errors errors = new Errors();
        validateSubscription(campaignChangeRequest.getSubscriptionId(), errors);
        validateReason(campaignChangeRequest.getReason(), errors);

        return errors;
    }

    private void validateSubscription(String subscriptionId, Errors errors) {
        if (subscriptionService.findBySubscriptionId(subscriptionId) == null)
            errors.add("Invalid subscriptionId %s", subscriptionId);
    }

    private void validateReason(String reason, Errors errors) {
        if(!ValidationUtils.assertCampaignChangeReason(reason))
            errors.add("Invalid reason %s", reason);
    }
}
