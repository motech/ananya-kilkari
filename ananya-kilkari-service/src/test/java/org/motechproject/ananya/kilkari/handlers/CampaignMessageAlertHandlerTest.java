package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.handlers.CampaignMessageAlertHandler;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CampaignMessageAlertHandlerTest {

    @Mock
    private KilkariCampaignService kilkariCampaignService;

    private CampaignMessageAlertHandler campaignMessageAlertHandler;

    @Before
    public void setUp() {
        initMocks(this);
        campaignMessageAlertHandler = new CampaignMessageAlertHandler(kilkariCampaignService);
    }

    @Test
    public void shouldInvokeCampaignServiceWhenMilestoneAlertIsRaised() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, "myexternalid");
        parameters.put(EventKeys.MESSAGE_KEY, "mymessagekey");
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, "mypack");
        parameters.put(EventKeys.MESSAGE_NAME_KEY, "mymessagenamekey");

        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_FIRED_EVENT_SUBJECT, parameters);

        campaignMessageAlertHandler.handleEvent(motechEvent);
        verify(kilkariCampaignService).scheduleWeeklyMessage("myexternalid");
    }
}
