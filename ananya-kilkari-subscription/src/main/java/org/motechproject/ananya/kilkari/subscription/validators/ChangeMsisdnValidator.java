package org.motechproject.ananya.kilkari.subscription.validators;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeMsisdnRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ChangeMsisdnValidator {

    private AllSubscriptions allSubscriptions;

    @Autowired
    public ChangeMsisdnValidator(AllSubscriptions allSubscriptions) {
        this.allSubscriptions = allSubscriptions;
    }

    private void raiseExceptionIfThereAreErrors(Errors validationErrors) {
        if (validationErrors.hasErrors()) {
            throw new ValidationException(validationErrors.allMessages());
        }
    }

    public void validate(ChangeMsisdnRequest changeMsisdnRequest) {
        Errors errors = new Errors();

        List<Subscription> allSubscriptionsByMsisdn = allSubscriptions.findSubscriptionsInProgress(changeMsisdnRequest.getOldMsisdn());
        if(allSubscriptionsByMsisdn.isEmpty()) {
            errors.add("Requested Msisdn has no subscriptions");
            raiseExceptionIfThereAreErrors(errors);
        }

        Collection<SubscriptionPack> packs = CollectionUtils.collect(allSubscriptionsByMsisdn, new Transformer() {
            @Override
            public Object transform(Object o) {
                return ((Subscription) o).getPack();
            }
        });

        if (!changeMsisdnRequest.getShouldChangeAllPacks()) {
            if (!packs.containsAll(changeMsisdnRequest.getPacks())) {
                errors.add("Requested Msisdn doesn't actively subscribe to all the packs which have been requested");
            }
        }

        raiseExceptionIfThereAreErrors(errors);
    }

}
