package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import static org.junit.Assert.assertEquals;


public class KilkariMessageCampaignRequestMapperTest {

    @Test
    public void shouldMapKilkariMessageCampaignRequestToCampaignRequest() throws Exception {
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = new KilkariMessageCampaignRequest(
                "externalId", "campaignName", null, new DateTime(2012, 5, 5, 0, 0), 0);

        CampaignRequest campaignRequest = KilkariMessageCampaignRequestMapper.newRequestFrom(kilkariMessageCampaignRequest);

        assertEquals(kilkariMessageCampaignRequest.getExternalId(), campaignRequest.externalId());
        assertEquals(kilkariMessageCampaignRequest.getCampaignName(), campaignRequest.campaignName());
        assertEquals(new LocalDate(2012, 5, 5), campaignRequest.referenceDate());
        assertEquals(kilkariMessageCampaignRequest.getStartOffset(), campaignRequest.startOffset());
    }
}
