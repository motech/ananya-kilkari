package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ChangePackProcessorIT extends SpringIntegrationTest {

    @Autowired
    private ChangePackService changePackService;

    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    private SubscriptionService subscriptionService;
    private String msisdn = "1111111111";

    @Before
    @After
    public void tearDown() {
        allSubscriptions.removeAll();
    }

    @Test
    public void shouldChangePackForAnExistingSubscription() {
        Subscription existingSubscription = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(SubscriptionPack.BARI_KILKARI).build();
        allSubscriptions.add(existingSubscription);
        ChangeScheduleRequest changeScheduleRequest = new ChangeScheduleRequest(ChangeType.CHANGE_PACK, msisdn, existingSubscription.getSubscriptionId(), SubscriptionPack.CHOTI_KILKARI, Channel.CALL_CENTER, DateTime.now(), DateTime.now().plusMonths(1), null, "reason");

        changePackService.process(changeScheduleRequest);

        List<Subscription> subscriptions = allSubscriptions.findByMsisdn(msisdn);
        Subscription deactivatedSubscription = subscriptions.get(0);
        assertTrue(deactivatedSubscription.getStatus() == SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED ||
                deactivatedSubscription.getStatus() == SubscriptionStatus.PENDING_DEACTIVATION);
        assertEquals(deactivatedSubscription.getPack(), deactivatedSubscription.getPack());
        Subscription newSubscription = subscriptions.get(1);
        assertEquals(SubscriptionStatus.NEW_EARLY, newSubscription.getStatus());
        assertEquals(changeScheduleRequest.getPack(), newSubscription.getPack());
    }
}
