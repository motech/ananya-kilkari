package org.motechproject.ananya.kilkari.handlers.callback.subscription;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivationFailedHandler implements  SubscriptionStateHandler {
    private SubscriptionService subscriptionService;

    @Autowired
    public ActivationFailedHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.activationFailed(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getReason(), callbackRequestWrapper.getOperator(),callbackRequestWrapper.getMode());
    }

	public void performForSMReq(CallbackRequestWrapper callbackRequestWrapper) {
		String msisdn = callbackRequestWrapper.getMsisdn();
		SubscriptionPack pack = callbackRequestWrapper.getPack();
	    subscriptionService.activationFailedForSM(msisdn, pack, callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getReason(), callbackRequestWrapper.getOperator(),callbackRequestWrapper.getMode());
	}
}
