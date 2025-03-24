package com.rubyproducti9n.unofficialmech;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class PricingManager {
    private static PricingManager instance;
    private Context context;
    private BillingClient billingClient;
    private ProductDetails productDetailsForBasic;
    private ProductDetails productDetailsForStandard;
    private ProductDetails productDetailsForPremium;
    private boolean isBasic, isStandard, isPremium;
    private float adLimit;
    private boolean geminiFlashEnabled;

    private PricingManager(Context context) {
        this.context = context.getApplicationContext();
        initiate();
    }

    public static PricingManager getInstance(Context context) {
        if (instance == null) {
            instance = new PricingManager(context);
        }
        return instance;
    }

    private void initiate() {
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryAvailableSubscriptions();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                retryConnection();
            }
        });
    }

    private void queryAvailableSubscriptions() {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("basic_plan")
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build(),
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("standard_subscription")
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build(),
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("premium_subscription")
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                    for (ProductDetails productDetails : productDetailsList) {
                        switch (productDetails.getProductId()) {
                            case "basic_plan":
                                productDetailsForBasic = productDetails;
                                break;
                            case "standard_subscription":
                                productDetailsForStandard = productDetails;
                                break;
                            case "premium_subscription":
                                productDetailsForPremium = productDetails;
                                break;
                        }
                    }
                    checkUserSubscription();
                }
            }
        });
    }

    private void checkUserSubscription() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                isBasic = false;
                isStandard = false;
                isPremium = false;

                for (Purchase purchase : purchases) {
                    ArrayList<String> skus = purchase.getSkus();
                    if (skus.equals("basic_plan")) {
                        isBasic = true;
                        adLimit = 0.25f; // Limited ads
                    } else if (skus.equals("standard_subscription")) {
                        isStandard = true;
                        geminiFlashEnabled = true; // Enable Gemini Flash
                    } else if (skus.equals("premium_subscription")) {
                        isPremium = true;
                        adLimit = 0.0f; // No ads
                    }
                }

                // Save subscription details in SharedPreferences for use in other activities
                saveSubscriptionDetails();
            }
        });
    }

    private void saveSubscriptionDetails() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isBasic", isBasic);
        editor.putBoolean("isStandard", isStandard);
        editor.putBoolean("isPremium", isPremium);
        editor.putFloat("adLimit", adLimit);
        editor.putBoolean("geminiFlashEnabled", geminiFlashEnabled);
        editor.apply();
    }

    private void retryConnection() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            queryAvailableSubscriptions();
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {
                        retryConnection();
                    }
                });
            }
        }, 1000); // Retry after 1 second
    }
    // Methods to get subscription status
    public boolean isBasic() { return isBasic; }
    public boolean isStandard() { return isStandard; }
    public boolean isPremium() { return isPremium; }
    public float getAdLimit() { return adLimit; }
    public boolean isGeminiFlashEnabled() { return geminiFlashEnabled; }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> Purchase) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && Purchase != null) {
                for (Purchase purchase : Purchase) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Toast.makeText(context, "Current", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    };

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener =
                    new AcknowledgePurchaseResponseListener() {
                        @Override
                        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                // Grant entitlement to the user.
                            }
                        }
                    };

            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
        }
    }
//
//    public static String getBasicPlanPrice(){
//
//        ExecutorService e = Executors.newSingleThreadExecutor();
//        e.execute(new Runnable() {
//            @Override
//            public void run() {
//                billingClient.startConnection(new BillingClientStateListener() {
//                    @Override
//                    public void onBillingServiceDisconnected() {
//
//                    }
//
//                    @Override
//                    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
//                        QueryProductDetailsParams queryProductDetailsParams =
//                                QueryProductDetailsParams.newBuilder()
//                                        .setProductList(
//                                                ImmutableList.of(
//                                                        QueryProductDetailsParams.Product.newBuilder()
//                                                                .setProductId("basic_plan")
//                                                                .setProductType(BillingClient.ProductType.SUBS)
//                                                                .build()))
////                                                        QueryProductDetailsParams.Product.newBuilder()
////                                                                .setProductId("standard_subscription")
////                                                                .setProductType(BillingClient.ProductType.SUBS)
////                                                                .build(),
////                                                        QueryProductDetailsParams.Product.newBuilder()
////                                                                .setProductId("premium_subscription")
////                                                                .setProductType(BillingClient.ProductType.SUBS)
////                                                                .build()))
//                                        .build();
//
//                        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
//                            @Override
//                            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
//                                for (ProductDetails productDetails : list){
//                                    response = productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
//                                    description = productDetails.getDescription();
//                                    sku = productDetails.getName();
//                                }
//                            }
//                        });
//
//                    }
//                });
//            }
//        });



}
