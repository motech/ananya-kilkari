package org.motechproject.ananya.kilkari.functional.test.utils;

import org.junit.After;
import org.motechproject.ananya.kilkari.functional.test.domain.CallCenter;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionManager;
import org.motechproject.ananya.kilkari.functional.test.domain.Time;
import org.motechproject.ananya.kilkari.functional.test.verifiers.CampaignMessageVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.ReportVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.SubscriptionVerifier;
import org.springframework.beans.factory.annotation.Autowired;

public class FunctionalTestUtils extends SpringIntegrationTest {

    @Autowired
    protected CallCenter callCenter;
    @Autowired
    protected SubscriptionManager subscriptionManager;
    @Autowired
    protected Time time;
    @Autowired
    protected CampaignMessageVerifier campaignMessageVerifier;
    @Autowired
    protected SubscriptionVerifier subscriptionVerifier;

    @Autowired
    protected ReportVerifier reportVerifier;

    @After
    public void after() {
        reportVerifier.resetMockBehaviour();
        super.after();
    }

}
