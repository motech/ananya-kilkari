package org.motechproject.ananya.kilkari.obd.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FailedCallReports implements Serializable {

    @JsonProperty("callrecords")
    private List<FailedCallReport> failedCallReports = new ArrayList<>();

    @JsonIgnore
    private DateTime createdAt;

    public FailedCallReports() {
        this.createdAt = DateTime.now();
    }

    public List<FailedCallReport> getCallrecords() {
        return failedCallReports;
    }

    @JsonIgnore
    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setFailedCallReports(List<FailedCallReport> failedCallReports) {
        this.failedCallReports = failedCallReports;
    }
}
