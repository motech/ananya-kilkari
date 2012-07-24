package org.motechproject.ananya.kilkari.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.contract.FailedCallReport;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

import java.util.List;

import static org.junit.Assert.assertEquals;
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

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport1);
        List<String> errors2 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport2);
        List<String> errors3 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport3);

        assertEquals(1, errors1.size());
        assertEquals("Invalid msisdn 12345", errors1.get(0));
        assertEquals(1, errors2.size());
        assertEquals("Invalid msisdn 123a", errors2.get(0));
        assertEquals(1, errors3.size());
        assertEquals("Invalid msisdn null", errors3.get(0));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForCampaignId() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "1234567890", "WEEK", "DNP");
        FailedCallReport failedCallReport2 =  new FailedCallReport(subscriptionId, "1234567890", "WEEKS13", "DNP");
        FailedCallReport failedCallReport3 =  new FailedCallReport(subscriptionId, "1234567890", "WEEK132", "DNP");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport1);
        List<String> errors2 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport2);
        List<String> errors3 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport3);

        assertEquals(1, errors1.size());
        assertEquals("Invalid campaign id WEEK", errors1.get(0));
        assertEquals(1, errors2.size());
        assertEquals("Invalid campaign id WEEKS13", errors2.get(0));
        assertEquals(1, errors3.size());
        assertEquals("Invalid campaign id WEEK132", errors3.get(0));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForSubscriptionId() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "1234567890", "WEEK13", "DNP");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport1);

        assertEquals(1, errors1.size());
        assertEquals("Invalid subscription id subscriptionId", errors1.get(0));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForStatusCode() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "1234567890", "WEEK13", "DNP1");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(failedCallReport1);

        assertEquals(1, errors1.size());
        assertEquals("Invalid status code DNP1", errors1.get(0));
    }
}
