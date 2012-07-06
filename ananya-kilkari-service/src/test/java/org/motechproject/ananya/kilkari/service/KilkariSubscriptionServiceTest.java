package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariSubscriptionServiceTest {

    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private SubscriptionPublisher subscriptionPublisher;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Before
    public void setup() {
        initMocks(this);
        kilkariSubscriptionService = new KilkariSubscriptionService(subscriptionPublisher, subscriptionService, kilkariMessageCampaignService);
    }

    @Test
    public void shouldCreateSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        kilkariSubscriptionService.createSubscription(subscriptionRequest);
        verify(subscriptionPublisher).createSubscription(subscriptionRequest);
    }

    @Test
    public void shouldGetSubscriptionsFor() {
        String msisdn = "998800";
        kilkariSubscriptionService.getSubscriptionsFor(msisdn);
        verify(subscriptionService).findByMsisdn(msisdn);
    }

    @Test
    public void shouldProcessSubscriptionRequest() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        String subscriptionId = "111222333";
        String pack = "SEVEN_MONTHS";
        subscriptionRequest.setCreatedAt(DateTime.now());
        subscriptionRequest.setPack(pack);
        when(subscriptionService.createSubscription(subscriptionRequest)).thenReturn(subscriptionId);
        
        kilkariSubscriptionService.processSubscriptionRequest(subscriptionRequest);

        ArgumentCaptor<KilkariMessageCampaignRequest> captor = ArgumentCaptor.forClass(KilkariMessageCampaignRequest.class);
        verify(kilkariMessageCampaignService).start(captor.capture());
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = captor.getValue();
        assertEquals(subscriptionId,kilkariMessageCampaignRequest.getExternalId());
        assertEquals(pack,kilkariMessageCampaignRequest.getSubscriptionPack());
        assertEquals(subscriptionRequest.getCreatedAt(),kilkariMessageCampaignRequest.getSubscriptionCreationDate());
    }

    @Test
    public void shouldPublishSubscriberCareRequestEvent() {
        String msisdn = "1234566789";
        String reason = SubscriberCareReasons.CHANGE_PACK.name();
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(msisdn, reason);

        kilkariSubscriptionService.processSubscriberCareRequest(subscriberCareRequest);

        ArgumentCaptor<SubscriberCareRequest> subscriberCareRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareRequest.class);
        verify(subscriptionPublisher).processSubscriberCareRequest(subscriberCareRequestArgumentCaptor.capture());
        SubscriberCareRequest careRequest = subscriberCareRequestArgumentCaptor.getValue();

        Assert.assertEquals(msisdn, careRequest.getMsisdn());
        Assert.assertEquals(reason, careRequest.getReason());
    }

    @Test
    public void shouldNotScheduleMessageCampaignIfDuplicateSubscriptionIsRequested() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults()
                .withMsisdn("123").withPack(SubscriptionPack.FIFTEEN_MONTHS.toString()).build();

        doThrow(new DuplicateSubscriptionException("")).when(subscriptionService).createSubscription(subscriptionRequest);

        kilkariSubscriptionService.processSubscriptionRequest(subscriptionRequest);

        verify(kilkariMessageCampaignService, never()).start(any(KilkariMessageCampaignRequest.class));
    }
}
