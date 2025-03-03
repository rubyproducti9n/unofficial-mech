package com.rubyproducti9n.unofficialmech;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class MDC extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
