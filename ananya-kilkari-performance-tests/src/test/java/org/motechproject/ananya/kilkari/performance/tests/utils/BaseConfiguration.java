package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Properties;
@Component
public class BaseConfiguration {

    @Autowired
    private Properties performanceProperties;
    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private AllCampaignMessages allCampaignMessages;
    @Autowired
    private CampaignMessageService campaignMessageService;


    public Properties getPerformanceProperties() {
        return performanceProperties;
    }

    public AllSubscriptions getAllSubscriptions() {
        return allSubscriptions;
    }

    public AllCampaignMessages getAllCampaignMessages() {
        return allCampaignMessages;
    }

    public String baseUrl(){
        return performanceProperties.getProperty("baseurl");
    }

    public CampaignMessageService getCampaignMessageService() {
        return campaignMessageService;
    }
}
