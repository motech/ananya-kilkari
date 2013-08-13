package org.motechproject.ananya.kilkari.sync.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class RefdataSyncService {

    private HttpClientService httpClientService;

    private static final String NEW_LOCATION_SYNC_URL = "new.location.sync.url";
    private static final String API_KEY_NAME = "api.key.name";
    private static final String API_KEY_VALUE = "api.key.value";

    private Logger logger = Logger.getLogger(RefdataSyncService.class);

    private String apiKeyName;
    private String apiKeyValue;
    private String newLocationSyncUrl;

    @Autowired
    public RefdataSyncService(HttpClientService httpClientService, @Qualifier("refdataProperties") Properties refDataProperties) {
        this.httpClientService = httpClientService;
        readProperties(refDataProperties);
    }

    private void readProperties(Properties refDataProperties) {
        this.apiKeyName = refDataProperties.getProperty(API_KEY_NAME);
        this.apiKeyValue = refDataProperties.getProperty(API_KEY_VALUE);
        this.newLocationSyncUrl = refDataProperties.getProperty(NEW_LOCATION_SYNC_URL);
    }

    public void syncNewLocation(String state, String district, String block, String panchayat) {
        logger.info(String.format("Synchronizing new location to reference database for district: %s, block: %s, panchayat: %s", district, block, panchayat));

        httpClientService.post(newLocationSyncUrl, new NewLocationSyncRequest(state, district, block, panchayat), constructHeaders());
    }

    private Map<String, String> constructHeaders() {
        Map<String, String> headers = new HashMap<>();
        if(!StringUtils.isBlank(apiKeyName)) {

            headers.put(apiKeyName, apiKeyValue);
        }
        return headers;
    }
}
