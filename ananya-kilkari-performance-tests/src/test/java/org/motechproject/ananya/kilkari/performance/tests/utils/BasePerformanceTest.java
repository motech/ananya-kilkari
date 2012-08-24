package org.motechproject.ananya.kilkari.performance.tests.utils;

import junit.framework.TestCase;
import org.junit.After;

public class BasePerformanceTest extends TestCase {

    public BasePerformanceTest(String name) {
        super(name);
    }

    @After
    public void after() {
        ContextUtils.getConfiguration().getAllSubscriptions().removeAll();
    }
}
