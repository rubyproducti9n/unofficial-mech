package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.Algorithms.fetchServer;
import static com.rubyproducti9n.unofficialmech.Algorithms.getYear;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public class ProjectToolkit extends AppCompatActivity {
    public static SharedPreferences p;
    public static String parent = "app-configuration/";
    public static DatabaseReference ref;

    static Context context;
    private static RewardedAd rewardedAd;
    public static ConnectionReceiver receiver = new ConnectionReceiver();

    //Public SharedPreferences
    private static SharedPreferences sharedPreferences;
    static Boolean disableService = Boolean.FALSE;

    private static InterstitialAd mInterstitialAd;
    static AdManager adManager;



    //Public variables that can be called in other activities too

    public static void fServer(Context c){
        getAdValue(c);
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void register(Context c){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("android.app.action.SERVICE_DESTROYED");
        c.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }

    public static void unregister(Context c){
        c.unregisterReceiver(receiver);
    }


    public static void disableStatusBar(Activity activity){
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
    }

    public static boolean fServiceRunning(Context context){
        ActivityManager man = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : man.getRunningServices(Integer.MAX_VALUE)){
            if (ForegroundService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    public static void startF(Context context){
        if (!fServiceRunning(context)){
            Intent intent = new Intent(context, ForegroundService.class);
            context.startForegroundService(intent);
        }
    }

    public static void initiateAds(Activity activity, Context context){
//        adManager = new AdManager(activity,
//                context,
//                null,
//                "ca-app-pub-5180621516690353/8609265521",
//                "ca-app-pub-5180621516690353/2338712470",
//                "ca-app-pub-5180621516690353/4142806860");
//        adManager.loadInterstitialAd();
//        adManager.loadRewardedVideoAd();
    }

//    public static boolean showInterstitial(){
//        return adManager.showInterstitialAd();
//    }
//    public static boolean showReward(){
//        return adManager.showInterstitialAd();
//    }

    public static  void loadBannerAd(AdView adView){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

        public static void prefetchAds(Activity activity, Context context) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    // Load interstitial ad

                    InterstitialAd.load(context, "ca-app-pub-5180621516690353/8609265521", new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            interstitialAd.show(activity);
                        }
                    });
                    return null;
                }
            }.execute();
        }

        public static InterstitialAd getPreloadedInterstitial() {
            return mInterstitialAd;
        }


//    public static InterstitialAd loadInterstitialAd(Context context){
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(context,"ca-app-pub-5180621516690353/4142806860", adRequest,
//            new InterstitialAdLoadCallback() {
//                @Override
//                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                    // The mInterstitialAd reference will be null until
//                    // an ad is loaded.
//                    mInterstitialAd = interstitialAd;
//                    Log.i(TAG, "onAdLoaded");
//                }
//
//                @Override
//                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                    // Handle the error
//                    Log.d(TAG, loadAdError.toString());
//                    mInterstitialAd = null;
//                }
//            });
//        if (mInterstitialAd!=null){
//            mInterstitialAd.show(activity);
//        }else{
//            Toast.makeText(context, "Ad not loaded", Toast.LENGTH_SHORT).show();
//        }
//    }


    public static void loadNativeAd(Context context, ConstraintLayout adContainer, String adUnit) {
        AdLoader.Builder builder = new AdLoader.Builder(context, adUnit)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        //inflate a layout for the ad (replace with your actual layout)
                        View adView = inflater.inflate(R.layout.native_ad_layout, null);

                        // Populate the ad view with the native ad assets
//                        populateNativeAdView(nativeAd, adView);
//                        TextView headline = adView.findViewById(R.id.ad_headline);
//                        headline.setText(nativeAd.getHeadline());
                        TextView body = adView.findViewById(R.id.ad_body);
                        body.setText(nativeAd.getBody());
//                        ImageView icon = adView.findViewById(R.id.ad_icon);
//                        if (nativeAd.getIcon()!=null){
//                            icon.setImageDrawable(nativeAd.getIcon().getDrawable());
//                        }else{
//                            icon.setVisibility(View.GONE);
//                        }

                        adView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle extras = new Bundle();
//                                extras.putString("clicked_ad_unit_id", nativeAd.getAdvertiser()); // Optional: Add custom data
                                nativeAd.performClick(extras);
                            }
                        });

                        // Add the ad view to the container
                        adContainer.removeAllViews(); // Clear any existing ad
                        adContainer.addView(adView);
                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d(MotionEffect.TAG, "onAdFailedToLoad: " + loadAdError);
                    }
                });

        builder.build().loadAd(new AdRequest.Builder().build());
    }

    public static void loadAd(Activity context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (getSystemAdValue(pref)){
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, "ca-app-pub-5180621516690353/8609265521", adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    interstitialAd.show(context);
                }
            });
        }
    }
    public static void loadInterstitialAd(Activity context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (getSystemAdValue(pref)){
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, "ca-app-pub-5180621516690353/7275112879", adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    interstitialAd.show(context);
                }
            });
        }
    }

    public static String getServerUrl(){
        return fetchServer();
    }

    public static boolean checkSystemSetting(SharedPreferences preferences){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("adValue", true);
    }

    public static boolean getSystemAdValue(SharedPreferences preferences){
        return preferences.getBoolean("adValue", true);
    }
    public static boolean getSystemForceUpdate(SharedPreferences preferences){
        return preferences.getBoolean("forceUpdateValue", true);
    }
    public static boolean getSystemLockdown(SharedPreferences preferences){
        return preferences.getBoolean("lockdownValue", false);
    }
    public static boolean getSystemPayment(SharedPreferences preferences){
        return preferences.getBoolean("payValue", true);
    }

    public interface InitialCallback{
        void onInitialsStatus(String gender);
    }


    public static void initiatePanicMode(Context context){

        final String[] items = {"Antiragging", "Other Services"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Panic mode")
//        builder.setMessage("In case of emergency situations, call the college ambulance and know them your location. \n \n" +
//                "Note: Activating Panic mode will share your details to the authority.");
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                antiRagging(context);
                                break;
                            case 1:
                                activatePanicMode(context);
                                break;
                        }
                    }
                })
                .setPositiveButton("Learn more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MaterialAlertDialogBuilder learnMore = new MaterialAlertDialogBuilder(context);
                        learnMore.setTitle("Learn more")
                                .setMessage("In case of any emergency you can directly call emergency services \n\n Note: Activating Panic mode will share your details to the authority.")
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    public static void antiRagging(Context c){
        dial(c, "9552021276");
        shareDetails(c, "Anti-ragging");
    }

    public static void activatePanicMode(Context context){
        final String[] items = {"Ambulance", "Fire Brigade", "Security Officer", "Anti-ragging", "Chief Warden"};

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Panic mode")
//        builder.setMessage("In case of emergency situations, call the college ambulance and know them your location. \n \n" +
//                "Note: Activating Panic mode will share your details to the authority.");
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                dial(context, "8975900900");
                                shareDetails(context, "Ambulance");
                                break;
                            case 1:
                                dial(context, "8411002749");
                                shareDetails(context, "Fire Brigade");
                                break;
                            case 2:
                                dial(context, "9869201190");
                                shareDetails(context, "Security Officer");
                                break;
                            case 3:
                                dial(context, "9552021276");
                                shareDetails(context, "Antiragging");
                                break;
                            case 4:
                                dial(context, "9923607460");
                                shareDetails(context, "Chief Warden");
                                break;
                        }
                    }
                })
                .setPositiveButton("Learn more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MaterialAlertDialogBuilder learnMore = new MaterialAlertDialogBuilder(context);
                        learnMore.setTitle("Learn more")
                                .setMessage("In case of any emergency you can directly call emergency services \n\n Note: Activating Panic mode will share your details to the authority.")
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public static void dial(Context c, String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        c.startActivity(intent);
    }
    public static void shareDetails(Context context, String service){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentUser = preferences.getString("auth_name", null);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String serviceTime = currentDateTime.format(dateTimeFormatter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("panic");
        String panicId = databaseReference.push().getKey();
        assert panicId != null;
        String data = currentUser + " used " + service + " service " + " at " + serviceTime;
        databaseReference.child(panicId).setValue(data);

    }

    public static void getInitials(Context context, InitialCallback callback){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String initials = pref.getString("auth_initials", null);
        if (initials!=null){
            if (initials.startsWith("Mr_") || initials.startsWith("Prof_") || initials.startsWith("Dr_")){
                callback.onInitialsStatus("Male");
            }
            if (initials.startsWith("Mrs_")){
                callback.onInitialsStatus("Female");
            }
        }
    }

    public static Bitmap blurBitmap(Context context, Bitmap bitmap, int radius) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(radius);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        rs.destroy();
        return outBitmap;
    }




    public static Boolean checkSpecialUser(Context context){
        HashSet<String> prnList = new HashSet<>();
        prnList.add("UME21M1002");
        prnList.add("UME21M1003");
        prnList.add("UME21M1004");
        prnList.add("UME21M1005");
        prnList.add("UME21M1007");
        prnList.add("UME21M1009");
        prnList.add("UME21M1010");
        prnList.add("UME21M1012");
        prnList.add("UME21F1013");
        prnList.add("UME21F1014");
        prnList.add("UME21M1016");
        prnList.add("UME21M1017");
        prnList.add("UME21F1018");
        prnList.add("UME21M1019");
        prnList.add("UME21M1020");
        prnList.add("UME21M1021");
        prnList.add("UME21F1024");
        prnList.add("UME21M1025");
        prnList.add("UME21M1026");
        prnList.add("UME21M1027");
        prnList.add("UME21M1028");
        prnList.add("UME21M1029");
        prnList.add("UME21M1030");
        prnList.add("UME21F1031");
        prnList.add("UME21M1033");
        prnList.add("UME21F1034");
        prnList.add("UME21M1037");
        prnList.add("UME21M1038");
        prnList.add("UME21M1039");
        prnList.add("UME21F1041");
        prnList.add("UME21F1042");
        prnList.add("UME21M1043");
        prnList.add("UME21M1044");
        prnList.add("UME21M1045");
        prnList.add("UME21M1046");
        prnList.add("UME21F1047");
        prnList.add("UME21M1049");
        prnList.add("UME21M1050");
        prnList.add("UME21M1051");
        prnList.add("UME21M1052");
        prnList.add("UME21M1053");
        prnList.add("UME21M1054");
        prnList.add("UME21F1055");
        prnList.add("UME21M1057");
        prnList.add("UME21M1058");
        prnList.add("UME21F1059");
        prnList.add("UME21M1061");
        prnList.add("UME21M1062");
        prnList.add("UME21M1063");
        prnList.add("UME21M1064");
        prnList.add("UME21F1065");
        prnList.add("UME21M1066");
        prnList.add("UME21F1067");
        prnList.add("UME21M1069");
        prnList.add("UME21M1070");
        prnList.add("UME21F1071");
        prnList.add("UME21M1072");
        prnList.add("UME21M1073");
        prnList.add("UME21M1074");
        prnList.add("UME21M1075");
        prnList.add("UME21M1076");
        prnList.add("UME21F1077");
        prnList.add("UME21M1079");
        prnList.add("UME21M1081");
        prnList.add("UME21M1084");
        prnList.add("UME21M1085");
        prnList.add("UME21F1086");
        prnList.add("UME21F1087");
        prnList.add("UME21M1088");
        prnList.add("UME21M1089");
        prnList.add("UME21M1091");
        prnList.add("UME21M1092");
        prnList.add("UME21M1093");
        prnList.add("UME21M1094");
        prnList.add("UME21M1095");
        prnList.add("UME21M1096");
        prnList.add("UME21M1097");
        prnList.add("UME21M1098");
        prnList.add("UME21F1099");
        prnList.add("UME21F1100");
        prnList.add("UME21M1102");
        prnList.add("UME21M1103");
        prnList.add("UME21M1104");
        prnList.add("UME21M1105");
        prnList.add("UME21M1106");
        prnList.add("UME21F1107");
        prnList.add("UME21F1109");
        prnList.add("UME21M1110");
        prnList.add("UME21M1112");
        prnList.add("UME21M1114");
        prnList.add("UME21M1117");
        prnList.add("UME21M1118");
        prnList.add("UME21M1119");
        prnList.add("UME21F1121");
        prnList.add("UME21M1122");
        prnList.add("UME21M1123");
        prnList.add("UME21F1124");
        prnList.add("UME21M1125");
        prnList.add("UME21F1126");
        prnList.add("UME21M1127");
        prnList.add("UME21M1128");
        prnList.add("UME21M1129");
        prnList.add("UME21M1130");
        prnList.add("UME21M1131");
        prnList.add("UME21F1132");
        prnList.add("UME21M1080");
        prnList.add("UME22M2025");
        prnList.add("UME22M2028");
        prnList.add("UME22M2032");
        prnList.add("UME22M2033");
        prnList.add("UME22M2034");
        prnList.add("UME22M2035");
        prnList.add("UME22M2036");
        prnList.add("UME22M2038");
        prnList.add("UME22M2041");
        prnList.add("UME22M2042");
        prnList.add("UME22M2043");
        prnList.add("UME22M2044");
        prnList.add("UME22M2045");
        prnList.add("UME22M2046");
        prnList.add("UME22M2047");
        prnList.add("UME22M2048");
        prnList.add("UME22M2049");
        prnList.add("UME22M2050");
        prnList.add("UME22F2002");
        prnList.add("UME22F2026");
        prnList.add("UME22F2037");
        prnList.add("UME22F2052");
        prnList.add("UME22F2055");
        prnList.add("UME22F2079");
        prnList.add("UME22F2090");
        prnList.add("UME22F2099");
        prnList.add("UME22F2108");
        prnList.add("UME22M2051");
        prnList.add("UME22M2054");
        prnList.add("UME22M2057");
        prnList.add("UME22M2059");
        prnList.add("UME22M2061");
        prnList.add("UME22M2062");
        prnList.add("UME22M2063");
        prnList.add("UME22M2064");
        prnList.add("UME22M2066");
        prnList.add("UME22M2067");
        prnList.add("UME22M2068");
        prnList.add("UME22M2070");
        prnList.add("UME22M2071");
        prnList.add("UME22M2073");
        prnList.add("UME22M2074");
        prnList.add("UME22M2075");
        prnList.add("UME22M2076");
        prnList.add("UME22M2077");
        prnList.add("UME22M2078");
        prnList.add("UME22M2080");
        prnList.add("UME22M2081");
        prnList.add("UME22M2082");
        prnList.add("UME22M2083");
        prnList.add("UME22M2084");
        prnList.add("UME22M2085");
        prnList.add("UME22M2086");
        prnList.add("UME22M2087");
        prnList.add("UME22M2089");
        prnList.add("UME22M2091");
        prnList.add("UME22M2092");
        prnList.add("UME22M2093");
        prnList.add("UME22M2095");
        prnList.add("UME22M2096");
        prnList.add("UME22M2098");
        prnList.add("UME22M2101");
        prnList.add("UME22M2102");
        prnList.add("UME22M2103");
        prnList.add("UME22M2104");
        prnList.add("UME22M2105");
        prnList.add("UME22M2107");
        prnList.add("UME22M2110");
        prnList.add("UME22M2111");
        prnList.add("UME22F2007");
        prnList.add("UME22F2012");
        prnList.add("UME22F2014");
        prnList.add("UME22F2100");
        prnList.add("UME22F2106");
        prnList.add("UME22M2009");
        prnList.add("UME22M2021");
        prnList.add("UME22M2027");
        prnList.add("UME22M2030");
        prnList.add("UME22F2065");
        prnList.add("UME22M2039");
        prnList.add("UME22M2040");
        prnList.add("UME22M2056");
        prnList.add("UME22M2058");
        prnList.add("UME22M2060");
        prnList.add("UME22M2069");
        prnList.add("UME22M2072");
        prnList.add("UME22M2097");
        prnList.add("UME22M2109");
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String storedPrn = preferences.getString("auth_prn", null);
        boolean isSpecialUser = false;
        if (storedPrn!=null){
            if (prnList.contains(storedPrn)){
                isSpecialUser = true;
            }
        }else{
            //Toast.makeText(context, "Oops! something went wrong, please re-login to continue", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, PasswordActivity.class);
//            context.startActivity(intent);
        }

        return isSpecialUser;
    }

    public static String getTimeTable(Context context, String div){
        final String[] timetable = {null};
        CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("faculty_console/timetable");
        ref.child(div).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timetable[0] = snapshot.getValue(String.class);
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                latch.countDown();
            }
        });

        try {
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return timetable[0];
    }

    private void updateNotificationButtonVisibility(View disabledView, View enabledView, MaterialButton notiBtn){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();

        if (areNotificationsEnabled){
            disabledView.setVisibility(View.GONE);
            enabledView.setVisibility(View.VISIBLE);
        }else{
            disabledView.setVisibility(View.VISIBLE);
            enabledView.setVisibility(View.GONE);
            notiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                }
            });
        }
    }

    public interface ServiceCheckCallBack{
        void onResult(Boolean result);
    }

    public static void serviceCheck(Context context, ServiceCheckCallBack callback){
        final Boolean[] disableService = {Boolean.FALSE, Boolean.TRUE};
        final Boolean[] finalVal = {null};

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("app-configuration/uploads");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean val = snapshot.getValue(Boolean.class);
                if(val!=null){
                    callback.onResult(val);
                }else{
                    callback.onResult(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    public static Uri compress(Context context, Uri imgUri, int quality){
        Uri compressed = null;

        try{
            //Loading image from the given url
            InputStream inputStream = context.getContentResolver().openInputStream(imgUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            assert inputStream != null;
            inputStream.close();

            //Compress the Bitmap
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            assert originalBitmap != null;
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            //Create a temp comp img file
            File compressedImg = File.createTempFile("compressed_image", ".jpg", context.getCacheDir());
            FileOutputStream fileOutputStream = new FileOutputStream(compressedImg);
            outputStream.writeTo(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            compressed = Uri.fromFile(compressedImg);
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "Image compressed!", Toast.LENGTH_SHORT).show();
//                }
//            });



        }catch (IOException e){
            e.printStackTrace();
        }

        return compressed;
    }


    public static void initiateAdapterDialog(Context context, String title, String msg){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    //For function test purpose
    public static void showToast(){
        Toast.makeText(context, "A simple toast!", Toast.LENGTH_SHORT).show();
    }

    public void updateProfile(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = preferences.getString("auth_userId", null);
        if (userId != null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            databaseReference.child(userId).child("userId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String userId = dataSnapshot.child("userId").getValue(String.class);
                            String firstName = dataSnapshot.child("firstName").getValue(String.class);
                            String lastName = dataSnapshot.child("lastName").getValue(String.class);
                            String personalEmail = dataSnapshot.child("personalEmail").getValue(String.class);
                            String clgEmail = dataSnapshot.child("clgEmail").getValue(String.class);
                            String division = dataSnapshot.child("div").getValue(String.class);
                            String roll = dataSnapshot.child("rollNo").getValue(String.class);
                            String role = dataSnapshot.child("role").getValue(String.class);
                            String suspended = dataSnapshot.child("suspended").getValue(String.class);
                            String verified = dataSnapshot.child("verified").getValue(String.class);
                            String dateCreated = dataSnapshot.child("dateCreated").getValue(String.class);
                            String paymentDate = dataSnapshot.child("lastPayment").getValue(String.class);
                            String altPassword = dataSnapshot.child("altPassword").getValue(String.class);
                            String password = dataSnapshot.child("password").getValue(String.class);
                            String userName = dataSnapshot.child("userName").getValue(String.class);

                            if (userId == null){

                            }

                            if (firstName == null){

                            }

                            if (lastName == null){

                            }

                            if (personalEmail == null){

                            }

                            if (clgEmail == null){

                            }

                            if (division == null){

                            }

                            if (roll == null){

                            }

                            if (role == null){

                            }

                            if (suspended == null){

                            }

                            if (verified == null){

                            }

                            if (dateCreated == null){

                            }

                            if (paymentDate == null){

                            }

                            if (altPassword == null){

                            }

                            if (password == null){

                            }
                            if (userName == null){
                            String setUsername = firstName + " " + lastName;
                            databaseReference.child(userId).child("userName").setValue(setUsername);
                            }

                        }
                    }else{
                        Toast.makeText(context, "User not found please login and try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Intent intent = new Intent(context, PasswordActivity.class);
            startActivity(intent);
        }


    }

// ==========================================ANIMATION KITS===================================================================================
    public static void fadeIn(View view){
        AlphaAnimation fadeInAnim = new AlphaAnimation(0, 1);
        fadeInAnim.setDuration(250);
//        fadeInAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadeInAnim);
    }
    public static void fadeOut(View view){
        AlphaAnimation fadeOutAnim = new AlphaAnimation(1, 0);
        fadeOutAnim.setDuration(250);
//        fadeOutAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        view.setVisibility(View.GONE);
        view.startAnimation(fadeOutAnim);
    }
    public static void setButtonEnabledAnim(View view){
        AlphaAnimation fadeInAnim = new AlphaAnimation(0.5F, 1.0F);
        fadeInAnim.setDuration(200);
        fadeInAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(fadeInAnim);
    }
    public static void setButtonDisabledAnim(View view){
        AlphaAnimation fadeOutAnim = new AlphaAnimation(1.0F, 0.5F);
        fadeOutAnim.setDuration(200);
        fadeOutAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(fadeOutAnim);
    }
    public static void pulseAmin(int animStatus, View view){
        ObjectAnimator pulseAnim = ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.5F, 1.0f);
        pulseAnim.setDuration(2000);
        pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnim.setRepeatCount(ValueAnimator.REVERSE);
        if (animStatus==1){
            if (pulseAnim!=null && !pulseAnim.isRunning()){
                pulseAnim.start();
            }
        }else{
            if (pulseAnim!=null && pulseAnim.isRunning()){
                pulseAnim.cancel();
            }
        }
    }

    public static void animateView(View view, int direction, float offset, int duration, Interpolator interpolator) {
        // Get the current position based on direction (0: X, 1: Y)
        float currentPosition = direction == 0 ? view.getX() : view.getY();

        // Calculate the final position based on offset
        float finalPosition = currentPosition + offset;

        // Choose the animation function based on direction
        if (direction == 0) {
            animateXView(view, currentPosition, finalPosition - currentPosition, duration, 250, interpolator);
        } else {
            animateYView(view, currentPosition, finalPosition - currentPosition, duration, 250, interpolator);
        }
    }
    public static  void animateXView(View view, float fromX, float toX, int duration, int delay, Interpolator interpolator) {
        // Create ObjectAnimator for translationX
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", fromX, toX);

        // Set animation duration and interpolator
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.setInterpolator(interpolator);

        // Start the animation
        animator.start();
    }
    public static void animateYView(View view, float fromY, float toY, int duration, int delay, Interpolator interpolator) {
        // Create ObjectAnimator for translationX
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", fromY, toY);

        // Set animation duration and interpolator
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.setInterpolator(interpolator);

        // Start the animation
        animator.start();
    }
    public static void animateVertically(Context context, View view, int movement, int dur) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();

        // Move the ImageView to the left by 50dp
        int marginInDp = movement;
        float density = context.getResources().getDisplayMetrics().density;
        int marginInPixels = (int) (marginInDp * density);

        // Create an ObjectAnimator for the translationX property
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", marginInPixels);
        animator.setDuration(dur); // Set the animation duration in milliseconds (1 second in this example)
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }
    public static void animateHorizontically(Context context, View view, int movement, int dur) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();

        // Move the ImageView to the left by 50dp
        int marginInDp = movement;
        float density = context.getResources().getDisplayMetrics().density;
        int marginInPixels = (int) (marginInDp * density);

        // Create an ObjectAnimator for the translationX property
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", marginInPixels);
        animator.setDuration(dur); // Set the animation duration in milliseconds (1 second in this example)
        animator.start();
    }

    public static void autoUpdate(Context context, String apkUrl){

        Intent serviceIntent = new Intent(context, ForegroundService.class);
        serviceIntent.putExtra("apk_url", apkUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

    public static void show(Context context, String title, String[] options, RadioGroup.OnCheckedChangeListener listener){

        Dialog dialog = new Dialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.material_dialog_radio_option, null);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        for (String option : options){
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }
        dialog.setContentView(view);
        radioGroup.setOnCheckedChangeListener(listener);

        dialog.show();

    }

    public static boolean isBtech(String prn){
        String yr = getYear(prn);
        if (yr.equals("B.Tech")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isAdmin(Context c){
        String r = pref(c).getString("auth_userole", null);
        return r.equals("Admin");
    }
    public static boolean isStudent(Context c){
        String r = pref(c).getString("auth_userole", null);
        return r.equals("Student");
    }
    public static boolean isFaculty(Context c){
        String r = pref(c).getString("auth_userole", null);
        return r.equals("Faculty");
    }
    public static boolean isMale(Context c){
        String r = pref(c).getString("auth_gender", null);
        return r.equals("Male");
    }
    public static boolean isFemale(Context c){
        String r = pref(c).getString("auth_gender", null);
        return r.equals("Female");
    }
    public static boolean isHOD(Context c){
        String r = pref(c).getString("auth_userole", null);
        return r.equals("HOD");
    }

    public static SharedPreferences pref(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c);
    }

    public static void getAdValue(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "ad_value");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                edit.putBoolean("adValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putBoolean("adValue", true);
            }
        });
        getUpdate(c);
        edit.apply();
    }

    public static void getUpdate(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "forceUpdate");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                edit.putBoolean("forceUpdateValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putBoolean("forceUpdateValue", true);
            }
        });
        getLockdownVal(c);
        edit.apply();
    }

    public static void getLockdownVal(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "lockdown");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                edit.putBoolean("lockdownValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putBoolean("lockdownValue", false);
            }
        });
        getNotificationVal(c);
        edit.apply();
    }

    public static void getNotificationVal(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "notifications");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                edit.putBoolean("notificationValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putBoolean("notificationValue", true);
            }
        });
        getPaymentVal(c);
        edit.apply();
    }

    public static void getPaymentVal(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "payments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                edit.putBoolean("payValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putBoolean("payValue", true);
            }
        });
        getServerStatus(c);
        edit.apply();
    }

    public static void getServerStatus(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "status");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                edit.putBoolean("statusValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putBoolean("statusValue", true);
            }
        });
        getUploadVal(c);
        edit.apply();
    }

    public static void getUploadVal(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "uploads");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                edit.putBoolean("uploadsValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putBoolean("uploadsValue", true);
            }
        });
        getVersion(c);
        edit.apply();
    }

    public static void getVersion(Context c){
        SharedPreferences.Editor edit = pref(c).edit();
        ref = FirebaseDatabase.getInstance().getReference(parent + "version");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.getValue(String.class);
                edit.putString("versionValue", val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                edit.putString("versionValue", null);
            }
        });
        edit.apply();
    }

    public static boolean isEncoded(String str) {
        if (str.length() % 2 != 0) {
            return false; // Not a valid hex string if odd length
        }

        try {
            Integer.parseInt(str, 16); // Try parsing as hex
            return true;
        } catch (NumberFormatException e) {
            return false; // Not a valid hex string
        }
    }

    public static String h(Context c, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            storeH(c, password);
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void storeH(Context c, String p) {
        SharedPreferences i = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor e = i.edit();
        e.putString("pass", p);
        e.apply();
    }

    public static String getH(Context c) {
        SharedPreferences i = android.preference.PreferenceManager.getDefaultSharedPreferences(c);
        return i.getString("pass", null);
    }

    //Other functions goes here
    public ProjectToolkit(SharedPreferences preferences){
        this.sharedPreferences = preferences;
    }
    public String getUserName(String key){
        return sharedPreferences.getString(key, null);
    }
    public String getUserEmail(String key){
        return sharedPreferences.getString(key, null);
    }
    public String getUserPrnNo(String key){
        return sharedPreferences.getString(key, null);
    }
    public String getUserDivision(String key){
        return sharedPreferences.getString(key, null);
    }
    public String getUserGender(String key){
        return sharedPreferences.getString(key, null);
    }
    public String getUserRoll(String key){
        return sharedPreferences.getString(key, null);
    }
    public String getUserId(String key){
        return sharedPreferences.getString(key, null);
    }

}
