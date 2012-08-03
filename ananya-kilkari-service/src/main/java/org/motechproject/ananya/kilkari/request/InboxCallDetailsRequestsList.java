package org.motechproject.ananya.kilkari.request;


import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;

public class InboxCallDetailsRequestsList {
    @JsonProperty
    private ArrayList<InboxCallDetailsWebRequest> inboxCallDetailsRequests;
}
