package org.motechproject.ananya.kilkari.obd.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;

@Component
public class OBDProperties {

    private Properties obdProperties;
    private HashMap<String, CampaignMessageStatus> statusCodesMap = new HashMap<>();

    @Autowired
    public OBDProperties(@Qualifier("obdProperties") Properties obdProperties) {
        this.obdProperties = obdProperties;
        populateStatusCodes();
    }

    private void populateStatusCodes() {
        populateMapWithPropertyValue("campaign.message.dnp.status.codes", CampaignMessageStatus.DNP);
        populateMapWithPropertyValue("campaign.message.dnc.status.codes", CampaignMessageStatus.DNC);
    }

    private void populateMapWithPropertyValue(String propertyName, CampaignMessageStatus campaignMessageStatus) {
        String propertyValue = obdProperties.getProperty(propertyName);
        if (propertyValue == null) {
            throw new RuntimeException(String.format("%s property should be available.", propertyName));
        }

        String[] statusCodes = propertyValue.split(",");
        for (String statusCode : statusCodes) {
            statusCode = StringUtils.trim(statusCode);
            if (StringUtils.isEmpty(statusCode)) continue;
            statusCodesMap.put(statusCode, campaignMessageStatus);
        }
    }

    public String getFailureReportUrl() {
        return obdProperties.getProperty("obd.failure.report.url");
    }

    public String getMessageDeliveryBaseUrl() {
        return obdProperties.getProperty("obd.message.delivery.base.url");
    }

    public String getMessageDeliveryFileName() {
        return obdProperties.getProperty("obd.message.delivery.filename");
    }

    public String getMessageDeliveryFile() {
        return obdProperties.getProperty("obd.message.delivery.file");
    }

    public String getNewMessageSlotStartTime() {
        return obdProperties.getProperty("obd.new.message.slot.start.time");
    }

    public String getNewMessageSlotEndTime() {
        return obdProperties.getProperty("obd.new.message.slot.end.time");
    }

    public Integer getMaximumDNPRetryCount() {
        return Integer.parseInt(obdProperties.getProperty("obd.dnp.message.max.retry.count"));
    }

    public Integer getMaximumDNCRetryCount() {
        return Integer.parseInt(obdProperties.getProperty("obd.dnc.message.max.retry.count"));
    }

    public String getRetryMessageSlotStartTime() {
        return obdProperties.getProperty("obd.retry.message.slot.start.time");
    }

    public String getRetryMessageSlotEndTime() {
        return obdProperties.getProperty("obd.retry.message.slot.end.time");
    }

    public CampaignMessageStatus getCampaignMessageStatusFor(String statusCode) {
        return statusCodesMap.get(statusCode);
    }

    public String getNewMessageJobCronExpression() {
        return obdProperties.getProperty("obd.new.messages.job.cron.expression");
    }

    public String getRetryMessageJobCronExpression() {
        return obdProperties.getProperty("obd.retry.messages.job.cron.expression");
    }

    public int getNewMessageStartTimeLimitHours() {
        return Integer.parseInt(obdProperties.getProperty("obd.new.message.start.time.limit").split(":")[0]);
    }

    public int getNewMessageStartTimeLimitMinute() {
        return Integer.parseInt(obdProperties.getProperty("obd.new.message.start.time.limit").split(":")[1]);
    }

    public int getRetryMessageStartTimeLimitHours() {
        return Integer.parseInt(obdProperties.getProperty("obd.retry.message.start.time.limit").split(":")[0]);
    }

    public int getRetryMessageStartTimeLimitMinute() {
        return Integer.parseInt(obdProperties.getProperty("obd.retry.message.start.time.limit").split(":")[1]);
    }
}