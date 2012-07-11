package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.gateway.ReportingGateway;
import org.motechproject.ananya.kilkari.mappers.SubscriptionMapper;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.motechproject.common.domain.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private AllSubscriptions allSubscriptions;

    private Publisher publisher;
    private ReportingGateway reportingGateway;

    @Autowired
    public SubscriptionService(AllSubscriptions allSubscriptions, Publisher publisher, ReportingGateway reportingGateway) {
        this.allSubscriptions = allSubscriptions;
        this.publisher = publisher;
        this.reportingGateway = reportingGateway;
    }

    public Subscription createSubscription(SubscriptionRequest subscriptionRequest) {
        SubscriberLocation reportLocation = reportingGateway.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
        Subscription existingSubscription = findActiveSubscription(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack());

        subscriptionRequest.validate(reportLocation, existingSubscription);

        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(subscriptionRequest);
        Subscription subscription = subscriptionMapper.getSubscription();
        allSubscriptions.add(subscription);

        sendProcessSubscriptionEvent(subscriptionMapper.getSubscriptionActivationRequest());
        sendReportSubscriptionCreationEvent(subscriptionMapper.getSubscriptionCreationReportRequest());

        return subscription;
    }

    public List<Subscription> findByMsisdn(String msisdn) {
        validateMsisdn(msisdn);
        return allSubscriptions.findByMsisdn(msisdn);
    }

    public Subscription findActiveSubscription(String msisdn, String pack) {
        List<Subscription> allSubscriptionsByMsisdnAndPack = allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.from(pack));
        for (Subscription subscription : allSubscriptionsByMsisdnAndPack) {
            if (subscription.isActive())
                return subscription;
        }
        return null;
    }

    public void activate(String subscriptionId, DateTime activatedOn, final String operator) {
        updateStatusAndReport(subscriptionId, activatedOn, null, operator, null, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription) {
                subscription.activate(operator);
            }
        });
    }

    public void activationFailed(String subscriptionId, DateTime updatedOn, String reason, final String operator) {
        updateStatusAndReport(subscriptionId, updatedOn, reason, operator, null, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription) {
                subscription.activationFailed(operator);
            }
        });
    }

    public void activationRequested(String subscriptionId) {
        updateStatusAndReport(subscriptionId, DateTime.now(), null, null, null, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription) {
                subscription.activationRequested();
            }
        });
    }

    public void renewSubscription(String subscriptionId, final DateTime renewedDate, Integer graceCount) {
        updateStatusAndReport(subscriptionId, renewedDate, null, null, graceCount, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription) {
                subscription.activateOnRenewal();
            }
        });
    }

    public void suspendSubscription(String subscriptionId, final DateTime renewalDate, String reason, Integer graceCount) {
        updateStatusAndReport(subscriptionId, renewalDate, reason, null, graceCount, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription) {
                subscription.suspendOnRenewal();
            }
        });
    }

    public void deactivateSubscription(String subscriptionId, final DateTime deactivationDate, String reason, Integer graceCount) {
        updateStatusAndReport(subscriptionId, deactivationDate, reason, null, graceCount, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription) {
                subscription.deactivate();
            }
        });
    }

    private void updateStatusAndReport(String subscriptionId, DateTime updatedOn, String reason, String operator, Integer graceCount, Action<Subscription> action) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        action.perform(subscription);
        allSubscriptions.update(subscription);
        publisher.reportSubscriptionStateChange(new SubscriptionStateChangeReportRequest(subscription.getSubscriptionId(), subscription.getStatus(), updatedOn, reason, operator, graceCount));
    }

    private void sendProcessSubscriptionEvent(SubscriptionActivationRequest subscriptionActivationRequest) {
        publisher.processSubscription(subscriptionActivationRequest);
    }

    private void sendReportSubscriptionCreationEvent(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        publisher.reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    private void validateMsisdn(String msisdn) {
        if (PhoneNumber.isNotValid(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    public Subscription findBySubscriptionId(String subscriptionId) {
        return allSubscriptions.findBySubscriptionId(subscriptionId);
    }
}