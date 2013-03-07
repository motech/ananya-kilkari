package org.motechproject.ananya.kilkari.obd.service.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDeliveryFailureRecordValidatorTest {

    @Mock
    private AllCampaignMessages allCampaignMessages;
    @Mock
    private CampaignMessageService campaignMessageService;
    private CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator;

    @Before
    public void setUp() {
        initMocks(this);
        callDeliveryFailureRecordValidator = new CallDeliveryFailureRecordValidator(allCampaignMessages, campaignMessageService);
    }

    @Test
    public void shouldValidateCallDeliveryFailureRecordForMsisdn() {
        String subscriptionId = "subscriptionId";
        String statusCode = "iu_dnp";
        FailedCallReport failedCallReport1 = new FailedCallReport(subscriptionId, "12345", "WEEK13", statusCode);
        FailedCallReport failedCallReport2 = new FailedCallReport(subscriptionId, "123a", "WEEK13", statusCode);
        FailedCallReport failedCallReport3 = new FailedCallReport(subscriptionId, null, "WEEK13", statusCode);

        when(allCampaignMessages.findBySubscriptionId(subscriptionId)).thenReturn(Arrays.asList(new CampaignMessage()));
        when(campaignMessageService.getCampaignMessageStatusFor(any(FailedCallReport.class))).thenReturn(CampaignMessageStatus.ND);


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
        FailedCallReport failedCallReport2 = new FailedCallReport(subscriptionId, "1234567890", "WEEKS13", statusCode);
        FailedCallReport failedCallReport3 = new FailedCallReport(subscriptionId, "1234567890", "WEEK132", statusCode);
        when(allCampaignMessages.findBySubscriptionId(subscriptionId)).thenReturn(Arrays.asList(new CampaignMessage()));
        when(campaignMessageService.getCampaignMessageStatusFor(any(FailedCallReport.class))).thenReturn(CampaignMessageStatus.ND);

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
        when(allCampaignMessages.findBySubscriptionId(subscriptionId)).thenReturn(null);
        when(campaignMessageService.getCampaignMessageStatusFor(failedCallReport)).thenReturn(CampaignMessageStatus.ND);

        Errors errors = callDeliveryFailureRecordValidator.validate(failedCallReport);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid subscription id subscriptionId"));
    }
}
