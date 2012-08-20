package org.motechproject.ananya.kilkari.obd.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReport;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ValidCallDeliveryFailureRecordObjectMapperTest {
    @Mock
    private CampaignMessageService campaignMessageService;

    @Before
    public void setup(){
        initMocks(this);
    }

    @Test
    public void shouldMapFromCallDeliveryFailureRecordObjectToValidCallDeliveryFailureRecordObject() {
        FailedCallReport failedCallReport = new FailedCallReport("subscriptionId", "msisdn", "WEEK13", "iu_dnp");

        when(campaignMessageService.getCampaignMessageStatusFor("iu_dnp")).thenReturn(CampaignMessageStatus.DNP);
        ValidFailedCallReport validFailedCallReport = new ValidCallDeliveryFailureRecordObjectMapper(campaignMessageService).mapFrom(failedCallReport);

        assertEquals("subscriptionId", validFailedCallReport.getSubscriptionId());
        assertEquals("msisdn", validFailedCallReport.getMsisdn());
        assertEquals(CampaignMessageStatus.DNP, validFailedCallReport.getStatusCode());
        assertEquals("WEEK13", validFailedCallReport.getCampaignId());
        assertEquals(failedCallReport.getCreatedAt(), validFailedCallReport.getCreatedAt());
    }
}
