package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.request.ReferredByFlwMsisdnRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SubscriptionForFlwHandler {

    private final static Logger logger = LoggerFactory.getLogger(SubscriptionForFlwHandler.class);

    @Autowired
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Autowired
    public SubscriptionForFlwHandler(KilkariSubscriptionService kilkariSubscriptionService) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_REFFERED_BY_SUBSCRIPTION})
    public void handleSubscriptionForFLW(MotechEvent event) {
        ReferredByFlwMsisdnRequest referredByFlwMsisdnRequest = (ReferredByFlwMsisdnRequest) event.getParameters().get("0");
        logger.info(String.format("Create subscription event for msisdn: %s, pack: %s, channel: %s, referredBy: %s",
                referredByFlwMsisdnRequest.getMsisdn(), referredByFlwMsisdnRequest.getPack(), referredByFlwMsisdnRequest.getChannel(), referredByFlwMsisdnRequest.getReferredBy()));

        kilkariSubscriptionService.subscriptionForReferredByFLWRequest(referredByFlwMsisdnRequest);
    }
}
