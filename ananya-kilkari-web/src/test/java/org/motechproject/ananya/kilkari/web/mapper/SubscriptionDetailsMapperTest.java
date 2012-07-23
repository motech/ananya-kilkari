package org.motechproject.ananya.kilkari.web.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionDetailsMapperTest {
    private SubscriptionDetailsMapper subscriptionDetailsMapper;
    @Mock
    private KilkariInboxService kilkariInboxService;

    @Before
    public void setup() {
        initMocks(this);
        subscriptionDetailsMapper = new SubscriptionDetailsMapper(kilkariInboxService);
    }

    @Test
    public void shouldMapFromSubscriptionToSubscriptionDetails() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());

        SubscriptionDetails subscriptionDetails = subscriptionDetailsMapper.mapFrom(subscription);

        assertEquals(subscription.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(subscription.getPack().name(), subscriptionDetails.getPack());
        assertEquals(subscription.getStatus().name(), subscriptionDetails.getStatus());
    }

    @Test
    public void shouldIncludeLastCampaignIdInTheSubscriptionDetails() {
        String messageId = "week3";
        Subscription subscription = new Subscription("9988776655", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now().minusWeeks(3));
        when(kilkariInboxService.getMessageFor(subscription.getSubscriptionId())).thenReturn(messageId);

        SubscriptionDetails subscriptionDetails = subscriptionDetailsMapper.mapFrom(subscription);

        assertEquals(messageId, subscriptionDetails.getLastCampaignId());
    }
}
