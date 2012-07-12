package org.motechproject.ananya.kilkari.validators;

import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.gateway.ReportingGateway;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionRequestValidator {
    private AllSubscriptions allSubscriptions;
    private ReportingGateway reportingService;

    @Autowired
    public SubscriptionRequestValidator(AllSubscriptions allSubscriptions, ReportingGateway reportingService) {
        this.allSubscriptions = allSubscriptions;
        this.reportingService = reportingService;
    }

    public void validate(SubscriptionRequest subscriptionRequest) {
        subscriptionRequest.validate();

        if (!Channel.isIVR(subscriptionRequest.getChannel()) && !subscriptionRequest.isLocationEmpty()) {
            validateLocationExists(subscriptionRequest);
        }

        validateActiveSubscriptionDoesNotExist(subscriptionRequest);
    }

    private void validateActiveSubscriptionDoesNotExist(SubscriptionRequest subscriptionRequest) {
        Subscription existingActiveSubscription = allSubscriptions.findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack());
        if (existingActiveSubscription != null) {
            throw new DuplicateSubscriptionException(String.format("Active subscription already exists for msisdn[%s] and pack[%s]", subscriptionRequest.getMsisdn(), subscriptionRequest.getPack()));
        }
    }

    private void validateLocationExists(SubscriptionRequest subscriptionRequest) {
        SubscriberLocation existingLocation = reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());

        ValidationUtils.assertNotNull(existingLocation, String.format("Location does not exist for District[%s] Block[%s] and Panchayat[%s]",
                subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat()));
    }
}
