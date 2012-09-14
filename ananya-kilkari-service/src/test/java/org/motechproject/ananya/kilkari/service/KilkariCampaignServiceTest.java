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
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

import java.util.ArrayList;
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        kilkariCampaignService = new KilkariCampaignService(messageCampaignService, kilkariSubscriptionService,
                campaignMessageAlertService, inboxService
        );
    }

    @Test
    public void shouldGetMessageTimings() {
        String msisdn = "1234567890";
        List<Subscription> subscriptions = new ArrayList<>();

        DateTime now = DateTime.now();
        DateTime subscriptionStartDate1 = now.plusWeeks(2);
        DateTime subscriptionStartDate2 = now.plusWeeks(3);
        Subscription subscription1 = new SubscriptionBuilder().withDefaults().withCreationDate(now)
                .withScheduleStartDate(subscriptionStartDate1).withStatus(SubscriptionStatus.ACTIVE).build();
        Subscription subscription2 = new SubscriptionBuilder().withDefaults().withCreationDate(now)
                .withScheduleStartDate(subscriptionStartDate2).withStatus(SubscriptionStatus.ACTIVE).build();
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);

        List<DateTime> dateTimes = new ArrayList<>();
        dateTimes.add(now);

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        when(messageCampaignService.getMessageTimings(
                subscription1.getSubscriptionId(),
                subscription1.getCreationDate(),
                subscription1.endDate())).thenReturn(dateTimes);
        when(messageCampaignService.getMessageTimings(
                subscription2.getSubscriptionId(),
                subscription2.getCreationDate(),
                subscription2.endDate())).thenReturn(dateTimes);

        Map<String, List<DateTime>> messageTimings = kilkariCampaignService.getMessageTimings(msisdn);

        verify(messageCampaignService).getMessageTimings(
                eq(subscription1.getSubscriptionId()),
                eq(subscription1.getCreationDate()),
                eq(subscription1.endDate()));

        verify(messageCampaignService).getMessageTimings(
                eq(subscription2.getSubscriptionId()),
                eq(subscription2.getCreationDate()),
                eq(subscription2.endDate()));

        assertThat(messageTimings.size(), is(2));
        assertThat(messageTimings, hasEntry(subscription1.getSubscriptionId(), dateTimes));
        assertThat(messageTimings, hasEntry(subscription2.getSubscriptionId(), dateTimes));
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