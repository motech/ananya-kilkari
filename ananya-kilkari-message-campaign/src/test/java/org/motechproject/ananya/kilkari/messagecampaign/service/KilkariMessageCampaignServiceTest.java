package org.motechproject.ananya.kilkari.messagecampaign.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
        String campaignName = "campaignName";
        ArrayList<CampaignEnrollmentRecord> campaignEnrollmentRecords = new ArrayList<>();
        campaignEnrollmentRecords.add(new CampaignEnrollmentRecord(externalId, campaignName,
                DateTime.now().toLocalDate(), CampaignEnrollmentStatus.ACTIVE));
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = new KilkariMessageCampaignRequest(
                externalId, campaignName, null , DateTime.now(),0);
        when(messageCampaignService.search(new CampaignEnrollmentsQuery().withExternalId(externalId)
                .withCampaignName(campaignName))).thenReturn(campaignEnrollmentRecords);

        kilkariMessageCampaignService.start(kilkariMessageCampaignRequest);

        ArgumentCaptor<CampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).startFor(campaignRequestArgumentCaptor.capture());
        CampaignRequest campaignRequest = campaignRequestArgumentCaptor.getValue();
        assertRequestParameters(kilkariMessageCampaignRequest, campaignRequest);
    }

    @Test
    public void shouldStop() {
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = new KilkariMessageCampaignRequest(
                "externalId", "campaignName", DateTime.now(), DateTime.now(), 0);

        kilkariMessageCampaignService.stop(kilkariMessageCampaignRequest);

        ArgumentCaptor<CampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).stopAll(campaignRequestArgumentCaptor.capture());
        CampaignRequest campaignRequest = campaignRequestArgumentCaptor.getValue();
        assertRequestParameters(kilkariMessageCampaignRequest, campaignRequest);
    }

    private void assertRequestParameters(KilkariMessageCampaignRequest kilkariMessageCampaignRequest, CampaignRequest campaignRequest) {
        DateTime referenceDate = kilkariMessageCampaignRequest.getReferenceDate();

        assertEquals(kilkariMessageCampaignRequest.getExternalId(), campaignRequest.externalId());
        assertEquals(kilkariMessageCampaignRequest.getCampaignName(), campaignRequest.campaignName());
        assertEquals(new LocalDate(referenceDate.getYear(), referenceDate.getMonthOfYear(), referenceDate.getDayOfMonth()), campaignRequest.referenceDate());
    }
}
