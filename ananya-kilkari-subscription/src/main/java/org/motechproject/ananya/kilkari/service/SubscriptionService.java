package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.DuplicateSubscriptionException;
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
    private ReportingService reportingService;

    @Autowired
    public SubscriptionService(AllSubscriptions allSubscriptions, Publisher publisher, ReportingService reportingService) {
        this.allSubscriptions = allSubscriptions;
        this.publisher = publisher;
        this.reportingService = reportingService;
    }

    public String createSubscription(SubscriptionRequest subscriptionRequest) {
        subscriptionRequest.validate(reportingService, this);

        Subscription existingSubscription = allSubscriptions.findByMsisdnAndPack(
                subscriptionRequest.getMsisdn(), SubscriptionPack.from(subscriptionRequest.getPack()));

        if(existingSubscription!=null) {
            throw new DuplicateSubscriptionException(String.format("Subscription already exists for msisdn[%s] and pack[%s]",
                    subscriptionRequest.getMsisdn(), subscriptionRequest.getPack()));
        }

        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(subscriptionRequest);
        Subscription subscription = subscriptionMapper.getSubscription();
        allSubscriptions.add(subscription);

        sendProcessSubscriptionEvent(subscriptionMapper.getSubscriptionActivationRequest());
        sendReportSubscriptionCreationEvent(subscriptionMapper.getSubscriptionCreationReportRequest());

        return subscription.getSubscriptionId();
    }

    public List<Subscription> findByMsisdn(String msisdn) {
        validateMsisdn(msisdn);
        return allSubscriptions.findByMsisdn(msisdn);
    }

    public Subscription findByMsisdnAndPack(String msisdn, String pack) {
        return allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.from(pack));
    }

    public void activate(String subscriptionId, DateTime activatedOn, String operator) {
        updateStatusAndReport(subscriptionId, activatedOn, null, operator, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription, String operator) {
                subscription.activate(operator);
            }
        });
    }

    public void activationFailed(String subscriptionId, DateTime updatedOn, String reason, String operator) {
        updateStatusAndReport(subscriptionId, updatedOn, reason, operator, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription, String operator) {
                subscription.activationFailed(operator);
            }
        });
    }

    public void activationRequested(String subscriptionId) {
        updateStatusAndReport(subscriptionId, DateTime.now(), null, null, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription, String operator) {
                subscription.activationRequested();
            }
        });
    }

    private void updateStatusAndReport(String subscriptionId, DateTime updatedOn, String reason, String operator, Action<Subscription> action) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        action.perform(subscription, operator);
        updateWithReporting(subscription, updatedOn, reason, operator);
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

    private void updateWithReporting(Subscription subscription, DateTime updatedOn, String reason, String operator) {
        allSubscriptions.update(subscription);
        publisher.reportSubscriptionStateChange(new SubscriptionStateChangeReportRequest(subscription.getSubscriptionId(), subscription.getStatus(), updatedOn, reason, operator));
    }
}