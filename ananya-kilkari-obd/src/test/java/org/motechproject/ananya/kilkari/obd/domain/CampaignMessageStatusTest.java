package org.motechproject.ananya.kilkari.obd.domain;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CampaignMessageStatusTest {

    @Test
    public void shouldReturnFailedStatusCodes(){
        List<CampaignMessageStatus> failedStatusCodes = CampaignMessageStatus.getFailedStatusCodes();

        assertEquals(3, failedStatusCodes.size());
        assertTrue(failedStatusCodes.contains(CampaignMessageStatus.SO));
        assertTrue(failedStatusCodes.contains(CampaignMessageStatus.ND));
        assertTrue(failedStatusCodes.contains(CampaignMessageStatus.NA));
        assertFalse(failedStatusCodes.contains(CampaignMessageStatus.NEW));
        assertFalse(failedStatusCodes.contains(CampaignMessageStatus.SUCCESS));
    }
}
