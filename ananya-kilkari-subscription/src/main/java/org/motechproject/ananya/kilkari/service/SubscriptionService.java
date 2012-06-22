package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    public SubscriptionService(AllSubscriptions allSubscriptions) {
        this.allSubscriptions = allSubscriptions;
    }

    public void createSubscription(String msisdn, String subscriptionPack) throws ValidationException {
        validateMsisdn(msisdn);
        validatePack(subscriptionPack);
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.getFor(subscriptionPack));
        allSubscriptions.add(subscription);

        // event for report
        // event for OMSM
    }

    public List<Subscription> findByMsisdn(String msisdn) throws ValidationException {
        validateMsisdn(msisdn);
        return allSubscriptions.findByMsisdn(msisdn);
    }

    private void validatePack(String subscriptionPack) throws ValidationException {
        if(!SubscriptionPack.isValid(subscriptionPack))
            throw new ValidationException(String.format("Invalid subscription pack %s", subscriptionPack));
    }

    private void validateMsisdn(String msisdn) throws ValidationException {
        if(!isValidMsisdn(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }
}
