package org.motechproject.ananya.kilkari.web.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.domain.CallbackAction;
import org.motechproject.ananya.kilkari.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.domain.Operator;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CallbackRequestValidator {

    private List<String> errors;
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Autowired
    public CallbackRequestValidator(KilkariSubscriptionService kilkariSubscriptionService) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.errors = new ArrayList<>();
    }

    public List<String> validate(CallbackRequestWrapper callbackRequestWrapper) {

        final boolean isValidCallbackAction = validateCallbackAction(callbackRequestWrapper.getAction());
        final boolean isValidCallbackStatus = validateCallbackStatus(callbackRequestWrapper.getStatus());
        if (isValidCallbackAction && isValidCallbackStatus) {
            errors.addAll(kilkariSubscriptionService.validate(callbackRequestWrapper));
        }

        validateMsisdn(callbackRequestWrapper.getMsisdn());
        validateOperator(callbackRequestWrapper.getOperator());

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
