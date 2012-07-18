package org.motechproject.ananya.kilkari.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.contract.CallDeliveryFailureRecordObject;
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
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject1 = new CallDeliveryFailureRecordObject(subscriptionId, "12345", "WEEK13", "DNP");
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject2 =  new CallDeliveryFailureRecordObject(subscriptionId, "123a", "WEEK13", "DNP");
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject3 =  new CallDeliveryFailureRecordObject(subscriptionId, null, "WEEK13", "DNP");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject1);
        List<String> errors2 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject2);
        List<String> errors3 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject3);

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
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject1 = new CallDeliveryFailureRecordObject(subscriptionId, "1234567890", "WEEK", "DNP");
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject2 =  new CallDeliveryFailureRecordObject(subscriptionId, "1234567890", "WEEKS13", "DNP");
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject3 =  new CallDeliveryFailureRecordObject(subscriptionId, "1234567890", "WEEK132", "DNP");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject1);
        List<String> errors2 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject2);
        List<String> errors3 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject3);

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
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject1 = new CallDeliveryFailureRecordObject(subscriptionId, "1234567890", "WEEK13", "DNP");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject1);

        assertEquals(1, errors1.size());
        assertEquals("Invalid subscription id subscriptionId", errors1.get(0));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForStatusCode() {
        String subscriptionId = "subscriptionId";
        CallDeliveryFailureRecordObject callDeliveryFailureRecordErrorObject1 = new CallDeliveryFailureRecordObject(subscriptionId, "1234567890", "WEEK13", "DNP1");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        List<String> errors1 = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService).validate(callDeliveryFailureRecordErrorObject1);

        assertEquals(1, errors1.size());
        assertEquals("Invalid status code DNP1", errors1.get(0));
    }
}
