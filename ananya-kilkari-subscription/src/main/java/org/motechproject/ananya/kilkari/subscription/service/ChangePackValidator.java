package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;

public class ChangePackValidator {

    public static void validate(Subscription subscription, ChangeSubscriptionRequest changeSubscriptionRequest) {
        validateStatus(subscription);
        validateSamePack(subscription, changeSubscriptionRequest.getPack());
    }

    private static void validateSamePack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        if(existingSubscription.getPack().equals(requestedPack))
            throw new ValidationException(String.format("Subscription %s is already subscribed to requested pack ",existingSubscription.getSubscriptionId()));
    }

    private static void validateStatus(Subscription subscription) {
        if(!subscription.getStatus().canChangePack())
            throw new ValidationException("Subscription is not active for subscription " + subscription.getSubscriptionId());
    }
}