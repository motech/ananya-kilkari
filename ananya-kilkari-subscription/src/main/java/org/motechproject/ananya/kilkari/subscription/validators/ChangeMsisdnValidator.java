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

    public void validate(ChangeMsisdnRequest changeMsisdnRequest) {
        if (changeMsisdnRequest.getShouldChangeAllPacks()) {
            List<Subscription> allSubscriptionsByMsisdn = allSubscriptions.findByMsisdn(changeMsisdnRequest.getOldMsisdn());
            validateIfSubscriptionsExists(allSubscriptionsByMsisdn);
            for (Subscription subscription : allSubscriptionsByMsisdn) {
                if (!subscription.isInUpdatableState())
                    throw new ValidationException("Requested Msisdn doesn't have all subscriptions in updatable state");
            }
        } else {
            List<Subscription> updatableSubscriptionsByMsisdn = allSubscriptions.findUpdatableSubscriptions(changeMsisdnRequest.getOldMsisdn());
            validateIfSubscriptionsExists(updatableSubscriptionsByMsisdn);
            Collection<SubscriptionPack> packs = CollectionUtils.collect(updatableSubscriptionsByMsisdn, new Transformer() {
                @Override
                public Object transform(Object o) {
                    return ((Subscription) o).getPack();
                }
            });
            if (!packs.containsAll(changeMsisdnRequest.getPacks()))
                throw new ValidationException("Requested Msisdn doesn't actively subscribe to all the packs which have been requested");
        }
    }

    private void validateIfSubscriptionsExists(List<Subscription> subscriptions) {
        if (subscriptions.isEmpty()) {
            throw new ValidationException("Requested Msisdn has no subscriptions in the updatable state");
        }
    }

}
