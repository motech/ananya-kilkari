package org.motechproject.ananya.kilkari.service.validator;

import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallDetailsRequestValidator {

    private SubscriptionService subscriptionService;

    @Autowired
    public CallDetailsRequestValidator(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public Errors validate(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        Errors errors = new Errors();
        validateSubscription(obdSuccessfulCallDetailsRequest.getSubscriptionId(), errors);
        return errors;
    }

    private void validateSubscription(String subscriptionId, Errors errors) {
        if (subscriptionService.findBySubscriptionId(subscriptionId) == null)
            errors.add(String.format("Invalid subscription id %s", subscriptionId));
    }
}
