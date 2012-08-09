package org.motechproject.ananya.kilkari.service.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.obd.domain.CampaignCode;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.domain.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CallDeliveryFailureRecordValidator {

    private KilkariSubscriptionService subscriptionService;
    private CampaignMessageService campaignMessageService;

    @Autowired
    public CallDeliveryFailureRecordValidator(KilkariSubscriptionService subscriptionService, CampaignMessageService campaignMessageService) {
        this.subscriptionService = subscriptionService;
        this.campaignMessageService = campaignMessageService;
    }

    public Errors validate(FailedCallReport failedCallReport) {
        Errors errors = new Errors();
        validateMsisdn(failedCallReport.getMsisdn(), errors);
        validateCampaignId(failedCallReport.getCampaignId(), errors);
        validateSubscription(failedCallReport.getSubscriptionId(), errors);
        validateStatusCode(failedCallReport.getStatusCode(), errors);
        return errors;
    }

    private void validateStatusCode(String statusCode, Errors errors) {
        if (campaignMessageService.getCampaignMessageStatusFor(statusCode) == null)
            errors.add(String.format("Invalid status code %s", statusCode));
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

    private void validateMsisdn(String msisdn, Errors errors) {
        if (PhoneNumber.isNotValid(msisdn))
            errors.add(String.format("Invalid msisdn %s", msisdn));
    }
}
