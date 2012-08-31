package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextUtils {

    private static ClassPathXmlApplicationContext context;
    static {
        try {
            context = new ClassPathXmlApplicationContext("applicationKilkariPerformanceContext.xml");
        } catch (Exception e) {
            System.err.println("Exception occurred while initializing spring context: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    public static BaseConfiguration getConfiguration() {
        return (BaseConfiguration) context.getBean("baseConfiguration");
    }
}
