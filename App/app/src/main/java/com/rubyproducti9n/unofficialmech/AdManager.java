package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadInterstitialAd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdManager {

    private static final
    String AD_LIMIT_KEY = "adLimit";
    private static final float MAX_ADS_PER_SESSION = 1.0f;

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private float mAdLimit;

    private AdView mBannerAd;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    public AdManager(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mAdLimit = mSharedPreferences.getFloat(AD_LIMIT_KEY, 1f);

        MobileAds.initialize(mContext);
    }

    public static  void loadBannerAd(AdView adView){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void loadInterstitialAd(Activity activity) {
        if (shouldShowAd()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(activity, "ca-app-pub-5180621516690353/7275112879", adRequest, new InterstitialAdLoadCallback() {
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
        }
    }

    public void loadRewardedInterstitialAd(Activity activity) {
        if (shouldShowAd()) {
            RewardedAd.load(activity, "ca-app-pub-5180621516690353/6410422068", // Replace with your rewarded ad unit ID
                    new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            // Handle ad loading failure
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            // Ad loaded successfully, ready to show
                        }
                    });
        }
    }



    private boolean shouldShowAd() {
        if (mAdLimit <= 0) {
            return false; // No ads allowed
        }

        double random = Math.random();
        return random <= mAdLimit;
    }



}
