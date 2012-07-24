package org.motechproject.ananya.kilkari.obd.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class OBDProperties {

    private Properties obdProperties;

    @Autowired
    public OBDProperties(@Qualifier("obdProperties") Properties obdProperties) {
        this.obdProperties = obdProperties;
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
}

