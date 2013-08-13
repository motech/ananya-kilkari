package org.motechproject.ananya.kilkari.request;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CampaignChangeRequestTest {
    @Test
    public void shouldValidateCampaignChangeRequest() {
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();

        campaignChangeRequest.setChannel("InvalidChannel");
        campaignChangeRequest.setReason("RandomReason");
        Errors errors = campaignChangeRequest.validate();
        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getCount());

        campaignChangeRequest.setChannel("IVR");
        campaignChangeRequest.setReason("MISCARRIAGE");
        errors = campaignChangeRequest.validate();
        assertFalse(errors.hasErrors());
    }
}
