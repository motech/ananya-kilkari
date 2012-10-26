package org.motechproject.ananya.kilkari.web.validators;

import org.motechproject.ananya.kilkari.domain.CallbackAction;
import org.motechproject.ananya.kilkari.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.obd.domain.PhoneNumber;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
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
        final boolean isValidCallbackAction = validateCallbackAction(callbackRequestWrapper, errors);
        final boolean isValidCallbackStatus = validateCallbackStatus(callbackRequestWrapper, errors);
        if (isValidCallbackAction && isValidCallbackStatus) {
            errors.addAll(validateSubscriptionRequest(callbackRequestWrapper));
        }

        validateMsisdn(callbackRequestWrapper, errors);
        validateOperator(callbackRequestWrapper, errors);

        return errors;
    }

    private Errors validateSubscriptionRequest(CallbackRequestWrapper callbackRequestWrapper) {
        Errors errors = new Errors();
        final String requestStatus = callbackRequestWrapper.getStatus();
        final String requestAction = callbackRequestWrapper.getAction();

        if (subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper) == null) {
            errors.add(String.format("Invalid status %s for action %s for subscription %s", requestStatus, requestAction, callbackRequestWrapper.getSubscriptionId()));
        }

        Subscription subscription = subscriptionService.findBySubscriptionId(callbackRequestWrapper.getSubscriptionId());
        if (subscription == null) {
            errors.add(String.format("No subscription for subscriptionId : %s", callbackRequestWrapper.getSubscriptionId()));
            return errors;
        }


        if (CallbackAction.REN.name().equals(requestAction)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.canRenew()) {
                errors.add(String.format("Cannot renew. Subscription %s in %s status", callbackRequestWrapper.getSubscriptionId(), subscriptionStatus));
            }
        }

        if (CallbackAction.DCT.name().equals(requestAction) && CallbackStatus.BAL_LOW.name().equals(requestStatus)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.canDeactivateOnRenewal())
                errors.add(String.format("Cannot deactivate on renewal. Subscription %s in %s status", callbackRequestWrapper.getSubscriptionId(), subscriptionStatus));
        }

        if (CallbackAction.ACT.name().equals(requestAction) && (CallbackStatus.SUCCESS.name().equals(requestStatus) || CallbackStatus.BAL_LOW.name().equals(requestStatus))) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.canActivate())
                errors.add(String.format("Cannot activate. Subscription %s in %s status", callbackRequestWrapper.getSubscriptionId(), subscriptionStatus));
        }

        return errors;
    }


    private void validateOperator(CallbackRequestWrapper callbackRequestWrapper, Errors errors) {
        String operator = callbackRequestWrapper.getOperator();
        if (!Operator.isValid(operator))
            errors.add(String.format("Invalid operator %s for subscription %s", callbackRequestWrapper.getOperator(), callbackRequestWrapper.getSubscriptionId()));
    }

    private void validateMsisdn(CallbackRequestWrapper callbackRequestWrapper, Errors errors) {
        String msisdn = callbackRequestWrapper.getMsisdn();
        if (PhoneNumber.isNotValid(msisdn))
            errors.add(String.format("Invalid msisdn %s for subscription id %s", msisdn, callbackRequestWrapper.getSubscriptionId()));
    }

    private boolean validateCallbackAction(CallbackRequestWrapper callbackRequestWrapper, Errors errors) {
        String callbackAction = callbackRequestWrapper.getAction();
        if (!CallbackAction.isValid(callbackAction)) {
            errors.add(String.format("Invalid callbackAction %s  for subscription %s", callbackAction, callbackRequestWrapper.getSubscriptionId()));
            return false;
        }
        return true;
    }

    private boolean validateCallbackStatus(CallbackRequestWrapper callbackRequestWrapper, Errors errors) {
        String callbackStatus = callbackRequestWrapper.getStatus();
        if (!CallbackStatus.isValid(callbackStatus)) {
            errors.add(String.format("Invalid callbackStatus %s for subscription %s", callbackStatus, callbackRequestWrapper.getSubscriptionId()));
            return false;
        }
        return true;
    }
}
