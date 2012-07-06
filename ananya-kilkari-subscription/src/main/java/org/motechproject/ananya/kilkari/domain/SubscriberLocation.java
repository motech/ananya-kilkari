package org.motechproject.ananya.kilkari.domain;

import java.io.Serializable;

public class SubscriberLocation implements Serializable {
    private String district;

    private String block;

    private String panchayat;

    public SubscriberLocation() {

    }

    public SubscriberLocation(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }
}
