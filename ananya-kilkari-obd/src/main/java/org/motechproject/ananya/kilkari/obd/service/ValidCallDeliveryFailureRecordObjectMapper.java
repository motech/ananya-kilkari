package org.motechproject.ananya.kilkari.obd.service;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ValidCallDeliveryFailureRecordObjectMapper {

    private CampaignMessageService campaignMessageService;

    @Autowired
    public ValidCallDeliveryFailureRecordObjectMapper(CampaignMessageService campaignMessageService) {
        this.campaignMessageService = campaignMessageService;
    }

    public ValidFailedCallReport mapFrom(FailedCallReport failedCallReport) {
        CampaignMessageStatus statusCode = campaignMessageService.getCampaignMessageStatusFor(failedCallReport);
        return new ValidFailedCallReport(failedCallReport.getSubscriptionId(), trimMobileNumber(failedCallReport.getMsisdn()), failedCallReport.getCampaignId(), statusCode, failedCallReport.getCreatedAt());
    }
    
    private String trimMobileNumber(String msisdn){
    	if(msisdn.startsWith("0") && msisdn.length()==11)
    		return msisdn.substring(1);
    	return msisdn;
    }
}
