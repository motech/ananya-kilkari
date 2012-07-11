package org.motechproject.ananya.kilkari.web.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.OBDRequest;
import org.motechproject.ananya.kilkari.domain.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.contract.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.domain.OBDRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OBDController {
    private SubscriptionService subscriptionService;
    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public OBDController(SubscriptionService subscriptionService, KilkariCampaignService kilkariCampaignService) {
        this.subscriptionService = subscriptionService;
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @RequestMapping(value = "/obd/calldetails/{subscriptionId}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse handleSuccessfulResponse(@RequestBody OBDRequest obdRequest, @PathVariable String subscriptionId){
        List<String> validationErrors = new OBDRequestValidator(subscriptionService).validate(obdRequest, subscriptionId);
        if (!(validationErrors.isEmpty())) {
            return BaseResponse.failure(String.format("OBD Request Invalid: %s", StringUtils.join(validationErrors.toArray(), ",")));
        }

        kilkariCampaignService.processSuccessfulMessageDelivery(new OBDRequestWrapper(obdRequest, subscriptionId, DateTime.now()));
        return new BaseResponse("SUCCESS","OBD call details received successfully");
    }
}
