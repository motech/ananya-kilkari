package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@XmlRootElement
public class LocationRequest implements Serializable {
    private static final long serialVersionUID = -4180457398904997956L;
    @JsonProperty
    @XmlElement
    private String state;
    @JsonProperty
    @XmlElement
    private String district;
    @JsonProperty
    @XmlElement
    private String block;
    @JsonProperty
    @XmlElement
    private String panchayat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationRequest)) return false;

        LocationRequest that = (LocationRequest) o;

        return new EqualsBuilder()
                .append(this.state, that.state)
                .append(this.district, that.district)
                .append(this.block, that.block)
                .append(this.panchayat, that.panchayat)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.state)
                .append(this.district)
                .append(this.block)
                .append(this.panchayat)
                .hashCode();
    }

    @XmlTransient
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @XmlTransient
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @XmlTransient
    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    @XmlTransient
    public String getPanchayat() {
        return panchayat;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

}
