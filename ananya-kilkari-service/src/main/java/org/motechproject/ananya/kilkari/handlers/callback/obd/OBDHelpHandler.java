package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OBDHelpHandler implements ServiceOptionHandler {

    private SubscriberCareService subscriberCareService;

    @Autowired
    public OBDHelpHandler(SubscriberCareService subscriberCareService) {
        this.subscriberCareService = subscriberCareService;
    }

    @Override
    public void process(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        createSubscriberCareDoc(successfulCallRequestWrapper.getSuccessfulCallRequest());
    }

    private void createSubscriberCareDoc(OBDSuccessfulCallRequest obdRequest) {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(obdRequest.getMsisdn(), obdRequest.getServiceOption(), Channel.IVR.name());
        subscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
    }
}
