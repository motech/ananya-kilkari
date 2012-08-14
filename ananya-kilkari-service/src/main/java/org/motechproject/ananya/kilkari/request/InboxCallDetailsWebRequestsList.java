package org.motechproject.ananya.kilkari.request;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;

public class InboxCallDetailsWebRequestsList extends BaseWebRequest{
    @JsonProperty
    private ArrayList<InboxCallDetailsWebRequest> callrecords;

    @JsonIgnore
    public ArrayList<InboxCallDetailsWebRequest> getCallRecords() {
        return callrecords;
    }
}
