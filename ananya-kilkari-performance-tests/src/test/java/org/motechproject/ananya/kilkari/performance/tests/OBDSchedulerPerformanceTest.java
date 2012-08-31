package org.motechproject.ananya.kilkari.performance.tests;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.performance.tests.service.OBDService;
import org.motechproject.ananya.kilkari.performance.tests.utils.BasePerformanceTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.ContextUtils;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.performance.tests.LoadRunner;
import org.motechproject.performance.tests.LoadTest;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(LoadRunner.class)
public class OBDSchedulerPerformanceTest extends BasePerformanceTest {

    private List<Location> locationList;

    private Random random = new Random();

    public OBDSchedulerPerformanceTest(String testName) {
        super(testName);
    }

    private OBDService obdService = new OBDService();

    @Before
    public void setUp() {
        for (int i = 0; i < 25; i++) {
            String subscriptionId = UUID.randomUUID().toString();
            String msisdn = "9" + RandomStringUtils.randomNumeric(9);

            obdService.add(new CampaignMessage(subscriptionId, "week1", msisdn, "idea", DateTime.now().plusWeeks(1)));
        }
    }

    @LoadTest(concurrentUsers = 1)
    public void shouldPerformanceTestOBDScheduling() throws InterruptedException {
        DateTime beforeScheduling = DateTime.now();
        obdService.sendMessagesToOBD();
        DateTime afterScheduling = DateTime.now();

        Period p = new Period(beforeScheduling, afterScheduling);
        System.out.println("******************** " + p.getMillis() + " ms **********************");
    }
}
