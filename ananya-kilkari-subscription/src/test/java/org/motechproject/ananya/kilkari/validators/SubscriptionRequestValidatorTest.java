package org.motechproject.ananya.kilkari.validators;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.service.ReportingService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionRequestValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ReportingService reportingService;
    @Mock
    private AllSubscriptions allSubscriptions;

    private SubscriptionRequestValidator subscriptionRequestValidator;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionRequestValidator = new SubscriptionRequestValidator(allSubscriptions, reportingService);
    }

    @Test
    public void shouldValidateSubscriptionRequestValues() {
        //given
        SubscriptionRequest subscriptionRequest = mock(SubscriptionRequest.class);
        when(subscriptionRequest.getChannel()).thenReturn(Channel.CALL_CENTER.toString());
        SubscriberLocation existingLocation = new SubscriberLocation();
        when(reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat()))
                .thenReturn(existingLocation);
        when(allSubscriptions.findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack())).thenReturn(null);

        //when
        subscriptionRequestValidator.validate(subscriptionRequest);

        //then
        InOrder inOrder = Mockito.inOrder(subscriptionRequest, reportingService, allSubscriptions);

        inOrder.verify(subscriptionRequest).validate(Matchers.<List<String>>any());
        inOrder.verify(reportingService).getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
        inOrder.verify(allSubscriptions).findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack());
    }

    @Test
    public void shouldValidateIfLocationExists() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().build();
        SubscriberLocation existingLocation = new SubscriberLocation();
        when(reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat()))
                .thenReturn(existingLocation);

        subscriptionRequestValidator.validate(subscriptionRequest);

        verify(reportingService).getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
    }

    @Test
    public void shouldValidateIfLocationDoesNotExist() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().build();
        when(reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat()))
                .thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Location does not exist for District[district] Block[block] and Panchayat[panchayat]");

        subscriptionRequestValidator.validate(subscriptionRequest);

        verify(reportingService).getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
    }

    @Test
    public void shouldValidateIfSubscriptionAlreadyExists() {
        //given
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().build();
        SubscriberLocation existingLocation = new SubscriberLocation();
        when(reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat()))
                .thenReturn(existingLocation);

        Subscription existingActiveSubscription = new Subscription();
        when(allSubscriptions.findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack())).thenReturn(existingActiveSubscription);

        expectedException.expect(DuplicateSubscriptionException.class);
        expectedException.expectMessage("Active subscription already exists for msisdn[9876543210] and pack[FIFTEEN_MONTHS]");

        //when
        subscriptionRequestValidator.validate(subscriptionRequest);

        //then
        verify(allSubscriptions).findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack());
        verify(reportingService).getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
    }

    @Test
    public void shouldNotValidateForLocation_ForIVRChannel() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withChannel(Channel.IVR.toString()).build();

        subscriptionRequestValidator.validate(subscriptionRequest);

        verify(reportingService, never()).getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat());
    }

    @Test
    public void shouldNotValidateLocationIfLocationIsCompletelyEmptyForCC() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withChannel(Channel.CALL_CENTER.name()).withDistrict(null).withBlock(null).withPanchayat(null).build();
        when(reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat()))
                     .thenReturn(null);

        try {
            subscriptionRequestValidator.validate(subscriptionRequest);
        } catch (ValidationException e) {
            Assert.fail("Unexpected ValidationException");
        }
    }

    @Test
    public void shouldAccumulateAllErrorsAndThrowValidationExceptionIfErrorsPresent() {
        String dob = DateTime.now().plusDays(50).toString("dd-MM-yyyy");
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withChannel(Channel.CALL_CENTER.name()).withDOB(dob).build();
        when(reportingService.getLocation(subscriptionRequest.getDistrict(), subscriptionRequest.getBlock(), subscriptionRequest.getPanchayat()))
                     .thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid date of birth " + dob + ",Location does not exist for District[district] Block[block] and Panchayat[panchayat]");

        subscriptionRequestValidator.validate(subscriptionRequest);
    }
}
