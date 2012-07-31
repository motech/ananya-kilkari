package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OBDController {
    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public OBDController(KilkariCampaignService kilkariCampaignService) {
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @RequestMapping(value = "/obd/calldetails/{subscriptionId}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse handleSuccessfulResponse(@RequestBody OBDSuccessfulCallRequest successfulCallRequest, @PathVariable String subscriptionId) {
        kilkariCampaignService.publishSuccessfulCallRequest(new OBDSuccessfulCallRequestWrapper(successfulCallRequest, subscriptionId, DateTime.now(), Channel.IVR));
        return BaseResponse.success("OBD call details received successfully for subscriptionId : " + subscriptionId);
    }

    @RequestMapping(value = "/obd/calldetails", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse handleCallDeliveryFailureRecords(@RequestBody FailedCallReports failedCallReports) {
        kilkariCampaignService.publishCallDeliveryFailureRequest(failedCallReports);
        return BaseResponse.success("OBD call delivery failure records received successfully");
    }

    @RequestMapping(value = "/obd/invalidcallrecords", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse handleInvalidCallRecords(@RequestBody InvalidOBDRequestEntries invalidRecordsRequestEntries) {
        kilkariCampaignService.publishInvalidCallRecordsRequest(invalidRecordsRequestEntries);
        return BaseResponse.success("OBD invalid call records received successfully");
    }
}
