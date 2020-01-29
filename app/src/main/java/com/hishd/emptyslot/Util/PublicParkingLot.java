package com.hishd.emptyslot.Util;

import java.util.ArrayList;

public class PublicParkingLot {

    public String parking_name;
    public String landmarks;
    public String available_slots;
    public String special_notes;
    public double location_lat;
    public double location_lon;
    public String image_url;
    public boolean is_temporary;
    public boolean is_approved;
    public boolean smart_parking_active;

    public ArrayList<ParkingNode> parking_nodes;

    public PublicParkingLot() {

    }

    public PublicParkingLot(String parking_name, String landmarks, String available_slots, String special_notes, double location_lat, double location_lon, String image_url, boolean is_temporary, boolean is_approved, boolean smart_parking_active, ArrayList<ParkingNode> parking_nodes) {
        this.parking_name = parking_name;
        this.landmarks = landmarks;
        this.available_slots = available_slots;
        this.special_notes = special_notes;
        this.location_lat = location_lat;
        this.location_lon = location_lon;
        this.image_url = image_url;
        this.is_temporary = is_temporary;
        this.is_approved = is_approved;
        this.smart_parking_active = smart_parking_active;
        this.parking_nodes = parking_nodes;
    }
}
