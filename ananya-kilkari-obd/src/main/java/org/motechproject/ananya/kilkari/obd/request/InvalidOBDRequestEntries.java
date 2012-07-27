package org.motechproject.ananya.kilkari.obd.request;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvalidOBDRequestEntries implements Serializable {

    @JsonProperty("callrecords")
    private List<InvalidOBDRequestEntry> invalidOBDRequestEntryList = new ArrayList<>();

    public List<InvalidOBDRequestEntry> getInvalidOBDRequestEntryList() {
        return invalidOBDRequestEntryList;
    }
}

