package com.rubyproducti9n.unofficialmech;

import static android.content.ContentValues.TAG;
import static com.rubyproducti9n.unofficialmech.Algorithms.isFaculty;
import static com.rubyproducti9n.unofficialmech.Algorithms.selectYear;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.animateVertically;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.context;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadBannerAd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.WindowCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BetaActivity extends AppCompatActivity{
    //Defined variables
    private Home2Adapter adapter;
    private InterstitialAd mInterstitialAd;

    ImageView bgImg1;
    ImageView bgImg2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beta);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        bgImg1 = findViewById(R.id.bgImg1);
        bgImg2 = findViewById(R.id.bgImg2);


        new Task().execute();

//        AdView adView = findViewById(R.id.adView);
//        loadBannerAd(this, adView);
        AdView adView = findViewById(R.id.adView);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                if (initializationStatus != null){
                    Log.d("Admob: ", "Ad received!");
//                    loadBannerAd(adView);
                }else{
                    Log.d("Admob: ", "Error receiving ads!");
                }
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

//        InterstitialAd.load(this,"ca-app-pub-5180621516690353/4142806860", adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
//                        mInterstitialAd = interstitialAd;
//                        Log.i(TAG, "onAdLoaded");
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error
//                        Log.d(TAG, loadAdError.toString());
//                        mInterstitialAd = null;
//                    }
//                });
//        if (mInterstitialAd!=null){
//            mInterstitialAd.show(BetaActivity.this);
//        }else{
//            Toast.makeText(this, "Ad not loaded", Toast.LENGTH_SHORT).show();
//        }

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.matte_black));
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTransitionName("beta");


        MaterialCardView b1 = findViewById(R.id.b1);
        TextView b1_txt = findViewById(R.id.b1_txt);
        b1_txt.setText("Plugins");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog();
                Intent intent = new Intent(BetaActivity.this, PluginsActivity.class);
                ActivityOptionsCompat option = ActivityOptionsCompat.makeSceneTransitionAnimation(BetaActivity.this, b1, "test1");
                startActivity(intent, option.toBundle());
            }
        });

        MaterialCardView b2 = findViewById(R.id.b2);
        TextView b2_txt = findViewById(R.id.b2_txt);
        b2_txt.setText("Letter Assistant");
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FacultyCheckActivity bottomSheetCreateAccount = new FacultyCheckActivity();
//                bottomSheetCreateAccount.show(getSupportFragmentManager(), "BottomSheet");
//                Intent intent = new Intent(BetaActivity.this, StatusActivity.class);
//                startActivity(intent);
            }
        });

        MaterialCardView b3 = findViewById(R.id.b3);
        TextView b3_txt = findViewById(R.id.b3_txt);
        b3_txt.setText("Navigator");
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BetaActivity.this, NavigationAIActivity.class);
                startActivity(intent);
            }
        });

        MaterialCardView b4 = findViewById(R.id.b4);
        TextView b4_txt = findViewById(R.id.b4_txt);
        b4_txt.setText("Attendance Tracker");
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFaculty(BetaActivity.this)){
                    selectYear(BetaActivity.this);
                }else{
                    selectYear(BetaActivity.this);
//                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(BetaActivity.this);
//                    builder.setTitle("Access Denied!")
//                            .setMessage("This feature is only for Faculties")
//                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    builder.show();
                }
//                Intent intent = new Intent(BetaActivity.this, AttendenceTrackerActivity.class);
//                startActivity(intent);
            }
        });

        b1.setTransitionName("beta");
//        b2.setTransitionName("beta");
//        b3.setTransitionName("beta");
//        b4.setTransitionName("beta");

        //Uncomment to add Lottie Animation
//        LottieAnimationView lottieAnimationView = findViewById(R.id.animTest);
//        lottieAnimationView.setAnimation(R.raw.login);
//        lottieAnimationView.loop(true);
//        lottieAnimationView.playAnimation();

        //Fix this (temp stopped)
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(BetaActivity, 1);
//        recyclerView.setLayoutManager(layoutManager);
//        List<BetaItem> items = Arrays.asList(
//                new BetaItem(
//                        "Om Lokhande",
//                        "MainActivity.class")
//        );
//        adapter = new Home2Adapter(items);
//
//        recyclerView.setAdapter(adapter);

    }



    private void dialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(BetaActivity.this);
        builder.setTitle("Beta test");
        builder.setMessage("This feature is in its Developer preview and will be available for beta test soon.");
        builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(downloadCompleteReceiver);
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }


    // Define your AsyncTask class
    private class Task extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            // Perform your background work here
            // Example: Simulate a long-running task
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    animateVertically(BetaActivity.this, bgImg2, -250, 2000);
                    animateVertically(BetaActivity.this, bgImg1, 50, 3000);
                }
            });
            try {
                Thread.sleep(1000); // Sleep for 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed!";
        }

        @Override
        protected void onPostExecute(String result) {
            // Update UI on the main thread after the background task is finished
        }
    }

}