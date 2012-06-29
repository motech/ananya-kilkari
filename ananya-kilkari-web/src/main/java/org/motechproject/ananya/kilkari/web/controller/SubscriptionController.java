package org.motechproject.ananya.kilkari.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.domain.CallbackAction;
import org.motechproject.ananya.kilkari.web.domain.CallbackRequestValidator;
import org.motechproject.ananya.kilkari.web.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.web.services.SubscriptionPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubscriptionController {

    private SubscriptionService subscriptionService;
    private SubscriptionPublisher subscriptionPublisher;

    Logger logger = Logger.getLogger(SubscriptionController.class);

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, SubscriptionPublisher subscriptionPublisher) {
        this.subscriptionService = subscriptionService;
        this.subscriptionPublisher = subscriptionPublisher;
    }

    @RequestMapping(value = "/subscription", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse createSubscription(SubscriptionRequest subscriptionRequest) {
        subscriptionPublisher.createSubscription(subscriptionRequest);
        return new BaseResponse("SUCCESS", "Subscription request submitted successfully");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse activateSubscriptionCallback(@RequestBody CallbackRequest callbackRequest, @PathVariable String subscriptionId) {
        List<String> validationErrors = new CallbackRequestValidator().validate(callbackRequest);
        if (!(validationErrors.isEmpty()))
            return new BaseResponse("ERROR", String.format("Callback Request Invalid: %s", StringUtils.join(validationErrors.toArray(), ",")));

        subscriptionPublisher.processCallbackRequest(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        return new BaseResponse("SUCCESS", "Callback request processed successfully");
    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.GET)
    @ResponseBody
    public SubscriberResponse getSubscriptions(@RequestParam String msisdn, @RequestParam String channel)
            throws ValidationException {
        SubscriberResponse subscriberResponse = new SubscriberResponse();

        List<Subscription> subscriptions = subscriptionService.findByMsisdn(msisdn);

        if (subscriptions != null) {
            for (Subscription subscription : subscriptions)
                subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription));
        }

        return subscriberResponse;
    }

}
