package org.motechproject.ananya.kilkari.obd.request;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FailedCallReports implements Serializable {

    @JsonProperty("callrecords")
    private List<FailedCallReport> failedCallReports = new ArrayList<>();

    public List<FailedCallReport> getCallrecords() {
        return failedCallReports;
    }

    public void setFailedCallReports(List<FailedCallReport> failedCallReports) {
        this.failedCallReports = failedCallReports;
    }
}
