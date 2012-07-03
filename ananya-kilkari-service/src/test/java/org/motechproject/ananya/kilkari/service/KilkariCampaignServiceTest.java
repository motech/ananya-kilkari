package org.motechproject.ananya.kilkari.service;

import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
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
        List<Subscription> subscriptions = new ArrayList<> ();
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS);
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.SEVEN_MONTHS);
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);

        ArrayList<DateTime> dateTimes = new ArrayList<>();
        dateTimes.add(DateTime.now());

        when(kilkariSubscriptionService.getSubscriptionsFor(msisdn)).thenReturn(subscriptions);
        when(kilkariMessageCampaignService.getMessageTimings(subscription1.getSubscriptionId(),
                KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME)).thenReturn(dateTimes);
        when(kilkariMessageCampaignService.getMessageTimings(subscription2.getSubscriptionId(), 
                KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME)).thenReturn(dateTimes);
        
        HashMap<String,List<DateTime>> messageTimings = kilkariCampaignService.getMessageTimings(msisdn);

        verify(kilkariMessageCampaignService).getMessageTimings(subscription1.getSubscriptionId(), KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME);
        verify(kilkariMessageCampaignService).getMessageTimings(subscription2.getSubscriptionId(), KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME);
        assertNotNull(messageTimings.get(subscription1.getSubscriptionId()));
        assertNotNull(messageTimings.get(subscription2.getSubscriptionId()));
    }
}
