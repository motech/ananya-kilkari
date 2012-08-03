package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class InboxCallDetailsWebRequest extends CallDetailsWebRequest {
    @JsonProperty
    private String pack;

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
        if (!(o instanceof InboxCallDetailsWebRequest)) return false;

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
