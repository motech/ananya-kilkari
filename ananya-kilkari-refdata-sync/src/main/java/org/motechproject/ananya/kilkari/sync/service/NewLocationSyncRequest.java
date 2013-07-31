package org.motechproject.ananya.kilkari.sync.service;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class NewLocationSyncRequest implements Serializable {
    private static final long serialVersionUID = 5630093949090637856L;
    @JsonProperty
    private String district;
    @JsonProperty
    private String block;
    @JsonProperty
    private String panchayat;

    public NewLocationSyncRequest(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public String getDistrict() {
        return district;
    }

    public String getBlock() {
        return block;
    }

    public String getPanchayat() {
        return panchayat;
    }
}
