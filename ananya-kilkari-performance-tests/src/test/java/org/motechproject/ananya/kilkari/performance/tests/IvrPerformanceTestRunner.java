package org.motechproject.ananya.kilkari.performance.tests;

import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.motechproject.ananya.kilkari.performance.tests.testThreads.IvrPerformanceTestThread;
import org.motechproject.ananya.kilkari.performance.tests.utils.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.TestSuiteUtils;

public class IvrPerformanceTestRunner extends SpringIntegrationTest {

    @org.junit.Test
    public void shouldCreateAnIvrSubscription() {
        TestSuite testSuite = TestSuiteUtils.createTestSuite(10, IvrPerformanceTestThread.class, "shouldCreateAnIvrSubscription");
        TestRunner.run(testSuite);
    }

}
