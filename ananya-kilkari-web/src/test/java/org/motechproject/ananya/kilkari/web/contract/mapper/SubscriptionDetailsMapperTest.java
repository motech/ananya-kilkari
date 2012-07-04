package org.motechproject.ananya.kilkari.web.contract.mapper;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.web.contract.response.SubscriptionDetails;

import static org.junit.Assert.assertEquals;

public class SubscriptionDetailsMapperTest {
    @Test
    public void shouldMapFromSubscriptionToSubscriptionDetails() {
        Subscription subscription = new Subscription("1234", SubscriptionPack.FIFTEEN_MONTHS);

        SubscriptionDetails subscriptionDetails = SubscriptionDetailsMapper.mapFrom(subscription);

        assertEquals(subscription.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(subscription.getPack().name(), subscriptionDetails.getPack());
        assertEquals(subscription.getStatus().name(), subscriptionDetails.getStatus());
    }
}
