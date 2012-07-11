package org.motechproject.ananya.kilkari.messagecampaign.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.messagecampaign.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariMessageCampaignServiceTest {

    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Mock
    private MessageCampaignService messageCampaignService;

    @Before
    public void setUp() {
        initMocks(this);
        kilkariMessageCampaignService = new KilkariMessageCampaignService(messageCampaignService);
    }

    @Test
    public void shouldStartNewCampaignForTheGivenRequest() {
        String externalId = "externalId";
        String subscriptionPack = SubscriptionPack.TWELVE_MONTHS.name();
        DateTime subscriptionCreationDate = DateTime.now();
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = new KilkariMessageCampaignRequest(
                externalId, subscriptionPack, subscriptionCreationDate);

        kilkariMessageCampaignService.start(kilkariMessageCampaignRequest);

        ArgumentCaptor<CampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).startFor(campaignRequestArgumentCaptor.capture());
        CampaignRequest campaignRequest = campaignRequestArgumentCaptor.getValue();

        assertEquals(externalId,campaignRequest.externalId());
        assertEquals(SubscriptionPack.TWELVE_MONTHS.getCampaignName(),campaignRequest.campaignName());
        assertEquals(subscriptionCreationDate.toLocalDate(),campaignRequest.referenceDate());
        assertEquals(new Time(subscriptionCreationDate.toLocalTime()),campaignRequest.reminderTime());
    }

    @Test
    public void shouldStop() {
        String externalId = "externalId";
        String subscriptionPack = SubscriptionPack.TWELVE_MONTHS.name();
        DateTime subscriptionCreationDate = DateTime.now();
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = new KilkariMessageCampaignRequest(
                externalId, subscriptionPack, subscriptionCreationDate);

        kilkariMessageCampaignService.stop(kilkariMessageCampaignRequest);

        ArgumentCaptor<CampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).stopAll(campaignRequestArgumentCaptor.capture());
        CampaignRequest campaignRequest = campaignRequestArgumentCaptor.getValue();

        assertEquals(externalId,campaignRequest.externalId());
        assertEquals(SubscriptionPack.TWELVE_MONTHS.getCampaignName(),campaignRequest.campaignName());
        assertEquals(subscriptionCreationDate.toLocalDate(),campaignRequest.referenceDate());
        assertEquals(new Time(subscriptionCreationDate.toLocalTime()), campaignRequest.reminderTime());
    }
    
    @Test
    public void shouldGetMessageTimingsForASubscription() {
        DateTime startDate = DateTime.now();
        String subscriptionId = "abcd1234";
        SubscriptionPack subscriptionPack = SubscriptionPack.SEVEN_MONTHS;
        DateTime endDate = startDate.plusYears(2);
        Date messageTime = DateTime.now().toDate();
        
        HashMap<String, List<Date>> campaignTimings = new HashMap<>();
        ArrayList<Date> dates = new ArrayList<Date>();
        dates.add(messageTime);
        campaignTimings.put(KilkariMessageCampaignService.CAMPAIGN_MESSAGE_NAME, dates);
        when(messageCampaignService.getCampaignTimings(subscriptionId, subscriptionPack.getCampaignName(),
                startDate.toDate(), endDate.toDate())).thenReturn(campaignTimings);

        List<DateTime> messageTimings = kilkariMessageCampaignService.getMessageTimings(
                subscriptionId, subscriptionPack.name(), startDate, endDate);

        verify(messageCampaignService).getCampaignTimings(subscriptionId, subscriptionPack.getCampaignName(),
                startDate.toDate(), endDate.toDate());
        
        assertEquals(new DateTime(messageTime), messageTimings.get(0));
    }
}
