package org.motechproject.ananya.kilkari.handlers.callback.subscription;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RenewalSuspensionHandler implements SubscriptionStateHandler {
    private SubscriptionService subscriptionService;

    @Autowired
    public RenewalSuspensionHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.suspendSubscription(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getReason(), callbackRequestWrapper.getGraceCount(),callbackRequestWrapper.getMode());
    }

	@Override
	public void performForSMReq(CallbackRequestWrapper callbackRequestWrapper) {
	       subscriptionService.suspendSubscriptionForSM(callbackRequestWrapper.getMsisdn(), callbackRequestWrapper.getPack(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getReason(), callbackRequestWrapper.getGraceCount(),callbackRequestWrapper.getMode());
	}
}
