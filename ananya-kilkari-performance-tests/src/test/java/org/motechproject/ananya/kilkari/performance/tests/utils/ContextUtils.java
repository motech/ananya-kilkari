package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextUtils {

    private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationKilkariPerformanceContext.xml");

    public static BaseConfiguration getConfiguration() {
        return (BaseConfiguration) context.getBean("baseConfiguration");
    }
}
