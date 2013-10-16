package org.motechproject.ananya.kilkari.purge.service;

import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.scheduler.MotechSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartzSchedulerPurgeService {

    private MotechSchedulerService motechSchedulerService;
    private MessageCampaignService messageCampaignService;
    private AllSubscriptions allSubscriptions;
    private final Logger logger = LoggerFactory.getLogger(QuartzSchedulerPurgeService.class);


    @Autowired
    public QuartzSchedulerPurgeService(MotechSchedulerService motechSchedulerService,
                                       MessageCampaignService messageCampaignService,
                                       AllSubscriptions allSubscriptions) {
        this.motechSchedulerService = motechSchedulerService;
        this.messageCampaignService = messageCampaignService;
        this.allSubscriptions = allSubscriptions;
    }

    public void deleteFor(String msisdn) {
        List<Subscription> subscriptionList = allSubscriptions.findByMsisdn(msisdn);
        if (subscriptionList.isEmpty()) {
            logger.info(String.format("[Quartz Purger] No subscription found for msisdn: %s", msisdn));
            return;
        }
        for (Subscription subscription : subscriptionList)
            unscheduleForSubscription(subscription);

        logger.info(String.format("[Quartz Purger] Finished unscheduling jobs for msisdn: %s", msisdn));
    }

    private void unscheduleForSubscription(Subscription subscription) {
        String subscriptionId = subscription.getSubscriptionId();
        logger.info(String.format("[Quartz Purger] Unscheduling for subscriptionId: %s, msisdn: %s",
                subscriptionId, subscription.getMsisdn()));
        if (subscription.isNewEarly()) {
            logger.info(String.format("[Quartz Purger] Unscheduling from early subscription for subscriptionId: %s, msisdn: %s",
                    subscriptionId, subscription.getMsisdn()));
            motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.EARLY_SUBSCRIPTION, subscriptionId);
            return;
        }
        unScheduleCampaign(subscription);
        unscheduleFrom(subscription, InboxEventKeys.DELETE_INBOX, "Delete Inbox Job");
        unscheduleFrom(subscription, SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, "Subscription Deactivation");
        unscheduleFrom(subscription, SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, "Subscription Completion");
    }

    private void unscheduleFrom(Subscription subscription, String jobKey, String jobName) {
        logger.info(String.format("[Quartz Purger] Unscheduling from"+ jobName +"for subscriptionId: %s, msisdn: %s",
                subscription.getSubscriptionId(), subscription.getMsisdn()));
        motechSchedulerService.safeUnscheduleRunOnceJob(jobKey, subscription.getSubscriptionId());
    }

    private void unScheduleCampaign(Subscription subscription) {
        String activeCampaignName = messageCampaignService.getActiveCampaignName(subscription.getSubscriptionId());
        MessageCampaignRequest unEnrollRequest = new MessageCampaignRequest(subscription.getSubscriptionId(),
                activeCampaignName, subscription.getScheduleStartDate());
        messageCampaignService.stop(unEnrollRequest);
        logger.info(String.format("[Quartz Purger] Finished unscheduling Message Campaign Enrollment for subscriptionId: %s, msisdn: %s",
                subscription.getSubscriptionId(), subscription.getMsisdn()));

    }
}
