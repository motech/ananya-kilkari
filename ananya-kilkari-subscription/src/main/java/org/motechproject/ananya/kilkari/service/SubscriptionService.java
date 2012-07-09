package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.handlers.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.mappers.SubscriptionMapper;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Subscription createSubscription(SubscriptionRequest subscriptionRequest) {
        SubscriberLocation reportLocation = reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
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

    public void renewSubscription(String subscriptionId, final DateTime renewedDate, int graceCount) {
        updateStatusAndReport(subscriptionId, renewedDate, null, null, graceCount, new Action<Subscription>() {
            @Override
            public void perform(Subscription subscription) {
                subscription.activateOnRenewal();
            }
        });
    }

    public void suspendSubscription(String subscriptionId, final DateTime renewalDate, String reason, int graceCount) {
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

    public List<String> validate(CallbackRequestWrapper callbackRequestWrapper) {
        List<String> errors = new ArrayList<>();
        final String requestStatus = callbackRequestWrapper.getStatus();
        final String requestAction = callbackRequestWrapper.getAction();
        final String subscriptionId = callbackRequestWrapper.getSubscriptionId();

        if (SubscriptionStateHandlerFactory.getHandlerClass(callbackRequestWrapper) == null) {
            errors.add(String.format("Invalid status %s for action %s", requestStatus, requestAction));
        }

        Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
        
        if (CallbackAction.REN.name().equals(requestAction)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!(subscriptionStatus.equals(SubscriptionStatus.ACTIVE) || subscriptionStatus.equals(SubscriptionStatus.SUSPENDED)))
                errors.add(String.format("Cannot renew. Subscription in %s status", subscriptionStatus));
        }

        if (CallbackAction.DCT.name().equals(requestAction) && CallbackStatus.BAL_LOW.name().equals(requestStatus)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.equals(SubscriptionStatus.SUSPENDED))
                errors.add(String.format("Cannot deactivate on renewal. Subscription in %s status", subscriptionStatus));
        }

        if (CallbackAction.ACT.name().equals(requestAction) && (CallbackStatus.SUCCESS.name().equals(requestStatus) || CallbackStatus.BAL_LOW.name().equals(requestStatus))) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.equals(SubscriptionStatus.PENDING_ACTIVATION))
                errors.add(String.format("Cannot activate. Subscription in %s status", subscriptionStatus));
        }

        return errors;
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
        if (!isValidMsisdn(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }

    public Subscription findBySubscriptionId(String subscriptionId) {
        return allSubscriptions.findBySubscriptionId(subscriptionId);
    }
}