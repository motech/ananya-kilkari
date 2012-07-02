package org.motechproject.ananya.kilkari.web.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.web.domain.CallbackRequestValidator;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubscriptionController {

    private KilkariSubscriptionService kilkariSubscriptionService;

    private final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    public SubscriptionController(KilkariSubscriptionService kilkariSubscriptionService) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
    }

    @RequestMapping(value = "/subscription", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse createSubscription(SubscriptionRequest subscriptionRequest) {
        if(!Channel.isIVR(subscriptionRequest.getChannel())) {
            subscriptionRequest.validate();
        }

        kilkariSubscriptionService.createSubscription(subscriptionRequest);

        return new BaseResponse("SUCCESS", "Subscription request submitted successfully");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse activateSubscriptionCallback(@RequestBody CallbackRequest callbackRequest, @PathVariable String subscriptionId) {
        List<String> validationErrors = new CallbackRequestValidator().validate(callbackRequest);
        if (!(validationErrors.isEmpty()))
            return new BaseResponse("ERROR", String.format("Callback Request Invalid: %s", StringUtils.join(validationErrors.toArray(), ",")));

        kilkariSubscriptionService.processCallbackRequest(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        return new BaseResponse("SUCCESS", "Callback request processed successfully");
    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.GET)
    @ResponseBody
    public SubscriberResponse getSubscriptions(@RequestParam String msisdn, @RequestParam String channel)
            throws ValidationException {
        SubscriberResponse subscriberResponse = new SubscriberResponse();

        List<Subscription> subscriptions = kilkariSubscriptionService.getSubscriptionsFor(msisdn);

        if (subscriptions != null) {
            for (Subscription subscription : subscriptions)
                subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription));
        }

        return subscriberResponse;
    }

}
