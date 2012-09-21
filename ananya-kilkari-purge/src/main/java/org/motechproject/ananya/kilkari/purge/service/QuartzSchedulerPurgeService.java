package org.motechproject.ananya.kilkari.purge.service;

import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzSchedulerPurgeService {

    private SubscriptionService subscriptionService;
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    public QuartzSchedulerPurgeService(SubscriptionService subscriptionService, MotechSchedulerService motechSchedulerService) {
        this.subscriptionService = subscriptionService;
        this.motechSchedulerService = motechSchedulerService;
    }

    public void deleteFor(Subscription subscription) {
        if (subscription.isNewEarly()) {
            motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.EARLY_SUBSCRIPTION, subscription.getSubscriptionId());
            return;
        }
        subscriptionService.unScheduleCampaign(subscription);
        motechSchedulerService.safeUnscheduleRunOnceJob(InboxEventKeys.DELETE_INBOX, subscription.getSubscriptionId());
        motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, subscription.getSubscriptionId());
        motechSchedulerService.safeUnscheduleRunOnceJob(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, subscription.getSubscriptionId());
    }
}
