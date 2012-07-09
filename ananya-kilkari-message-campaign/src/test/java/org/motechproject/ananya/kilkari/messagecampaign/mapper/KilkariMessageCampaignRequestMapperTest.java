package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import static org.junit.Assert.assertEquals;


public class KilkariMessageCampaignRequestMapperTest {

    @Test
    public void shouldMapKilkariMessageCampaignRequestToCampaignRequest() throws Exception {
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = new KilkariMessageCampaignRequest(
                "externalId", "twelve_months", new DateTime(2012, 5, 5, 0, 0));

        CampaignRequest campaignRequest = KilkariMessageCampaignRequestMapper.newRequestFrom(kilkariMessageCampaignRequest);

        assertEquals(kilkariMessageCampaignRequest.getExternalId(), campaignRequest.externalId());
        assertEquals(KilkariMessageCampaignService.TWELVE_MONTHS, campaignRequest.campaignName());
        assertEquals(kilkariMessageCampaignRequest.getSubscriptionCreationDate().toLocalDate(), campaignRequest.referenceDate());
        assertEquals(new Time(kilkariMessageCampaignRequest.getSubscriptionCreationDate().toLocalTime()), campaignRequest.reminderTime());
    }
}
