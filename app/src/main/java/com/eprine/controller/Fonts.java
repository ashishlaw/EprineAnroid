package com.eprine.controller;

import android.content.Context;
import android.graphics.Typeface;

public class Fonts {

    public static Typeface setLatoRegular(Context context) {

        return Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
    }

    public static Typeface setRalewayBold(Context context) {

        return Typeface.createFromAsset(context.getAssets(), "Raleway_Bold.ttf");
    }

    public static Typeface setRalewayRegular(Context context) {

        return Typeface.createFromAsset(context.getAssets(), "Raleway-Regular.ttf");
    }


}