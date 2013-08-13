package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.*;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionBaseWebResponse;
import org.motechproject.ananya.kilkari.web.validators.CallbackRequestValidator;
import org.motechproject.web.context.HttpThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SubscriptionController {

    private KilkariSubscriptionService kilkariSubscriptionService;
    private CallbackRequestValidator callbackRequestValidator;
    private KilkariPropertiesData kilkariPropertiesData;
    private final static Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    public SubscriptionController(KilkariSubscriptionService kilkariSubscriptionService,
                                  CallbackRequestValidator callbackRequestValidator,
                                  KilkariPropertiesData kilkariPropertiesData) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.callbackRequestValidator = callbackRequestValidator;
        this.kilkariPropertiesData = kilkariPropertiesData;
    }


    @RequestMapping(value = "/subscription", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse createSubscriptionForIVR(SubscriptionWebRequest subscriptionWebRequest) {
        subscriptionWebRequest.validateChannel();
        kilkariSubscriptionService.createSubscriptionAsync(subscriptionWebRequest);
        return BaseResponse.success("Subscription request submitted successfully");
    }

    @RequestMapping(value = "/setReferredByFLWMsisdn", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse setReferredByFLWMsisdnForIVR(ReferredByFlwMsisdnRequest referredByFlwMsisdnRequest) {
        referredByFlwMsisdnRequest.validateChannel();   
        kilkariSubscriptionService.subscriptionAsyncForReferredBy(referredByFlwMsisdnRequest);
        return BaseResponse.success("Subscription request submitted successfully");
    }

    @RequestMapping(value = "/subscription", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse createSubscription(@RequestBody SubscriptionWebRequest subscriptionWebRequest, @RequestParam String channel) {
        subscriptionWebRequest.setChannel(channel);
        subscriptionWebRequest.validateChannel();
        subscriptionWebRequest.defaultState(kilkariPropertiesData.getDefaultState());
        kilkariSubscriptionService.createSubscription(subscriptionWebRequest);
        return BaseResponse.success("Subscription request submitted successfully");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse subscriptionCallback(@RequestBody CallbackRequest callbackRequest, @PathVariable String subscriptionId) {
        final CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now(),true);
        Errors validationErrors = callbackRequestValidator.validate(callbackRequestWrapper, false);
        raiseExceptionIfThereAreErrors(validationErrors);

        kilkariSubscriptionService.processCallbackRequest(callbackRequestWrapper);

        return BaseResponse.success("Callback request processed successfully");
    }

	@RequestMapping(value = "/subscription/handleCallBack", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse subscriptionCallbackWithoutId(@RequestBody CallbackRequest callbackRequest) {
        final CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, null, DateTime.now(),false);
        Errors validationErrors = callbackRequestValidator.validate(callbackRequestWrapper, true);
        raiseExceptionIfThereAreErrors(validationErrors);
        HttpThreadContext.set("NOT_INITIATED_BY_MOTECH");
        logger.info("set HttpThreadContext to:"+HttpThreadContext.get());
        kilkariSubscriptionService.processCallbackRequest(callbackRequestWrapper);
        return BaseResponse.success("Callback request processed successfully");
    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.GET)
    @ResponseBody
    public SubscriptionBaseWebResponse getSubscriptions(@RequestParam String msisdn, @RequestParam String channel) {
        validateChannel(channel);
        List<SubscriptionDetailsResponse> subscriptionDetailsList = kilkariSubscriptionService.getSubscriptionDetails(msisdn, Channel.from(channel));
        return SubscriptionDetailsMapper.mapFrom(subscriptionDetailsList, Channel.from(channel));
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.DELETE)
    @ResponseBody
    public BaseResponse removeSubscription(@RequestBody UnSubscriptionWebRequest unSubscriptionWebRequest, @PathVariable String subscriptionId, @RequestParam String channel) {
        unSubscriptionWebRequest.setChannel(channel);
        kilkariSubscriptionService.requestUnsubscription(subscriptionId, unSubscriptionWebRequest);
        return BaseResponse.success("Subscription unsubscribed successfully");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}/changecampaign", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse changeCampaignForInfantDeathAndMiscarriage(@RequestBody CampaignChangeRequest campaignChangeRequest, @PathVariable String subscriptionId, @RequestParam String channel) {
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

    @RequestMapping(value = "/subscriber/changemsisdn", method = RequestMethod.POST)
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
        subscriberWebRequest.defaultState(kilkariPropertiesData.getDefaultState());
        kilkariSubscriptionService.updateSubscriberDetails(subscriberWebRequest, subscriptionId);
        return BaseResponse.success("Subscriber Update request submitted successfully");
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }

    private void validateChannel(String channel) {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChannel(channel);
        raiseExceptionIfThereAreErrors(webRequestValidator.getErrors());
    }
}
