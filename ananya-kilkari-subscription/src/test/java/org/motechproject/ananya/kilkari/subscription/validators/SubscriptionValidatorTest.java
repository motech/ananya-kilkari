package org.motechproject.ananya.kilkari.subscription.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ReportingService reportingService;
    @Mock
    private AllSubscriptions allSubscriptions;

    private SubscriptionValidator subscriptionValidator;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionValidator = new SubscriptionValidator(allSubscriptions, reportingService);
    }

    @Test
    public void shouldValidateIfLocationDoesNotExist() {
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().build();
        Location location = subscription.getLocation();
        when(reportingService.getLocation(location.getDistrict(), location.getBlock(), location.getPanchayat())).thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Location does not exist for District[district] Block[block] and Panchayat[panchayat]");

        subscriptionValidator.validate(subscription);
    }

    @Test
    public void shouldValidateIfSubscriptionAlreadyExists() {
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().build();

        SubscriberLocation existingLocation = new SubscriberLocation();
        Location location = subscription.getLocation();
        when(reportingService.getLocation(location.getDistrict(), location.getBlock(), location.getPanchayat())).thenReturn(existingLocation);

        Subscription existingActiveSubscription = new Subscription();
        when(allSubscriptions.findSubscriptionInProgress(subscription.getMsisdn(), subscription.getPack())).thenReturn(existingActiveSubscription);

        expectedException.expect(DuplicateSubscriptionException.class);
        expectedException.expectMessage("Active subscription already exists for msisdn[9876543210] and pack[FIFTEEN_MONTHS]");

        subscriptionValidator.validate(subscription);
    }

    @Test
    public void shouldNotValidateLocationIfLocationIsCompletelyEmptyForCC() {
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().withDistrict(null).withBlock(null).withPanchayat(null).build();

        try {
            subscriptionValidator.validate(subscription);
        } catch (ValidationException e) {
            Assert.fail("Unexpected ValidationException");
        }

        verify(reportingService, never()).getLocation(anyString(), anyString(), anyString());
    }
}
