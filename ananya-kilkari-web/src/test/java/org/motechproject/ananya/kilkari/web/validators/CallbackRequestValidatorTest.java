package org.motechproject.ananya.kilkari.web.validators;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.CallbackAction;
import org.motechproject.ananya.kilkari.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.utils.DummySubscriptionStateHandler;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class CallbackRequestValidatorTest {

    private CallbackRequestValidator callbackRequestValidator;

    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setup() {
        initMocks(this);
        callbackRequestValidator = new CallbackRequestValidator(subscriptionStateHandlerFactory, subscriptionService);
    }

    @Test
    public void shouldReturnValidIfCallbackRequestDetailsAreCorrect() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        new CallbackRequestWrapper(callbackRequest, "subId", DateTime.now());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "subId", DateTime.now());
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();
        when(subscriptionService.findBySubscriptionId(callbackRequestWrapper.getSubscriptionId())).thenReturn(subscription);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        assertTrue(errors.hasNoErrors());
    }

    @Test
    public void shouldReturnInValidIfMsisdnIsAnInvalidNumber() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("12345");
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        when(subscriptionStateHandlerFactory.getHandler(any(CallbackRequestWrapper.class))).thenReturn(new DummySubscriptionStateHandler());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(subscription);

        String subscriptionId = "subId";
        Errors errors = callbackRequestValidator.validate(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid msisdn 12345 for subscription id " + subscriptionId));
    }

    @Test
    public void shouldReturnInValidIfMsisdnIsAlphaNumeric() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("123456789a");
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        when(subscriptionStateHandlerFactory.getHandler(any(CallbackRequestWrapper.class))).thenReturn(new DummySubscriptionStateHandler());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(subscription);

        String subscriptionId = "subId";
        Errors errors = callbackRequestValidator.validate(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid msisdn 123456789a for subscription id " + subscriptionId));
    }

    @Test
    public void shouldReturnInValidIfCallbackActionIsInvalid() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setAction("invalid");
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        when(subscriptionStateHandlerFactory.getHandler(any(CallbackRequestWrapper.class))).thenReturn(new DummySubscriptionStateHandler());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(subscription);

        String subscriptionId = "subId";
        Errors errors = callbackRequestValidator.validate(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid callbackAction invalid  for subscription " + subscriptionId));
    }

    @Test
    public void shouldMarkRenewalDeactivationRequestAsInvalidWhenSubscriptionStateIsOtherThanSuspended() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.DCT.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).build();
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage(String.format("Cannot deactivate on renewal. Subscription %s in ACTIVE status", subscriptionId)));
    }

    @Test
    public void shouldReturnInValidIfCallbackStatusIsInvalid() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus("invalid");
        when(subscriptionStateHandlerFactory.getHandler(any(CallbackRequestWrapper.class))).thenReturn(new DummySubscriptionStateHandler());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(subscription);

        String subscriptionId = "subId";
        Errors errors = callbackRequestValidator.validate(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid callbackStatus invalid for subscription " + subscriptionId));
    }

    @Test
    public void shouldReturnInvalidIfOperatorIsInvalid() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        callbackRequest.setOperator("invalid_operator");
        when(subscriptionStateHandlerFactory.getHandler(any(CallbackRequestWrapper.class))).thenReturn(new DummySubscriptionStateHandler());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(subscription);

        String subscriptionId = "subId";
        Errors errors = callbackRequestValidator.validate(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid operator invalid_operator for subscription " + subscriptionId));
    }

    @Test
    public void shouldReturnInvalidIfOperatorIsNotGiven() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        callbackRequest.setOperator(null);
        when(subscriptionStateHandlerFactory.getHandler(any(CallbackRequestWrapper.class))).thenReturn(new DummySubscriptionStateHandler());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(subscription);

        String subscriptionId = "subId";
        Errors errors = callbackRequestValidator.validate(new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now()));

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid operator null for subscription " + subscriptionId));
    }

    @Test
    public void shouldNotInvokeSubscriptionServiceValidationIfCallbackActionOrStatusIsInvalid() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("12345");
        callbackRequest.setAction("Invalid Action");
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        callbackRequest.setOperator(Operator.AIRTEL.name());
        final CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "subId", DateTime.now());

        final Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        assertEquals(2, errors.getCount());
    }

    @Test
    public void shouldMarkRenewalRequestAsInvalidWhenSubscriptionStateIsOtherThanActiveOrSuspended() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.NEW).build();
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage(String.format("Cannot renew. Subscription %s in NEW status", subscriptionId)));
    }

    @Test
    public void shouldMarkActivationRequestAsInvalidWhenSubscriptionStateIsOtherThanPendingActivation() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).build();
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage(String.format("Cannot activate. Subscription %s in ACTIVE status", subscriptionId)));
    }

    @Test
    public void shouldMarkRenewalRequestAsValidWhenSubscriptionStateIsActive() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).build();
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        errors.hasNoErrors();
    }

    @Test
    public void shouldMarkRenewalRequestAsValidWhenSubscriptionStateIsSuspended() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.BAL_LOW.name());
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.SUSPENDED).build();
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        errors.hasNoErrors();
    }

    @Test
    public void shouldReturnErrorWhenSubscriptionRequestActionStateCombinationIsInvalid() {
        final String subscriptionId = "subId";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.REN.name());
        callbackRequest.setStatus(CallbackStatus.ERROR.name());
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).build();
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(null);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid status ERROR for action REN for subscription " + subscriptionId));
    }

    @Test
    public void shouldReturnErrorIfSubscriptionIdIsNotPresent() {
        String subscriptionId = "subscriptionId";
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction(CallbackAction.DCT.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        when(subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper)).thenReturn(new DummySubscriptionStateHandler());
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);

        Errors errors = callbackRequestValidator.validate(callbackRequestWrapper);

        assertEquals(1, errors.getCount());
        assertEquals(String.format("No subscription for subscriptionId : %s", subscriptionId), errors.allMessages());
    }
}