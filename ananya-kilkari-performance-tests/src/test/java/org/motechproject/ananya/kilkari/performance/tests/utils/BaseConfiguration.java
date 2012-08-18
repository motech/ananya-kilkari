package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.motechproject.ananya.kilkari.performance.tests.service.AllSubscriptions;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

public class BaseConfiguration {

    private static Properties performanceProperties;
    private static AllSubscriptions allSubscriptions;
    private static final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationKilkariPerformanceContext.xml");

    public static AllSubscriptions getAllSubscriptions() {
        if(allSubscriptions!=null) return allSubscriptions;
        allSubscriptions = (AllSubscriptions) context.getBean("allSubscriptions");
        return allSubscriptions;
    }

    public static Properties getPerformanceProperties() {
        if(performanceProperties!=null) return performanceProperties;
        performanceProperties = (Properties) context.getBean("performanceProperties");
        return performanceProperties;
    }

    public static String baseUrl() {
        return performanceProperties.getProperty("baseurl");
    }
}
