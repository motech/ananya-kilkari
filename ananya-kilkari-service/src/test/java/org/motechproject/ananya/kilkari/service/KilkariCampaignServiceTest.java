package org.motechproject.ananya.kilkari.service;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.scheduler.MotechSchedulerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariCampaignServiceTest {

    private KilkariCampaignService kilkariCampaignService;

    @Mock
    private MessageCampaignService messageCampaignService;
    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private ReportingService reportingService;
    @Mock
    private InboxService inboxService;
    @Mock
    private CampaignMessageAlertService campaignMessageAlertService;
    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        kilkariCampaignService = new KilkariCampaignService(messageCampaignService, kilkariSubscriptionService,
                campaignMessageAlertService, inboxService,
                motechSchedulerService);
    }

    @Test
    public void shouldGetAllScheduleTimesForAGivenMsisdn() {
        String msisdn = "1234567890";
        List<Subscription> subscriptions = new ArrayList<>();

        final DateTime now = DateTime.now();
        DateTime subscriptionStartDate = now.plusWeeks(2);
        Subscription subscription = new SubscriptionBuilder().withDefaults().withCreationDate(now)
                .withScheduleStartDate(subscriptionStartDate).withStatus(SubscriptionStatus.ACTIVE).build();
        subscriptions.add(subscription);
        String subscriptionId = subscription.getSubscriptionId();
        DateTime endDate = subscription.endDate().plusWeeks(2);

        List<DateTime> dateTimes = new ArrayList<DateTime>(){{ add(now);}};
        List<Date> dates = new ArrayList<Date>(){{ add(now.toDate());}};

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        when(messageCampaignService.getMessageTimings(
                subscriptionId,
                subscription.getCreationDate(),
                subscription.endDate())).thenReturn(dateTimes);
        when(motechSchedulerService.getScheduledJobTimingsWithPrefix(
                anyString(),
                eq(subscriptionId),
                eq(subscription.getCreationDate().toDate()),
                eq(endDate.toDate()))).thenReturn(dates);

        Map<String, List<DateTime>> timings = kilkariCampaignService.getTimings(msisdn);

        verify(messageCampaignService).getMessageTimings(
                eq(subscriptionId),
                eq(subscription.getCreationDate()),
                eq(subscription.endDate()));

        verify(motechSchedulerService).getScheduledJobTimingsWithPrefix(
                InboxEventKeys.DELETE_INBOX,
                subscriptionId,
                subscription.getCreationDate().toDate(),
                endDate.toDate());

        verify(motechSchedulerService).getScheduledJobTimingsWithPrefix(
                SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION,
                subscriptionId,
                subscription.getCreationDate().toDate(),
                endDate.toDate());

        verify(motechSchedulerService).getScheduledJobTimingsWithPrefix(
                SubscriptionEventKeys.SUBSCRIPTION_COMPLETE,
                subscriptionId,
                subscription.getCreationDate().toDate(),
                endDate.toDate());

        assertThat(timings.size(), is(4));
        assertThat(timings, hasEntry("Message Schedule: " + subscriptionId, dateTimes));
        assertThat(timings, hasEntry("Inbox Deletion: " + subscriptionId, dateTimes));
        assertThat(timings, hasEntry("Subscription Deactivation: " + subscriptionId, dateTimes));
        assertThat(timings, hasEntry("Subscription Completion: " + subscriptionId, dateTimes));
    }

    @Test
    public void shouldScheduleUnsubscriptionWhenPackIsCompletedAndWhenStatusIsNotDeactivated() {
        String subscriptionId = "abcd1234";

        Subscription subscription = new Subscription("9988776655", SubscriptionPack.BARI_KILKARI, DateTime.now().minusWeeks(1), DateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        kilkariCampaignService.processCampaignCompletion(subscriptionId);

        verify(kilkariSubscriptionService).processSubscriptionCompletion(subscription);
    }

    @Test
    public void shouldNotScheduleUnsubscriptionWhenPackIsCompletedAndStatusIsDeactivated() {
        String subscriptionId = "abcd1234";
        Subscription subscription = new Subscription("9988776655", SubscriptionPack.BARI_KILKARI, DateTime.now().minusWeeks(1), DateTime.now());
        subscription.setStatus(SubscriptionStatus.PENDING_DEACTIVATION);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        kilkariCampaignService.processCampaignCompletion(subscriptionId);

        verify(kilkariSubscriptionService, never()).processSubscriptionCompletion(subscription);
    }

    @Test
    public void shouldCallCampaignMessageAlertServiceAndUpdateInboxToHoldLastScheduledMessage() {
        DateTime creationDate = DateTime.now();
        DateTime activationDate = creationDate.plusDays(1);
        DateTime scheduleStartDate = creationDate.plusDays(2);
        String messageId = "WEEK1";
        String campaignName = MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY;
        Operator operator = Operator.AIRTEL;
        String msisdn = "9988776655";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, creationDate, DateTime.now());
        subscription.activate(operator.name(), scheduleStartDate, activationDate);
        String subscriptionId = subscription.getSubscriptionId();
        DateTime expiryDate = activationDate.plusWeeks(1);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(messageCampaignService.getCampaignStartDate(subscriptionId, campaignName)).thenReturn(creationDate);

        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId, campaignName);

        verify(campaignMessageAlertService).scheduleCampaignMessageAlert(eq(subscriptionId), eq(messageId), Mockito.argThat(dateMatches(expiryDate)), eq(msisdn), eq(operator.name()));
        verify(inboxService).newMessage(subscriptionId, messageId);
    }

    private Matcher<DateTime> dateMatches(final DateTime expiryDate) {
        return new TypeSafeMatcher<DateTime>() {
            @Override
            public boolean matchesSafely(DateTime dateTime) {
                return expiryDate.toLocalDate().equals(dateTime.toLocalDate());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ExpiryDate expected :" + expiryDate);

            }
        };
    }
}