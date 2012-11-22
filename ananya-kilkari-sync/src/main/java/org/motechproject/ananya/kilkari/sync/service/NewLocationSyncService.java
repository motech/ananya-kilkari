package org.motechproject.ananya.kilkari.sync.service;

import org.apache.log4j.Logger;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class NewLocationSyncService {

    private HttpClientService httpClientService;
    private Properties kilkariProperties;
    private static final String REFDB_SYNC_ENDPOINT = "new.location.sync.url";
    private Logger logger = Logger.getLogger(NewLocationSyncService.class);

    @Autowired
    public NewLocationSyncService(HttpClientService httpClientService, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.httpClientService = httpClientService;
        this.kilkariProperties = kilkariProperties;
    }

    public void sync(String district, String block, String panchayat) {
        logger.info(String.format("Synchronizing new location to reference database for district: %s, block: %s, panchayat: %s", district, block, panchayat));
        httpClientService.post((String) kilkariProperties.get(REFDB_SYNC_ENDPOINT),
                new NewLocationSyncRequest(district, block, panchayat));
    }
}
