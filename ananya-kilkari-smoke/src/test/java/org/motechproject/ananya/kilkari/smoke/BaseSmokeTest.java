package org.motechproject.ananya.kilkari.smoke;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseSmokeTest {
    @Autowired
    private SmokeConfig smokeConfig;

    protected String baseUrl() {
        return smokeConfig.baseUrl();
    }
}
