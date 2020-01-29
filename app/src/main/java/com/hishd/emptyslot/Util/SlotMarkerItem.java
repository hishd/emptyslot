package com.hishd.emptyslot.Util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class SlotMarkerItem implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private boolean isPublicParking;
    private String mSnippet = "Parking Area";
    private String lotID;

    public SlotMarkerItem(String lotID, boolean isPublicParking, String title, double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        this.lotID = lotID;
        this.isPublicParking = isPublicParking;
    }

    public SlotMarkerItem(LatLng mPosition, String mTitle, String mSnippet) {
        this.mPosition = mPosition;
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
    }

    public boolean isPublicParking() {
        return isPublicParking;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getParkingID() {
        return lotID;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
