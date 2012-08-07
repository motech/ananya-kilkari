package org.motechproject.ananya.kilkari.messagecampaign.domain;

import org.junit.Test;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;

import static org.junit.Assert.assertEquals;

public class MessageCampaignPackTest {
    @Test
    public void shouldMapFromCampaignPackNameToMessageCampaignKeyInJson(){
        assertEquals(MessageCampaignPack.BARI_KILKARI.getCampaignName(), MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.CHOTI_KILKARI.getCampaignName(), MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.NANHI_KILKARI.getCampaignName(), MessageCampaignService.SEVEN_MONTHS_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.INFANT_DEATH.getCampaignName(), MessageCampaignService.INFANT_DEATH_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.MISCARRIAGE.getCampaignName(), MessageCampaignService.MISCARRIAGE_CAMPAIGN_KEY);
    }
}
