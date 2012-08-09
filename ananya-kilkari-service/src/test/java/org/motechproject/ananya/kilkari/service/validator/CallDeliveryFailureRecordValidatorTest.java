package org.motechproject.ananya.kilkari.service.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
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
    @Mock
    private CampaignMessageService campaignMessageService;
    private CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator;

    @Before
    public void setUp() {
        initMocks(this);
        callDeliveryFailureRecordValidator = new CallDeliveryFailureRecordValidator(kilkariSubscriptionService, campaignMessageService);
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForMsisdn() {
        String subscriptionId = "subscriptionId";
        String statusCode = "iu_dnp";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "12345", "WEEK13", statusCode);
        FailedCallReport failedCallReport2 =  new FailedCallReport(subscriptionId, "123a", "WEEK13", statusCode);
        FailedCallReport failedCallReport3 =  new FailedCallReport(subscriptionId, null, "WEEK13", statusCode);
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(campaignMessageService.getCampaignMessageStatusFor(statusCode)).thenReturn(CampaignMessageStatus.DNC);


        Errors errors1 = callDeliveryFailureRecordValidator.validate(failedCallReport1);
        Errors errors2 = callDeliveryFailureRecordValidator.validate(failedCallReport2);
        Errors errors3 = callDeliveryFailureRecordValidator.validate(failedCallReport3);

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
        String statusCode = "iu_dnp";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "1234567890", "WEEK", statusCode);
        FailedCallReport failedCallReport2 =  new FailedCallReport(subscriptionId, "1234567890", "WEEKS13", statusCode);
        FailedCallReport failedCallReport3 =  new FailedCallReport(subscriptionId, "1234567890", "WEEK132", statusCode);
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(campaignMessageService.getCampaignMessageStatusFor(statusCode)).thenReturn(CampaignMessageStatus.DNC);

        Errors errors1 = callDeliveryFailureRecordValidator.validate(failedCallReport1);
        Errors errors2 = callDeliveryFailureRecordValidator.validate(failedCallReport2);
        Errors errors3 = callDeliveryFailureRecordValidator.validate(failedCallReport3);

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
        String statusCode = "iu_dnp";
        FailedCallReport failedCallReport = new FailedCallReport(subscriptionId, "1234567890", "WEEK13", statusCode);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);
        when(campaignMessageService.getCampaignMessageStatusFor(statusCode)).thenReturn(CampaignMessageStatus.DNC);

        Errors errors = callDeliveryFailureRecordValidator.validate(failedCallReport);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid subscription id subscriptionId"));
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForStatusCode() {
        String subscriptionId = "subscriptionId";
        FailedCallReport failedCallReport = new FailedCallReport(subscriptionId, "1234567890", "WEEK13", "iu_dnp");
        Subscription subscription = mock(Subscription.class);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        Errors errors = callDeliveryFailureRecordValidator.validate(failedCallReport);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid status code iu_dnp"));
    }
}