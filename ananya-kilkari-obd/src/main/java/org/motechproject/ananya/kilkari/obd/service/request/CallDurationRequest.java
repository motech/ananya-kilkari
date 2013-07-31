package org.motechproject.ananya.kilkari.obd.service.request;

import org.joda.time.DateTime;

import java.io.Serializable;

public class CallDurationRequest implements Serializable {
    private static final long serialVersionUID = 6717379342660672535L;
    private DateTime startTime;

    private DateTime endTime;

    public CallDurationRequest(DateTime startTime, DateTime endTime) {
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
