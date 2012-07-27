package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
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
    public void process(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        DeactivationRequest deactivationRequest = new DeactivationRequest(
                successfulCallRequestWrapper.getSubscriptionId(),
                successfulCallRequestWrapper.getChannel(),
                successfulCallRequestWrapper.getCreatedAt()
        );
        subscriptionService.requestDeactivation(deactivationRequest);
    }
}
