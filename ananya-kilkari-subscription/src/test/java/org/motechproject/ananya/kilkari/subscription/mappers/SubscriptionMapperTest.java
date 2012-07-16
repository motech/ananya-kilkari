package org.motechproject.ananya.kilkari.subscription.mappers;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static junit.framework.Assert.assertEquals;

public class SubscriptionMapperTest {

    @Test
    public void shouldMapASubscriptionToProcessSubscriptionRequest() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        Channel channel = Channel.IVR;

        ProcessSubscriptionRequest processSubscriptionRequest = SubscriptionMapper.mapFrom(subscription, channel);

        assertEquals(subscription.getMsisdn(), processSubscriptionRequest.getMsisdn());
        assertEquals(subscription.getPack(), processSubscriptionRequest.getPack());
        assertEquals(subscription.getSubscriptionId(), processSubscriptionRequest.getSubscriptionId());
        assertEquals(channel, processSubscriptionRequest.getChannel());
    }
}
