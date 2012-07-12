package org.motechproject.ananya.kilkari.mappers;

import org.motechproject.ananya.kilkari.domain.*;

public class SubscriptionMapper {
    private Subscription subscription;
    private SubscriptionActivationRequest subscriptionActivationRequest;
    private SubscriptionCreationReportRequest subscriptionCreationReportRequest;

    public SubscriptionMapper(SubscriptionRequest subscriptionRequest) {
        this.subscription = new Subscription(subscriptionRequest.getMsisdn(), SubscriptionPack.from(subscriptionRequest.getPack()), subscriptionRequest.getCreatedAt());
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
        return new SubscriptionActivationRequest(subscriptionRequest.getMsisdn(), SubscriptionPack.from(subscriptionRequest.getPack()), Channel.from(subscriptionRequest.getChannel()), subscription.getSubscriptionId());
    }

    private SubscriptionCreationReportRequest createSubscriptionReportRequest(SubscriptionRequest subscriptionRequest) {
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails(subscription.getMsisdn(), subscription.getPack().name(), subscription.getCreationDate(), subscription.getStatus().name(), subscription.getSubscriptionId());
        SubscriberLocation subscriberLocation = new SubscriberLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
        return new SubscriptionCreationReportRequest(subscriptionDetails, subscriptionRequest.getChannel(), subscriptionRequest.getBeneficiaryAge(), subscriptionRequest.getBeneficiaryName(), subscriptionRequest.getDateOfBirth(), subscriptionRequest.getExpectedDateOfDelivery(), subscriberLocation);
    }
}
