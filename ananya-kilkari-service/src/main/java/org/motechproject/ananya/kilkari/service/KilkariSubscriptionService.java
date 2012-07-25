package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.mapper.SubscriptionRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriptionMapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service
public class KilkariSubscriptionService {
    private SubscriptionPublisher subscriptionPublisher;
    private SubscriptionService subscriptionService;
    private MessageCampaignService messageCampaignService;
    private MotechSchedulerService motechSchedulerService;
    private KilkariPropertiesData kilkariProperties;

    private final Logger LOGGER = LoggerFactory.getLogger(KilkariSubscriptionService.class);

    @Autowired
    public KilkariSubscriptionService(SubscriptionPublisher subscriptionPublisher,
                                      SubscriptionService subscriptionService,
                                      MessageCampaignService messageCampaignService,
                                      MotechSchedulerService motechSchedulerService, KilkariPropertiesData kilkariProperties) {
        this.subscriptionPublisher = subscriptionPublisher;
        this.subscriptionService = subscriptionService;
        this.messageCampaignService = messageCampaignService;
        this.motechSchedulerService = motechSchedulerService;
        this.kilkariProperties = kilkariProperties;
    }

    public void createSubscriptionAsync(SubscriptionWebRequest subscriptionWebRequest) {
        subscriptionPublisher.createSubscription(subscriptionWebRequest);
    }

    public void createSubscription(SubscriptionWebRequest subscriptionWebRequest) {
        validateSubscriptionRequest(subscriptionWebRequest);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestMapper().createSubscriptionDomainRequest(subscriptionWebRequest);
        try {
            subscriptionService.createSubscription(subscriptionRequest, Channel.from(subscriptionWebRequest.getChannel()));
        } catch (DuplicateSubscriptionException e) {
            LOGGER.warn(String.format("Subscription for msisdn[%s] and pack[%s] already exists.",
                    subscriptionWebRequest.getMsisdn(), subscriptionWebRequest.getPack()));
        }
    }

    private void validateSubscriptionRequest(SubscriptionWebRequest subscriptionWebRequest) {
        Errors errors = new Errors();
        subscriptionWebRequest.validate(errors);
        if (errors.hasErrors()) {
            throw new ValidationException(errors.allMessages());
        }
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

    public void processSubscriptionCompletion(Subscription subscription) {
        String subjectKey = SubscriptionEventKeys.SUBSCRIPTION_COMPLETE;
        Date startDate = DateTime.now().plusDays(kilkariProperties.getBufferDaysToAllowRenewalForPackCompletion()).toDate();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(MotechSchedulerService.JOB_ID_KEY, subscription.getSubscriptionId());
        parameters.put("0", new SubscriptionMapper().createOMSubscriptionRequest(subscription, Channel.MOTECH));

        MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDate);

        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
    }

    public void requestDeactivation(String subscriptionId, UnsubscriptionRequest unsubscriptionRequest) {
        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, Channel.from(unsubscriptionRequest.getChannel()), unsubscriptionRequest.getCreatedAt()));
    }
}
