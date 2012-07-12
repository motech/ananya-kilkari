package org.motechproject.ananya.kilkari.web.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallbackRequestValidator {

    private List<String> errors;
    private final SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;
    private final SubscriptionService subscriptionService;

    @Autowired
    public CallbackRequestValidator(SubscriptionStateHandlerFactory subscriptionStateHandlerFactory, SubscriptionService subscriptionService) {
        this.subscriptionStateHandlerFactory = subscriptionStateHandlerFactory;
        this.subscriptionService = subscriptionService;
        this.errors = new ArrayList<>();
    }

    public List<String> validate(CallbackRequestWrapper callbackRequestWrapper) {

        final boolean isValidCallbackAction = validateCallbackAction(callbackRequestWrapper.getAction());
        final boolean isValidCallbackStatus = validateCallbackStatus(callbackRequestWrapper.getStatus());
        if (isValidCallbackAction && isValidCallbackStatus) {
            errors.addAll(validateSubscriptionRequest(callbackRequestWrapper));
        }

        validateMsisdn(callbackRequestWrapper.getMsisdn());
        validateOperator(callbackRequestWrapper.getOperator());

        return errors;
    }

    private List<String> validateSubscriptionRequest(CallbackRequestWrapper callbackRequestWrapper) {
        List<String> errors = new ArrayList<>();
        final String requestStatus = callbackRequestWrapper.getStatus();
        final String requestAction = callbackRequestWrapper.getAction();

        if (subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper) == null) {
            errors.add(String.format("Invalid status %s for action %s", requestStatus, requestAction));
        }

        Subscription subscription = subscriptionService.findBySubscriptionId(callbackRequestWrapper.getSubscriptionId());
        if (CallbackAction.REN.name().equals(requestAction)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!(subscriptionStatus.equals(SubscriptionStatus.ACTIVE) || subscriptionStatus.equals(SubscriptionStatus.SUSPENDED)))
                errors.add(String.format("Cannot renew. Subscription in %s status", subscriptionStatus));
        }

        if (CallbackAction.DCT.name().equals(requestAction) && CallbackStatus.BAL_LOW.name().equals(requestStatus)) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.equals(SubscriptionStatus.SUSPENDED))
                errors.add(String.format("Cannot deactivate on renewal. Subscription in %s status", subscriptionStatus));
        }

        if (CallbackAction.ACT.name().equals(requestAction) && (CallbackStatus.SUCCESS.name().equals(requestStatus) || CallbackStatus.BAL_LOW.name().equals(requestStatus))) {
            final SubscriptionStatus subscriptionStatus = subscription.getStatus();
            if (!subscriptionStatus.equals(SubscriptionStatus.PENDING_ACTIVATION))
                errors.add(String.format("Cannot activate. Subscription in %s status", subscriptionStatus));
        }

        return errors;
    }


    private void validateOperator(String operator) {
        if (!Operator.isValid(operator))
            errors.add(String.format("Invalid operator %s", operator));
    }

    private void validateMsisdn(String msisdn) {
        if (!isValidMsisdn(msisdn))
            errors.add(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean validateCallbackAction(String callbackAction) {
        if (!CallbackAction.isValid(callbackAction)) {
            errors.add(String.format("Invalid callbackAction %s", callbackAction));
            return false;
        }
        return true;
    }

    private boolean validateCallbackStatus(String callbackStatus) {
        if (!CallbackStatus.isValid(callbackStatus)) {
            errors.add(String.format("Invalid callbackStatus %s", callbackStatus));
            return false;
        }
        return true;
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }
}
