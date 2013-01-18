package org.motechproject.ananya.kilkari.web.response;

import org.motechproject.export.annotation.ExportValue;

public class SubscriberCareDocResponse {
    private String msisdn;
    private String date;
    private String time;
    private String reason;

    public SubscriberCareDocResponse(String msisdn, String reason, String date, String time) {
        this.msisdn = msisdn;
        this.date = date;
        this.time = time;
        this.reason = reason;
    }

    @ExportValue(column = "msisdn", index = 0)
    public String getMsisdn() {
        return msisdn;
    }

    @ExportValue(column = "reason", index = 1)
    public String getReason() {
        return reason;
    }

    @ExportValue(column = "date", index = 2)
    public String getDate() {
        return date;
    }

    @ExportValue(column = "time", index = 3)
    public String getTime() {
        return time;
    }
}
