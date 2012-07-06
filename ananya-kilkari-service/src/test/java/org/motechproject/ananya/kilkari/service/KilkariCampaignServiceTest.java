package org.motechproject.ananya.kilkari.service;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariCampaignServiceTest {

    private KilkariCampaignService kilkariCampaignService;

    @Mock
    private KilkariMessageCampaignService kilkariMessageCampaignService;
    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
        kilkariCampaignService = new KilkariCampaignService(kilkariMessageCampaignService, kilkariSubscriptionService);
    }

    @Test
    public void shouldGetMessageTimings() {
        String msisdn = "99880";

        List<Subscription> subscriptions = new ArrayList<>();
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS);
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.SEVEN_MONTHS);
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);

        List<DateTime> dateTimes = new ArrayList<>();
        dateTimes.add(DateTime.now());

        when(kilkariSubscriptionService.getSubscriptionsFor(msisdn)).thenReturn(subscriptions);

        when(kilkariMessageCampaignService.getMessageTimings(
                subscription1.getSubscriptionId(),
                KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME,
                subscription1.getCreationDate(), subscription1.endDate().plusWeeks(1))).thenReturn(dateTimes);
        when(kilkariMessageCampaignService.getMessageTimings(
                subscription2.getSubscriptionId(),
                KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME,
                subscription2.getCreationDate(), subscription2.endDate().plusWeeks(1))).thenReturn(dateTimes);


        Map<String, List<DateTime>> messageTimings = kilkariCampaignService.getMessageTimings(msisdn);

        verify(kilkariMessageCampaignService).getMessageTimings(
                eq(subscription1.getSubscriptionId()),
                eq(KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME),
                eq(subscription1.getCreationDate()),
                eq(subscription1.endDate()));

        verify(kilkariMessageCampaignService).getMessageTimings(
                eq(subscription2.getSubscriptionId()),
                eq(KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME),
                eq(subscription2.getCreationDate()),
                eq(subscription2.endDate()));

        assertThat(messageTimings.size(), is(2));
        assertThat(messageTimings, hasEntry(subscription1.getSubscriptionId(), dateTimes));
        assertThat(messageTimings, hasEntry(subscription2.getSubscriptionId(), dateTimes));
    }
}
