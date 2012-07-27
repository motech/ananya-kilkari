package org.motechproject.ananya.kilkari.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReport;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDeliveryFailureRecordValidatorTest {

    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForMsisdn() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "12345", "WEEK13", "DNP");
        FailedCallReport failedCallReport2 =  new FailedCallReport(subscriptionId, "123a", "WEEK13", "DNP");
        FailedCallReport failedCallReport3 =  new FailedCallReport(subscriptionId, null, "WEEK13", "DNP");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport1);
        Errors errors2 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport2);
        Errors errors3 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport3);

        assertEquals(1, errors1.getCount());
        assertTrue(errors1.hasMessage("Invalid msisdn 12345"));
        assertEquals(1, errors2.getCount());
        assertTrue(errors2.hasMessage("Invalid msisdn 123a"));
        assertEquals(1, errors2.getCount());
        assertTrue(errors3.hasMessage("Invalid msisdn null"));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForCampaignId() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "1234567890", "WEEK", "DNP");
        FailedCallReport failedCallReport2 =  new FailedCallReport(subscriptionId, "1234567890", "WEEKS13", "DNP");
        FailedCallReport failedCallReport3 =  new FailedCallReport(subscriptionId, "1234567890", "WEEK132", "DNP");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport1);
        Errors errors2 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport2);
        Errors errors3 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport3);

        assertEquals(1, errors1.getCount());
        assertTrue(errors1.hasMessage("Invalid campaign id WEEK"));
        assertEquals(1, errors2.getCount());
        assertTrue(errors2.hasMessage("Invalid campaign id WEEKS13"));
        assertEquals(1, errors2.getCount());
        assertTrue(errors3.hasMessage("Invalid campaign id WEEK132"));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForSubscriptionId() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport = new FailedCallReport(subscriptionId, "1234567890", "WEEK13", "DNP");
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);

        Errors errors = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid subscription id subscriptionId"));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForStatusCode() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport = new FailedCallReport(subscriptionId, "1234567890", "WEEK13", "DNP1");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid status code DNP1"));
    }
}