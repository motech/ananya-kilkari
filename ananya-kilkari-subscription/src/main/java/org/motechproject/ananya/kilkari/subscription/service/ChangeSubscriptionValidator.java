package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.ChangeSubscriptionType;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChangeSubscriptionValidator {

    SubscriptionService subscriptionService;

    @Autowired
    public ChangeSubscriptionValidator(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void validate(ChangeSubscriptionRequest changeSubscriptionRequest) {
        Subscription subscription = validateAndReturnIfSubscriptionExists(changeSubscriptionRequest.getSubscriptionId());
        validateSubscriptionStatus(subscription);

        if (ChangeSubscriptionType.isChangePack(changeSubscriptionRequest.getChangeType()))
            validateIfSubscriptionAlreadyExistsFor(subscription.getMsisdn(), changeSubscriptionRequest.getPack());
        else
            validateRequestedPackIsSameAsExistingPack(subscription, changeSubscriptionRequest.getPack());
    }

    private void validateIfSubscriptionAlreadyExistsFor(String msisdn, SubscriptionPack pack) {
        List<Subscription> subscriptionList = subscriptionService.findByMsisdnAndPack(msisdn, pack);
        for (Subscription subscription : subscriptionList) {
            if (subscription.isInProgress())
                throw new ValidationException(String.format("Active subscription already exists for %s and %s", msisdn, pack));
        }
    }

    public Subscription validateAndReturnIfSubscriptionExists(String subscriptionId) {
        Subscription subscription = subscriptionService.findBySubscriptionId(subscriptionId);
        if (subscription == null) {
            throw new ValidationException(String.format("Subscription does not exist for subscriptionId %s", subscriptionId));
        }
        return subscription;
    }

    private void validateRequestedPackIsSameAsExistingPack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        SubscriptionPack existingSubscriptionPack = existingSubscription.getPack();
        if (existingSubscriptionPack != null && !existingSubscriptionPack.equals(requestedPack))
            throw new ValidationException(String.format("Subscription %s is not subscribed to requested pack", existingSubscription.getSubscriptionId()));
    }

    private void validateSubscriptionStatus(Subscription subscription) {
        if (!subscription.getStatus().canChangeSubscription())
            throw new ValidationException("Subscription is not active for subscriptionId " + subscription.getSubscriptionId());
    }
}