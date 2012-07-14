package org.motechproject.ananya.kilkari.smoke.domain.kilkari;

public class Location{
    private String district;
    private String block;
    private String panchayat;

    public Location(String district, String block, String panchayat) {
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
