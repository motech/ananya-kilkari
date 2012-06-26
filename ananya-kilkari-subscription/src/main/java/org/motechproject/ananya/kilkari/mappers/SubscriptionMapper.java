package org.motechproject.ananya.kilkari.mappers;

import org.motechproject.ananya.kilkari.domain.*;

public class SubscriptionMapper {
    private Subscription subscription;
    private SubscriptionActivationRequest subscriptionActivationRequest;

    public SubscriptionMapper(SubscriptionRequest subscriptionRequest) {
        this.subscription = new Subscription(subscriptionRequest.getMsisdn(), SubscriptionPack.getFor(subscriptionRequest.getPack()));
        this.subscriptionActivationRequest = createSubscriptionActivationRequest(subscriptionRequest);
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public SubscriptionActivationRequest getSubscriptionActivationRequest() {
        return subscriptionActivationRequest;
    }

    private SubscriptionActivationRequest createSubscriptionActivationRequest(SubscriptionRequest subscriptionRequest) {
        return new SubscriptionActivationRequest(subscriptionRequest.getMsisdn(), SubscriptionPack.getFor(subscriptionRequest.getPack()), Channel.getFor(subscriptionRequest.getChannel()), subscription.getSubscriptionId());
    }
}
