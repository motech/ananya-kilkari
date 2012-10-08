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
        logger.info(String.format("[Quartz Purger] Deleting based on subscriptionId: %s, msisdn: %s",
                subscriptionId, subscription.getMsisdn()));
        if (subscription.isNewEarly()) {
            motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.EARLY_SUBSCRIPTION, subscriptionId);
            return;
        }
        unScheduleCampaign(subscription);
        motechSchedulerService.safeUnscheduleRunOnceJob(InboxEventKeys.DELETE_INBOX, subscriptionId);
        motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, subscriptionId);
        motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, subscriptionId);
    }

    private void unScheduleCampaign(Subscription subscription) {
        String activeCampaignName = messageCampaignService.getActiveCampaignName(subscription.getSubscriptionId());
        MessageCampaignRequest unEnrollRequest = new MessageCampaignRequest(subscription.getSubscriptionId(),
                activeCampaignName, subscription.getScheduleStartDate());
        messageCampaignService.stop(unEnrollRequest);
    }
}
