package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.*;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionWebResponse;
import org.motechproject.ananya.kilkari.web.validators.CallbackRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubscriptionController {

    private KilkariSubscriptionService kilkariSubscriptionService;
    private CallbackRequestValidator callbackRequestValidator;
    private SubscriptionDetailsMapper subscriptionDetailsMapper;

    @Autowired
    public SubscriptionController(KilkariSubscriptionService kilkariSubscriptionService,
                                  CallbackRequestValidator callbackRequestValidator,
                                  SubscriptionDetailsMapper subscriptionDetailsMapper) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.callbackRequestValidator = callbackRequestValidator;
        this.subscriptionDetailsMapper = subscriptionDetailsMapper;
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
    public BaseResponse createSubscription(@RequestBody SubscriptionWebRequest subscriptionWebRequest, @RequestParam String channel) {
        subscriptionWebRequest.setChannel(channel);
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
    public SubscriptionWebResponse getSubscriptions(@RequestParam String msisdn, @RequestParam String channel) {
        SubscriptionWebResponse subscriptionWebResponse = new SubscriptionWebResponse();

        List<Subscription> subscriptions = kilkariSubscriptionService.findByMsisdn(msisdn);

        if (subscriptions != null) {
            for (Subscription subscription : subscriptions)
                subscriptionWebResponse.addSubscriptionDetail(subscriptionDetailsMapper.mapFrom(subscription));
        }

        return subscriptionWebResponse;
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.DELETE)
    @ResponseBody
    public BaseResponse removeSubscription(@RequestBody UnSubscriptionWebRequest unSubscriptionWebRequest, @PathVariable String subscriptionId, @RequestParam String channel) {
        unSubscriptionWebRequest.setChannel(channel);
        kilkariSubscriptionService.requestDeactivation(subscriptionId, unSubscriptionWebRequest);
        return BaseResponse.success("Subscription unsubscribed successfully");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}/changecampaign", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse changeCampaign(@RequestBody CampaignChangeRequest campaignChangeRequest, @PathVariable String subscriptionId, @RequestParam String channel) {
        campaignChangeRequest.setChannel(channel);

        kilkariSubscriptionService.processCampaignChange(campaignChangeRequest, subscriptionId);
        return BaseResponse.success("Campaign Change successfully completed");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}/changesubscription", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse changeSubscription(@RequestBody ChangeSubscriptionWebRequest changeSubscriptionWebRequest, @PathVariable String subscriptionId, @RequestParam String channel) {
        changeSubscriptionWebRequest.setChannel(channel);
        kilkariSubscriptionService.changeSubscription(changeSubscriptionWebRequest, subscriptionId);
        return BaseResponse.success("Change Subscription successfully completed");
    }

    @RequestMapping(value = "/subscription/changemsisdn", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse changeMsisdn(@RequestBody ChangeMsisdnWebRequest changeMsisdnWebRequest, @RequestParam String channel) {
        changeMsisdnWebRequest.setChannel(channel);

        kilkariSubscriptionService.changeMsisdn(changeMsisdnWebRequest);
        return BaseResponse.success("Change Msisdn request submitted successfully");
    }

    @RequestMapping(value = "/subscriber/{subscriptionId}", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse updateSubscriberDetails(@RequestBody SubscriberWebRequest subscriberWebRequest, @PathVariable String subscriptionId, @RequestParam String channel) {
        subscriberWebRequest.setChannel(channel);
        kilkariSubscriptionService.updateSubscriberDetails(subscriberWebRequest, subscriptionId);
        return BaseResponse.success("Subscriber Update request submitted successfully");
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }
}