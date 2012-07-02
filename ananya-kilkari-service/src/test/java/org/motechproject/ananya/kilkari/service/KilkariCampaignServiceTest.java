package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariCampaignServiceTest {

    private KilkariCampaignService kilkariCampaignService;

    @Mock
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Before
    public void setUp() {
        initMocks(this);
        kilkariCampaignService = new KilkariCampaignService(kilkariMessageCampaignService);
    }

    @Test
    public void shouldGetMessageTimings() {
        String msisdn = "99880";
        kilkariCampaignService.getMessageTimings(msisdn);
        verify(kilkariMessageCampaignService).getMessageTimings(msisdn, KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME);
    }
}
