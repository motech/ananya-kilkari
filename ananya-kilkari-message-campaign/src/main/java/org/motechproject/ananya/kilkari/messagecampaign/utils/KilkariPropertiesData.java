package org.motechproject.ananya.kilkari.messagecampaign.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KilkariPropertiesData {


    private Properties kilkariProperties;

    @Autowired
    public KilkariPropertiesData(@Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.kilkariProperties = kilkariProperties;
    }

    public int getCampaignScheduleDeltaDays() {
        return Integer.parseInt(kilkariProperties.getProperty("kilkari.campaign.schedule.delta.days"));
    }

    public int getCampaignScheduleDeltaMinutes() {
        return Integer.parseInt(kilkariProperties.getProperty("kilkari.campaign.schedule.delta.minutes"));

    }

    public int getBufferDaysToAllowRenewalForPackCompletion() {
        return Integer.parseInt(kilkariProperties.getProperty("buffer.days.to.allow.renewal.for.pack.completion"));

    }

}
