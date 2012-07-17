package org.motechproject.ananya.kilkari.obd.contract;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class InvalidCallRecordsRequest implements Serializable {

    @JsonProperty
    private ArrayList<InvalidCallRecordRequestObject> callrecords = new ArrayList<>();

    public ArrayList<InvalidCallRecordRequestObject> getCallrecords() {
        return callrecords;
    }
}

