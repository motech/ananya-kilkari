package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.HelpWebRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.web.mapper.SubscriberCareDocsResponseMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberCareDocResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HelpController {

    private KilkariSubscriberCareService kilkariSubscriberCareService;

    @Autowired
    public HelpController(KilkariSubscriberCareService kilkariSubscriberCareService) {
        this.kilkariSubscriberCareService = kilkariSubscriberCareService;
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse createSubscriberCareRequest(@RequestParam String msisdn, @RequestParam String reason, @RequestParam String channel) {
        kilkariSubscriberCareService.processSubscriberCareRequest(msisdn, reason, channel, DateTime.now());
        return BaseResponse.success("Subscriber care request processed successfully");
    }

    @RequestMapping(value = "/help/list", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody
    public SubscriberCareDocResponseList getSubscriberCareDocs(@RequestParam String startDateTime, @RequestParam String endDateTime, @RequestParam String channel) {
        HelpWebRequest helpWebRequest = new HelpWebRequest(startDateTime, endDateTime, channel);
        helpWebRequest.validate();
        List<SubscriberCareDoc> subscriberCareDocList = kilkariSubscriberCareService.fetchSubscriberCareDocs(helpWebRequest);
        return SubscriberCareDocsResponseMapper.mapToSubscriberDocsResponseList(subscriberCareDocList);
    }
}