package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionChangePackRequest;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangePackServiceTest {

    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ReportingService reportingService;
    @Mock
    private SubscriptionValidator subscriptionValidator;
    ChangePackService changePackService;

    @Before
    public void setup() {
        changePackService = new ChangePackService(subscriptionService, subscriptionValidator, reportingService);
    }

    @Test
    public void shouldChangeThePackOfAnSubscription() {
        DateTime dateOfBirth = DateTime.now();
        String reason = "some reason";

        Subscription existingSubscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = existingSubscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(existingSubscription);


        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, existingSubscription.getMsisdn(), subscriptionId, SubscriptionPack.CHOTI_KILKARI, Channel.CALL_CENTER, DateTime.now().plusWeeks(20), null, dateOfBirth, reason);

        Subscription newSubscription = new SubscriptionBuilder().withDefaults().withPack(changeSubscriptionRequest.getPack()).build();
        when(subscriptionService.createSubscription(any(SubscriptionRequest.class), eq(Channel.CALL_CENTER))).thenReturn(newSubscription);

        changePackService.process(changeSubscriptionRequest);

        InOrder order = inOrder(subscriptionService, reportingService, subscriptionValidator);
        order.verify(subscriptionValidator).validateSubscriptionExists(subscriptionId);

        ArgumentCaptor<DeactivationRequest> deactivationRequestArgumentCaptor = ArgumentCaptor.forClass(DeactivationRequest.class);
        order.verify(subscriptionService).requestDeactivation(deactivationRequestArgumentCaptor.capture());
        validateDeactivationRequest(deactivationRequestArgumentCaptor.getValue(), existingSubscription);

        ArgumentCaptor<SubscriptionRequest> createSubscriptionCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        order.verify(subscriptionService).createSubscription(createSubscriptionCaptor.capture(), eq(Channel.CALL_CENTER));
        validateSubscriptionCreationRequest(createSubscriptionCaptor.getValue(), changeSubscriptionRequest, existingSubscription);


        ArgumentCaptor<SubscriptionChangePackRequest> reportRequestCaptor = ArgumentCaptor.forClass(SubscriptionChangePackRequest.class);
        order.verify(reportingService).reportChangePack(reportRequestCaptor.capture());
        SubscriptionChangePackRequest reportRequest = reportRequestCaptor.getValue();
        validateReportsRequest(dateOfBirth, existingSubscription, newSubscription, reason, reportRequest);
    }

    @Test
    public void shouldGetEddOrDobFromReportingDBIfThePassedValuesAreNull() {
        DateTime dateOfBirth = DateTime.now();
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(subscriptionService.createSubscription(any(SubscriptionRequest.class), eq(Channel.CALL_CENTER))).thenReturn(subscription);
        SubscriberResponse subscriberResponse = new SubscriberResponse(null, null, dateOfBirth, null, null);
        when(reportingService.getSubscriber(subscriptionId)).thenReturn(subscriberResponse);

        changePackService.process(new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, "1234567890", subscriptionId, SubscriptionPack.NANHI_KILKARI, Channel.CALL_CENTER, null, null, null, "reason"));

        verify(reportingService).getSubscriber(subscriptionId);
        ArgumentCaptor<Channel> channelArgumentCaptor = ArgumentCaptor.forClass(Channel.class);
        ArgumentCaptor<SubscriptionRequest> requestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        verify(subscriptionService).createSubscription(requestArgumentCaptor.capture(), channelArgumentCaptor.capture());
        SubscriptionRequest subscriptionRequest = requestArgumentCaptor.getValue();

        assertEquals(subscriberResponse.getExpectedDateOfDelivery(), subscriptionRequest.getSubscriber().getExpectedDateOfDelivery());
        assertEquals(subscriberResponse.getDateOfBirth(), subscriptionRequest.getSubscriber().getDateOfBirth());
    }

    private void validateReportsRequest(DateTime dateOfBirth, Subscription existingSubscription, Subscription newSubscription, String reason, SubscriptionChangePackRequest reportRequest) {
        assertEquals(existingSubscription.getMsisdn(), reportRequest.getMsisdn().toString());
        assertEquals(newSubscription.getSubscriptionId(), reportRequest.getSubscriptionId());
        assertEquals(newSubscription.getPack().name(), reportRequest.getPack());
        assertEquals(Channel.CALL_CENTER.name(), reportRequest.getChannel());
        assertEquals(newSubscription.getStatus().name(), reportRequest.getSubscriptionStatus());
        assertEquals(dateOfBirth, reportRequest.getDateOfBirth());
        assertEquals(newSubscription.getStartDate(), reportRequest.getStartDate());
        assertNull(reportRequest.getExpectedDateOfDelivery());
        assertEquals(reason, reportRequest.getReason());
    }

    private void validateSubscriptionCreationRequest(SubscriptionRequest subscriptionRequest, ChangeSubscriptionRequest changeSubscriptionRequest, Subscription existingSubscription) {
        assertEquals(changeSubscriptionRequest.getDateOfBirth(), subscriptionRequest.getSubscriber().getDateOfBirth());
        assertEquals(changeSubscriptionRequest.getExpectedDateOfDelivery(), subscriptionRequest.getSubscriber().getExpectedDateOfDelivery());
    }

    private void validateDeactivationRequest(DeactivationRequest deactivationRequest, Subscription existingSubscription) {

        assertEquals(existingSubscription.getSubscriptionId(), deactivationRequest.getSubscriptionId());
        assertEquals(Channel.CALL_CENTER, deactivationRequest.getChannel());
        assertNotNull(deactivationRequest.getCreatedAt());
    }

}
