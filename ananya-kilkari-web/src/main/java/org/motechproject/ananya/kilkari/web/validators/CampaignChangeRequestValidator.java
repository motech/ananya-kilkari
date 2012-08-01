package org.motechproject.ananya.kilkari.web.validators;

import org.motechproject.ananya.kilkari.request.CampaignChangeRequest;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.ValidationUtils;
import org.springframework.stereotype.Component;

@Component
public class CampaignChangeRequestValidator {

    public Errors validate(CampaignChangeRequest campaignChangeRequest) {
        Errors errors = new Errors();
        validateReason(campaignChangeRequest.getReason(), errors);

        return errors;
    }

    private void validateReason(String reason, Errors errors) {
        if(!ValidationUtils.assertCampaignChangeReason(reason))
            errors.add("Invalid reason %s", reason);
    }
}
