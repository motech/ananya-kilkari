package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.*;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.web.validators.CallbackRequestValidator;
import org.motechproject.ananya.kilkari.web.validators.CampaignChangeRequestValidator;
import org.motechproject.ananya.kilkari.web.validators.UnsubscriptionRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubscriptionController {

    private KilkariSubscriptionService kilkariSubscriptionService;
    private CallbackRequestValidator callbackRequestValidator;
    private UnsubscriptionRequestValidator unsubscriptionRequestValidator;
    private SubscriptionDetailsMapper subscriptionDetailsMapper;
    private CampaignChangeRequestValidator campaignChangeRequestValidator;

    @Autowired
    public SubscriptionController(KilkariSubscriptionService kilkariSubscriptionService,
                                  CallbackRequestValidator callbackRequestValidator,
                                  UnsubscriptionRequestValidator unsubscriptionRequestValidator,
                                  SubscriptionDetailsMapper subscriptionDetailsMapper, CampaignChangeRequestValidator campaignChangeRequestValidator) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.callbackRequestValidator = callbackRequestValidator;
        this.unsubscriptionRequestValidator = unsubscriptionRequestValidator;
        this.subscriptionDetailsMapper = subscriptionDetailsMapper;
        this.campaignChangeRequestValidator = campaignChangeRequestValidator;
    }


    @RequestMapping(value = "/subscription", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse createSubscriptionForIVR(SubscriptionWebRequest subscriptionWebRequest) {
        subscriptionWebRequest.validateChannel();
        kilkariSubscriptionService.createSubscriptionAsync(subscriptionWebRequest);
        return BaseResponse.success("Subscription request submitted successfully");
    }

    @RequestMapping(value = "/subscription", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse createSubscription(@RequestBody SubscriptionWebRequest subscriptionWebRequest) {
        subscriptionWebRequest.validateChannel();
        kilkariSubscriptionService.createSubscription(subscriptionWebRequest);
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

    @RequestMapping(value = "/subscription/changecampaign", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse changeCampaign(@RequestBody CampaignChangeRequest campaignChangeRequest) {
        Errors validationErrors = campaignChangeRequestValidator.validate(campaignChangeRequest);
        raiseExceptionIfThereAreErrors(validationErrors);

        return BaseResponse.success("Campaign Change request submitted successfully");
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}
