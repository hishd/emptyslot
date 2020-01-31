package com.hishd.emptyslot.Util;

public class MyVehicle {
    public String reg_no;
    public String vehicle_model;
    public String vehicle_category;

    public MyVehicle() {
    }

    public MyVehicle(String reg_no, String vehicle_model, String vehicle_category) {
        this.reg_no = reg_no;
        this.vehicle_model = vehicle_model;
        this.vehicle_category = vehicle_category;
    }

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public String getVehicle_category() {
        return vehicle_category;
    }

    public void setVehicle_category(String vehicle_category) {
        this.vehicle_category = vehicle_category;
    }
}
