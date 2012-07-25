package org.motechproject.ananya.kilkari.subscription.validators;

import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionValidator {
    private AllSubscriptions allSubscriptions;
    private ReportingService reportingService;

    @Autowired
    public SubscriptionValidator(AllSubscriptions allSubscriptions, ReportingService reportingService) {
        this.allSubscriptions = allSubscriptions;
        this.reportingService = reportingService;
    }

    public void validate(SubscriptionRequest subscriptionRequest) {
        if (subscriptionRequest.hasLocation()) {
            validateLocationExists(subscriptionRequest);
        }

        validateActiveSubscriptionDoesNotExist(subscriptionRequest);
    }

    private void validateActiveSubscriptionDoesNotExist(SubscriptionRequest subscription) {
        Subscription existingActiveSubscription = allSubscriptions.findSubscriptionInProgress(subscription.getMsisdn(), subscription.getPack());
        if (existingActiveSubscription != null) {
            throw new DuplicateSubscriptionException(String.format("Active subscription already exists for msisdn[%s] and pack[%s]", subscription.getMsisdn(), subscription.getPack()));
        }
    }

    private void validateLocationExists(SubscriptionRequest subscription) {
        Location location = subscription.getLocation();

        String district = location.getDistrict();
        String block = location.getBlock();
        String panchayat = location.getPanchayat();
        SubscriberLocation existingLocation = reportingService.getLocation(district, block, panchayat);

        if (!ValidationUtils.assertNotNull(existingLocation)) {
            throw new ValidationException(String.format("Location does not exist for District[%s] Block[%s] and Panchayat[%s]", district, block, panchayat));
        }
    }

}
