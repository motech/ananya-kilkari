package org.motechproject.ananya.kilkari.smoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class SmokeConfig {

    private Properties smokeProperties;

    @Autowired
    public SmokeConfig(Properties smokeProperties) {
        this.smokeProperties = smokeProperties;
    }

    public String baseUrl() {
        return smokeProperties.getProperty("baseurl");
    }
}
