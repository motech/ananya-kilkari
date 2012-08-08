package org.motechproject.ananya.kilkari.web.validators;

import org.motechproject.ananya.kilkari.domain.CallbackAction;
import org.motechproject.ananya.kilkari.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.domain.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallbackRequestValidator {

    private final SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;
    private final SubscriptionService subscriptionService;

    @Autowired
    public CallbackRequestValidator(SubscriptionStateHandlerFactory subscriptionStateHandlerFactory, SubscriptionService subscriptionService) {
        this.subscriptionStateHandlerFactory = subscriptionStateHandlerFactory;
        this.subscriptionService = subscriptionService;
    }

    public Errors validate(CallbackRequestWrapper callbackRequestWrapper) {
        Errors errors = new Errors();
        final boolean isValidCallbackAction = validateCallbackAction(callbackRequestWrapper.getAction(), errors);
        final boolean isValidCallbackStatus = validateCallbackStatus(callbackRequestWrapper.getStatus(), errors);
        if (isValidCallbackAction && isValidCallbackStatus) {
            errors.addAll(validateSubscriptionRequest(callbackRequestWrapper));
        }

        validateMsisdn(callbackRequestWrapper.getMsisdn(), errors);
        validateOperator(callbackRequestWrapper.getOperator(), errors);

        return errors;
    }

    private Errors validateSubscriptionRequest(CallbackRequestWrapper callbackRequestWrapper) {
        Errors errors = new Errors();
        final String requestStatus = callbackRequestWrapper.getStatus();
        final String requestAction = callbackRequestWrapper.getAction();

        if (subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper) == null) {
            errors.add(String.format("Invalid status %s for action %s", requestStatus, requestAction));
        }

        Subscription subscription = subscriptionService.findBySubscriptionId(callbackRequestWrapper.getSubscriptionId());
        if (CallbackAction.REN.name().equals(requestAction)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if(!subscriptionStatus.canRenew()) {
                errors.add(String.format("Cannot renew. Subscription in %s status", subscriptionStatus));
            }
        }

        if (CallbackAction.DCT.name().equals(requestAction) && CallbackStatus.BAL_LOW.name().equals(requestStatus)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.canDeactivateOnRenewal())
                errors.add(String.format("Cannot deactivate on renewal. Subscription in %s status", subscriptionStatus));
        }

        if (CallbackAction.ACT.name().equals(requestAction) && (CallbackStatus.SUCCESS.name().equals(requestStatus) || CallbackStatus.BAL_LOW.name().equals(requestStatus))) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.canActivate())
                errors.add(String.format("Cannot activate. Subscription in %s status", subscriptionStatus));
        }

        return errors;
    }


    private void validateOperator(String operator, Errors errors) {
        if (!Operator.isValid(operator))
            errors.add(String.format("Invalid operator %s", operator));
    }

    private void validateMsisdn(String msisdn, Errors errors) {
        if (PhoneNumber.isNotValid(msisdn))
            errors.add(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean validateCallbackAction(String callbackAction, Errors errors) {
        if (!CallbackAction.isValid(callbackAction)) {
            errors.add(String.format("Invalid callbackAction %s", callbackAction));
            return false;
        }
        return true;
    }

    private boolean validateCallbackStatus(String callbackStatus, Errors errors) {
        if (!CallbackStatus.isValid(callbackStatus)) {
            errors.add(String.format("Invalid callbackStatus %s", callbackStatus));
            return false;
        }
        return true;
    }
}
