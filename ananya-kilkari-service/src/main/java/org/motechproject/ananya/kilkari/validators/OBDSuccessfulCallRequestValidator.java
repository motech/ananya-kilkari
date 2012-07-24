package org.motechproject.ananya.kilkari.validators;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.obd.domain.CampaignCode;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.ValidationUtils;
import org.motechproject.common.domain.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OBDSuccessfulCallRequestValidator {

    private SubscriptionService subscriptionService;

    @Autowired
    public OBDSuccessfulCallRequestValidator(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public Errors validate(OBDSuccessfulCallRequestWrapper obdRequestWrapper) {
        Errors errors = new Errors();
        OBDSuccessfulCallRequest successfulCallRequest = obdRequestWrapper.getSuccessfulCallRequest();
        validateMsisdn(successfulCallRequest.getMsisdn(), errors);
        validateServiceOption(successfulCallRequest.getServiceOption(), errors);
        validateCampaignId(successfulCallRequest.getCampaignId(), errors);
        validateDateFormat(successfulCallRequest.getCallDetailRecord(), errors);
        validateSubscription(obdRequestWrapper.getSubscriptionId(), errors);

        return errors;
    }

    private void validateDateFormat(CallDetailRecord callDetailRecord, Errors errors) {
        boolean formatInvalid = false;
        if (!ValidationUtils.assertDateTimeFormat(callDetailRecord.getStartTime())) {
            errors.add(String.format("Invalid start time format %s", callDetailRecord.getStartTime()));
            formatInvalid = true;
        }
        if (!ValidationUtils.assertDateTimeFormat(callDetailRecord.getEndTime())) {
            errors.add(String.format("Invalid end time format %s", callDetailRecord.getEndTime()));
            formatInvalid = true;
        }
        if (!formatInvalid && !ValidationUtils.assertDateBefore(parseDateTime(callDetailRecord.getStartTime()), parseDateTime(callDetailRecord.getEndTime())))
            errors.add(String.format("Start DateTime[%s] should not be greater than End DateTime[%s]", callDetailRecord.getStartTime(), callDetailRecord.getEndTime()));
    }

    private DateTime parseDateTime(String time) {
        return DateTimeFormat.forPattern("dd-MM-yyyy HH-mm-ss").parseDateTime(time);
    }

    private void validateCampaignId(String campaignId, Errors errors) {
        if (StringUtils.isEmpty(campaignId))
            errors.add(String.format("Invalid campaign id %s", campaignId));
        else {
            String campaignIdRegExPattern = "^([A-Z]*)([0-9]{1,2})$";
            Pattern pattern = Pattern.compile(campaignIdRegExPattern);
            Matcher matcher = pattern.matcher(campaignId);
            if (!matcher.find() || !CampaignCode.isValid(matcher.group(1)))
                errors.add(String.format("Invalid campaign id %s", campaignId));
        }
    }

    private void validateSubscription(String subscriptionId, Errors errors) {
        if (subscriptionService.findBySubscriptionId(subscriptionId) == null)
            errors.add(String.format("Invalid subscription id %s", subscriptionId));
    }

    private void validateServiceOption(String serviceOption, Errors errors) {
        if (!StringUtils.isEmpty(serviceOption) && !ServiceOption.isValid(serviceOption))
            errors.add(String.format("Invalid service option %s", serviceOption));
    }

    private void validateMsisdn(String msisdn, Errors errors) {
        if (PhoneNumber.isNotValid(msisdn))
            errors.add(String.format("Invalid msisdn %s", msisdn));
    }
}
