package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getSystemAdValue;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAds extends AppCompatActivity {
    public static final int MIN_INTERVAL = 10 * 1000;
    public static final int MAX_INTERVAL = 40 * 1000;
    public static ConstraintLayout localAdLayout;
    public static CircularProgressIndicator progress;
    public static LinearProgressIndicator adProgress;
    TextView timer;
    ExecutorService t;
    ImageView img;

    public static Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ad);
        t = Executors.newFixedThreadPool(10);

        Toast.makeText(this, "You'll be resumed after a short break", Toast.LENGTH_SHORT).show();

        localAdLayout = findViewById(R.id.ad_layout);
        progress = findViewById(R.id.progress);
        adProgress = findViewById(R.id.adProgress);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAd();
            }
        },2000);

        img = findViewById(R.id.img);
        timer = findViewById(R.id.skipTime);
//        alternate();
        timer.setText("");
//        startTime(timer);
    }

    private void loadAd(){
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyAds.this);
                if (getSystemAdValue(pref)){
                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(MyAds.this, "ca-app-pub-5180621516690353/7847130348", adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            loadAlternateAd();
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            interstitialAd.show(MyAds.this);
                            alternate();
                        }
                    });
                }
    }

    private void loadAlternateAd(){
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyAds.this);
                if (getSystemAdValue(pref)){
                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(MyAds.this, "ca-app-pub-5180621516690353/3524741956", adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            showLocalAds();
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            interstitialAd.show(MyAds.this);
                            alternate();
                        }
                    });
                }
    }

    private void showLocalAds(){
        Picasso.get().load("https://github.com/rubyproducti9n/group-3/blob/main/chai.jpg?raw=true").into(img);

        final int progressMax = adProgress.getMax();
        ValueAnimator animator = ValueAnimator.ofInt(0, progressMax);
        animator.setDuration(20000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                adProgress.setProgress((Integer) animation.getAnimatedValue(), true);
            }
        });

        Runnable uiUpdate = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProjectToolkit.fadeOut(progress);
                        ProjectToolkit.fadeIn(localAdLayout);
                        ProjectToolkit.fadeIn(adProgress);
                    }
                });
            }
        };
        animator.start();
        t.execute(uiUpdate);

        t.execute(() -> {
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alternate();
            }
        }, 5000);
        });
        t.shutdown();

    }


    public static void showDialog(Context context){
        final AlertDialog.Builder alertD = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_ad, null);
        alertD.setView(view);

        ImageView imageView = view.findViewById(R.id.img);
        Picasso.get().load("https://rubyproducti9n.github.io/grpup-3/chai.jpg").into(imageView);

        AlertDialog alert = alertD.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
//        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
    }


    private void startTime(TextView view) {
//        for (int i=10;i>1;i--){
//            view.setText("Skip " + i);
//        }
        new CountDownTimer(8000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                view.setText("Skip " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                view.setText("Skip 00");
                Toast.makeText(MyAds.this, "Skipped!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void alternate(){
        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
