package com.hishd.emptyslot.Util;

public class PublicParkingNode {

    public String slot_id;
    public boolean isAvailable;

    public PublicParkingNode() {

    }

    public PublicParkingNode(String slot_id, boolean isAvailable) {
        this.slot_id = slot_id;
        this.isAvailable = isAvailable;
    }
}
