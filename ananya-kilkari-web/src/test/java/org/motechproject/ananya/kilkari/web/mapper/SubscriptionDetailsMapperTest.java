package org.motechproject.ananya.kilkari.web.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;
import org.motechproject.ananya.kilkari.subscription.service.response.ISubscription;
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
        ISubscription subscriptionResponse = new SubscriptionBuilder().withDefaults().withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS).withCreationDate(DateTime.now()).build();

        SubscriptionDetails subscriptionDetails = subscriptionDetailsMapper.mapFrom(subscriptionResponse);

        assertEquals(subscriptionResponse.getSubscriptionId(), subscriptionDetails.getSubscriptionId());
        assertEquals(subscriptionResponse.getPack().name(), subscriptionDetails.getPack());
        assertEquals(subscriptionResponse.getStatus().name(), subscriptionDetails.getStatus());
    }

    @Test
    public void shouldIncludeLastCampaignIdInTheSubscriptionDetails() {
        String messageId = "week3";
        ISubscription subscriptionResponse = new SubscriptionBuilder().withDefaults().withMsisdn("9988776655").withPack(SubscriptionPack.FIFTEEN_MONTHS).withCreationDate(DateTime.now().minusWeeks(3)).build();
        when(kilkariInboxService.getMessageFor(subscriptionResponse.getSubscriptionId())).thenReturn(messageId);

        SubscriptionDetails subscriptionDetails = subscriptionDetailsMapper.mapFrom(subscriptionResponse);

        assertEquals(messageId, subscriptionDetails.getLastCampaignId());
    }
}
