package org.motechproject.ananya.kilkari.mapper;

import org.junit.Test;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;

import static org.junit.Assert.assertEquals;

public class SubscriptionRequestMapperTest {
    @Test
    public void shouldMapFromWebRequestToSubscriptionRequest() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withWeek("2").build();

        SubscriptionRequest subscriptionDomainRequest = new SubscriptionRequestMapper().createSubscriptionDomainRequest(subscriptionWebRequest);

        assertEquals(subscriptionWebRequest.getMsisdn(), subscriptionDomainRequest.getMsisdn());
        assertEquals(subscriptionWebRequest.getPack(), subscriptionDomainRequest.getPack().name());
        assertEquals(subscriptionWebRequest.getCreatedAt(), subscriptionDomainRequest.getCreationDate());
        Location location = subscriptionDomainRequest.getLocation();
        assertEquals(subscriptionWebRequest.getLocation().getBlock(), location.getBlock());
        assertEquals(subscriptionWebRequest.getLocation().getDistrict(), location.getDistrict());
        assertEquals(subscriptionWebRequest.getLocation().getPanchayat(), location.getPanchayat());
        Subscriber subscriber = subscriptionDomainRequest.getSubscriber();
        assertEquals(subscriptionWebRequest.getBeneficiaryAge(), subscriber.getBeneficiaryAge());
        assertEquals(subscriptionWebRequest.getBeneficiaryName(), subscriber.getBeneficiaryName());
        assertEquals(subscriptionWebRequest.getDateOfBirth(), subscriber.getDateOfBirth());
        assertEquals(subscriptionWebRequest.getExpectedDateOfDelivery(), subscriber.getExpectedDateOfDelivery());
        assertEquals(subscriptionWebRequest.getWeek(), subscriber.getWeek().toString());
    }
}
