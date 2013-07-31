package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
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
    public void process(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        DeactivationRequest deactivationRequest = new DeactivationRequest(
                obdSuccessfulCallDetailsRequest.getSubscriptionId(),
                obdSuccessfulCallDetailsRequest.getChannel(),
                obdSuccessfulCallDetailsRequest.getCreatedAt(),
                null);
        subscriptionService.requestDeactivation(deactivationRequest);
    }
}
