package org.motechproject.ananya.kilkari.performance.tests.service.api;

import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.performance.tests.utils.ContextUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;

import java.util.HashMap;

public class OBDApiService {
    private CampaignMessageService campaignMessageService;

    public OBDApiService() {
        campaignMessageService = ContextUtils.getConfiguration().getCampaignMessageService();
    }

    public void sendMessagesToOBD() {
        campaignMessageService.sendNewMessages();
        campaignMessageService.sendRetryMessages();
    }

    public void sendOBDCallbackRequest(OBDSuccessfulCallDetailsWebRequest request){
        HttpUtils.httpPostKilkariWithJsonResponse(new HashMap<String, String>(), request, "obd/calldetails/" + request.getSubscriptionId());
    }

    public void sendOBDFailedCallRecords(FailedCallReports failedCallReports) {
        HttpUtils.httpPostKilkariWithJsonResponse(null, failedCallReports, "obd/calldetails");
    }
}
