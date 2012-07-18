package org.motechproject.ananya.kilkari.obd.contract;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallDeliveryFailureRecord implements Serializable {

    @JsonProperty
    private List<CallDeliveryFailureRecordObject> callDeliveryFailureRecordObjects = new ArrayList<>();

    @JsonIgnore
    private DateTime createdAt;

    public CallDeliveryFailureRecord() {
        this.createdAt = DateTime.now();
    }

    public List<CallDeliveryFailureRecordObject> getCallrecords() {
        return callDeliveryFailureRecordObjects;
    }

    @JsonIgnore
    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCallDeliveryFailureRecordObjects(List<CallDeliveryFailureRecordObject> callDeliveryFailureRecordObjects) {
        this.callDeliveryFailureRecordObjects = callDeliveryFailureRecordObjects;
    }
}
