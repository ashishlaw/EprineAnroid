package com.eprine.controller;

import android.content.Context;

import com.eprine.Constants;

public class SharedPrefEmail {

    public static void setStringEmail(Context context, String var_name, String var_value) {

        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME_EMAIL, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(var_name, var_value);
        editor.apply();
    }

    public static String getStringEmail(Context context, String var_value) {

        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME_EMAIL, Context.MODE_PRIVATE);
        return sharedPreferences.getString(var_value, "");

    }
}