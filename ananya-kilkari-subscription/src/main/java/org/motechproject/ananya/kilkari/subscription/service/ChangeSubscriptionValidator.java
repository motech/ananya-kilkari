package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.ChangeSubscriptionType;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;

public class ChangeSubscriptionValidator {

    public static void validate(Subscription subscription, ChangeSubscriptionRequest changeSubscriptionRequest) {
        validateStatus(subscription);
        if(ChangeSubscriptionType.isChangePack(changeSubscriptionRequest.getChangeType()))
            validateExistingSubscriptionBelongsToDifferentPack(subscription, changeSubscriptionRequest.getPack());
        else
            validateRequestedPackIsSameAsExistingPack(subscription, changeSubscriptionRequest.getPack());
    }

    private static void validateRequestedPackIsSameAsExistingPack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        SubscriptionPack existingSubscriptionPack = existingSubscription.getPack();
        if(existingSubscriptionPack != null && !existingSubscriptionPack.equals(requestedPack))
            throw new ValidationException(String.format("Subscription %s is not subscribed to requested pack for change schedule",existingSubscription.getSubscriptionId()));
    }

    private static void validateExistingSubscriptionBelongsToDifferentPack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        if(existingSubscription.getPack().equals(requestedPack))
            throw new ValidationException(String.format("Subscription %s is already subscribed to requested pack ",existingSubscription.getSubscriptionId()));
    }

    private static void validateStatus(Subscription subscription) {
        if(!subscription.getStatus().canChangePack())
            throw new ValidationException("Subscription is not active for subscription " + subscription.getSubscriptionId());
    }
}