package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangeSubscriptionServiceTest {

    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ReportingService reportingService;
    @Mock
    private SubscriptionValidator subscriptionValidator;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ChangeSubscriptionService changeSubscriptionService;

    @Before
    public void setup() {
        changeSubscriptionService = new ChangeSubscriptionService(subscriptionService, subscriptionValidator, reportingService);
    }

    @Test
    public void shouldChangeThePackOfAnSubscription() {
        DateTime dateOfBirth = DateTime.now();
        String reason = "some reason";

        String msisdn = "9876543210";
        SubscriptionPack newPack = SubscriptionPack.CHOTI_KILKARI;
        Subscription existingSubscription = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withStatus(SubscriptionStatus.ACTIVE).withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = existingSubscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(existingSubscription);
        when(subscriptionService.findByMsisdnAndPack(msisdn, newPack)).thenReturn(new ArrayList<Subscription>());
        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, null, subscriptionId, newPack, Channel.CALL_CENTER, DateTime.now().plusWeeks(20), null, dateOfBirth, reason);

        Subscription newSubscription = new SubscriptionBuilder().withDefaults().withPack(changeSubscriptionRequest.getPack()).build();
        when(subscriptionService.createSubscription(any(SubscriptionRequest.class), eq(Channel.CALL_CENTER))).thenReturn(newSubscription);

        changeSubscriptionService.process(changeSubscriptionRequest);

        InOrder order = inOrder(subscriptionService, reportingService, subscriptionValidator);
        order.verify(subscriptionValidator).validateSubscriptionExists(subscriptionId);

        ArgumentCaptor<DeactivationRequest> deactivationRequestArgumentCaptor = ArgumentCaptor.forClass(DeactivationRequest.class);
        order.verify(subscriptionService).requestDeactivation(deactivationRequestArgumentCaptor.capture());
        validateDeactivationRequest(deactivationRequestArgumentCaptor.getValue(), existingSubscription);

        ArgumentCaptor<SubscriptionRequest> createSubscriptionCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        order.verify(subscriptionService).createSubscription(createSubscriptionCaptor.capture(), eq(Channel.CALL_CENTER));
        validateSubscriptionCreationRequest(createSubscriptionCaptor.getValue(), changeSubscriptionRequest, existingSubscription, reason);
    }


    @Test
    public void shouldThrowExceptionIfChangeSubscriptionIsSentForExistingMsisdnAndPack() {
        DateTime dateOfBirth = DateTime.now();
        String reason = "some reason";
        String msisdn = "9876543210";
        SubscriptionPack newPack = SubscriptionPack.CHOTI_KILKARI;
        Subscription existingSubscription = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withStatus(SubscriptionStatus.ACTIVE).withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = existingSubscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(existingSubscription);
        Subscription mockedSubscription = mock(Subscription.class);
        when(subscriptionService.findByMsisdnAndPack(msisdn, newPack)).thenReturn(Arrays.asList(mockedSubscription));
        when(mockedSubscription.isInProgress()).thenReturn(true);

        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, null, subscriptionId, newPack, Channel.CALL_CENTER, DateTime.now().plusWeeks(20), null, dateOfBirth, reason);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Active subscription already exists for %s and %s", msisdn, newPack));

        changeSubscriptionService.process(changeSubscriptionRequest);
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

        changeSubscriptionService.process(new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, "1234567890", subscriptionId, SubscriptionPack.NANHI_KILKARI, Channel.CALL_CENTER, null, null, null, "reason"));

        verify(reportingService).getSubscriber(subscriptionId);
        ArgumentCaptor<Channel> channelArgumentCaptor = ArgumentCaptor.forClass(Channel.class);
        ArgumentCaptor<SubscriptionRequest> requestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        verify(subscriptionService).createSubscription(requestArgumentCaptor.capture(), channelArgumentCaptor.capture());
        SubscriptionRequest subscriptionRequest = requestArgumentCaptor.getValue();

        assertEquals(subscriberResponse.getExpectedDateOfDelivery(), subscriptionRequest.getSubscriber().getExpectedDateOfDelivery());
        assertEquals(subscriberResponse.getDateOfBirth(), subscriptionRequest.getSubscriber().getDateOfBirth());
    }

    private void validateSubscriptionCreationRequest(SubscriptionRequest subscriptionRequest, ChangeSubscriptionRequest changeSubscriptionRequest, Subscription existingSubscription, String reason) {
        assertEquals(changeSubscriptionRequest.getDateOfBirth(), subscriptionRequest.getSubscriber().getDateOfBirth());
        assertEquals(changeSubscriptionRequest.getExpectedDateOfDelivery(), subscriptionRequest.getSubscriber().getExpectedDateOfDelivery());
        assertEquals(existingSubscription.getMsisdn(), subscriptionRequest.getMsisdn());
        assertEquals("change pack - " + reason, subscriptionRequest.getReason());
    }

    private void validateDeactivationRequest(DeactivationRequest deactivationRequest, Subscription existingSubscription) {

        assertEquals(existingSubscription.getSubscriptionId(), deactivationRequest.getSubscriptionId());
        assertEquals(Channel.CALL_CENTER, deactivationRequest.getChannel());
        assertNotNull(deactivationRequest.getCreatedAt());
    }

}
