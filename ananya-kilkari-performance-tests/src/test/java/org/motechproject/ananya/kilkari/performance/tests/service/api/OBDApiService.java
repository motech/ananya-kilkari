package org.motechproject.ananya.kilkari.performance.tests.service.api;

import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class OBDApiService {

    @Autowired
    private CampaignMessageService campaignMessageService;
    @Autowired
    private HttpUtils httpUtils;

    public void sendMessagesToOBD() {
        campaignMessageService.sendNewMessages();
        campaignMessageService.sendRetryMessages();
    }

    public void sendOBDCallbackRequest(OBDSuccessfulCallDetailsWebRequest request){
        httpUtils.httpPostKilkariWithJsonResponse(new HashMap<String, String>(), request, "obd/calldetails/" + request.getSubscriptionId());
    }

    public void sendOBDFailedCallRecords(FailedCallReports failedCallReports) {
        httpUtils.httpPostKilkariWithJsonResponse(null, failedCallReports, "obd/calldetails");
    }
}
