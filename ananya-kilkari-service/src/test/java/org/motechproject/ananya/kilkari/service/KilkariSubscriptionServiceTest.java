package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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
    @Mock
    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;

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
        String msisdn = "1234567890";
        kilkariSubscriptionService.findByMsisdn(msisdn);
        verify(subscriptionService).findByMsisdn(msisdn);
    }

    @Test
    public void shouldProcessSubscriptionRequest() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        SubscriptionPack pack = SubscriptionPack.FIFTEEN_MONTHS;
        subscriptionRequest.setCreatedAt(DateTime.now());
        subscriptionRequest.setPack(pack.name());
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());

        when(subscriptionService.createSubscription(subscriptionRequest)).thenReturn(subscription);

        kilkariSubscriptionService.processSubscriptionRequest(subscriptionRequest);

        ArgumentCaptor<KilkariMessageCampaignRequest> captor = ArgumentCaptor.forClass(KilkariMessageCampaignRequest.class);
        verify(kilkariMessageCampaignService).start(captor.capture());
        KilkariMessageCampaignRequest kilkariMessageCampaignRequest = captor.getValue();
        assertNotNull(kilkariMessageCampaignRequest.getExternalId());
        assertEquals(pack.name(),kilkariMessageCampaignRequest.getSubscriptionPack());

        assertEquals(subscriptionRequest.getCreatedAt().toLocalDate(),kilkariMessageCampaignRequest.getSubscriptionCreationDate().toLocalDate());
    }

    @Test
    public void shouldNotScheduleMessageCampaignIfDuplicateSubscriptionIsRequested() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults()
                .withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS.toString()).build();

        doThrow(new DuplicateSubscriptionException("")).when(subscriptionService).createSubscription(subscriptionRequest);

        kilkariSubscriptionService.processSubscriptionRequest(subscriptionRequest);

        verify(kilkariMessageCampaignService, never()).start(any(KilkariMessageCampaignRequest.class));
    }

    @Test
    public void shouldReturnSubscriptionGivenASubscriptionId() {
        Subscription exptectedSubscription = new Subscription();
        String susbscriptionid = "susbscriptionid";
        when(subscriptionService.findBySubscriptionId(susbscriptionid)).thenReturn(exptectedSubscription);

        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(susbscriptionid);

        assertEquals(exptectedSubscription, subscription);
    }
}
