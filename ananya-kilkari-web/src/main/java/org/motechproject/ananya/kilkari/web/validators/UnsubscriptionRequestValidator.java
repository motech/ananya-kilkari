package org.motechproject.ananya.kilkari.web.validators;

import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.common.domain.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UnsubscriptionRequestValidator {

    private final SubscriptionService subscriptionService;

    @Autowired
    public UnsubscriptionRequestValidator(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public List<String> validate(UnsubscriptionRequest unsubscriptionRequest) {
        List<String> errors = new ArrayList<>();
        validateMsisdn(unsubscriptionRequest.getMsisdn(), errors);
        validatePack(unsubscriptionRequest.getPack(), errors);
        validateSubscription(unsubscriptionRequest.getSubscriptionId(), errors);

        return errors;
    }

    private void validateMsisdn(String msisdn, List<String> errors) {
        if (PhoneNumber.isNotValid(msisdn))
            errors.add(String.format("Invalid msisdn %s", msisdn));
    }

    private void validatePack(String pack, List<String> errors) {
        if (!SubscriptionPack.isValid(pack))
            errors.add(String.format("Invalid pack %s", pack));
    }

    private void validateSubscription(String subscriptionId, List<String> errors) {
        if (subscriptionService.findBySubscriptionId(subscriptionId) == null)
            errors.add(String.format("Invalid subscriptionId %s", subscriptionId));
    }
}
