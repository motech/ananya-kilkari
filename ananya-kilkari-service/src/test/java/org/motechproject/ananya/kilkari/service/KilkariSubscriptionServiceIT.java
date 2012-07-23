package org.motechproject.ananya.kilkari.service;

import org.junit.Test;
import org.motechproject.ananya.kilkari.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class KilkariSubscriptionServiceIT extends SpringIntegrationTest {
    @Autowired
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Test
    public void shouldLoadBufferDaysPropertiesFromThePropertiesFile() {
        assertEquals(3, kilkariSubscriptionService.bufferDaysToAllowRenewalForPackCompletion);
    }
}
