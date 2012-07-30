package org.motechproject.ananya.kilkari.subscription.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class AllSubscriptionsIT extends SpringIntegrationTest {
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Test
    public void shouldAddASubscriptionIntoDb() {
        String msisdn = "1234567890";
        DateTime createdAt = DateTime.now();

        Subscription subscription = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, createdAt);
        allSubscriptions.add(subscription);

        markForDeletion(subscription);
        List<Subscription> allSubscriptionsList = allSubscriptions.getAll();

        assertNotNull(allSubscriptionsList);
        assertEquals(1, allSubscriptionsList.size());
        Subscription subscriptionFromDb = allSubscriptionsList.get(0);
        assertEquals(msisdn, subscriptionFromDb.getMsisdn());
        assertNotNull(subscriptionFromDb.getSubscriptionId());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscriptionFromDb.getPack());
        assertEquals(SubscriptionStatus.NEW, subscriptionFromDb.getStatus());
        assertEquals(createdAt.withZone(DateTimeZone.UTC), subscriptionFromDb.getCreationDate());
    }

    @Test
    public void shouldQueryForSubscriptionsInDbBasedOnGivenMsisdn() {
        String msisdn = "1234567890";

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        Subscription subscription3 = new Subscription("2314567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);
        allSubscriptions.add(subscription3);

        markForDeletion(subscription1);
        markForDeletion(subscription2);
        markForDeletion(subscription3);
        List<Subscription> filteredSubscriptions = allSubscriptions.findByMsisdn(msisdn);

        assertNotNull(filteredSubscriptions);
        assertEquals(2, filteredSubscriptions.size());
        assertEquals(msisdn, filteredSubscriptions.get(0).getMsisdn());
        assertEquals(msisdn, filteredSubscriptions.get(1).getMsisdn());
        assertTrue(Arrays.asList(new SubscriptionPack[]{SubscriptionPack.TWELVE_MONTHS, SubscriptionPack.FIFTEEN_MONTHS}).contains(filteredSubscriptions.get(0).getPack()));
        assertTrue(Arrays.asList(new SubscriptionPack[]{SubscriptionPack.TWELVE_MONTHS, SubscriptionPack.FIFTEEN_MONTHS}).contains(filteredSubscriptions.get(1).getPack()));
    }

    @Test
    public void shouldReturnEmptyListIfTheSubscriptionListIsNull() {
        List<Subscription> filteredSubscriptions = allSubscriptions.findByMsisdn("1001100110");

        assertNotNull(filteredSubscriptions);
        assertThat(filteredSubscriptions, is(Collections.<Subscription>emptyList()));
    }

    @Test
    public void shouldFindSubscriptionByMsisdnAndPack() {
        String msisdn = "1234567890";

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        allSubscriptions.add(subscription1);

        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        allSubscriptions.add(subscription2);

        Subscription subscription3 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        subscription3.setStatus(SubscriptionStatus.COMPLETED);
        allSubscriptions.add(subscription3);

        markForDeletion(subscription1);
        markForDeletion(subscription2);
        markForDeletion(subscription3);

        List<Subscription> allSubscriptionsByMsisdnAndPack = allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.TWELVE_MONTHS);

        assertEquals(2, allSubscriptionsByMsisdnAndPack.size());
        assertEquals(subscription1, allSubscriptionsByMsisdnAndPack.get(0));
        assertEquals(subscription3, allSubscriptionsByMsisdnAndPack.get(1));
    }

    @Test
    public void shouldFindSubscriptionBySubscriptionId() {
        String msisdn = "1234567890";

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);

        markForDeletion(subscription1);
        markForDeletion(subscription2);
        Subscription filteredSubscription = allSubscriptions.findBySubscriptionId(subscription1.getSubscriptionId());

        assertNotNull(filteredSubscription);
        assertEquals(subscription1, filteredSubscription);
    }

    @Test
    public void shouldFindSubscriptionInProgress() {
        String pack = "twelve_months";
        String msisdn1 = "1234567890";
        String msisdn = "91" + msisdn1;

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        subscription1.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(subscription1);

        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        subscription2.setStatus(SubscriptionStatus.COMPLETED);
        allSubscriptions.add(subscription2);

        markForDeletion(subscription1);
        markForDeletion(subscription2);

        Subscription actualSubscription = allSubscriptions.findSubscriptionInProgress(msisdn1, SubscriptionPack.from(pack));

        assertEquals(subscription1, actualSubscription);
    }
}
