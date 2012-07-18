package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.mappers.SubscriptionMapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
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
    private KilkariMessageCampaignService kilkariMessageCampaignService;
    private MotechSchedulerService motechSchedulerService;

    public static final String SUBSCRIPTION_ID_PARAMETER_KEY = "subscription.id";
    public static final int BUFFER_DAYS_TO_ALLOW_RENEWAL = 3;
    private final Logger LOGGER = LoggerFactory.getLogger(KilkariSubscriptionService.class);

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

            KilkariMessageCampaignRequest campaignRequest = new KilkariMessageCampaignRequest(
                    subscription.getSubscriptionId(), subscription.getPack().name(), subscription.getCreationDate());

            kilkariMessageCampaignService.start(campaignRequest);
        } catch (DuplicateSubscriptionException e) {
            LOGGER.warn(String.format("Subscription for msisdn[%s] and pack[%s] already exists.",
                    subscriptionRequest.getMsisdn(), subscriptionRequest.getPack()));
        }
    }

    public void scheduleSubscriptionPackCompletionEvent(Subscription subscription) {
        Date startDate = DateTime.now().plusDays(BUFFER_DAYS_TO_ALLOW_RENEWAL).toDate();

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(MotechSchedulerService.JOB_ID_KEY, subscription.getSubscriptionId());
        parameters.put("0", SubscriptionMapper.mapFrom(subscription, null));

        MotechEvent motechEvent = new MotechEvent(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, parameters);
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDate);

        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
    }
}
