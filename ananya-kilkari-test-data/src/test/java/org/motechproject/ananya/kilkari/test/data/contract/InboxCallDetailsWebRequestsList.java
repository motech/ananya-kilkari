package org.motechproject.ananya.kilkari.test.data.contract;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;

import java.util.ArrayList;

public class InboxCallDetailsWebRequestsList {
    @JsonProperty
    private ArrayList<InboxCallDetailsWebRequest> callrecords;

    @JsonIgnore
    public ArrayList<InboxCallDetailsWebRequest> getCallRecords() {
        return callrecords;
    }

    public InboxCallDetailsWebRequestsList(ArrayList<InboxCallDetailsWebRequest> callrecords) {
        this.callrecords = callrecords;
    }
}
