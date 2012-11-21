package org.motechproject.ananya.kilkari.sync.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.sync.mapper.LocationSyncRequestMapper;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberLocation;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class LocationSyncService {
    private HttpClientService httpClientService;
    private Properties clientServiceProperties;
    private static final String REFDB_SYNC_ENDPOINT = "location.sync.url.reference.data";
    private Logger logger = Logger.getLogger(LocationSyncService.class);

    @Autowired
    public LocationSyncService(HttpClientService httpClientService, @Qualifier("clientServiceProperties") Properties clientServiceProperties) {
        this.httpClientService = httpClientService;
        this.clientServiceProperties = clientServiceProperties;
    }

    public void sync(SubscriberLocation location) {
        if (location != null) {
            logger.info("Raising event to sync for location: " + location.toString());
            httpClientService.post((String) clientServiceProperties.get(REFDB_SYNC_ENDPOINT), LocationSyncRequestMapper.map(location));
        }
    }
}
