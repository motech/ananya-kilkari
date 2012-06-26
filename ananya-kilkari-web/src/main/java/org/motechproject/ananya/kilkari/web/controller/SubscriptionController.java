package org.motechproject.ananya.kilkari.web.controller;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.controller.requests.CallbackRequest;
import org.motechproject.ananya.kilkari.web.domain.CallBackAction;
import org.motechproject.ananya.kilkari.web.domain.CallBackStatus;
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
    public BaseResponse createSubscription(@RequestParam String msisdn, @RequestParam String pack, @RequestParam String channel) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(msisdn, pack, channel);
        subscriptionPublisher.createSubscription(subscriptionRequest);
        return new BaseResponse("SUCCESS", "Subscription request submitted successfully");
    }

    @RequestMapping(value = "/subscription/{subscriptionId}", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResponse activateSubscriptionCallback(@RequestBody CallbackRequest callbackRequest, @PathVariable String subscriptionId) {
        logger.info(String.format("Processing request: %s", callbackRequest.toString()));
        if(callbackRequest.getStatus() == CallBackStatus.SUCCESS && callbackRequest.getAction() == CallBackAction.ACT) {
            logger.info(String.format("Changing subscription status to ACTIVE for msisdn: %s, subscriptionId: %s", callbackRequest.getMsisdn(), subscriptionId));
            subscriptionService.updateSubscriptionStatus(subscriptionId , SubscriptionStatus.ACTIVE);
        }
        return new BaseResponse("SUCCESS", "Callback request processed successfully");
    }

    @RequestMapping(value = "/subscriber", method = RequestMethod.GET)
    @ResponseBody
    public SubscriberResponse getSubscriptions(@RequestParam String msisdn, @RequestParam String channel) {
        SubscriberResponse subscriberResponse = new SubscriberResponse();

        List<Subscription> subscriptions;
        try {
            subscriptions = subscriptionService.findByMsisdn(msisdn);
        } catch (ValidationException e) {
            return subscriberResponse.forInvalidMsisdn();
        }

        if (subscriptions != null) {
            for (Subscription subscription : subscriptions)
                subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription));
        }

        return subscriberResponse;
    }

}
