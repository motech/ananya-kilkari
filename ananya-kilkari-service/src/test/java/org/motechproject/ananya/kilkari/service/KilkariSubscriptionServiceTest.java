package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.handlers.callback.SubscriptionStateHandler;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariSubscriptionServiceTest {

    class DummySubscriptionStateHandler implements SubscriptionStateHandler {
        @Override
        public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        }
    }

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
        kilkariSubscriptionService = new KilkariSubscriptionService(subscriptionPublisher, subscriptionService, kilkariMessageCampaignService, subscriptionStateHandlerFactory);
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
        kilkariSubscriptionService.findByMsisdn(msisdn);
        verify(subscriptionService).findByMsisdn(msisdn);
    }

    @Test
    public void shouldProcessSubscriptionRequest() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        SubscriptionPack pack = SubscriptionPack.FIFTEEN_MONTHS;
        subscriptionRequest.setCreatedAt(DateTime.now());
        subscriptionRequest.setPack(pack.name());
        Subscription subscription = new Subscription("msisdn", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());

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
                .withMsisdn("123").withPack(SubscriptionPack.FIFTEEN_MONTHS.toString()).build();

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

    @Test
    public void shouldMarkRenewalRequestAsInvalidWhenSubscriptionStateIsOtherThanActiveOrSuspended() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        final Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatus.NEW);
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        final List<String> errors = kilkariSubscriptionService.validate(callbackRequestWrapper);

        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Cannot renew. Subscription in NEW status", errors.get(0));
    }

    @Test
    public void shouldMarkRenewalDeactivationRequestAsInvalidWhenSubscriptionStateIsOtherThanSuspended() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.DCT.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        final Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        final List<String> errors = kilkariSubscriptionService.validate(callbackRequestWrapper);

        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Cannot deactivate on renewal. Subscription in ACTIVE status", errors.get(0));
    }

    @Test
    public void shouldMarkActivationRequestAsInvalidWhenSubscriptionStateIsOtherThanPendingActivation() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        final Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        final List<String> errors = kilkariSubscriptionService.validate(callbackRequestWrapper);

        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Cannot activate. Subscription in ACTIVE status", errors.get(0));
    }

    @Test
    public void shouldMarkRenewalRequestAsValidWhenSubscriptionStateIsActive() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        final Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        final List<String> errors = kilkariSubscriptionService.validate(callbackRequestWrapper);

        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void shouldMarkRenewalRequestAsValidWhenSubscriptionStateIsSuspended() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        final Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        final List<String> errors = kilkariSubscriptionService.validate(callbackRequestWrapper);

        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void shouldReturnErrorWhenSubscriptionRequestActionStateCombinationIsInvalid() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.ERROR.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        final Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(null);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        final List<String> errors = kilkariSubscriptionService.validate(callbackRequestWrapper);

        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Invalid status ERROR for action REN", errors.get(0));
    }
}
