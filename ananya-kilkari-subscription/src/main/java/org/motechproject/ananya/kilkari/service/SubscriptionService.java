package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.mappers.SubscriptionMapper;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private AllSubscriptions allSubscriptions;

    private Publisher publisher;

    @Autowired
    public SubscriptionService(AllSubscriptions allSubscriptions, Publisher publisher) {
        this.allSubscriptions = allSubscriptions;
        this.publisher = publisher;
    }

    public void createSubscription(SubscriptionRequest subscriptionRequest) {
        subscriptionRequest.validate();
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(subscriptionRequest);
        Subscription subscription = subscriptionMapper.getSubscription();
        allSubscriptions.add(subscription);

        sendProcessSubscriptionEvent(subscriptionMapper.getSubscriptionActivationRequest());
        sendReportSubscriptionCreationEvent(subscriptionMapper.getSubscriptionCreationReportRequest());
    }

    public List<Subscription> findByMsisdn(String msisdn) {
        validateMsisdn(msisdn);
        return allSubscriptions.findByMsisdn(msisdn);
    }

    public Subscription findByMsisdnAndPack(String msisdn, String pack) {
        return allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.getFor(pack));
    }

    public void updateSubscriptionStatus(String msisdn, String pack, SubscriptionStatus status, DateTime updatedOn) {
        Subscription subscription = allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.getFor(pack));
        subscription.setStatus(status);
        updateWithReporting(subscription, updatedOn);
    }

    public void updateSubscriptionStatus(String subscriptionId, SubscriptionStatus status, DateTime updatedOn) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        subscription.setStatus(status);
        updateWithReporting(subscription, updatedOn);
    }

    public void activate(String subscriptionId, DateTime activatedOn) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        updateWithReporting(subscription, activatedOn);
    }

    private void sendProcessSubscriptionEvent(SubscriptionActivationRequest subscriptionActivationRequest) {
        publisher.processSubscription(subscriptionActivationRequest);
    }

    private void sendReportSubscriptionCreationEvent(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        publisher.reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    private void validateMsisdn(String msisdn) {
        if (!isValidMsisdn(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }

    private void sendSubscriptionStateChangeEvent(String subscriptionId, SubscriptionStatus status, DateTime updatedOn) {
        publisher.reportSubscriptionStateChange(new SubscriptionStateChangeReportRequest(subscriptionId, status.name(), updatedOn));
    }

    private void updateWithReporting(Subscription subscription, DateTime updatedOn) {
        allSubscriptions.update(subscription);
        sendSubscriptionStateChangeEvent(subscription.getSubscriptionId(), subscription.getStatus(), updatedOn);
    }
}
