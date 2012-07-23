package org.motechproject.ananya.kilkari.web.validators;

import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UnsubscriptionRequestValidator {

    private final SubscriptionService subscriptionService;

    @Autowired
    public UnsubscriptionRequestValidator(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public List<String> validate(String subscriptionId) {
        List<String> errors = new ArrayList<>();
        validateSubscription(subscriptionId, errors);

        return errors;
    }

    private void validateSubscription(String subscriptionId, List<String> errors) {
        if (subscriptionService.findBySubscriptionId(subscriptionId) == null)
            errors.add(String.format("Invalid subscriptionId %s", subscriptionId));
    }
}
