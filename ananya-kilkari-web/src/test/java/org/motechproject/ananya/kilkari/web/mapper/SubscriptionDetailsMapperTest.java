package org.motechproject.ananya.kilkari.web.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionDetailsMapperTest {
    private SubscriptionDetailsMapper subscriptionDetailsMapper;
    @Mock
    private InboxService inboxService;

    @Before
    public void setup() {
        initMocks(this);
        subscriptionDetailsMapper = new SubscriptionDetailsMapper(inboxService);
    }

    @Test
    public void shouldMapFromSubscriptionToSubscriptionDetails() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS).withCreationDate(DateTime.now()).build();

        SubscriptionDetails subscriptionDetails = subscriptionDetailsMapper.mapFrom(subscription);

        assertEquals(subscription.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(subscription.getPack().name(), subscriptionDetails.getPack());
        assertEquals(subscription.getStatus().name(), subscriptionDetails.getStatus());
    }

    @Test
    public void shouldIncludeLastCampaignIdInTheSubscriptionDetails() {
        String messageId = "week3";
        Subscription subscription = new SubscriptionBuilder().withDefaults().withMsisdn("9988776655").withPack(SubscriptionPack.FIFTEEN_MONTHS).withCreationDate(DateTime.now().minusWeeks(3)).build();
        when(inboxService.getMessageFor(subscription.getSubscriptionId())).thenReturn(messageId);

        SubscriptionDetails subscriptionDetails = subscriptionDetailsMapper.mapFrom(subscription);

        assertEquals(messageId, subscriptionDetails.getLastCampaignId());
    }
}
