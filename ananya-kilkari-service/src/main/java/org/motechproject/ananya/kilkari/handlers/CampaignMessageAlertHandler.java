package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CampaignMessageAlertHandler {

    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public CampaignMessageAlertHandler(KilkariCampaignService kilkariCampaignService) {
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @MotechListener(subjects = {EventKeys.MESSAGE_CAMPAIGN_FIRED_EVENT_SUBJECT})
    public void handleEvent(MotechEvent motechEvent) {
        Map<String,Object> parameters = motechEvent.getParameters();
        String subscriptionId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId);
    }
}
