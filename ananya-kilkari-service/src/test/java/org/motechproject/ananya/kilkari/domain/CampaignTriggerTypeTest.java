package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CampaignTriggerTypeTest {

    @Test
    public void isNotActivation() {
        assertFalse(CampaignTriggerType.ACTIVATION.isNotActivation());
        assertTrue(CampaignTriggerType.RENEWAL.isNotActivation());
        assertTrue(CampaignTriggerType.WEEKLY_MESSAGE.isNotActivation());
    }
}
