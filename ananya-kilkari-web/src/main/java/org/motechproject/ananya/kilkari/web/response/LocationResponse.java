package org.motechproject.ananya.kilkari.web.response;


import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocationResponse {
    @JsonProperty
    @XmlElement
    private String district;

    @JsonProperty
    @XmlElement
    private String block;

    @JsonProperty
    @XmlElement
    private String panchayat;

    public LocationResponse() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationResponse that = (LocationResponse) o;

        if (block != null ? !block.equals(that.block) : that.block != null) return false;
        if (district != null ? !district.equals(that.district) : that.district != null) return false;
        if (panchayat != null ? !panchayat.equals(that.panchayat) : that.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = district != null ? district.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        return result;
    }
}


