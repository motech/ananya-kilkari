package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.contract.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.contract.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.springframework.stereotype.Component;

@Component
public class ValidCallDeliveryFailureRecordObjectMapper {
    public ValidFailedCallReport mapFrom(FailedCallReport failedCallReport, FailedCallReports failedCallReports) {
        CampaignMessageStatus statusCode = CampaignMessageStatus.getFor(failedCallReport.getStatusCode());
        DateTime createdAt = failedCallReports.getCreatedAt();
        return new ValidFailedCallReport(failedCallReport.getSubscriptionId(), failedCallReport.getMsisdn(), failedCallReport.getCampaignId(), statusCode, createdAt);
    }
}
