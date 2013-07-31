package org.motechproject.ananya.kilkari.purge.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.scheduler.MotechSchedulerService;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuartzSchedulerPurgeServiceTest {
    @Mock
    private MotechSchedulerService motechSchedulerService;
    @Mock
    private Scheduler motechScheduler;
    @Mock
    private SchedulerFactoryBean schedulerFactoryBean;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private MessageCampaignService messageCampaignService;

    private QuartzSchedulerPurgeService quartzSchedulerPurgeService;
    private String msisdn;

    private Subscription subscription;

    @Before
    public void setup() {
        when(schedulerFactoryBean.getScheduler()).thenReturn(motechScheduler);
        quartzSchedulerPurgeService = new QuartzSchedulerPurgeService(motechSchedulerService, messageCampaignService, allSubscriptions);
        msisdn = "123456";
        subscription = new Subscription(msisdn, SubscriptionPack.NANHI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(new ArrayList<Subscription>() {{
            add(subscription);
        }});
    }

    @Test
    public void shouldUnscheduleCampaignForAGivenSubscription() {
        String expectedCampaignName = "campaignName";
        when(messageCampaignService.getActiveCampaignName(subscription.getSubscriptionId())).thenReturn(expectedCampaignName);
        
        quartzSchedulerPurgeService.deleteFor(msisdn);

        ArgumentCaptor<MessageCampaignRequest> requestCaptor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        verify(messageCampaignService).stop(requestCaptor.capture());
        MessageCampaignRequest actualRequest = requestCaptor.getValue();
        assertEquals(expectedCampaignName, actualRequest.getCampaignName());
    }

    @Test
    public void shouldUnscheduleAnEarlySubscription() {
        subscription.setStatus(SubscriptionStatus.NEW_EARLY);

        quartzSchedulerPurgeService.deleteFor(msisdn);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(SubscriptionEventKeys.EARLY_SUBSCRIPTION, subscription.getSubscriptionId());
    }

    @Test
    public void shouldUnscheduleInboxDeletion() {
        quartzSchedulerPurgeService.deleteFor(msisdn);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(InboxEventKeys.DELETE_INBOX, subscription.getSubscriptionId());
    }

    @Test
    public void shouldUnscheduleDeactivationReqeust() {
        quartzSchedulerPurgeService.deleteFor(msisdn);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, subscription.getSubscriptionId());
    }

    @Test
    public void shouldUnscheduleSubscriptionCompletionEvent() {
        quartzSchedulerPurgeService.deleteFor(msisdn);

        verify(motechSchedulerService).safeUnscheduleRunOnceJob(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, subscription.getSubscriptionId());
    }
}
