package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvalidOBDRequestEntriesWebRequest extends BaseWebRequest implements Serializable {

    @JsonProperty("callrecords")
    private List<InvalidOBDRequestEntry> invalidOBDRequestEntryList = new ArrayList<>();

    public List<InvalidOBDRequestEntry> getInvalidOBDRequestEntryList() {
        return invalidOBDRequestEntryList;
    }
}

