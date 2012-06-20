package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.CampaignSchedule;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.motechproject.ananya.kilkari.web.response.SubscriptionResponse;
import org.motechproject.ananya.kilkari.web.response.UserCampaignSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SubscriptionController {

    private KilkariSubscriptionService kilkariSubscriptionService;

    @Autowired
    public SubscriptionController(KilkariSubscriptionService kilkariSubscriptionService) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
    }

    @RequestMapping(value = "/subscription", method = RequestMethod.GET)
    @ResponseBody
    public SubscriptionResponse getSubscriptions(@RequestParam String msisdn, @RequestParam String channel) {
        List<Subscription> subscriptions = kilkariSubscriptionService.findByMsisdn(msisdn);

        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        for(Subscription subscription : subscriptions)
            subscriptionResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription));

        return subscriptionResponse;
    }
}
