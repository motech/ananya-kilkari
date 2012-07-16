package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
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
    public void process(OBDRequestWrapper obdRequestWrapper) {
        subscriptionService.requestDeactivation(obdRequestWrapper.getSubscriptionId(), Channel.IVR);
    }
}
