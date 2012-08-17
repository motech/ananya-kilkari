package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.motechproject.ananya.kilkari.performance.tests.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AutowiredProperties {

    public static Properties performanceProperties;
    public static SubscriptionService subscriptionService;

    @Autowired
    public void setPerformanceProperties(Properties performanceProperties) {
        AutowiredProperties.performanceProperties = performanceProperties;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        AutowiredProperties.subscriptionService = subscriptionService;
    }
}
