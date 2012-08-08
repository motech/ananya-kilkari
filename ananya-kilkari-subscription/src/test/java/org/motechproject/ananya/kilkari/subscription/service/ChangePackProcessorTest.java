package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionChangePackRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangePackRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangePackProcessorTest {

    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ReportingService reportingService;
    @Mock
    private SubscriptionValidator subscriptionValidator;


    @Test
    public void shouldChangeThePackOfAnSubscription() {
        ChangePackProcessor changePackProcessor = new ChangePackProcessor(subscriptionService, subscriptionValidator, reportingService);
        DateTime dateOfBirth = DateTime.now();

        Subscription existingSubscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = existingSubscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(existingSubscription);


        ChangePackRequest changePackRequest = new ChangePackRequest(existingSubscription.getMsisdn(), subscriptionId, SubscriptionPack.CHOTI_KILKARI, Channel.CALL_CENTER, DateTime.now().plusWeeks(20), null, dateOfBirth);

        Subscription newSubscription = new SubscriptionBuilder().withDefaults().withPack(changePackRequest.getPack()).build();
        when(subscriptionService.createSubscription(any(SubscriptionRequest.class), eq(Channel.CALL_CENTER))).thenReturn(newSubscription);

        changePackProcessor.process(changePackRequest);

        InOrder order = inOrder(subscriptionService, reportingService, subscriptionValidator);
        order.verify(subscriptionValidator).validateSubscriptionExists(subscriptionId);

        ArgumentCaptor<DeactivationRequest> deactivationRequestArgumentCaptor = ArgumentCaptor.forClass(DeactivationRequest.class);
        order.verify(subscriptionService).requestDeactivation(deactivationRequestArgumentCaptor.capture());
        validateDeactivationRequest(deactivationRequestArgumentCaptor.getValue(), existingSubscription);

        ArgumentCaptor<SubscriptionRequest> createSubscriptionCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        order.verify(subscriptionService).createSubscription(createSubscriptionCaptor.capture(), eq(Channel.CALL_CENTER));
        validateSubscriptionCreationRequest(createSubscriptionCaptor.getValue(), changePackRequest);


        ArgumentCaptor<SubscriptionChangePackRequest> reportRequestCaptor = ArgumentCaptor.forClass(SubscriptionChangePackRequest.class);
        order.verify(reportingService).reportChangePack(reportRequestCaptor.capture());
        SubscriptionChangePackRequest reportRequest = reportRequestCaptor.getValue();
        validateReportsRequest(dateOfBirth, existingSubscription, newSubscription, reportRequest);
    }

    private void validateReportsRequest(DateTime dateOfBirth, Subscription existingSubscription, Subscription newSubscription, SubscriptionChangePackRequest reportRequest) {
        assertEquals(existingSubscription.getMsisdn(), reportRequest.getMsisdn().toString());
        assertEquals(newSubscription.getSubscriptionId(), reportRequest.getSubscriptionId());
        assertEquals(newSubscription.getPack().name(), reportRequest.getPack());
        assertEquals(Channel.CALL_CENTER.name(), reportRequest.getChannel());
        assertEquals(newSubscription.getStatus().name(), reportRequest.getSubscriptionStatus());
        assertEquals(dateOfBirth, reportRequest.getDateOfBirth());
        assertEquals(newSubscription.getStartDate(), reportRequest.getStartDate());
        assertNull(reportRequest.getExpectedDateOfDelivery());
    }

    private void validateSubscriptionCreationRequest(SubscriptionRequest subscriptionRequest, ChangePackRequest changePackRequest) {
        assertEquals(changePackRequest.getDateOfBirth(), subscriptionRequest.getSubscriber().getDateOfBirth());
        assertEquals(changePackRequest.getExpectedDateOfDelivery(), subscriptionRequest.getSubscriber().getExpectedDateOfDelivery());
    }

    private void validateDeactivationRequest(DeactivationRequest deactivationRequest, Subscription existingSubscription) {

        assertEquals(existingSubscription.getSubscriptionId(), deactivationRequest.getSubscriptionId());
        assertEquals(Channel.CALL_CENTER, deactivationRequest.getChannel());
        assertNotNull(deactivationRequest.getCreatedAt());
    }

}
