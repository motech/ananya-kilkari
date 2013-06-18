package org.motechproject.ananya.kilkari.subscription.service.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Location {
    private String panchayat;
    private String block;
    private String district;
    public static final Location NULL = new Location();

    public Location(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    private Location() {
    }

    public String getPanchayat() {
        return panchayat;
    }

    public String getBlock() {
        return block;
    }

    public String getDistrict() {
        return district;
    }

    public boolean isEmpty() {
        return panchayat == null && block == null && district == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location that = (Location) o;

        return new EqualsBuilder().append(this.panchayat, that.panchayat)
                .append(this.block, that.block)
                .append(this.district, that.district)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.panchayat)
                .append(this.block)
                .append(this.district)
                .hashCode();
    }


}
