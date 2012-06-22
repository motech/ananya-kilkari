package org.motechproject.ananya.kilkari.web.controller;

import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.web.services.SubscriptionPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SubscriptionController {

    private SubscriptionService subscriptionService;
    private SubscriptionPublisher subscriptionPublisher;

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
