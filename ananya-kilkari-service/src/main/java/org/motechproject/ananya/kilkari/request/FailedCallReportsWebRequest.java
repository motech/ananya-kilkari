package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FailedCallReportsWebRequest extends BaseWebRequest implements Serializable {

    @JsonProperty("callrecords")
    private List<FailedCallReport> failedCallReports = new ArrayList<>();

    public List<FailedCallReport> getCallrecords() {
        return failedCallReports;
    }

    public void setFailedCallReports(List<FailedCallReport> failedCallReports) {
        this.failedCallReports = failedCallReports;
    }
}
