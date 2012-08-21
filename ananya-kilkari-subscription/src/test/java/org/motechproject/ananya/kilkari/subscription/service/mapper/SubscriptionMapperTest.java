package org.motechproject.ananya.kilkari.subscription.service.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberLocation;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionReportRequest;

import static junit.framework.Assert.assertEquals;

public class SubscriptionMapperTest {

    private Subscription subscription;
    private Channel channel;

    @Before
    public void setUp() throws Exception {
        subscription = new SubscriptionBuilder().withDefaults().withMsisdn("1234567890")
                .withPack(SubscriptionPack.NANHI_KILKARI).withCreationDate(DateTime.now())
                .withStartDate(DateTime.now()).build();
        channel = Channel.IVR;
    }

    @Test
    public void shouldCreateOMSubscriptionRequest() {
        OMSubscriptionRequest omSubscriptionRequest = SubscriptionMapper.createOMSubscriptionRequest(subscription, channel);

        assertEquals(subscription.getMsisdn(), omSubscriptionRequest.getMsisdn());
        assertEquals(subscription.getPack(), omSubscriptionRequest.getPack());
        assertEquals(subscription.getSubscriptionId(), omSubscriptionRequest.getSubscriptionId());
        assertEquals(channel, omSubscriptionRequest.getChannel());
    }

    @Test
    public void shouldCreateReportingRequest() {
        SubscriptionReportRequest request = SubscriptionMapper.createSubscriptionCreationReportRequest(
                subscription, channel, new SubscriptionRequest("msisdn", null, null, Location.NULL, Subscriber.NULL));

        assertEquals(subscription.getMsisdn(), request.getMsisdn().toString());
        assertEquals(subscription.getPack().name(), request.getPack());
        assertEquals(subscription.getSubscriptionId(), request.getSubscriptionId());
        assertEquals(subscription.getStartDate(), request.getStartDate());
        assertEquals(channel.name(), request.getChannel());
    }

    @Test
    public void shouldCreateReportingRequest_WithLocation() {
        subscription = new SubscriptionBuilder().withDefaults().build();
        String panchayat = "panchayat";
        String block = "block";
        String district = "district";
        Location location = new Location(district, block, panchayat);

        SubscriptionReportRequest request = new SubscriptionMapper().createSubscriptionCreationReportRequest(
                subscription, channel, new SubscriptionRequest("msisdn", null, null, location, Subscriber.NULL));

        SubscriberLocation actualLocation = request.getLocation();
        assertEquals(panchayat, actualLocation.getPanchayat());
        assertEquals(block, actualLocation.getBlock());
        assertEquals(district, actualLocation.getDistrict());
    }
}
