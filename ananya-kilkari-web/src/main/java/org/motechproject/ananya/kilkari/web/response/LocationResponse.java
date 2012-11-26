package org.motechproject.ananya.kilkari.web.response;


import org.codehaus.jackson.annotate.JsonProperty;

public class LocationResponse {
    @JsonProperty
    private String district;

    @JsonProperty
    private String block;

    @JsonProperty
    private String panchayat;

    public LocationResponse(String district, String block, String panchayat) {
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


