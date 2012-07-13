package org.motechproject.ananya.kilkari.subscription.gateway;

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
        return String.format("%s/%s?msisdn={msisdn}&srvkey={srvkey}&mode={mode}&refid={refid}&user={user}&pass={pass}",
                baseUrl(), kilkariProperties.get("omsm.activate.subscription.url"));
    }

    private String baseUrl() {
        return kilkariProperties.getProperty("omsm.base.url");
    }

    public String username() {
        return kilkariProperties.getProperty("omsm.username");
    }

    public String password() {
        return kilkariProperties.getProperty("omsm.password");
    }
}
