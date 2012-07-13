package org.motechproject.ananya.kilkari.reporting.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class SubscriberLocation implements Serializable {
    @JsonProperty
    private String district;
    @JsonProperty
    private String block;
    @JsonProperty
    private String panchayat;

    public SubscriberLocation() {
    }

    public SubscriberLocation(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    @JsonIgnore
    public String getDistrict() {
        return district;
    }

    @JsonIgnore
    public String getBlock() {
        return block;
    }

    @JsonIgnore
    public String getPanchayat() {
        return panchayat;
    }
}
