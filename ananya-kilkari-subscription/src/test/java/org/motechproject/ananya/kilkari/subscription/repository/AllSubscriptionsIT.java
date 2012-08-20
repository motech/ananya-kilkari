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
        DateTime startDate = createdAt.plusWeeks(2);

        Subscription subscription = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, createdAt, DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.setStartDate(startDate);
        allSubscriptions.add(subscription);

        markForDeletion(subscription);
        List<Subscription> allSubscriptionsList = allSubscriptions.getAll();

        assertNotNull(allSubscriptionsList);
        assertEquals(1, allSubscriptionsList.size());
        Subscription subscriptionFromDb = allSubscriptionsList.get(0);
        assertEquals(msisdn, subscriptionFromDb.getMsisdn());
        assertNotNull(subscriptionFromDb.getSubscriptionId());
        assertEquals(SubscriptionPack.CHOTI_KILKARI, subscriptionFromDb.getPack());
        assertEquals(SubscriptionStatus.NEW, subscriptionFromDb.getStatus());
        assertEquals(createdAt.withZone(DateTimeZone.UTC), subscriptionFromDb.getCreationDate());
        assertEquals(startDate.withZone(DateTimeZone.UTC), subscriptionFromDb.getStartDate());
    }

    @Test
    public void shouldQueryForSubscriptionsInDbBasedOnGivenMsisdn() {
        String msisdn = "1234567890";

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), DateTime.now());
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        Subscription subscription3 = new Subscription("2314567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription1.setStatus(SubscriptionStatus.NEW);
        subscription2.setStatus(SubscriptionStatus.NEW);
        subscription3.setStatus(SubscriptionStatus.NEW);


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
        assertTrue(Arrays.asList(new SubscriptionPack[]{SubscriptionPack.CHOTI_KILKARI, SubscriptionPack.BARI_KILKARI}).contains(filteredSubscriptions.get(0).getPack()));
        assertTrue(Arrays.asList(new SubscriptionPack[]{SubscriptionPack.CHOTI_KILKARI, SubscriptionPack.BARI_KILKARI}).contains(filteredSubscriptions.get(1).getPack()));
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

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), DateTime.now());
        subscription1.setStatus(SubscriptionStatus.NEW);

        allSubscriptions.add(subscription1);

        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription2.setStatus(SubscriptionStatus.NEW);

        allSubscriptions.add(subscription2);

        Subscription subscription3 = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), DateTime.now());
        subscription3.setStatus(SubscriptionStatus.COMPLETED);
        allSubscriptions.add(subscription3);

        markForDeletion(subscription1);
        markForDeletion(subscription2);
        markForDeletion(subscription3);

        List<Subscription> allSubscriptionsByMsisdnAndPack = allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.CHOTI_KILKARI);

        assertEquals(2, allSubscriptionsByMsisdnAndPack.size());
        assertEquals(subscription1, allSubscriptionsByMsisdnAndPack.get(0));
        assertEquals(subscription3, allSubscriptionsByMsisdnAndPack.get(1));
    }

    @Test
    public void shouldFindSubscriptionBySubscriptionId() {
        String msisdn = "1234567890";

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), DateTime.now());
        subscription1.setStatus(SubscriptionStatus.NEW);

        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription2.setStatus(SubscriptionStatus.NEW);

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
        String pack = "choti_kilkari";
        String msisdn = "1234567890";

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), DateTime.now());
        subscription1.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(subscription1);

        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), DateTime.now());
        subscription2.setStatus(SubscriptionStatus.COMPLETED);
        allSubscriptions.add(subscription2);

        markForDeletion(subscription1);
        markForDeletion(subscription2);

        Subscription actualSubscription = allSubscriptions.findSubscriptionInProgress(msisdn, SubscriptionPack.from(pack));

        assertEquals(subscription1, actualSubscription);
    }

    @Test
    public void shouldFindSubscriptionsInProgress() {
        String msisdn = "1234567890";

        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), DateTime.now());
        subscription1.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(subscription1);

        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.NANHI_KILKARI, DateTime.now(), DateTime.now());
        subscription2.setStatus(SubscriptionStatus.COMPLETED);
        allSubscriptions.add(subscription2);

        Subscription subscription3 = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription3.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(subscription3);

        markForDeletion(subscription1);
        markForDeletion(subscription2);
        markForDeletion(subscription3);

        List<Subscription> subscriptionInProgress = allSubscriptions.findSubscriptionsInProgress(msisdn);

        assertEquals(2, subscriptionInProgress.size());
        assertTrue(subscriptionInProgress.contains(subscription1));
        assertTrue(subscriptionInProgress.contains(subscription3));
        assertFalse(subscriptionInProgress.contains(subscription2));
    }
}
