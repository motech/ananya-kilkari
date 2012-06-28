package org.motechproject.ananya.kilkari.mappers;

import org.motechproject.ananya.kilkari.domain.*;

public class SubscriptionMapper {
    private Subscription subscription;
    private SubscriptionActivationRequest subscriptionActivationRequest;
    private SubscriptionCreationReportRequest subscriptionCreationReportRequest;

    public SubscriptionMapper(SubscriptionRequest subscriptionRequest) {
        this.subscription = new Subscription(subscriptionRequest.getMsisdn(), SubscriptionPack.getFor(subscriptionRequest.getPack()));
        this.subscriptionActivationRequest = createSubscriptionActivationRequest(subscriptionRequest);
        this.subscriptionCreationReportRequest = createSubscriptionReportRequest(subscriptionRequest);
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public SubscriptionActivationRequest getSubscriptionActivationRequest() {
        return subscriptionActivationRequest;
    }

    public SubscriptionCreationReportRequest getSubscriptionCreationReportRequest() {
        return subscriptionCreationReportRequest;
    }

    private SubscriptionActivationRequest createSubscriptionActivationRequest(SubscriptionRequest subscriptionRequest) {
        return new SubscriptionActivationRequest(subscriptionRequest.getMsisdn(), SubscriptionPack.getFor(subscriptionRequest.getPack()), Channel.getFor(subscriptionRequest.getChannel()), subscription.getSubscriptionId());
    }

    private SubscriptionCreationReportRequest createSubscriptionReportRequest(SubscriptionRequest subscriptionRequest) {
        return new SubscriptionCreationReportRequest(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack(), subscriptionRequest.getChannel(), subscription.getSubscriptionId(), subscriptionRequest.getCreatedAt());
    }
}
