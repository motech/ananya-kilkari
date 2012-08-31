package org.motechproject.ananya.kilkari.subscription.validators;

import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnsubscriptionValidator {

    private AllSubscriptions allSubscriptions;

    @Autowired
    public UnsubscriptionValidator(AllSubscriptions allSubscriptions) {
        this.allSubscriptions = allSubscriptions;
    }

    public void validate(String subscriptionId) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);

        if (subscription == null)
            throw new ValidationException(String.format("Invalid subscriptionId %s", subscriptionId));

        if (!subscription.isInUpdatableState())
            throw new ValidationException(String.format("Cannot unsubscribe. Subscription in %s status", subscription.getStatus()));
    }

}
