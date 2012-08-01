package org.motechproject.ananya.kilkari.subscription.validators;

import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberUpdateRequest;
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
        Errors errors = new Errors();
        if (subscriptionRequest.hasLocation())
            validateLocationExists(subscriptionRequest.getLocation(), errors);
        validateWeek(subscriptionRequest, errors);
        validateActiveSubscriptionDoesNotExist(subscriptionRequest, errors);
        raiseExceptionIfThereAreErrors(errors);
    }

    public void validateActiveSubscription(Subscription subscription) {
        Errors errors = new Errors();
        if (!subscription.isInProgress())
            errors.add(String.format("Subscription is not active for subscriptionId %s", subscription.getSubscriptionId()));
        raiseExceptionIfThereAreErrors(errors);
    }

    public void validateSubscriberDetails(SubscriberUpdateRequest updateRequest) {
        Errors errors = new Errors();
        if (updateRequest.hasLocation()) {
            validateLocationExists(updateRequest.getLocation(), errors);
        }
        validateSubscriptionExists(updateRequest.getSubscriptionId(), errors);
        raiseExceptionIfThereAreErrors(errors);
    }

    private void validateWeek(SubscriptionRequest subscriptionRequest, Errors errors) {
        if (subscriptionRequest.getSubscriber().getWeek() != null) {
            if (!subscriptionRequest.getPack().isValidWeekNumber(subscriptionRequest.getSubscriber().getWeek()))
                errors.add(String.format("Given week[%s] is not within the pack[%s] range", subscriptionRequest.getSubscriber().getWeek(), subscriptionRequest.getPack().name()));
        }
    }

    private void validateSubscriptionExists(String subscriptionId, Errors errors) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        if (subscription == null) {
            errors.add(String.format("Subscription does not exist for subscriptionId %s", subscriptionId));
        }
    }

    private void validateActiveSubscriptionDoesNotExist(SubscriptionRequest subscription, Errors errors) {
        Subscription existingActiveSubscription = allSubscriptions.findSubscriptionInProgress(subscription.getMsisdn(), subscription.getPack());
        if (existingActiveSubscription != null) {
            errors.add(String.format("Active subscription already exists for msisdn[%s] and pack[%s]", subscription.getMsisdn(), subscription.getPack()));
        }
    }

    private void validateLocationExists(Location location, Errors errors) {
        String district = location.getDistrict();
        String block = location.getBlock();
        String panchayat = location.getPanchayat();
        SubscriberLocation existingLocation = reportingService.getLocation(district, block, panchayat);

        if (!ValidationUtils.assertNotNull(existingLocation)) {
            errors.add(String.format("Location does not exist for District[%s] Block[%s] and Panchayat[%s]", district, block, panchayat));
        }
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}
