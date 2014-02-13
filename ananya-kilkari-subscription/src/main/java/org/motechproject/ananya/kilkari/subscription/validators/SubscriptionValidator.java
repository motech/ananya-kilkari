package org.motechproject.ananya.kilkari.subscription.validators;

import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.domain.CampaignChangeReason;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionValidator {
    private AllSubscriptions allSubscriptions;

    @Autowired
    public SubscriptionValidator(AllSubscriptions allSubscriptions) {
        this.allSubscriptions = allSubscriptions;
    }

    public void validate(SubscriptionRequest subscriptionRequest) {
        Errors errors = new Errors();
        validateWeek(subscriptionRequest, errors);
        validateActiveSubscriptionDoesNotExist(subscriptionRequest, errors);
        validateDob(subscriptionRequest, errors);

        raiseExceptionIfThereAreErrors(errors);
    }

    public void validateChangeCampaign(String subscriptionId, CampaignChangeReason reason) {
        Errors errors = new Errors();
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        if (subscription == null)
            errors.add(String.format("Subscription does not exist for subscriptionId %s", subscriptionId));
        else if (!subscription.isActiveOrSuspended())
            errors.add(String.format("Subscription is not active for subscriptionId %s", subscriptionId));

        if (subscription != null && subscription.getMessageCampaignPack().isMCOrID())
            errors.add(String.format("Subscription with subscriptionId %s is already in %s", subscriptionId, subscription.getMessageCampaignPack().name()));

        raiseExceptionIfThereAreErrors(errors);
    }

    public void validateSubscriberDetails(SubscriberRequest request) {
        Errors errors = new Errors();
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

    private void validateDob(SubscriptionRequest subscriptionRequest, Errors errors) {
        if (subscriptionRequest.getSubscriber().getDateOfBirth() != null) {
            if (!subscriptionRequest.getPack().isValidDateOfBirth(subscriptionRequest.getSubscriber().getDateOfBirth(), subscriptionRequest.getCreationDate()))
                errors.add(String.format("Given dateOfBirth[%s] is not within the pack[%s] range", subscriptionRequest.getSubscriber().getDateOfBirth(), subscriptionRequest.getPack().name()));
        }
    }

    private void validateActiveSubscriptionDoesNotExist(SubscriptionRequest subscription, Errors errors) {
        Subscription existingActiveSubscription = allSubscriptions.findSubscriptionInProgress(subscription.getMsisdn(), subscription.getPack());
        if (existingActiveSubscription != null && !existingActiveSubscription.getStatus().equals(SubscriptionStatus.REFERRED_MSISDN_RECEIVED)) {
            errors.add(String.format("Active subscription already exists for msisdn[%s] and pack[%s]", subscription.getMsisdn(), subscription.getPack()));
        }
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}
