package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.mappers.SubscriptionMapper;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionRequestValidator;
import org.motechproject.common.domain.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private AllSubscriptions allSubscriptions;

    private OnMobileSubscriptionManagerPublisher onMobileSubscriptionManagerPublisher;

    private SubscriptionRequestValidator subscriptionRequestValidator;
    private ReportingService reportingService;

    @Autowired
    public SubscriptionService(AllSubscriptions allSubscriptions, OnMobileSubscriptionManagerPublisher onMobileSubscriptionManagerPublisher, SubscriptionRequestValidator subscriptionRequestValidator, ReportingService reportingService) {
        this.allSubscriptions = allSubscriptions;
        this.onMobileSubscriptionManagerPublisher = onMobileSubscriptionManagerPublisher;
        this.subscriptionRequestValidator = subscriptionRequestValidator;
        this.reportingService = reportingService;
    }

    public Subscription createSubscription(SubscriptionRequest subscriptionRequest) {

        validate(subscriptionRequest);

        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(subscriptionRequest);
        Subscription subscription = subscriptionMapper.getSubscription();
        allSubscriptions.add(subscription);

        sendProcessSubscriptionEvent(subscriptionMapper.getSubscriptionActivationRequest());
        sendReportSubscriptionCreationEvent(subscriptionMapper.getSubscriptionCreationReportRequest());

        return subscription;
    }

    private void validate(SubscriptionRequest subscriptionRequest) {
        if (Channel.isIVR(subscriptionRequest.getChannel())) {
            subscriptionRequestValidator.validate(subscriptionRequest);
        }
    }

    public List<Subscription> findByMsisdn(String msisdn) {
        validateMsisdn(msisdn);
        return allSubscriptions.findByMsisdn(msisdn);
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

    public Subscription findBySubscriptionId(String subscriptionId) {
        return allSubscriptions.findBySubscriptionId(subscriptionId);
    }

    private void updateStatusAndReport(String subscriptionId, DateTime updatedOn, String reason, String operator, Integer graceCount, Action<Subscription> action) {
        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        action.perform(subscription);
        allSubscriptions.update(subscription);
        reportingService.reportSubscriptionStateChange(new SubscriptionStateChangeReportRequest(subscription.getSubscriptionId(), subscription.getStatus().name(), updatedOn, reason, operator, graceCount));
    }

    private void sendProcessSubscriptionEvent(SubscriptionActivationRequest subscriptionActivationRequest) {
        onMobileSubscriptionManagerPublisher.processSubscription(subscriptionActivationRequest);
    }

    private void sendReportSubscriptionCreationEvent(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        reportingService.reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    private void validateMsisdn(String msisdn) {
        if (PhoneNumber.isNotValid(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }
}