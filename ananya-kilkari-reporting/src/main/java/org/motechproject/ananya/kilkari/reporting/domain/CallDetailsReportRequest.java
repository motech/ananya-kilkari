package org.motechproject.ananya.kilkari.reporting.domain;

import java.io.Serializable;

public class CallDetailsReportRequest implements Serializable {
    private String startTime;
    private String endTime;

    public CallDetailsReportRequest(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
