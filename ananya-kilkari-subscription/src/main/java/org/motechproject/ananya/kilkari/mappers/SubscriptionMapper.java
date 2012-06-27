package org.motechproject.ananya.kilkari.mappers;

import org.motechproject.ananya.kilkari.domain.*;

public class SubscriptionMapper {
    private Subscription subscription;
    private SubscriptionActivationRequest subscriptionActivationRequest;
    private SubscriptionReportRequest subscriptionReportRequest;

    public SubscriptionMapper(SubscriptionRequest subscriptionRequest) {
        this.subscription = new Subscription(subscriptionRequest.getMsisdn(), SubscriptionPack.getFor(subscriptionRequest.getPack()));
        this.subscriptionActivationRequest = createSubscriptionActivationRequest(subscriptionRequest);
        this.subscriptionReportRequest = createSubscriptionReportRequest(subscriptionRequest);
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public SubscriptionActivationRequest getSubscriptionActivationRequest() {
        return subscriptionActivationRequest;
    }

    public SubscriptionReportRequest getSubscriptionReportRequest() {
        return subscriptionReportRequest;
    }

    private SubscriptionActivationRequest createSubscriptionActivationRequest(SubscriptionRequest subscriptionRequest) {
        return new SubscriptionActivationRequest(subscriptionRequest.getMsisdn(), SubscriptionPack.getFor(subscriptionRequest.getPack()), Channel.getFor(subscriptionRequest.getChannel()), subscription.getSubscriptionId());
    }

    private SubscriptionReportRequest createSubscriptionReportRequest(SubscriptionRequest subscriptionRequest) {
        return new SubscriptionReportRequest(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack(), subscriptionRequest.getChannel(), subscription.getSubscriptionId());
    }
}
