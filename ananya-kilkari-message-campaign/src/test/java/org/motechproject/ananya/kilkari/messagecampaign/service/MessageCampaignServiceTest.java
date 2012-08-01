package org.motechproject.ananya.kilkari.messagecampaign.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageCampaignServiceTest {

    private MessageCampaignService messageCampaignService;

    @Mock
    private org.motechproject.server.messagecampaign.service.MessageCampaignService platformMessageCampaignService;

    @Mock
    private KilkariPropertiesData kilkariProperties;


    @Before
    public void setUp() {
        initMocks(this);
        this.messageCampaignService = new MessageCampaignService(platformMessageCampaignService, kilkariProperties);
    }

    @Test
    public void shouldStartNewCampaignForTheGivenRequest() {
        String externalId = "externalId";
        String subscriptionPack = MessageCampaignPack.TWELVE_MONTHS.name();
        DateTime subscriptionCreationDate = DateTime.now();
        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                externalId, subscriptionPack, subscriptionCreationDate);

        this.messageCampaignService.start(messageCampaignRequest);

        ArgumentCaptor<CampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(platformMessageCampaignService).startFor(campaignRequestArgumentCaptor.capture());
        CampaignRequest campaignRequest = campaignRequestArgumentCaptor.getValue();

        assertEquals(externalId, campaignRequest.externalId());
        assertEquals(MessageCampaignPack.TWELVE_MONTHS.getCampaignName(), campaignRequest.campaignName());
        assertEquals(subscriptionCreationDate.toLocalDate(), campaignRequest.referenceDate());
        assertEquals(new Time(subscriptionCreationDate.toLocalTime()), campaignRequest.deliverTime());
    }

    @Test
    public void shouldStop() {
        String externalId = "externalId";
        String subscriptionPack = MessageCampaignPack.TWELVE_MONTHS.name();
        DateTime subscriptionCreationDate = DateTime.now();
        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                externalId, subscriptionPack, subscriptionCreationDate);

        this.messageCampaignService.stop(messageCampaignRequest);

        ArgumentCaptor<CampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(platformMessageCampaignService).stopAll(campaignRequestArgumentCaptor.capture());
        CampaignRequest campaignRequest = campaignRequestArgumentCaptor.getValue();

        assertEquals(externalId, campaignRequest.externalId());
        assertEquals(MessageCampaignPack.TWELVE_MONTHS.getCampaignName(), campaignRequest.campaignName());
        assertEquals(subscriptionCreationDate.toLocalDate(), campaignRequest.referenceDate());
        assertEquals(new Time(subscriptionCreationDate.toLocalTime()), campaignRequest.deliverTime());
    }

    @Test
    public void shouldGetMessageTimingsForASubscription() {
        DateTime startDate = DateTime.now();
        String subscriptionId = "abcd1234";
        MessageCampaignPack messageCampaignPack = MessageCampaignPack.SEVEN_MONTHS;
        DateTime endDate = startDate.plusYears(2);
        Date messageTime = DateTime.now().toDate();

        HashMap<String, List<Date>> campaignTimings = new HashMap<>();
        ArrayList<Date> dates = new ArrayList<Date>();
        dates.add(messageTime);
        campaignTimings.put(MessageCampaignService.CAMPAIGN_MESSAGE_NAME, dates);
        when(platformMessageCampaignService.getCampaignTimings(subscriptionId, messageCampaignPack.getCampaignName(),
                startDate.toDate(), endDate.toDate())).thenReturn(campaignTimings);

        ArrayList<CampaignEnrollmentRecord> campaignEnrollmentRecords = new ArrayList<CampaignEnrollmentRecord>();
        campaignEnrollmentRecords.add(new CampaignEnrollmentRecord(subscriptionId, MessageCampaignPack.FIFTEEN_MONTHS.getCampaignName(), startDate.toLocalDate(), CampaignEnrollmentStatus.COMPLETED));
        campaignEnrollmentRecords.add(new CampaignEnrollmentRecord(subscriptionId, messageCampaignPack.getCampaignName(), startDate.toLocalDate(), CampaignEnrollmentStatus.ACTIVE));
        when(platformMessageCampaignService.search(any(CampaignEnrollmentsQuery.class))).thenReturn(campaignEnrollmentRecords);

        List<DateTime> messageTimings = this.messageCampaignService.getMessageTimings(
                subscriptionId, startDate, endDate);

        verify(platformMessageCampaignService).getCampaignTimings(subscriptionId, messageCampaignPack.getCampaignName(),
                startDate.toDate(), endDate.toDate());

        assertEquals(new DateTime(messageTime), messageTimings.get(0));
    }

    @Test
    public void shouldGetCampaignStartDateForGivenCampaignSubscription() {
        String subscriptionId = "abcd1234";
        String campaignName = "twelve_months";
        DateTime startDate = DateTime.now();

        ArrayList<CampaignEnrollmentRecord> campaignEnrollmentRecords = new ArrayList<>();
        campaignEnrollmentRecords.add(new CampaignEnrollmentRecord(null, "fifteen_months", startDate.minusYears(1).toLocalDate(), CampaignEnrollmentStatus.ACTIVE));
        campaignEnrollmentRecords.add(new CampaignEnrollmentRecord(null, campaignName, startDate.toLocalDate(), CampaignEnrollmentStatus.ACTIVE));
        when(platformMessageCampaignService.search(any(CampaignEnrollmentsQuery.class))).thenReturn(campaignEnrollmentRecords);

        DateTime campaignStartDate = this.messageCampaignService.getCampaignStartDate(subscriptionId, campaignName);

        assertEquals(startDate.toLocalDate(), campaignStartDate.toLocalDate());
    }
}