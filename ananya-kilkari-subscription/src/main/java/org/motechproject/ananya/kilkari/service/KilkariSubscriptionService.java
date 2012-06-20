package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KilkariSubscriptionService {
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    public KilkariSubscriptionService(AllSubscriptions allSubscriptions) {
        this.allSubscriptions = allSubscriptions;
    }

    public void createSubscription(String msisdn, SubscriptionPack subscriptionPack) {
        allSubscriptions.add(new Subscription(msisdn, subscriptionPack));
    }

    public List<Subscription> findByMsisdn(String msisdn) {
        return allSubscriptions.findByMsisdn(msisdn);
    }
}
