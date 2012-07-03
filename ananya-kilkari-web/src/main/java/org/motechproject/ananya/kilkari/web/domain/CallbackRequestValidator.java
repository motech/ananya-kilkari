package org.motechproject.ananya.kilkari.web.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.domain.CallbackRequest;

import java.util.ArrayList;
import java.util.List;

public class CallbackRequestValidator {

    private List<String> errors;

    public CallbackRequestValidator() {
        this.errors = new ArrayList<>();
    }

    public List<String> validate(CallbackRequest callbackRequest) {
        validateMsisdn(callbackRequest.getMsisdn());
        validateCallbackAction(callbackRequest.getAction());
        validateCallbackStatus(callbackRequest.getStatus());
        validateOperator(callbackRequest.getOperator());
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

    private void validateCallbackAction(String callbackAction) {
        if (!CallbackAction.isValid(callbackAction))
            errors.add(String.format("Invalid callbackAction %s", callbackAction));
    }

    private void validateCallbackStatus(String callbackStatus) {
        if (!CallbackStatus.isValid(callbackStatus))
            errors.add(String.format("Invalid callbackStatus %s", callbackStatus));
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }
}
