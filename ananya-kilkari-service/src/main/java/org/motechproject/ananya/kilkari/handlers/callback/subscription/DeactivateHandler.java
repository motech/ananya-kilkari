package org.motechproject.ananya.kilkari.handlers.callback.subscription;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeactivateHandler implements SubscriptionStateHandler{
    private SubscriptionService subscriptionService;

    @Autowired
    public DeactivateHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.processDeactivation(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(),
                callbackRequestWrapper.getReason(), callbackRequestWrapper.getGraceCount());
    }

	@Override
	public void performForSMReq(CallbackRequestWrapper callbackRequestWrapper) {
		 subscriptionService.processDeactivationForReqSM(callbackRequestWrapper.getMsisdn(), callbackRequestWrapper.getPack(), callbackRequestWrapper.getCreatedAt(),
	                callbackRequestWrapper.getReason(), callbackRequestWrapper.getGraceCount());
	}
}
