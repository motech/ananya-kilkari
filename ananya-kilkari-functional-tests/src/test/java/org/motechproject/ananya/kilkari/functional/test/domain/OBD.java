package org.motechproject.ananya.kilkari.functional.test.domain;

import org.motechproject.ananya.kilkari.functional.test.utils.JsonUtils;
import org.motechproject.ananya.kilkari.functional.test.verifiers.CampaignMessageVerifier;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.controller.OBDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import static org.motechproject.ananya.kilkari.functional.test.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@Component
public class OBD {
    @Autowired
    private OBDController obdController;
    @Autowired
    private CampaignMessageVerifier campaignMessageVerifier;

    public void reportsUserDidNotPickUpTheCall(SubscriptionData subscriptionData, String campaignId) throws Exception {
        FailedCallReport failedCallReport = new FailedCallReport(subscriptionData.getSubscriptionId(), subscriptionData.getMsisdn(), campaignId, "Q.850_18");
        FailedCallReports failedCallReports = new FailedCallReports();
        failedCallReports.setFailedCallReports(Arrays.asList(failedCallReport));

        mockMvc(obdController)
                .perform(post("/obd/calldetails")
                        .body(JsonUtils.toJson(failedCallReports).getBytes())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        campaignMessageVerifier.verifyCampaignMessageExists(subscriptionData, campaignId);
        campaignMessageVerifier.verifyCampaignMessageFailedDueToDNP(subscriptionData, campaignId);
    }

    public void doesNotCallTheUser(SubscriptionData subscriptionData, String campaignId) throws Exception {
        FailedCallReport failedCallReport = new FailedCallReport(subscriptionData.getSubscriptionId(), subscriptionData.getMsisdn(), campaignId, "Q.850_1");
        FailedCallReports failedCallReports = new FailedCallReports();
        failedCallReports.setFailedCallReports(Arrays.asList(failedCallReport));

        mockMvc(obdController)
                .perform(post("/obd/calldetails")
                        .body(JsonUtils.toJson(failedCallReports).getBytes())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        campaignMessageVerifier.verifyCampaignMessageExists(subscriptionData, campaignId);
        campaignMessageVerifier.verifyCampaignMessageFailedDueToDNC(subscriptionData, campaignId);
    }
}
