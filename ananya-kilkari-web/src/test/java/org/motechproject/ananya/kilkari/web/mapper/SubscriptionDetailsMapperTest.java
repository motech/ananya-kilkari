package org.motechproject.ananya.kilkari.web.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;

import static org.junit.Assert.assertEquals;

public class SubscriptionDetailsMapperTest {
    @Test
    public void shouldMapFromSubscriptionToSubscriptionDetails() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());

        SubscriptionDetails subscriptionDetails = SubscriptionDetailsMapper.mapFrom(subscription);

        assertEquals(subscription.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(subscription.getPack().name(), subscriptionDetails.getPack());
        assertEquals(subscription.getStatus().name(), subscriptionDetails.getStatus());
    }
}
