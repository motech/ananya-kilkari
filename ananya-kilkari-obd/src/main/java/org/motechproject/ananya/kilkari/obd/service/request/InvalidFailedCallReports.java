package org.motechproject.ananya.kilkari.obd.service.request;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class InvalidFailedCallReports implements Serializable {
    @JsonProperty(value = "msisdn")
    private List<InvalidFailedCallReport> recordObjectFaileds;

    public void setRecordObjectFaileds(List<InvalidFailedCallReport> recordObjectFaileds) {
        this.recordObjectFaileds = recordObjectFaileds;
    }

    @JsonIgnore
    public List<InvalidFailedCallReport> getRecordObjectFaileds() {
        return recordObjectFaileds;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
