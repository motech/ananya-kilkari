package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.HelpWebRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.web.mapper.SubscriberCareDocsResponseMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberCareDocResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
        kilkariSubscriberCareService.createSubscriberCareRequest(new SubscriberCareRequest(msisdn, reason, channel, DateTime.now()));
        return BaseResponse.success("Subscriber care request processed successfully");
    }

/*    @RequestMapping(value = "/help/list", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody
    public SubscriberCareDocResponseList getSubscriberCareDocs(@RequestParam String startTime, @RequestParam String endTime, @RequestParam String channel) {
        HelpWebRequest helpWebRequest = new HelpWebRequest(startTime, endTime, channel);

        Errors validationErrors = helpWebRequest.validate();
        raiseExceptionIfThereAreErrors(validationErrors);

        List<SubscriberCareDoc> subscriberCareDocList = kilkariSubscriberCareService.fetchSubscriberCareDocs(helpWebRequest);
        return SubscriberCareDocsResponseMapper.mapToSubscriberDocsResponseList(subscriberCareDocList);
    }
    */
    
    @RequestMapping(value = "/help/list", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody
    public SubscriberCareDocResponseList getSubscriberCareDocs(@RequestParam String startTime, @RequestParam String endTime, @RequestParam String channel) {
        HelpWebRequest helpWebRequest = new HelpWebRequest(startTime, endTime, channel);

        Errors validationErrors = helpWebRequest.validate();
        raiseExceptionIfThereAreErrors(validationErrors);

        List<SubscriberCareDoc> subscriberCareDocList = kilkariSubscriberCareService.fetchSubscriberCareDocs(helpWebRequest);
        TreeSet<SubscriberCareDoc> subscriberCareTreeSet=new TreeSet<SubscriberCareDoc>(new SubscriberCareDocComparator());
        subscriberCareTreeSet.addAll(subscriberCareDocList);
        List<SubscriberCareDoc> uniqueSubscriberCareDocList=new ArrayList<>(subscriberCareTreeSet);
        return SubscriberCareDocsResponseMapper.mapToSubscriberDocsResponseList(uniqueSubscriberCareDocList);
    }



    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
    
    
}