package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OBDHelpHandler implements ServiceOptionHandler {

    private KilkariSubscriberCareService kilkariSubscriberCareService;

    @Autowired
    public OBDHelpHandler(KilkariSubscriberCareService kilkariSubscriberCareService) {
        this.kilkariSubscriberCareService = kilkariSubscriberCareService;
    }

    @Override
    public void process(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(obdSuccessfulCallDetailsRequest.getMsisdn(), obdSuccessfulCallDetailsRequest.getServiceOption().name(),
                obdSuccessfulCallDetailsRequest.getChannel().name(), obdSuccessfulCallDetailsRequest.getCreatedAt());
        kilkariSubscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
    }
}
