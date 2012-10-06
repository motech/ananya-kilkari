package org.motechproject.ananya.kilkari.performance.tests.utils;

import junit.framework.TestCase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:applicationKilkariPerformanceContext.xml")
@ActiveProfiles("test")
public class BasePerformanceTest extends TestCase {

    public BasePerformanceTest(String name) {
        super(name);
    }

}
