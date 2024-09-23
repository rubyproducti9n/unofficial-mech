package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;

import java.util.List;

public class TourManager {

    private List<View> tourSteps;
    private int currentStep = 0;
    Activity activity;

    public TourManager(List<View> tourSteps, Activity activity){
        this.tourSteps = tourSteps;
        this.currentStep = 0;
        this.activity = activity;
    }

    public void startTour(){
        showCurrentStep();
    }

    public void nextStep(){
        if (currentStep<tourSteps.size()){
            currentStep++;
            showCurrentStep();
        }else{
            dismissTour();
        }
    }

    public void previousStep(){
        if (currentStep>0){
            currentStep--;
            showCurrentStep();
        }
    }

    private void showCurrentStep(){
        for (View step : tourSteps){
            step.setVisibility(View.GONE);
        }
        tourSteps.get(currentStep).setVisibility(View.VISIBLE);
    }

    private void dismissTour(){
        for (View step : tourSteps){
            step.setVisibility(View.GONE);
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("tour_completed", true);
        editor.apply();

    }

}
