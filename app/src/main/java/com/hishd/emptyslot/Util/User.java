package com.hishd.emptyslot.Util;

import java.util.ArrayList;

public class User {

    public String username;
    public String email;
    public String password;
    public String phone;

    public ArrayList<String> vehicles;

    public User() {

    }

    public User(String username, String email, String phone, String password, ArrayList<String> vehicles) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.vehicles = vehicles;
    }
}
