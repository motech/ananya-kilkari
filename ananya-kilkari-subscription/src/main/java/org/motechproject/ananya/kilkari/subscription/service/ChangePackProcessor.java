package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangePackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChangePackProcessor {
    private SubscriptionService subscriptionService;

    @Autowired
    public ChangePackProcessor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void process(ChangePackRequest changePackRequest) {
        String subscriptionId = changePackRequest.getSubscriptionId();
        Subscription subscription = subscriptionService.findBySubscriptionId(subscriptionId);

        validateStatus(subscription);
        validateSamePack(subscription, changePackRequest.getPack());
        validateChangeToEarlierPackWhenNotEarlySubscriptionInCurrentPack(subscription, changePackRequest.getPack());
        validateChangeWhenFewerWeeksAreLeftInTheCurrentPack(subscription,changePackRequest.getPack());

    }

    private void validateChangeWhenFewerWeeksAreLeftInTheCurrentPack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        if(existingSubscription.getCurrentWeekOfSubscription()>requestedPack.getStartWeek())
            throw new ValidationException(String.format("Subscription %s has fewer weeks left than the new pack request",existingSubscription.getSubscriptionId()));

    }

    private void validateChangeToEarlierPackWhenNotEarlySubscriptionInCurrentPack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        SubscriptionPack currentPack = existingSubscription.getPack();
        if(requestedPack.startsBefore(currentPack) && !existingSubscription.getStatus().equals(SubscriptionStatus.NEW_EARLY) )
            throw new ValidationException(String.format("Subscription %s is already in %s pack and cannot be moved to an earlier pack ",existingSubscription.getSubscriptionId(),existingSubscription.getPack().name()));

    }

    private void validateSamePack(Subscription existingSubscription, SubscriptionPack requestedPack) {
        if(existingSubscription.getPack().name().equalsIgnoreCase(requestedPack.name()))
            throw new ValidationException(String.format("Subscription %s is already subscribed to requested pack ",existingSubscription.getSubscriptionId()));
    }

    private void validateStatus(Subscription subscription) {
        if(!subscription.getStatus().canChangePack())
            throw new ValidationException("Subscription is not active for subscription " + subscription.getSubscriptionId());
    }

}
