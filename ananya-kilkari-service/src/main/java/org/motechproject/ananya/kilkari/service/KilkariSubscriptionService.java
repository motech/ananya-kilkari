package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.messagecampaign.contract.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.mappers.SubscriptionMapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class KilkariSubscriptionService {
    private SubscriptionPublisher subscriptionPublisher;
    private SubscriptionService subscriptionService;
    private KilkariMessageCampaignService kilkariMessageCampaignService;
    private MotechSchedulerService motechSchedulerService;

    private static final int BUFFER_DAYS_TO_ALLOW_INBOX_ACCESS = 7;
    private final Logger LOGGER = LoggerFactory.getLogger(KilkariSubscriptionService.class);

    @Value("#{kilkariProperties['buffer.days.to.allow.renewal.for.pack.completion']}")
    protected int bufferDaysToAllowRenewalForPackCompletion;

    @Autowired
    public KilkariSubscriptionService(SubscriptionPublisher subscriptionPublisher,
                                      SubscriptionService subscriptionService,
                                      KilkariMessageCampaignService kilkariMessageCampaignService,
                                      MotechSchedulerService motechSchedulerService) {
        this.subscriptionPublisher = subscriptionPublisher;
        this.subscriptionService = subscriptionService;
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.motechSchedulerService = motechSchedulerService;
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

            MessageCampaignRequest campaignRequest = new MessageCampaignRequest(
                    subscription.getSubscriptionId(), subscription.getPack().name(), subscription.getCreationDate());

            kilkariMessageCampaignService.start(campaignRequest);
        } catch (DuplicateSubscriptionException e) {
            LOGGER.warn(String.format("Subscription for msisdn[%s] and pack[%s] already exists.",
                    subscriptionRequest.getMsisdn(), subscriptionRequest.getPack()));
        }
    }

    public void processSubscriptionCompletion(Subscription subscription) {
        scheduleSubscriptionCompletionEvent(subscription);
        scheduleInboxDeletionEvent(subscription);
    }

    private void scheduleSubscriptionCompletionEvent(Subscription subscription) {
        String subjectKey = SubscriptionEventKeys.SUBSCRIPTION_COMPLETE;
        Date startDate = DateTime.now().plusDays(bufferDaysToAllowRenewalForPackCompletion).toDate();
        RunOnceSchedulableJob runOnceSchedulableJob = createRunOnceSchedulableJob(subscription, startDate, subjectKey);

        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
    }

    private void scheduleInboxDeletionEvent(Subscription subscription) {
        String subjectKey = SubscriptionEventKeys.DELETE_INBOX;
        Date startDate = DateTime.now().plusDays(BUFFER_DAYS_TO_ALLOW_INBOX_ACCESS + 1).toDate();
        RunOnceSchedulableJob runOnceSchedulableJob = createRunOnceSchedulableJob(subscription, startDate, subjectKey);

        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
    }

    private RunOnceSchedulableJob createRunOnceSchedulableJob(Subscription subscription, Date startDate, String subjectKey) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(MotechSchedulerService.JOB_ID_KEY, subscription.getSubscriptionId());
        parameters.put("0", SubscriptionMapper.mapFrom(subscription, Channel.MOTECH));

        MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);
        return new RunOnceSchedulableJob(motechEvent, startDate);
    }

    public void requestDeactivation(String subscriptionId, UnsubscriptionRequest unsubscriptionRequest) {
        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, Channel.from(unsubscriptionRequest.getChannel())));
    }
}
