package com.hishd.emptyslot.Util;

import java.util.HashMap;

public class PrivateParkingLot {

    public String availability;
    public HashMap<String, Boolean> facilities;
    public String image_url;
    public String info;
    public Double loc_lat;
    public Double loc_lng;
    public String lot_name;
    public HashMap<String, ParkingNode> parking_slots;
    public int rate_hr;
    public boolean smart_parking_active;
    public int total_slots;
    public int available_slots;
    public int booked_slots;
    public int accomodated_slots;
    public HashMap<String, Booking> bookings;

    public PrivateParkingLot() {

    }

    public PrivateParkingLot(String availability, HashMap<String, Boolean> facilities, String image_url, String info, Double loc_lat, Double loc_lng, String lot_name, HashMap<String, ParkingNode> parking_slots, int rate_hr, boolean smart_parking_active, int total_slots, int available_slots, int booked_slots, int accomodated_slots) {
        this.availability = availability;
        this.facilities = facilities;
        this.image_url = image_url;
        this.info = info;
        this.loc_lat = loc_lat;
        this.loc_lng = loc_lng;
        this.lot_name = lot_name;
        this.parking_slots = parking_slots;
        this.rate_hr = rate_hr;
        this.smart_parking_active = smart_parking_active;
        this.total_slots = total_slots;
        this.available_slots = available_slots;
        this.booked_slots = booked_slots;
        this.accomodated_slots = accomodated_slots;
    }
}
