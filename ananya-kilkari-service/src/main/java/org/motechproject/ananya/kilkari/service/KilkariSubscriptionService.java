package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KilkariSubscriptionService {

    private SubscriptionPublisher subscriptionPublisher;
    private SubscriptionService subscriptionService;
    private KilkariMessageCampaignService kilkariMessageCampaignService;
    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;

    private final Logger LOGGER = LoggerFactory.getLogger(KilkariSubscriptionService.class);

    private int campaignScheduleDeltaDays;

    private int campaignScheduleDeltaMinutes;

    @Value("#{kilkariProperties['kilkari.campaign.schedule.delta.days']}")
    public void setCampaignScheduleDeltaDays(int campaignScheduleDeltaDays) {
        this.campaignScheduleDeltaDays = campaignScheduleDeltaDays;
    }

    @Value("#{kilkariProperties['kilkari.campaign.schedule.delta.minutes']}")
    public void setCampaignScheduleDeltaMinutes(int campaignScheduleDeltaMinutes) {
        this.campaignScheduleDeltaMinutes = campaignScheduleDeltaMinutes;
    }

    @Autowired
    public KilkariSubscriptionService(SubscriptionPublisher subscriptionPublisher,
                                      SubscriptionService subscriptionService,
                                      KilkariMessageCampaignService kilkariMessageCampaignService,
                                      SubscriptionStateHandlerFactory subscriptionStateHandlerFactory) {
        this.subscriptionPublisher = subscriptionPublisher;
        this.subscriptionService = subscriptionService;
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.subscriptionStateHandlerFactory = subscriptionStateHandlerFactory;
    }

    public void createSubscription(SubscriptionRequest subscriptionRequest) {
        subscriptionPublisher.createSubscription(subscriptionRequest);
    }

    public void processCallbackRequest(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionPublisher.processCallbackRequest(callbackRequestWrapper);
    }

    public List<Subscription> findByMsisdn(String msisdn) {
        return subscriptionService.findByMsisdn(msisdn);
    }

    public Subscription findBySubscriptionId(String subscriptionId) {
        return subscriptionService.findBySubscriptionId(subscriptionId);
    }

    public void processSubscriptionRequest(SubscriptionRequest subscriptionRequest) {
        try {
            Subscription subscription = subscriptionService.createSubscription(subscriptionRequest);

            KilkariMessageCampaignRequest campaignRequest = new KilkariMessageCampaignRequest(
                    subscription.getSubscriptionId(), subscription.getPack().name(), subscription.getCreationDate());

            kilkariMessageCampaignService.start(campaignRequest);
        } catch (DuplicateSubscriptionException e) {
            LOGGER.warn(String.format("Subscription for msisdn[%s] and pack[%s] already exists.",
                    subscriptionRequest.getMsisdn(), subscriptionRequest.getPack()));
        }
    }

    public List<String> validate(CallbackRequestWrapper callbackRequestWrapper) {
        List<String> errors = new ArrayList<>();
        final String requestStatus = callbackRequestWrapper.getStatus();
        final String requestAction = callbackRequestWrapper.getAction();
        final String subscriptionId = callbackRequestWrapper.getSubscriptionId();

        if (subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper) == null) {
            errors.add(String.format("Invalid status %s for action %s", requestStatus, requestAction));
        }

        Subscription subscription = subscriptionService.findBySubscriptionId(subscriptionId);

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
}
