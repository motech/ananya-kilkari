package org.motechproject.ananya.kilkari.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.obd.contract.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.domain.CampaignCode;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.common.domain.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CallDeliveryFailureRecordValidator {

    private KilkariSubscriptionService subscriptionService;

    @Autowired
    public CallDeliveryFailureRecordValidator(KilkariSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public List<String> validate(FailedCallReport failedCallReport) {
        List<String> errors = new ArrayList<>();
        validateMsisdn(failedCallReport.getMsisdn(), errors);
        validateCampaignId(failedCallReport.getCampaignId(), errors);
        validateSubscription(failedCallReport.getSubscriptionId(), errors);
        validateStatusCode(failedCallReport.getStatusCode(), errors);
        return errors;
    }

    private void validateStatusCode(String statusCode, List<String> errors) {
        if (!CampaignMessageStatus.isValid(statusCode))
            errors.add(String.format("Invalid status code %s", statusCode));
    }


    private void validateCampaignId(String campaignId, List errors) {
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

    private void validateSubscription(String subscriptionId, List<String> errors) {
        if (subscriptionService.findBySubscriptionId(subscriptionId) == null)
            errors.add(String.format("Invalid subscription id %s", subscriptionId));
    }

    private void validateMsisdn(String msisdn, List<String> errors) {
        if (PhoneNumber.isNotValid(msisdn))
            errors.add(String.format("Invalid msisdn %s", msisdn));
    }
}
