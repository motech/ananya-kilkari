package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'SubscriberCareDoc'")
public class SubscriberCareDoc extends MotechBaseDataObject {
    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String reason;

    public SubscriberCareDoc() {
    }

    public SubscriberCareDoc(String msisdn, String reason) {
        this.msisdn = msisdn;
        this.reason = reason;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getReason() {
        return reason;
    }
}
