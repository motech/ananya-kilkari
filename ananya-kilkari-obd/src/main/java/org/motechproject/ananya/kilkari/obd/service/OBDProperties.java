package org.motechproject.ananya.kilkari.obd.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
        populateMapWithPropertyValue("campaign.message.na.status.codes", CampaignMessageStatus.NA);
        populateMapWithPropertyValue("campaign.message.nd.status.codes", CampaignMessageStatus.ND);
        populateMapWithPropertyValue("campaign.message.so.status.codes", CampaignMessageStatus.SO);
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

    public Integer getMaximumDNPRetryCount() {
        return Integer.parseInt(obdProperties.getProperty("obd.dnp.message.max.retry.count"));
    }

    public Integer getMaximumDNCRetryCount() {
        return Integer.parseInt(obdProperties.getProperty("obd.dnc.message.max.retry.count"));
    }

    public String getMainSlotStartTimeFor(String subSlot) {
        return obdProperties.getProperty(String.format("obd.main.sub.slot.%s.start.time", subSlot.toLowerCase()));
    }

    public String getMainSlotEndTimeFor(String subSlot) {
        return obdProperties.getProperty(String.format("obd.main.sub.slot.%s.end.time", subSlot.toLowerCase()));
    }

    public String getRetrySlotStartTimeFor(String subSlot) {
        return obdProperties.getProperty(String.format("obd.retry.sub.slot.%s.start.time", subSlot.toLowerCase()));
    }

    public String getRetrySlotEndTimeFor(String subSlot) {
        return obdProperties.getProperty(String.format("obd.retry.sub.slot.%s.end.time", subSlot.toLowerCase()));
    }

    public CampaignMessageStatus getCampaignMessageStatusFor(String statusCode) {
        return statusCodesMap.get(statusCode);
    }

    public String getMainSlotCronJobExpressionFor(String subSlot) {
        return obdProperties.getProperty(String.format("obd.main.sub.slot.%s.cron.job.expression", subSlot.toLowerCase()));
    }

    public String getRetrySlotCronJobExpressionFor(String subSlot) {
        return obdProperties.getProperty(String.format("obd.retry.sub.slot.%s.cron.job.expression", subSlot.toLowerCase()));
    }

    public DateTime getMainSlotStartTimeLimitFor(String subSlot) {
        return parseTime(obdProperties.getProperty(String.format("obd.main.sub.slot.%s.start.time.limit", subSlot.toLowerCase())));
    }

    public DateTime getRetrySlotStartTimeLimitFor(String subSlot) {
        return parseTime(obdProperties.getProperty(String.format("obd.retry.sub.slot.%s.start.time.limit", subSlot.toLowerCase())));
    }

    private DateTime parseTime(String time) {
        return DateTimeFormat.forPattern("HH:mm").parseDateTime(time);
    }
}