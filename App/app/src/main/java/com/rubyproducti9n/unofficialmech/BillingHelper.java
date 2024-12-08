package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.telephony.SubscriptionPlan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BillingHelper {

    private static final String TAG = "PlayBillingManager";

    private BillingClient billingClient;
    private Activity activity;
    private List<SkuDetails> skuDetailsList = new ArrayList<>();
    private HashMap<String, String> planDetails = new HashMap<>();

    public BillingHelper(Activity activity) {
        this.activity = activity;
        initializeBillingClient();
    }

    private void initializeBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing setup successful");
                    queryAvailableProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected");
            }
        });
    }

    private void queryAvailableProducts() {
        List<String> skuList = new ArrayList<>();
        skuList.add("basic_plan");
        skuList.add("standard_plan");
        skuList.add("premium_plan");

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                this.skuDetailsList = skuDetailsList;
                for (SkuDetails skuDetails : skuDetailsList) {
                    planDetails.put(skuDetails.getSku(), skuDetails.getPrice());
                }
            }
        });
    }

    public void purchasePlan(String skuId) {
        SkuDetails skuDetails = getSkuDetailsById(skuId);
        if (skuDetails != null) {
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            billingClient.launchBillingFlow(activity, flowParams);
        } else {
            Log.d(TAG, "SKU details not found for: " + skuId);
        }
    }

    private SkuDetails getSkuDetailsById(String skuId) {
        for (SkuDetails skuDetails : skuDetailsList) {
            if (skuDetails.getSku().equals(skuId)) {
                return skuDetails;
            }
        }
        return null;
    }

    public void checkPurchasedPlans() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (Purchase purchase : purchases) {
                    Log.d(TAG, "Purchased: " + purchase.getProducts());
                    // Handle the purchase as needed
                }
            }
        });
    }

    public boolean isPlanActive(String skuId) {
        for (SkuDetails skuDetails : skuDetailsList) {
            if (skuDetails.getSku().equals(skuId) && planDetails.containsKey(skuId)) {
                return true; // User has this plan
            }
        }
        return false; // User does not have the plan
    }

    public String getPlanValue(String skuId) {
        return planDetails.get(skuId);
    }

    public PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    // Grant entitlement to the user
                    Log.d(TAG, "Purchase successful: " + purchase.getProducts());
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.d(TAG, "User canceled the purchase");
            } else {
                Log.d(TAG, "Error purchasing: " + billingResult.getDebugMessage());
            }
        }
    } ;


}
