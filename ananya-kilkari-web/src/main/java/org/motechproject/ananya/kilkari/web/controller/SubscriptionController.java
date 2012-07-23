package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionRequestValidator;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.web.validators.CallbackRequestValidator;
import org.motechproject.ananya.kilkari.web.validators.Errors;
import org.motechproject.ananya.kilkari.web.validators.UnsubscriptionRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubscriptionController {

    private KilkariSubscriptionService kilkariSubscriptionService;
    private SubscriptionRequestValidator subscriptionRequestValidator;
    private CallbackRequestValidator callbackRequestValidator;
    private UnsubscriptionRequestValidator unsubscriptionRequestValidator;
    private SubscriptionDetailsMapper subscriptionDetailsMapper;
    @Autowired
    public SubscriptionController(KilkariSubscriptionService kilkariSubscriptionService, SubscriptionRequestValidator subscriptionRequestValidator, CallbackRequestValidator callbackRequestValidator, UnsubscriptionRequestValidator unsubscriptionRequestValidator, SubscriptionDetailsMapper subscriptionDetailsMapper) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.subscriptionRequestValidator = subscriptionRequestValidator;
        this.callbackRequestValidator = callbackRequestValidator;
        this.unsubscriptionRequestValidator = unsubscriptionRequestValidator;
        this.subscriptionDetailsMapper = subscriptionDetailsMapper;
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
        Errors validationErrors = callbackRequestValidator.validate(callbackRequestWrapper);
        raiseExceptionIfThereAreErrors(validationErrors);

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
                subscriberResponse.addSubscriptionDetail(subscriptionDetailsMapper.mapFrom(subscription));
        }

        return subscriberResponse;
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.DELETE)
    @ResponseBody
    public BaseResponse removeSubscription(@RequestBody UnsubscriptionRequest unsubscriptionRequest, @PathVariable String subscriptionId) {
        Errors validationErrors = unsubscriptionRequestValidator.validate(subscriptionId);
        raiseExceptionIfThereAreErrors(validationErrors);

        kilkariSubscriptionService.requestDeactivation(subscriptionId, unsubscriptionRequest);
        return BaseResponse.success("Subscription unsubscribed successfully");
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}
