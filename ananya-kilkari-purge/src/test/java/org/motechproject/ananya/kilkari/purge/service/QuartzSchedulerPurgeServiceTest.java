package org.motechproject.ananya.kilkari.purge.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuartzSchedulerPurgeServiceTest {
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private MotechSchedulerService motechSchedulerService;
    @Mock
    private Scheduler motechScheduler;
    @Mock
    private SchedulerFactoryBean schedulerFactoryBean;
    private QuartzSchedulerPurgeService quartzSchedulerPurgeService;
    private Subscription subscription;

    @Before
    public void setup() {
        when(schedulerFactoryBean.getScheduler()).thenReturn(motechScheduler);
        quartzSchedulerPurgeService = new QuartzSchedulerPurgeService(subscriptionService, motechSchedulerService);
        subscription = new Subscription("123456", SubscriptionPack.NANHI_KILKARI, DateTime.now(), DateTime.now());
    }

    @Test
    public void shouldUnscheduleCampaignForAGivenSubscription() {
        quartzSchedulerPurgeService.deleteFor(subscription);

        verify(subscriptionService).unScheduleCampaign(subscription);
    }

    @Test
    public void shouldUnscheduleAnEarlySubscription() {
        subscription.setStatus(SubscriptionStatus.NEW_EARLY);

        quartzSchedulerPurgeService.deleteFor(subscription);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(SubscriptionEventKeys.EARLY_SUBSCRIPTION, subscription.getSubscriptionId());
    }

    @Test
    public void shouldUnscheduleInboxDeletion() {
        quartzSchedulerPurgeService.deleteFor(subscription);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(InboxEventKeys.DELETE_INBOX, subscription.getSubscriptionId());
    }

    @Test
    public void shouldUnscheduleDeactivationReqeust() {
        quartzSchedulerPurgeService.deleteFor(subscription);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, subscription.getSubscriptionId());
    }

    @Test
    public void shouldUnscheduleSubscriptionCompletionEvent() {
        quartzSchedulerPurgeService.deleteFor(subscription);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, subscription.getSubscriptionId());
    }
}
