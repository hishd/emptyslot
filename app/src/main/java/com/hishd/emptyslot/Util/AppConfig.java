package com.hishd.emptyslot.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {

    private SharedPreferences sharedPreferences;
    Context context;
    static SharedPreferences.Editor editor;

    public AppConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("EMPTY_SLOT", Context.MODE_PRIVATE);
    }

    public void setAppIntroFinished() {
        editor = sharedPreferences.edit();
        editor.putBoolean("INTRO_FINISHED", true);
        editor.apply();
    }

    public boolean isAppIntroFinished() {
        return sharedPreferences.getBoolean("INTRO_FINISHED", false);
    }

    public void setUserLoggedIn() {
        editor = sharedPreferences.edit();
        editor.putBoolean("LOGGED_IN", true);
        editor.apply();
    }

    public void setUserLoggedOut() {
        editor = sharedPreferences.edit();
        editor.putBoolean("LOGGED_IN", false);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("LOGGED_IN", false);
    }

    public void setLoggedUserID(String userID) {
        editor = sharedPreferences.edit();
        editor.putString("LOGGED_USER_ID", userID);
        editor.apply();
    }

    public String getLogedUserID() {
        return sharedPreferences.getString("LOGGED_USER_ID", null);
    }

}
