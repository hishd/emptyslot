package com.hishd.emptyslot.Util;

public class Booking {
    public String owner_id;
    public String vehicle_no;
    public String placed_timestamp;
    public String lot_id;
    public String lot_name;

    public Booking() {
    }

    public Booking(String owner_id, String vehicle_no, String placed_timestamp, String lot_id, String lot_name) {
        this.owner_id = owner_id;
        this.vehicle_no = vehicle_no;
        this.placed_timestamp = placed_timestamp;
        this.lot_id = lot_id;
        this.lot_name = lot_name;
    }
}
