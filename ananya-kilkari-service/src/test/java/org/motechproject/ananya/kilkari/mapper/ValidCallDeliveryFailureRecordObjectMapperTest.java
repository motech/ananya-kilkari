package org.motechproject.ananya.kilkari.mapper;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.contract.CallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.CallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.contract.ValidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;

import static org.junit.Assert.assertEquals;

public class ValidCallDeliveryFailureRecordObjectMapperTest {
    @Test
    public void shouldMapFromCallDeliveryFailureRecordObjectToValidCallDeliveryFailureRecordObject() {
        ValidCallDeliveryFailureRecordObjectMapper mapper = new ValidCallDeliveryFailureRecordObjectMapper();
        CallDeliveryFailureRecordObject callDeliveryFailureRecordObject = new CallDeliveryFailureRecordObject("subscriptionId", "msisdn", "WEEK13", "DNP");
        CallDeliveryFailureRecord callDeliveryFailureRecord = new CallDeliveryFailureRecord();

        ValidCallDeliveryFailureRecordObject validCallDeliveryFailureRecordObject = mapper.mapFrom(callDeliveryFailureRecordObject, callDeliveryFailureRecord);

        assertEquals("subscriptionId",validCallDeliveryFailureRecordObject.getSubscriptionId());
        assertEquals("msisdn",validCallDeliveryFailureRecordObject.getMsisdn());
        assertEquals(CampaignMessageStatus.DNP,validCallDeliveryFailureRecordObject.getStatusCode());
        assertEquals("WEEK13",validCallDeliveryFailureRecordObject.getCampaignId());
        assertEquals(callDeliveryFailureRecord.getCreatedAt(),validCallDeliveryFailureRecordObject.getCreatedAt());
    }
}
