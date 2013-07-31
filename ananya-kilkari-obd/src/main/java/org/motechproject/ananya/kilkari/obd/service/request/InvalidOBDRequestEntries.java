package org.motechproject.ananya.kilkari.obd.service.request;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvalidOBDRequestEntries implements Serializable {

    private static final long serialVersionUID = -7820694103205706162L;
    @JsonProperty("callrecords")
    private List<InvalidOBDRequestEntry> invalidOBDRequestEntryList = new ArrayList<>();

    public List<InvalidOBDRequestEntry> getInvalidOBDRequestEntryList() {
        return invalidOBDRequestEntryList;
    }
}

