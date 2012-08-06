package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.request.CallDetailsRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.service.SubscriberCareService;
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
    public void process(CallDetailsRequest callDetailsRequest) {
        createSubscriberCareDoc(callDetailsRequest);
    }

    private void createSubscriberCareDoc(CallDetailsRequest callDetailsRequest) {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(callDetailsRequest.getMsisdn(), callDetailsRequest.getServiceOption().name(),
                callDetailsRequest.getChannel().name(), callDetailsRequest.getCreatedAt());
        subscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
    }
}
