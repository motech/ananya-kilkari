package org.motechproject.ananya.kilkari.obd.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class OBDEndPoints {

    private Properties obdProperties;

    @Autowired
    public OBDEndPoints(@Qualifier("obdProperties") Properties obdProperties) {
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

    public String getNewMessageDeliveryUrlQueryString() {
        return obdProperties.getProperty("obd.new.message.delivery.url.query.string");
    }

    public String getRetryMessageDeliveryUrlQueryString() {
        return obdProperties.getProperty("obd.retry.message.delivery.url.query.string");
    }
}

