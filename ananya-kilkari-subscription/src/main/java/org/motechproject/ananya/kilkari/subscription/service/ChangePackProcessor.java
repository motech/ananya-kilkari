package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangePackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChangePackProcessor {
    private AllSubscriptions allSubscriptions;

    @Autowired
    public ChangePackProcessor(AllSubscriptions allSubscriptions) {
        this.allSubscriptions = allSubscriptions;
    }

    public void process(ChangePackRequest changePackRequest) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(changePackRequest.getSubscriptionId());
        validateStatus(subscription);
        validateSamePack(subscription, changePackRequest.getPack());
        validatePossiblePack(subscription, changePackRequest);
    }

    private void validatePossiblePack(Subscription subscription, ChangePackRequest changePackRequest) {
        if(!subscription.isNewEarly()) {
            if(subscription.getCurrentWeekOfSubscription() > changePackRequest.getPack().getStartWeek() )
                throw new ValidationException(String.format("Subscripiton pack requested is not applicable for subscription ", subscription.getSubscriptionId()));
        }
    }

    private void validateSamePack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        if(existingSubscription.getPack().equals(requestedPack))
            throw new ValidationException(String.format("Subscription %s is already subscribed to requested pack ",existingSubscription.getSubscriptionId()));
    }

    private void validateStatus(Subscription subscription) {
        if(!subscription.getStatus().canChangePack())
            throw new ValidationException("Subscription is not active for subscription " + subscription.getSubscriptionId());
    }
}
