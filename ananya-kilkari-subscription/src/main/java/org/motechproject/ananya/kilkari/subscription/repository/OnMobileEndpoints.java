package org.motechproject.ananya.kilkari.subscription.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class OnMobileEndpoints {

    private Properties kilkariProperties;

    @Autowired
    public OnMobileEndpoints(@Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.kilkariProperties = kilkariProperties;
    }

    public String activateSubscriptionURL() {
        return String.format("%s/%s", baseUrl(), kilkariProperties.get("omsm.activate.subscription.url"));
    }

    public String deactivateSubscriptionURL() {
        return String.format("%s/%s", baseUrl(), kilkariProperties.get("omsm.deactivate.subscription.url"));
    }

    private String baseUrl() {
        return kilkariProperties.getProperty("omsm.base.url");
    }
}
