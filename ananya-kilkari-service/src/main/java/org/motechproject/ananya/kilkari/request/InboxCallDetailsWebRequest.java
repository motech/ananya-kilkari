package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;

public class InboxCallDetailsWebRequest extends CallDetailsWebRequest {
    @JsonProperty
    private String pack;

    public InboxCallDetailsWebRequest(CampaignMessageCallSource callSource) {
        super(callSource);
    }

    @JsonIgnore
    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OBDSuccessfulCallDetailsWebRequest)) return false;

        InboxCallDetailsWebRequest that = (InboxCallDetailsWebRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.pack, that.pack)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.pack)
                .hashCode();
    }
}
