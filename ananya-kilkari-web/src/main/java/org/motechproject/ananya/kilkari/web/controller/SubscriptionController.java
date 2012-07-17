package org.motechproject.ananya.kilkari.web.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionRequestValidator;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.web.validators.CallbackRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubscriptionController {

    private KilkariSubscriptionService kilkariSubscriptionService;
    private SubscriptionRequestValidator subscriptionRequestValidator;
    private CallbackRequestValidator callbackRequestValidator;

    @Autowired
    public SubscriptionController(KilkariSubscriptionService kilkariSubscriptionService, SubscriptionRequestValidator subscriptionRequestValidator, CallbackRequestValidator callbackRequestValidator) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.subscriptionRequestValidator = subscriptionRequestValidator;
        this.callbackRequestValidator = callbackRequestValidator;
    }


    @RequestMapping(value = "/subscription", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse createSubscriptionFromIVR(SubscriptionRequest subscriptionRequest) {
        return createSubscription(subscriptionRequest);
    }

    @RequestMapping(value = "/subscription", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse createSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        subscriptionRequest.validateChannel();

        if (Channel.isCallCenter(subscriptionRequest.getChannel())) {
            subscriptionRequestValidator.validate(subscriptionRequest);
        }

        kilkariSubscriptionService.createSubscription(subscriptionRequest);
        return BaseResponse.success("Subscription request submitted successfully");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse subscriptionCallback(@RequestBody CallbackRequest callbackRequest, @PathVariable String subscriptionId) {
        final CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        List<String> validationErrors = callbackRequestValidator.validate(callbackRequestWrapper);
        if (!(validationErrors.isEmpty())) {
            return BaseResponse.failure(String.format("Callback Request Invalid: %s", StringUtils.join(validationErrors.toArray(), ",")));
        }

        kilkariSubscriptionService.processCallbackRequest(callbackRequestWrapper);

        return BaseResponse.success("Callback request processed successfully");
    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.GET)
    @ResponseBody
    public SubscriberResponse getSubscriptions(@RequestParam String msisdn, @RequestParam String channel) {
        SubscriberResponse subscriberResponse = new SubscriberResponse();

        List<Subscription> subscriptions = kilkariSubscriptionService.findByMsisdn(msisdn);

        if (subscriptions != null) {
            for (Subscription subscription : subscriptions)
                subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription));
        }

        return subscriberResponse;
    }
}

