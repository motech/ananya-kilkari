package org.motechproject.ananya.kilkari.web.response;

import org.motechproject.export.annotation.ExportValue;

public class SubscriberCareDocResponse {

    private String msisdn;

    private String date;

    private String time;

    public SubscriberCareDocResponse(String msisdn, String date, String time) {
        this.msisdn = msisdn;
        this.date = date;
        this.time = time;
    }

    @ExportValue(column = "msisdn", index = 0)
    public String getMsisdn() {
        return msisdn;
    }

    @ExportValue(column = "date", index = 1)
    public String getDate() {
        return date;
    }

    @ExportValue(column = "time", index = 2)
    public String getTime() {
        return time;
    }
}
