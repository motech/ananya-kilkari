package org.motechproject.ananya.kilkari.mapper;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.contract.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.contract.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;

import static org.junit.Assert.assertEquals;

public class ValidCallDeliveryFailureRecordObjectMapperTest {
    @Test
    public void shouldMapFromCallDeliveryFailureRecordObjectToValidCallDeliveryFailureRecordObject() {
        ValidCallDeliveryFailureRecordObjectMapper mapper = new ValidCallDeliveryFailureRecordObjectMapper();
        FailedCallReport failedCallReport = new FailedCallReport("subscriptionId", "msisdn", "WEEK13", "DNP");
        FailedCallReports failedCallReports = new FailedCallReports();

        ValidFailedCallReport validFailedCallReport = mapper.mapFrom(failedCallReport, failedCallReports);

        assertEquals("subscriptionId", validFailedCallReport.getSubscriptionId());
        assertEquals("msisdn", validFailedCallReport.getMsisdn());
        assertEquals(CampaignMessageStatus.DNP, validFailedCallReport.getStatusCode());
        assertEquals("WEEK13", validFailedCallReport.getCampaignId());
        assertEquals(failedCallReports.getCreatedAt(), validFailedCallReport.getCreatedAt());
    }
}
