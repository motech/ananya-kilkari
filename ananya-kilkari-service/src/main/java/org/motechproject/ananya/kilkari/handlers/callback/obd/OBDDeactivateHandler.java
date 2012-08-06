package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.request.CallDetailsRequest;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OBDDeactivateHandler implements ServiceOptionHandler {
    private SubscriptionService subscriptionService;

    @Autowired
    public OBDDeactivateHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void process(CallDetailsRequest callDetailsRequest) {
        DeactivationRequest deactivationRequest = new DeactivationRequest(
                callDetailsRequest.getSubscriptionId(),
                callDetailsRequest.getChannel(),
                callDetailsRequest.getCreatedAt()
        );
        subscriptionService.requestDeactivation(deactivationRequest);
    }
}
