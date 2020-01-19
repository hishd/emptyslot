package com.hishd.emptyslot.Util;

public class ParkingInquiries {

    public String ownername;
    public String ownerContactNo;
    public String ownerAddress;
    public String noOfSlots;
    public String ratePerHour;
    public double locationLat;
    public double locationLon;
    public String imageURL;
    public String requestHW;
    public boolean isCompleted;

    public ParkingInquiries() {

    }

    public ParkingInquiries(String ownername, String ownerContactNo, String ownerAddress, String noOfSlots, String ratePerHour, double locationLat, double locationLon, String imageURL, String requestHW, boolean isCompleted) {
        this.ownername = ownername;
        this.ownerContactNo = ownerContactNo;
        this.ownerAddress = ownerAddress;
        this.noOfSlots = noOfSlots;
        this.ratePerHour = ratePerHour;
        this.locationLat = locationLat;
        this.locationLon = locationLon;
        this.imageURL = imageURL;
        this.requestHW = requestHW;
        this.isCompleted = isCompleted;
    }
}
