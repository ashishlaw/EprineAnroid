package com.eprine;

import android.app.Activity;
import android.content.Context;

public class SharedPreferences {

    public static void setStr(Context context, String var_name, String var_value) {

        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(var_name, var_value);
        editor.apply();
    }
    public static void setString(Context context, String var_name, String var_value) {

        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(var_name, var_value);
        editor.apply();
    }

    public static String getString(Context context, String var_value) {

        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(var_value, "");

    }

    public static void clear(Activity act) {
        android.content.SharedPreferences pref = act.getSharedPreferences(Constants.PREF_NAME,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.apply();
    }

}
