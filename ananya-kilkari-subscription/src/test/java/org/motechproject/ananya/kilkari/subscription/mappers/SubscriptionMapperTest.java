package org.motechproject.ananya.kilkari.subscription.mappers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.contract.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Location;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static junit.framework.Assert.assertEquals;

public class SubscriptionMapperTest {

    private Subscription subscription;
    private Channel channel;

    @Before
    public void setUp() throws Exception {
        subscription = new SubscriptionBuilder().withDefaults().withMsisdn("1234567890")
                .withPack(SubscriptionPack.SEVEN_MONTHS).withCreationDate(DateTime.now()).build();
        channel = Channel.IVR;
    }

    @Test
    public void shouldCreateOMSubscriptionRequest() {
        OMSubscriptionRequest omSubscriptionRequest = new SubscriptionMapper().createOMSubscriptionRequest(subscription, channel);

        assertEquals(subscription.getMsisdn(), omSubscriptionRequest.getMsisdn());
        assertEquals(subscription.getPack(), omSubscriptionRequest.getPack());
        assertEquals(subscription.getSubscriptionId(), omSubscriptionRequest.getSubscriptionId());
        assertEquals(channel, omSubscriptionRequest.getChannel());
    }

    @Test
    public void shouldCreateReportingRequest() {
        SubscriptionCreationReportRequest request = new SubscriptionMapper().createSubscriptionCreationReportRequest(subscription, channel);

        assertEquals(subscription.getMsisdn(), request.getMsisdn());
        assertEquals(subscription.getPack().name(), request.getPack());
        assertEquals(subscription.getSubscriptionId(), request.getSubscriptionId());
        assertEquals(channel.name(), request.getChannel());
    }

    @Test
    public void shouldCreateReportingRequest_WithLocation() {
        subscription = new SubscriptionBuilder().withDefaults().withDistrict("district")
                .withPanchayat("panchayat").withBlock("block").build();

        SubscriptionCreationReportRequest request = new SubscriptionMapper().createSubscriptionCreationReportRequest(subscription, channel);

        SubscriberLocation location = request.getLocation();
        assertEquals("panchayat", location.getPanchayat());
        assertEquals("block", location.getBlock());
        assertEquals("district", location.getDistrict());
    }

    @Test
    public void shouldCreateReportingRequest_WithSubscriberDetails() {
        DateTime dob = DateTime.now();
        DateTime edd = DateTime.now().minusDays(10);

        subscription = new SubscriptionBuilder().withDefaults().withBeneficiaryName("name")
                .withBeneficiaryAge(10)
                .withDateOfBirth(dob)
                .withExpectedDateOfDelivery(edd)
                .build();

        SubscriptionCreationReportRequest request = new SubscriptionMapper().createSubscriptionCreationReportRequest(subscription, channel);

        assertEquals("name", request.getName());
        assertEquals(10, request.getAgeOfBeneficiary());
        assertEquals(dob, request.getDob());
        assertEquals(edd, request.getEdd());
    }
}
