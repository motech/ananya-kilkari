package org.motechproject.ananya.kilkari.repository;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllSubscriptionsTest extends SubscriptionBaseIT {
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Test
    public void shouldAddASubscriptionIntoDb() {
        String msisdn = "123456";

        Subscription subscription = new Subscription(SubscriptionPack.TWELVE_MONTHS, msisdn);
        allSubscriptions.add(subscription);

        markForDeletion(subscription);
        List<Subscription> allSubscriptionsList = allSubscriptions.getAll();

        assertNotNull(allSubscriptionsList);
        assertEquals(1, allSubscriptionsList.size());
        Subscription subscriptionFromDb = allSubscriptionsList.get(0);
        assertEquals(msisdn, subscriptionFromDb.getMsisdn());
        assertNotNull(subscriptionFromDb.getSubscriptionDate());
        assertNotNull(subscriptionFromDb.getSubscriptionId());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscriptionFromDb.getPack());
        assertEquals(SubscriptionStatus.NEW, subscriptionFromDb.getStatus());
    }
}
