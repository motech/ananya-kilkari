package org.motechproject.ananya.kilkari.obd.contract;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;

public class InvalidCallRecordsRequest {

    @JsonProperty
    private ArrayList<InvalidCallRecordRequestObject> callrecords = new ArrayList<>();

    public ArrayList<InvalidCallRecordRequestObject> getCallrecords() {
        return callrecords;
    }
}

