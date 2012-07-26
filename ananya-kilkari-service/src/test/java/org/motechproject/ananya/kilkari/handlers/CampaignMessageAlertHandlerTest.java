package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

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
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, "myexternalid");
        parameters.put(EventKeys.MESSAGE_KEY, "mymessagekey");
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, "mypack");
        parameters.put(EventKeys.MESSAGE_NAME_KEY, "mymessagenamekey");

        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_FIRED_EVENT_SUBJECT, parameters);

        campaignMessageAlertHandler.handleAlertEvent(motechEvent);
        verify(kilkariCampaignService).scheduleWeeklyMessage("myexternalid", "mypack");
    }

    @Test
    public void shouldCompleteSubscriptionWhileHandlingCampaignCompletedEvent(){
        Map<String, Object> parameters = new HashMap<>();
        String subscriptionId = "subscriptionId";
        parameters.put(EventKeys.ENROLLMENT_KEY, new CampaignEnrollment(subscriptionId, "campaignName"));
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT, parameters);

        campaignMessageAlertHandler.handleCompletionEvent(motechEvent);

        verify(kilkariCampaignService).processCampaignCompletion(subscriptionId);
    }
}
