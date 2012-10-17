package org.motechproject.ananya.kilkari.subscription.validators;

import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
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

    public void validateActiveSubscriptionExists(String subscriptionId) {
        Errors errors = new Errors();
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        if (subscription == null)
            errors.add(String.format("Subscription does not exist for subscriptionId %s", subscriptionId));
        else if(!subscription.isActiveOrSuspended())
            errors.add(String.format("Subscription is not active for subscriptionId %s", subscriptionId));
        raiseExceptionIfThereAreErrors(errors);
    }

    public void validateSubscriberDetails(SubscriberRequest request) {
        Errors errors = new Errors();
        if (request.hasLocation()) {
            validateLocationExists(request.getLocation(), errors);
        }
        checkIfSubscriptionExists(request.getSubscriptionId(), errors);
        raiseExceptionIfThereAreErrors(errors);
    }

    private void checkIfSubscriptionExists(String subscriptionId, Errors errors) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        if (subscription == null) {
            errors.add(String.format("Subscription does not exist for subscriptionId %s", subscriptionId));
        }
    }

    private void validateWeek(SubscriptionRequest subscriptionRequest, Errors errors) {
        if (subscriptionRequest.getSubscriber().getWeek() != null) {
            if (!subscriptionRequest.getPack().isValidWeekNumber(subscriptionRequest.getSubscriber().getWeek()))
                errors.add(String.format("Given week[%s] is not within the pack[%s] range", subscriptionRequest.getSubscriber().getWeek(), subscriptionRequest.getPack().name()));
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
        LocationResponse existingLocation = reportingService.getLocation(district, block, panchayat);

        if (existingLocation == null) {
            errors.add(String.format("Location does not exist for District[%s] Block[%s] and Panchayat[%s]", district, block, panchayat));
        }
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}
