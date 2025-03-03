package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.Algorithms.getYear;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.context;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.disableService;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.disableStatusBar;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.collect.ImmutableList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import com.google.android.gms.wallet.PaymentsClient;
//import com.google.android.gms.wallet.Wallet;
//import com.google.android.gms.wallet.WalletConstants;

//import com.paypal.android.sdk.payments.PayPalConfiguration;

public class PaymentActivity extends BaseActivity {

    TextView current_plan;

//    private PurchasesUpdatedListener purchasesUpdatedListener;
    private BillingClient billingClient;

    private ProductDetails productDetailsForBasic;
    private ProductDetails productDetailsForStandard;
    private ProductDetails productDetailsForPremium;


    private MaterialButton basicBtn, standardBtn, premiumBtn;
//    private PaymentsClient paymentsClient;
//    private PhonePeSDK phonePeSDK;

//    private static final String PAYPAL_CLIENT_ID = "";
//    private static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.E;
//
//    private PayPalConfiguration config;

    private static String amount = "1";
private static final int TEZ_REQUEST_CODE = 123;
    private static final int GOOGLE_PAY_REQUEST_CODE = 123;

    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    private static final String PHONEPE_PACKAGE_NAME = "com.phonepe.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        disableStatusBar(this);

        basicBtn = findViewById(R.id.buyPlan0);
        standardBtn = findViewById(R.id.buyPlan1);
        premiumBtn = findViewById(R.id.buyPlan2);

        current_plan = findViewById(R.id.textView3);

        PricingManager manager = PricingManager.getInstance(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBasic = pref.getBoolean("isBasic", false);
        boolean isStandard = pref.getBoolean("isStandard", false);
        boolean isPremium = pref.getBoolean("isPremium", false);
        float adLimit = pref.getFloat("adLimit", 0.0f);
        boolean geminiFlashEnabled = pref.getBoolean("geminiFlashEnabled", false);

//        paymentsClient = Wallet.getPaymentsClient(this,
//                new Wallet.WalletOptions.Builder()
//                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
//                        .build());

        initiate();

        basicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBillingFlow(productDetailsForBasic);
//                initiate(0, 9.99);
            }
        });

        standardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBillingFlow(productDetailsForStandard);
//                initiate(1, 29.99);
            }
        });

        premiumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBillingFlow(productDetailsForPremium);
//                initiate(2, 149.99);
            }
        });

        Button policy = findViewById(R.id.pay_policy);
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PaymentActivity.this);
                builder.setTitle("Payment Policy")
                        .setMessage(R.string.payment_policy)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("app-configuration").child("amount/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    amount = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String transactionRefId = "Order_" + System.currentTimeMillis();


    }

    private void initiate(int plan, double amount){
        paymentMethod(plan, amount);
    }

    private void update(int plan){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = preferences.getString("auth_userId", null);
        if (userId!=null){

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String id = dataSnapshot.child("userId").getValue(String.class);
                            DatabaseReference date = dataSnapshot.getRef().child("lastPaymentDate");
                            dataSnapshot.getRef().child("plan").setValue(plan);

                            LocalDateTime currentDate = LocalDateTime.now();

                            currentDate = currentDate.plusMonths(1);

                            int year = currentDate.getYear();
                            int month = currentDate.getMonthValue();
                            if (month > 12) {
                                year++;
                                month = 1; // Reset to January
                                currentDate = currentDate.withYear(year).withMonth(month);
                            }
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String dateCreatedAccount = currentDate.format(dateTimeFormatter);

                            String lastPaymentDate = currentDate.format(dateTimeFormatter);

                            date.setValue(lastPaymentDate);
                        }
                    }else{
                        startActivity(new Intent(PaymentActivity.this, PasswordActivity.class));
                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                        finish();
                        Toast.makeText(PaymentActivity.this, "Sorry, your account was not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    startActivity(new Intent(PaymentActivity.this, PasswordActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    Toast.makeText(PaymentActivity.this, "Oops! something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void paymentMethod(int plan, double amount){
        String[] options = {"PhonePe", "Google Pay"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Pay via:")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            initiatePhonePePayment(amount);
                            update(plan);
                        } else if (which == 1) {
//                            startGooglePayTransaction("7020162178@axl", "29.99", "INR");
                            initiateGooglePay(amount);
                            update(plan);
                        }
                    }
                });
        builder.show();
    }

    public void initiatePhonePePayment(double amount) {
        String uri = "upi://pay?pa=" + "7020162178@axl" + "&am=" + amount;
//        String url = "https://PhonePe/upi/pay?pa=" + merchantId + "&am=" + amount;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        intent.setPackage(PHONEPE_PACKAGE_NAME);
        startActivity(intent);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "PhonePe app was not found. Opening Google Pay...", Toast.LENGTH_SHORT).show();
//            initiateGooglePay();
//        }
    }

    private void initiateGooglePay(double amount){
        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", "7020162178@axl")
//                                .appendQueryParameter("pa", "BCR2DN4TR3X3BBQP@upi")
                        .appendQueryParameter("pn", "Unofficial Mech")
                        .appendQueryParameter("mc", "6596-8373-5506")
                        .appendQueryParameter("tr", "Order_" + System.currentTimeMillis())
                        .appendQueryParameter("tn", "Service fees")
                        .appendQueryParameter("am", String.valueOf(amount))
                        .appendQueryParameter("cu", "INR")
//                                .appendQueryParameter("url", "https://test.merchant.website")
                        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
        startActivity(intent);
    }

    private void startGooglePayTransaction(String merchantId, String amount, String currencyCode) {
        // Replace with the details for your transaction
        String paymentDataRequest = getPaymentDataRequest(merchantId, amount, currencyCode);

        // Create the intent to open Google Pay
        Intent intent = new Intent(GOOGLE_TEZ_PACKAGE_NAME);
        intent.putExtra("paymentDataRequest", paymentDataRequest);
        startActivity(intent);
//        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
    }
    private String getPaymentDataRequest(String merchantId, String amount, String currencyCode) {
        // Implement your logic to generate the payment data request JSON
        // Refer to Google Pay API documentation for details on this
        // This JSON will include your transaction details and merchant information

        JSONObject paymentDataRequest = new JSONObject();

        try {
            paymentDataRequest.put("apiVersion", 2);
            paymentDataRequest.put("apiVersionMinor", 0);

            JSONArray allowedPaymentMethods = new JSONArray();
            JSONObject cardPaymentMethod = new JSONObject();
            cardPaymentMethod.put("type", "CARD");
            JSONArray allowedCardNetworks = new JSONArray();
            allowedCardNetworks.put("VISA");
            allowedCardNetworks.put("MASTERCARD");
            cardPaymentMethod.put("allowedCardNetworks", allowedCardNetworks);
            allowedPaymentMethods.put(cardPaymentMethod);
            paymentDataRequest.put("allowedPaymentMethods", allowedPaymentMethods);

            JSONObject transactionInfo = new JSONObject();
            transactionInfo.put("totalPrice", amount);
            transactionInfo.put("currencyCode", currencyCode);
            transactionInfo.put("merchant", merchantId);
            paymentDataRequest.put("transactionInfo", transactionInfo);

            JSONArray callbackIntents = new JSONArray();
            callbackIntents.put("PAYMENT_AUTHORIZATION");
            paymentDataRequest.put("callbackIntents", callbackIntents);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paymentDataRequest.toString();
    }


    private void initiate() {
        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    Toast.makeText(context, "Purchase cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Purchase error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryAvailableSubscriptions();
                } else {
                    Toast.makeText(context, "Error setting up billing: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                retryConnection();
            }
        });
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
                        } else {
                            Toast.makeText(context, "Error retrying connection: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
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
                    checkOwnedSubscriptions();
                } else {
                    Toast.makeText(context, "Error loading products: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkOwnedSubscriptions() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getProducts().equals("basic_plan")) {
                            basicBtn.setEnabled(false);
                        } else if (purchase.getProducts().equals("standard_subscription")) {
                            standardBtn.setEnabled(false);
                        } else if (purchase.getProducts().equals("premium_subscription")) {
                            premiumBtn.setEnabled(false);
                        }
                    }
                    findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, "Error checking subscriptions: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void launchBillingFlow(ProductDetails productDetails) {
        if (productDetails != null) {
            ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                    ImmutableList.of(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                    .build()
                    );

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();

            billingClient.launchBillingFlow(this, billingFlowParams);
        }
    }

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
                                Toast.makeText(context, "Subscription acknowledged", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error acknowledging purchase: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
        }
    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> Purchase) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && Purchase != null) {
                for (Purchase purchase : Purchase) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Toast.makeText(context, "Already subscribed!", Toast.LENGTH_SHORT).show();
                current_plan.setText("Active Plan");
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEZ_REQUEST_CODE) {
            // Process based on the data in response.
            Log.d("result", data.getStringExtra("Status"));
        }
    }




}