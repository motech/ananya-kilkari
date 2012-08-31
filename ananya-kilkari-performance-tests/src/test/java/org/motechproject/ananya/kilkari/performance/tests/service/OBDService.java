package org.motechproject.ananya.kilkari.performance.tests.service;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.performance.tests.utils.ContextUtils;

public class OBDService {
    private AllCampaignMessages allCampaignMessages;
    private CampaignMessageService campaignMessageService;

    public OBDService() {
        allCampaignMessages = ContextUtils.getConfiguration().getAllCampaignMessages();
        campaignMessageService = ContextUtils.getConfiguration().getCampaignMessageService();
    }

    public void add(CampaignMessage campaignMessage) {
        allCampaignMessages.add(campaignMessage);
    }

    public void sendMessagesToOBD() {
        campaignMessageService.sendNewMessages();
        campaignMessageService.sendRetryMessages();
    }
}
