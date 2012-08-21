package org.motechproject.ananya.kilkari.web.controller;

import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.KilkariCallDetailsService;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OBDController {
    private KilkariCallDetailsService kilkariCallDetailsService;

    @Autowired
    public OBDController(KilkariCallDetailsService kilkariCallDetailsService) {
        this.kilkariCallDetailsService = kilkariCallDetailsService;
    }

    @RequestMapping(value = "/obd/calldetails/{subscriptionId}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse handleSuccessfulResponse(@RequestBody OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest, @PathVariable String subscriptionId) {
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        kilkariCallDetailsService.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);
        return BaseResponse.success("OBD call details received successfully for subscriptionId : " + subscriptionId);
    }

    @RequestMapping(value = "/obd/calldetails", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse handleCallDeliveryFailureRecords(@RequestBody FailedCallReports failedCallReports) {
        kilkariCallDetailsService.processCallDeliveryFailureRequest(failedCallReports);
        return BaseResponse.success("OBD call delivery failure records received successfully");
    }

    @RequestMapping(value = "/obd/invalidcallrecords", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse handleInvalidCallRecords(@RequestBody InvalidOBDRequestEntries invalidRecordsRequestEntries) {
        kilkariCallDetailsService.processInvalidOBDRequestEntries(invalidRecordsRequestEntries);
        return BaseResponse.success("OBD invalid call records received successfully");
    }
}
