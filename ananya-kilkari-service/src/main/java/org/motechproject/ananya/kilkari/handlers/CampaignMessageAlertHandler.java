package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CampaignMessageAlertHandler {

    private KilkariCampaignService kilkariCampaignService;
    private static final Logger logger = LoggerFactory.getLogger(CampaignMessageAlertHandler.class);

    @Autowired
    public CampaignMessageAlertHandler(KilkariCampaignService kilkariCampaignService) {
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @MotechListener(subjects = {EventKeys.MESSAGE_CAMPAIGN_FIRED_EVENT_SUBJECT})
    public void handleEvent(MotechEvent motechEvent) {
        Map<String,Object> parameters = motechEvent.getParameters();
        String subscriptionId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        logger.info("Handling campaign message alert for subscription id: " + subscriptionId);
        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId);
    }
}
