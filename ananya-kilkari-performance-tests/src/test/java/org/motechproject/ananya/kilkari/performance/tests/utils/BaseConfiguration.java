package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.motechproject.ananya.kilkari.performance.tests.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class BaseConfiguration {

    public static Properties performanceProperties;
    public static SubscriptionService subscriptionService;

    @Autowired
    public void setPerformanceProperties(Properties performanceProperties) {
        BaseConfiguration.performanceProperties = performanceProperties;
    }

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        BaseConfiguration.subscriptionService = subscriptionService;
    }
}
