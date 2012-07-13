package org.motechproject.ananya.kilkari.subscription.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubscriptionRequestValidator {
    private AllSubscriptions allSubscriptions;
    private ReportingService reportingService;

    @Autowired
    public SubscriptionRequestValidator(AllSubscriptions allSubscriptions, ReportingService reportingService) {
        this.allSubscriptions = allSubscriptions;
        this.reportingService = reportingService;
    }

    public void validate(SubscriptionRequest subscriptionRequest) {
        List<String> errors =  new ArrayList<>();
        subscriptionRequest.validate(errors);

        if (!Channel.isIVR(subscriptionRequest.getChannel()) && !subscriptionRequest.isLocationEmpty()) {
            validateLocationExists(subscriptionRequest, errors);
        }
        
        if(!errors.isEmpty()){
            throw new ValidationException(StringUtils.join(errors,","));
        }

        validateActiveSubscriptionDoesNotExist(subscriptionRequest);
    }

    private void validateActiveSubscriptionDoesNotExist(SubscriptionRequest subscriptionRequest) {
        Subscription existingActiveSubscription = allSubscriptions.findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack());
        if (existingActiveSubscription != null) {
            throw new DuplicateSubscriptionException(String.format("Active subscription already exists for msisdn[%s] and pack[%s]", subscriptionRequest.getMsisdn(), subscriptionRequest.getPack()));
        }
    }

    private void validateLocationExists(SubscriptionRequest subscriptionRequest, List<String> errors) {
        String district = subscriptionRequest.getDistrict();
        String block = subscriptionRequest.getBlock();
        String panchayat = subscriptionRequest.getPanchayat();
        SubscriberLocation existingLocation = reportingService.getLocation(district, block, panchayat);

        if(!ValidationUtils.assertNotNull(existingLocation)){
            errors.add(String.format("Location does not exist for District[%s] Block[%s] and Panchayat[%s]", district, block, panchayat));
        }
    }
}
