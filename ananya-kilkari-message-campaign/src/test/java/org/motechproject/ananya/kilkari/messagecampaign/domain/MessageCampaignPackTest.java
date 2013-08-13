package org.motechproject.ananya.kilkari.messagecampaign.domain;

import org.junit.Test;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;

import static org.junit.Assert.*;

public class MessageCampaignPackTest {
    @Test
    public void shouldMapFromCampaignPackNameToMessageCampaignKeyInJson() {
        assertEquals(MessageCampaignPack.BARI_KILKARI.getCampaignName(), MessageCampaignService.SIXTEEN_MONTHS_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.NAVJAAT_KILKARI.getCampaignName(), MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.NANHI_KILKARI.getCampaignName(), MessageCampaignService.SEVEN_MONTHS_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.INFANT_DEATH.getCampaignName(), MessageCampaignService.INFANT_DEATH_CAMPAIGN_KEY);
        assertEquals(MessageCampaignPack.MISCARRIAGE.getCampaignName(), MessageCampaignService.MISCARRIAGE_CAMPAIGN_KEY);
    }

    @Test
    public void shouldCheckIfTheMessageCampaignPackIsIDOrMC() {
        assertTrue(MessageCampaignPack.MISCARRIAGE.isMCOrID());
        assertTrue(MessageCampaignPack.INFANT_DEATH.isMCOrID());
        assertFalse(MessageCampaignPack.BARI_KILKARI.isMCOrID());
        assertFalse(MessageCampaignPack.NAVJAAT_KILKARI.isMCOrID());
        assertFalse(MessageCampaignPack.NANHI_KILKARI.isMCOrID());
    }
}
