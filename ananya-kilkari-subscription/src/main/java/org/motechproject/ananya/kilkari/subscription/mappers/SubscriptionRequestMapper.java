package org.motechproject.ananya.kilkari.subscription.mappers;

import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionDetails;
import org.motechproject.ananya.kilkari.subscription.domain.*;

public class SubscriptionRequestMapper {
    private Subscription subscription;
    private ProcessSubscriptionRequest processSubscriptionRequest;
    private SubscriptionCreationReportRequest subscriptionCreationReportRequest;

    public SubscriptionRequestMapper(SubscriptionRequest subscriptionRequest) {
        this.subscription = new Subscription(subscriptionRequest.getMsisdn(), SubscriptionPack.from(subscriptionRequest.getPack()), subscriptionRequest.getCreatedAt());
        this.processSubscriptionRequest = createSubscriptionActivationRequest(subscriptionRequest);
        this.subscriptionCreationReportRequest = createSubscriptionReportRequest(subscriptionRequest);
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public ProcessSubscriptionRequest getProcessSubscriptionRequest() {
        return processSubscriptionRequest;
    }

    public SubscriptionCreationReportRequest getSubscriptionCreationReportRequest() {
        return subscriptionCreationReportRequest;
    }

    private ProcessSubscriptionRequest createSubscriptionActivationRequest(SubscriptionRequest subscriptionRequest) {
        return new ProcessSubscriptionRequest(subscriptionRequest.getMsisdn(), SubscriptionPack.from(subscriptionRequest.getPack()), Channel.from(subscriptionRequest.getChannel()), subscription.getSubscriptionId());
    }

    private SubscriptionCreationReportRequest createSubscriptionReportRequest(SubscriptionRequest subscriptionRequest) {
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails(subscription.getMsisdn(), subscription.getPack().name(), subscription.getCreationDate(), subscription.getStatus().name(), subscription.getSubscriptionId());
        SubscriberLocation subscriberLocation = new SubscriberLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
        return new SubscriptionCreationReportRequest(subscriptionDetails, subscriptionRequest.getChannel(), subscriptionRequest.getBeneficiaryAge(), subscriptionRequest.getBeneficiaryName(), subscriptionRequest.getDateOfBirth(), subscriptionRequest.getExpectedDateOfDelivery(), subscriberLocation);
    }
}
