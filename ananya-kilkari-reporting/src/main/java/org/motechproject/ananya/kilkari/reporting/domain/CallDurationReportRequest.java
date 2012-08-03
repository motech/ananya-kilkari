package org.motechproject.ananya.kilkari.reporting.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class CallDurationReportRequest implements Serializable {
    private DateTime startTime;
    private DateTime endTime;

    public CallDurationReportRequest(DateTime startTime, DateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }
}
