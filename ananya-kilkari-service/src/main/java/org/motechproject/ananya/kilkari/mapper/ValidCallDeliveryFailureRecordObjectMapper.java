package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidCallDeliveryFailureRecordObjectMapper {

    private CampaignMessageService campaignMessageService;

    @Autowired
    public ValidCallDeliveryFailureRecordObjectMapper(CampaignMessageService campaignMessageService) {
        this.campaignMessageService = campaignMessageService;
    }

    public ValidFailedCallReport mapFrom(FailedCallReport failedCallReport) {
        CampaignMessageStatus statusCode = campaignMessageService.getCampaignMessageStatusFor(failedCallReport.getStatusCode());
        return new ValidFailedCallReport(failedCallReport.getSubscriptionId(), failedCallReport.getMsisdn(), failedCallReport.getCampaignId(), statusCode, failedCallReport.getCreatedAt());
    }
}
