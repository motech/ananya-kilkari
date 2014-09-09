package org.motechproject.ananya.kilkari.obd.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.OBDSubSlot;
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

    public Integer getMaximumOBDRetryDays() {
        return Integer.parseInt(obdProperties.getProperty("obd.max.retry.days"));
    }

    public String getSlotStartTimeFor(OBDSubSlot subSlot) {
        return obdProperties.getProperty(String.format("obd.%s.sub.slot.start.time", subSlot.getSlotName().toLowerCase()));
    }

    public String getSlotEndTimeFor(OBDSubSlot subSlot) {
        return obdProperties.getProperty(String.format("obd.%s.sub.slot.end.time", subSlot.getSlotName().toLowerCase()));
    }

    public CampaignMessageStatus getCampaignMessageStatusFor(String statusCode) {
        return statusCodesMap.get(statusCode);
    }

    public String getCronJobExpressionFor(OBDSubSlot subSlot) {
        return obdProperties.getProperty(String.format("obd.%s.sub.slot.cron.job.expression", subSlot.getSlotName().toLowerCase()));
    }

    public DateTime getSlotStartTimeLimitFor(OBDSubSlot subSlot) {
        return parseTime(obdProperties.getProperty(String.format("obd.%s.sub.slot.start.time.limit", subSlot.getSlotName().toLowerCase())));
    }

    public DateTime getSlotEndTimeLimitFor(OBDSubSlot subSlot) {
        return parseTime(obdProperties.getProperty(String.format("obd.%s.sub.slot.end.time.limit", subSlot.getSlotName().toLowerCase())));
    }

    private DateTime parseTime(String time) {
        return DateTimeFormat.forPattern("HH:mm").parseDateTime(time);
    }

    public Integer getSlotMessagePercentageFor(OBDSubSlot subSlot) {
        String percentage = obdProperties.getProperty(String.format("obd.%s.sub.slot.message.percentage.to.send", subSlot.getSlotName().toLowerCase()));
        return percentage != null ? Integer.parseInt(percentage) : null;
    }

    public Integer getRetryIntervalForMessageUpdate() {
        return Integer.parseInt(obdProperties.getProperty("obd.retry.sent.messages.update.retry.interval"));
    }

    public Integer getRetryCountForMessageUpdate() {
        return Integer.parseInt(obdProperties.getProperty("obd.retry.sent.messages.update.max.retry.count"));
    }

    public Integer getInitialWaitForMessageUpdate() {
        return Integer.parseInt(obdProperties.getProperty("obd.retry.sent.messages.update.initial.wait"));
    }

    public CampaignMessageStatus getDefaultCampaignMessageStatus() {
        return CampaignMessageStatus.getFor(obdProperties.getProperty("obd.default.campaign.message.status"));
    }
    
    public Integer getBufferTime() {
    	return Integer.parseInt(obdProperties.getProperty("obd.message.buffer.minutes"));
    }
}