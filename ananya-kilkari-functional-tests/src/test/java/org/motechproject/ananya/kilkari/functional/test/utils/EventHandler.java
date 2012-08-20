package org.motechproject.ananya.kilkari.functional.test.utils;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.stereotype.Component;

@Component
public class EventHandler {

    private boolean campaignAlertRaised;

    @MotechListener(subjects = {EventKeys.SEND_MESSAGE})
    public void handleCampaignAlert(MotechEvent motechEvent) {
        campaignAlertRaised = true;
    }


    public boolean hasCampaignAlertBeenRaised() {
        return campaignAlertRaised;
    }

    public void reset() {
        campaignAlertRaised = false;
    }
}
