package org.motechproject.ananya.kilkari.web.controller;

import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.web.contract.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelpController {

    private SubscriberCareService subscriberCareService;

    @Autowired
    public HelpController(SubscriberCareService subscriberCareService) {
        this.subscriberCareService = subscriberCareService;
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse createSubscriberCareRequest(@RequestParam String msisdn, @RequestParam String reason, @RequestParam String channel) {
        subscriberCareService.processSubscriberCareRequest(new SubscriberCareRequest(msisdn, reason, channel));
        return BaseResponse.success("Subscriber care request processed successfully");
    }

}
