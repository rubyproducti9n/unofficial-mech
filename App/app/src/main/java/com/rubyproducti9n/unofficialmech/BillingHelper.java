package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BillingHelper {

    private static final String TAG = "PlayBillingManager";

    private BillingClient billingClient;
    private Activity activity;
    private List<ProductDetails> productDetailsList = new ArrayList<>();
    private HashMap<String, String> planDetails = new HashMap<>();

    public BillingHelper(Activity activity) {
        this.activity = activity;
        initializeBillingClient();
    }

    private void initializeBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().build())
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
        List<QueryProductDetailsParams.Product> products = Arrays.asList(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("basic_plan")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("standard_plan")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("premium_plan")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull QueryProductDetailsResult queryProductDetailsResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                    for (ProductDetails productDetails : productDetailsList) {
                        assert productDetails.getSubscriptionOfferDetails() != null;
                        String price = productDetails.getSubscriptionOfferDetails()
                                .get(0)
                                .getPricingPhases()
                                .getPricingPhaseList()
                                .get(0)
                                .getFormattedPrice();
                        planDetails.put(productDetails.getProductId(), price);
                    }
                }
            }
        });

    }

    public void purchasePlan(String productId) {
        ProductDetails productDetails = getProductDetailsById(productId);
        if (productDetails != null) {
            // Take the first available offer for simplicity
            ProductDetails.SubscriptionOfferDetails offer = productDetails.getSubscriptionOfferDetails().get(0);
            BillingFlowParams.ProductDetailsParams productDetailsParams =
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(offer.getOfferToken())
                            .build();

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(Collections.singletonList(productDetailsParams))
                    .build();

            billingClient.launchBillingFlow(activity, billingFlowParams);
        } else {
            Log.d(TAG, "ProductDetails not found for: " + productId);
        }
    }

    private ProductDetails getProductDetailsById(String productId) {
        for (ProductDetails productDetails : productDetailsList) {
            if (productDetails.getProductId().equals(productId)) {
                return productDetails;
            }
        }
        return null;
    }

    public void checkPurchasedPlans() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                (billingResult, purchasesList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : purchasesList) {
                            Log.d(TAG, "Purchased: " + purchase.getProducts());
                            // Handle the purchase
                        }
                    }
                });
    }

    public boolean isPlanActive(String productId) {
        return planDetails.containsKey(productId);
    }

    public String getPlanValue(String productId) {
        return planDetails.get(productId);
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    Log.d(TAG, "Purchase successful: " + purchase.getProducts());
                    // Acknowledge or consume if required
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.d(TAG, "User canceled the purchase");
            } else {
                Log.d(TAG, "Error purchasing: " + billingResult.getDebugMessage());
            }
        }
    };
}

