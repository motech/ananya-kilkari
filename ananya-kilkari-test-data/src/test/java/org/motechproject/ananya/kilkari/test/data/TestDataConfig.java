package org.motechproject.ananya.kilkari.test.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class TestDataConfig {

    private Properties testDataProperties;

    @Autowired
    public TestDataConfig(Properties testDataProperties) {
        this.testDataProperties = testDataProperties;
    }

    public String baseUrl() {
        return testDataProperties.getProperty("baseurl");
    }
}
