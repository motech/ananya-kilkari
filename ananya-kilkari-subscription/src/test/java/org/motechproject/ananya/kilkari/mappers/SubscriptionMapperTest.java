package org.motechproject.ananya.kilkari.mappers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.*;

import static org.junit.Assert.assertEquals;

public class SubscriptionMapperTest {
    @Test
    public void shouldReturnSubscriptionFromSubscriptionRequest() {
        DateTime createdAt = DateTime.now();
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(createSubscriptionRequest("msisdn", "twelve_months", "ivr", "25", null, null, null, null, null, null, createdAt));
        Subscription subscription = subscriptionMapper.getSubscription();
        assertEquals("msisdn", subscription.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscription.getPack());
        assertEquals(SubscriptionStatus.NEW, subscription.getStatus());

        assertEquals(createdAt, subscription.getCreationDate());
    }

    @Test
    public void shouldReturnSubscriptionActivationRequestFromSubscriptionRequest() {
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(createSubscriptionRequest("msisdn", "twelve_months", "ivr", "25", null, null, null, null, null, null, DateTime.now()));
        SubscriptionActivationRequest subscriptionActivationRequest = subscriptionMapper.getSubscriptionActivationRequest();
        Subscription subscription = subscriptionMapper.getSubscription();
        assertEquals("msisdn", subscriptionActivationRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscriptionActivationRequest.getPack());
        assertEquals(Channel.IVR, subscriptionActivationRequest.getChannel());
        assertEquals(subscription.getSubscriptionId(), subscriptionActivationRequest.getSubscriptionId());
    }

    @Test
    public void shouldReturnSubscriptionReportRequestFromSubscriptionRequest() {
        String dob = "21-01-2012";
        String edd = "23-02-2013";
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(SubscriptionRequest.DATE_TIME_FORMAT);
        String name = "name";

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("msisdn", "twelve_months", "ivr", "25", dob, edd, name, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper(subscriptionRequest);

        SubscriptionCreationReportRequest subscriptionCreationReportRequest = subscriptionMapper.getSubscriptionCreationReportRequest();
        Subscription subscription = subscriptionMapper.getSubscription();

        assertEquals("msisdn", subscriptionCreationReportRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS.name(), subscriptionCreationReportRequest.getPack());
        assertEquals(25, subscriptionCreationReportRequest.getAgeOfBeneficiary());
        assertEquals(dateTimeFormatter.parseDateTime(dob), subscriptionCreationReportRequest.getDob());
        assertEquals(dateTimeFormatter.parseDateTime(edd), subscriptionCreationReportRequest.getEdd());
        SubscriberLocation location = subscriptionCreationReportRequest.getLocation();
        assertEquals("mydistrict", location.getDistrict());
        assertEquals("myblock", location.getBlock());
        assertEquals("mypanchayat", location.getPanchayat());
        assertEquals(name, subscriptionCreationReportRequest.getName());
        assertEquals(SubscriptionStatus.NEW.name(), subscriptionCreationReportRequest.getSubscriptionStatus());
        assertEquals(subscriptionMapper.getSubscription().getCreationDate(), subscriptionCreationReportRequest.getCreatedAt());
        assertEquals(subscription.getSubscriptionId(), subscriptionCreationReportRequest.getSubscriptionId());
    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel, String age, String dob, String edd, String name, String district, String block, String panchayat, DateTime createdAt) {
        return new SubscriptionRequestBuilder().withDefaults().withCreatedAt(createdAt).withMsisdn(msisdn).withPack(pack).withChannel(channel).withBeneficiaryAge(age).withDOB(dob).withEDD(edd)
                .withBeneficiaryName(name)
                .withDistrict(district)
                .withBlock(block)
                .withPanchayat(panchayat)
                .build();
    }
}
