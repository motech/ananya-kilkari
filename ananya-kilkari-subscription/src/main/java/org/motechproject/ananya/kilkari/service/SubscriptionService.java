package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class    SubscriptionService {
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    private Publisher publisher;

    @Autowired
    public SubscriptionService(AllSubscriptions allSubscriptions, Publisher publisher) {
        this.allSubscriptions = allSubscriptions;
        this.publisher = publisher;
    }

    public void createSubscription(SubscriptionRequest subscriptionRequest) throws ValidationException {
        subscriptionRequest.validate();
        Subscription subscription = subscriptionRequest.getSubscription();
        allSubscriptions.add(subscription);

        SubscriptionActivationRequest subscriptionActivationRequest = subscriptionRequest.getSubscriptionActivationRequest();
        sendProcessSubscriptionEvent(subscriptionActivationRequest);
    }

    public List<Subscription> findByMsisdn(String msisdn) throws ValidationException {
        validateMsisdn(msisdn);
        return allSubscriptions.findByMsisdn(msisdn);
    }

    private void sendProcessSubscriptionEvent(SubscriptionActivationRequest subscriptionActivationRequest) {
        publisher.processSubscription(subscriptionActivationRequest);
    }

    private void validateMsisdn(String msisdn) throws ValidationException {
        if (!isValidMsisdn(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }

    public Subscription findByMsisdnAndPack(String msisdn, String pack) {
        return allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.getFor(pack));
    }

    public void update(Subscription subscription) {
        allSubscriptions.update(subscription);
    }

    public void updateSubscriptionStatus(String msisdn, String pack, SubscriptionStatus status) {
        Subscription subscription = allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.getFor(pack));
        subscription.setStatus(status);
        update(subscription);
    }

    public void updateSubscriptionStatus(String subscriptionId, SubscriptionStatus status) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        subscription.setStatus(status);
        update(subscription);
    }
}
