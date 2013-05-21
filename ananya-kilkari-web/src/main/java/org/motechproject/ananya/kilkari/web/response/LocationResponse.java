package org.motechproject.ananya.kilkari.web.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocationResponse {

    @JsonProperty
    @XmlElement
    private String block;

    @JsonProperty
    @XmlElement
    private String state;

    @JsonProperty
    @XmlElement
    private String panchayat;

    @JsonProperty
    @XmlElement
    private String district;

    public LocationResponse() {
    }

    public LocationResponse(String state, String district, String block, String panchayat) {
        this.state = state;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public String getBlock() {
        return block;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.district)
                .append(this.block)
                .append(this.panchayat)
                .hashCode();
    }
}
