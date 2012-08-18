package org.motechproject.ananya.kilkari.performance.tests.utils;

import junit.framework.TestCase;
import org.junit.After;

import java.util.Properties;

public class BasePerformanceTest extends TestCase{

    private Properties performanceProperties;

    public BasePerformanceTest(String name) {
        super(name);
        this.performanceProperties = BaseConfiguration.getPerformanceProperties();
    }

    @After
    public void after() {
        BaseConfiguration.getAllSubscriptions().removeAll();
    }


    protected String baseUrl() {
        return performanceProperties.getProperty("baseurl");
    }

}
