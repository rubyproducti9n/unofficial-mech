package com.rubyproducti9n.unofficialmech;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class MDC extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences pref0 = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme = pref0.getBoolean("theme", false);
        if (theme){

        }
        DynamicColors.applyToActivitiesIfAvailable(this);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
