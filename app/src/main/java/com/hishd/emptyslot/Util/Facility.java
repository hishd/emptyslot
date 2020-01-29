package com.hishd.emptyslot.Util;

public class Facility {
    String facilityName;
    int resID;
    int background;

    public Facility(String facilityName, int resID, int background) {
        this.facilityName = facilityName;
        this.resID = resID;
        this.background = background;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }
}
