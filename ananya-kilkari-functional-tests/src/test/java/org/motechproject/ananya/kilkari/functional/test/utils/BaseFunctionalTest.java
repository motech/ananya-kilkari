package org.motechproject.ananya.kilkari.functional.test.utils;

import org.junit.After;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.functional.test.domain.*;
import org.motechproject.ananya.kilkari.functional.test.verifiers.CampaignMessageVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.OnMobileOBDVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.ReportVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.SubscriptionVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariFunctionalTestContext.xml")
@ActiveProfiles("test")
public class BaseFunctionalTest {

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
    protected OnMobileOBDVerifier onMobileOBDVerifier;
    @Autowired
    protected User user;
    @Autowired
    protected OBD obd;

    @Autowired
    protected ReportVerifier reportVerifier;

    @After
    public void after() {
        reportVerifier.reset();
        onMobileOBDVerifier.reset();
        campaignMessageVerifier.reset();
    }
}
