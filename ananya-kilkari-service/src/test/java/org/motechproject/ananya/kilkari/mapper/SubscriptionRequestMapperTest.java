package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.builder.ChangeSubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.request.ChangeSubscriptionWebRequest;
import org.motechproject.ananya.kilkari.request.LocationRequest;
import org.motechproject.ananya.kilkari.request.SubscriberWebRequest;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.ChangeSubscriptionType;
import org.motechproject.ananya.kilkari.subscription.service.request.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SubscriptionRequestMapperTest {
    @Test
    public void shouldMapFromWebRequestToSubscriptionRequest() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withWeek("2").build();

        SubscriptionRequest subscriptionDomainRequest = SubscriptionRequestMapper.mapToSubscriptionRequest(subscriptionWebRequest);

        assertEquals(subscriptionWebRequest.getPack(), subscriptionDomainRequest.getPack().name());
        assertEquals(subscriptionWebRequest.getCreatedAt(), subscriptionDomainRequest.getCreationDate());
        assertEquals(null, subscriptionDomainRequest.getReason());
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

    @Test
    public void shouldMapToASubscriptionUpdateRequest() {
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        String subscriptionId = "subscriptionId";
        String name = "Name";
        String beneficiaryAge = "23";
        final String district = "district";
        final String block = "block";
        final String panchayat = "panchayat";
        String contactCenter = "CONTACT_CENTER";
        DateTime createdAtTime = DateTime.now();
        subscriberWebRequest.setBeneficiaryName(name);
        subscriberWebRequest.setBeneficiaryAge(beneficiaryAge);
        LocationRequest locationRequest = new LocationRequest(){{
            setDistrict(district);
            setBlock(block);
            setPanchayat(panchayat);
        }};
        subscriberWebRequest.setLocation(locationRequest);
        subscriberWebRequest.setChannel(contactCenter);
        subscriberWebRequest.setCreatedAt(createdAtTime);

        SubscriberRequest subscriberRequest = SubscriptionRequestMapper.mapToSubscriberRequest(subscriberWebRequest, subscriptionId);

        assertEquals(name, subscriberRequest.getBeneficiaryName());
        assertEquals(Integer.valueOf(beneficiaryAge), subscriberRequest.getBeneficiaryAge());
        assertEquals(new Location(district, block, panchayat), subscriberRequest.getLocation());
        assertEquals(subscriptionId, subscriberRequest.getSubscriptionId());
    }

    @Test
    public void allFieldsAreOptionalExceptSubscriptionId() {
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        String subscriptionId = "subscriptionId";

        SubscriberRequest subscriberRequest = SubscriptionRequestMapper.mapToSubscriberRequest(subscriberWebRequest, subscriptionId);

        assertNull(subscriberRequest.getBeneficiaryName());
        assertNull(subscriberRequest.getBeneficiaryAge());
        assertEquals(Location.NULL, subscriberRequest.getLocation());
        assertEquals(subscriptionId, subscriberRequest.getSubscriptionId());
    }

    @Test
    public void shouldMapToChangeSubscriptionRequest() {
        ChangeSubscriptionWebRequest webRequest = new ChangeSubscriptionWebRequestBuilder().withDefaults()
                .withChangeType("change_schedule")
                .withEDD("25-11-2013")
                .withReason("reason for change subscription")
                .build();

        String subscriptionId = "subscriptionId";
        ChangeSubscriptionRequest changeSubscriptionRequest = SubscriptionRequestMapper.mapToChangeSubscriptionRequest(webRequest, subscriptionId);

        assertEquals(subscriptionId, changeSubscriptionRequest.getSubscriptionId());
        assertEquals(webRequest.getPack(), changeSubscriptionRequest.getPack().name());
        assertEquals(webRequest.getChannel(), changeSubscriptionRequest.getChannel().name());
        assertEquals(new DateTime(2013, 11, 25, 0, 0, 0), changeSubscriptionRequest.getExpectedDateOfDelivery());
        assertNull(changeSubscriptionRequest.getDateOfBirth());
        assertEquals(webRequest.getCreatedAt(), changeSubscriptionRequest.getCreatedAt());
        assertEquals(ChangeSubscriptionType.CHANGE_SCHEDULE, changeSubscriptionRequest.getChangeType());
        assertEquals("reason for change subscription", changeSubscriptionRequest.getReason());
    }

    @Test
    public void shouldSetLocationToNullIfNotProvidedWhileCreateSubscription(){
        SubscriptionWebRequest webRequest = new SubscriptionWebRequestBuilder().withDefaults().withLocation(null).build();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestMapper.mapToSubscriptionRequest(webRequest);

        assertEquals(Location.NULL, subscriptionRequest.getLocation());

    }

    @Test
    public void shouldSetLocationToNullIfNotProvidedWhileUpdatingSubscriberDetails(){
        SubscriberRequest subscriberRequest = SubscriptionRequestMapper.mapToSubscriberRequest(new SubscriberWebRequest(), "subscriptionId");

        assertEquals(Location.NULL, subscriberRequest.getLocation());
    }
}
