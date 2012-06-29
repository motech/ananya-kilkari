package org.motechproject.ananya.kilkari.mappers;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.*;

import static org.junit.Assert.assertEquals;

public class SubscriptionMapperTest {
    @Test
    public void shouldReturnSubscriptionFromSubscriptionRequest() {
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(createSubscriptionRequest("msisdn", "twelve_months", "ivr"));
        Subscription subscription = subscriptionMapper.getSubscription();
        assertEquals("msisdn", subscription.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscription.getPack());
        assertEquals(SubscriptionStatus.NEW, subscription.getStatus());
    }

    @Test
    public void shouldReturnSubscriptionActivationRequestFromSubscriptionRequest() {
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(createSubscriptionRequest("msisdn", "twelve_months", "ivr"));
        SubscriptionActivationRequest subscriptionActivationRequest = subscriptionMapper.getSubscriptionActivationRequest();
        Subscription subscription = subscriptionMapper.getSubscription();
        assertEquals("msisdn", subscriptionActivationRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscriptionActivationRequest.getPack());
        assertEquals(Channel.IVR, subscriptionActivationRequest.getChannel());
        assertEquals(subscription.getSubscriptionId(), subscriptionActivationRequest.getSubscriptionId());
    }

    @Test
    public void shouldReturnSubscriptionReportRequestFromSubscriptionRequest() {
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(createSubscriptionRequest("msisdn", "twelve_months", "ivr"));
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = subscriptionMapper.getSubscriptionCreationReportRequest();
        Subscription subscription = subscriptionMapper.getSubscription();
        assertEquals("msisdn", subscriptionCreationReportRequest.getMsisdn());
        assertEquals("twelve_months", subscriptionCreationReportRequest.getPack());
        assertEquals("ivr", subscriptionCreationReportRequest.getChannel());
        assertEquals(subscription.getSubscriptionId(), subscriptionCreationReportRequest.getSubscriptionId());
    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel) {
        return new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(pack).withChannel(channel).build();
    }
}
