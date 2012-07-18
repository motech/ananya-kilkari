package org.motechproject.ananya.kilkari.obd.contract;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class InvalidCallDeliveryFailureRecord implements Serializable {
    @JsonProperty(value = "msisdn")
    private List<InvalidCallDeliveryFailureRecordObject> recordObjects;

    public void setRecordObjects(List<InvalidCallDeliveryFailureRecordObject> recordObjects) {
        this.recordObjects = recordObjects;
    }

    @JsonIgnore
    public List<InvalidCallDeliveryFailureRecordObject> getRecordObjects() {
        return recordObjects;
    }
}
