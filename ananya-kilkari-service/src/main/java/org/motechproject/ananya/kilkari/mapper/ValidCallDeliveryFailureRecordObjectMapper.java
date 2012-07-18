package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.contract.CallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.CallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.contract.ValidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.springframework.stereotype.Component;

@Component
public class ValidCallDeliveryFailureRecordObjectMapper {
    public ValidCallDeliveryFailureRecordObject mapFrom(CallDeliveryFailureRecordObject callDeliveryFailureRecordObject, CallDeliveryFailureRecord callDeliveryFailureRecord) {
        CampaignMessageStatus statusCode = CampaignMessageStatus.getFor(callDeliveryFailureRecordObject.getStatusCode());
        DateTime createdAt = callDeliveryFailureRecord.getCreatedAt();
        return new ValidCallDeliveryFailureRecordObject(callDeliveryFailureRecordObject.getSubscriptionId(), callDeliveryFailureRecordObject.getMsisdn(), callDeliveryFailureRecordObject.getCampaignId(), statusCode, createdAt);
    }
}
