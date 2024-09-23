package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.Algorithms.paymentServiceCheck;
import static com.rubyproducti9n.unofficialmech.PDFCreator.createPDF;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.disableStatusBar;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fServer;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.initiateAds;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadNativeAd;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.register;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.show;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.unregister;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.se.omapi.Session;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.widget.SearchView.OnQueryTextListener;

public class TestActivity extends AppCompatActivity {

    private BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListener;
    TextView plan;
//    DatabaseReference databaseReference;

    private ConstraintLayout adContainer;
    private MaterialAutoCompleteTextView divDrop, deptDrop;
    private DatabaseReference databaseReference;
    public static ImageView profilePic;
    MaterialButton facultyAcc, createAccBtn;
    TextInputEditText fName, lName, inPrn, inClgMail, inPEmail, inMobNum, inPassword;
    String inDivision, inDept, inGender = null;
    String inRRoll = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        checkTemp();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        profilePic = findViewById(R.id.profilePicture);

        fName = findViewById(R.id.firstNameEditText);
        lName = findViewById(R.id.lastNameEditText);
        inPrn = findViewById(R.id.prnEditText);
        inClgMail = findViewById(R.id.clgEmailEditText);
        inPEmail = findViewById(R.id.personalEmailEditText);
        inMobNum = findViewById(R.id.mobileNumEditText);
        inPassword = findViewById(R.id.passwordEditText);

        divDrop = findViewById(R.id.dropdown_menu2);
        deptDrop = findViewById(R.id.deptMenu);
        facultyAcc = findViewById(R.id.facultyBtn);
        createAccBtn = findViewById(R.id.cretaeAccountBtn);

        setupDivisionDropdown();
        setupDepartmentDropdown();
        setupGenderSelection();
        setupRollNumberInput();

        facultyAcc.setOnClickListener(v -> {
            startActivity(new Intent(TestActivity.this, LockActivity.class));
        });

        createAccBtn.setOnClickListener(view -> checkInput(
                Objects.requireNonNull(fName.getText()).toString(),
                Objects.requireNonNull(lName.getText()).toString(),
                inDivision, String.valueOf(inRRoll),
                Objects.requireNonNull(inPrn.getText()).toString(),
                Objects.requireNonNull(inClgMail.getText()).toString(),
                inGender, Objects.requireNonNull(inPEmail.getText()).toString(),
                Objects.requireNonNull(inMobNum.getText()).toString(),
                ProjectToolkit.h(TestActivity.this, Objects.requireNonNull(inPassword.getText()).toString()),
                "Student",
                inDept
        ));


        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        MaterialButton btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this, CreateAccount.class));
//                String userId = databaseReference.push().getKey();
//                BottomSheetCreateAccount.User user = new BottomSheetCreateAccount.User(userId, null, null, null, null, null, null, null, null, null, null, null, null + "@unofficialmech", false, null, null, null);
//                databaseReference.child(userId).setValue(user);
            }
        });



//        plan = findViewById(R.id.plan);
//        purchasesUpdatedListener = new PurchasesUpdatedListener() {
//            @Override
//            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
//                    for (Purchase purchase : purchases) {
//                        Log.d("PurchasesUpdated", "Purchase found: " + purchase.getSkus());
//                        validatePurchase(purchase);
//                    }
//                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
//                    Log.d("PurchasesUpdated", "User canceled the purchase flow.");
//                } else {
//                    Log.e("PurchasesUpdated", "Purchase update failed with code: " + billingResult.getResponseCode());
//                }
//            }
//
//
//        };
//
//        billingClient = BillingClient.newBuilder(this)
//                .setListener(purchasesUpdatedListener)
//                .enablePendingPurchases()
//                .build();
//
//        startConnection();
//
//        getCurrentSubscriptionPlan();
    }



    private void setupDivisionDropdown() {
        String[] items = getResources().getStringArray(R.array.div);
        ArrayAdapter<String> divAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        divDrop.setAdapter(divAdapter);
        divDrop.setOnItemClickListener((parent, view, position, id) -> inDivision = (String) parent.getItemAtPosition(position));
    }

    private void setupDepartmentDropdown() {
        String[] deptItems = getResources().getStringArray(R.array.dept);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, deptItems);
        deptDrop.setAdapter(deptAdapter);
        deptDrop.setOnItemClickListener((parent, view, position, id) -> inDept = (String) parent.getItemAtPosition(position));
    }

    private void setupGenderSelection() {
        RadioGroup radioGroup = findViewById(R.id.gender_radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            String selectedOption = selectedRadioButton.getText().toString();
            validGender(selectedOption);
            inGender = selectedOption;
        });
    }

    private void setupRollNumberInput() {
        TextInputEditText rollInput = findViewById(R.id.rollEditText);
        rollInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inRRoll = "0";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isInt = isInt(Objects.requireNonNull(rollInput.getText()).toString());
                if (isInt) {
                    inRRoll = rollInput.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void checkInput(String firstName, String lastName, String Div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role, String dept) {
        if (isEmpty(firstName)) {
            showToast("Please fill your First name");
            return;
        }
        if (isEmpty(lastName)) {
            showToast("Please fill your Last name");
            return;
        }
        if (isEmpty(Div)) {
            showToast("Please select Division");
            return;
        }
        if (isEmpty(roll) || roll.equals("0")) {
            showToast("Please enter a valid Roll No.");
            return;
        }
        if (isEmpty(oEmail)) {
            showToast("Please enter your Official Email Address");
            return;
        }
        if (isEmpty(gen)) {
            showToast("Please select appropriate gender");
            return;
        }
        if (isEmpty(pEmail)) {
            showToast("Please enter your personal Email Address");
            return;
        }
        if (isEmpty(mNum)) {
            showToast("Please enter your mobile number.");
            return;
        }
        if (isEmpty(pass)) {
            showToast("Please enter your password");
        } else {
            if (!validateMobileNumber(mNum)) {
                showToast("Invalid Mobile number, please enter a valid number");
                return;
            }
            if (!isValidPRN(prn)) {
                showToast("Invalid PRN number");
                return;
            }
            if (!isValidGmail(pEmail)) {
                showToast("Invalid Email address");
                return;
            }
            if (!isValidSanjivanicoe(oEmail)) {
                showToast("Invalid Clg Email Address");
                return;
            } else {
                createAccount(firstName, lastName, Div, roll, prn, oEmail, gen, pEmail, mNum, pass, role, dept);
            }
        }
    }

    private void createAccount(String firstName, String lastName, String div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role, String dept) {
        tempUser(null, firstName, lastName, div, roll, prn, oEmail, gen, pEmail, mNum, pass, role, dept);
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreatedAccount = currentDate.format(dateTimeFormatter);
        String lastPaymentDate = currentDate.format(dateTimeFormatter);

        String userId = databaseReference.push().getKey();

        if (userId != null) {
            BottomSheetCreateAccount.User user = new BottomSheetCreateAccount.User(userId, null, firstName, lastName, oEmail, gen, prn, roll, div, pEmail, pass, role, mNum, firstName.toLowerCase() + roll + "@unofficialmech", false, dateCreatedAccount, lastPaymentDate, dept);
            databaseReference.child(userId).setValue(user);
            saveUser(userId, firstName, lastName, div, roll, prn, oEmail, gen, pEmail, mNum, pass, role);
            initiateNewActivity();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Oops!")
                    .setMessage("Unable to create account, please try again.")
                    .setNegativeButton("Try again", (dialog, which) -> createAccount(firstName, lastName, div, roll, prn, oEmail, gen, pEmail, mNum, pass, role, dept))
                    .show();
        }
    }

    private void saveUser(String uid, String firstName, String lastName, String div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
        SharedPreferences.Editor e = p.edit();
        e.putString("auth_userId", uid);
        e.putString("auth_name", firstName + " " + lastName);
        e.putString("auth_email", oEmail);
        e.putString("auth_password", pass);
        e.putString("auth_prn", prn);
        e.putString("auth_roll", roll);
        e.putString("auth_division", div);
        e.putString("auth_gender", gen);
        e.putString("auth_userole", role);
        e.putString("auth_mob", mNum);
        e.putString("auth_personalEmail", pEmail);
        e.apply();
    }

    private void initiateNewActivity() {
        Toast.makeText(this, "Successfully created account!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void tempUser(String uid, String firstName, String lastName, String div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role, String dept) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
        SharedPreferences.Editor e = p.edit();
        e.putString("temp_userId", uid);
        e.putString("temp_name", firstName + " " + lastName);
        e.putString("temp_email", oEmail);
        e.putString("temp_password", pass);
        e.putString("temp_prn", prn);
        e.putString("temp_roll", roll);
        e.putString("temp_division", div);
        e.putString("temp_gender", gen);
        e.putString("temp_userole", role);
        e.putString("temp_mob", mNum);
        e.putString("temp_personalEmail", pEmail);
        e.putString("temp_dept", dept);
        e.apply();
        showToast("Saved!");
    }

    private void checkTemp() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
        String nm = p.getString("temp_name", null);
        if (nm != null) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Manager")
                    .setMessage("Would you like to autofill your details instead of filling it again?")
                    .setPositiveButton("Fill", (dialog, which) -> loadData())
                    .setNegativeButton("No", (dialog, which) -> {
                        SharedPreferences.Editor e = p.edit();
                        e.clear();
                        e.apply();
                    })
                    .show();
        }
    }

    private void loadData() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
        String nm = p.getString("temp_name", null);
        String email = p.getString("temp_email", null);
        String pass = p.getString("temp_password", null);
        String prn = p.getString("temp_prn", null);
        String div = p.getString("temp_division", null);
        String roll = p.getString("temp_roll", null);
        String gen = p.getString("temp_gender", null);
        String role = p.getString("temp_userole", null);
        String mNum = p.getString("temp_mob", null);
        String pEmail = p.getString("temp_personalEmail", null);
        String dept = p.getString("temp_dept", null);

        createAccount(nm.split(" ")[0], nm.split(" ")[1], div, roll, prn, email, gen, pEmail, mNum, pass, role, dept);
    }

    private boolean validateMobileNumber(String mobileNumber) {
        Pattern pattern = Pattern.compile("^[6-9]\\d{9}$");
        Matcher matcher = pattern.matcher(mobileNumber);
        return matcher.matches();
    }

    public static boolean isValidPRN(String prnNumber) {
        Pattern pattern = Pattern.compile("^[A-Z]{3}[0-9]{2}[A-Z]{1}[0-9]{4}$");
        Matcher matcher = pattern.matcher(prnNumber);
        return matcher.matches();
    }

    public static boolean isValidGmail(String emailAddress) {
        Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(gmail\\.com)$");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static boolean isValidSanjivanicoe(String emailAddress) {
        Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@sanjivanicoe\\.org\\.in$");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static void validGender(String gen) {
        if (gen.equals("Male")) {
            ProjectToolkit.fadeIn(profilePic);
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profilePic);
        }
        if (gen.equals("Female")) {
            ProjectToolkit.fadeIn(profilePic);
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profilePic);
        }
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }





    private String containsInvalidFirebaseKey(String key) {
        if (key.contains("/") || key.contains(".") || key.contains("#") || key.contains("$") || key.contains("[") || key.contains("]")){
            Toast.makeText(this, sanitizeKey(key), Toast.LENGTH_SHORT).show();
            return sanitizeKey(key);
        }else {
            return key;
        }
    }
    private String sanitizeKey(String key) {
        StringBuilder sanitizedKey = new StringBuilder();
        int index;

        while ((index = key.indexOf('/')) != -1 ||
                (index = key.indexOf('.')) != -1 ||
                (index = key.indexOf('#')) != -1 ||
                (index = key.indexOf('$')) != -1 ||
                (index = key.indexOf('[')) != -1 ||
                (index = key.indexOf(']')) != -1) {
            sanitizedKey.append(key.substring(0, index));
            key = key.substring(index + 1);
        }

        sanitizedKey.append(key);
        return sanitizedKey.toString();
    }



    void handlePurchase(Purchase purchase) {
        Log.d("HandlePurchase", "Purchase state: " + purchase.getPurchaseState());
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener =
                    billingResult -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // Grant entitlement to the user.
                        }
                    };

            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
        }
    }
    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryPurchases();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Retry connection or handle disconnection
            }
        });
    }

    private void queryPurchases() {
        QueryPurchasesParams queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build();

        billingClient.queryPurchasesAsync(queryPurchasesParams, (billingResult, purchasesList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                for (Purchase purchase : purchasesList) {
                    validatePurchase(purchase);
                }
            } else {
                plan.setText(billingResult.getDebugMessage());
                // Handle errors
            }
        });
    }

//    private void validatePurchase(Purchase purchase) {
//        for (String sku : purchase.getSkus()) {
//            Log.d("ValidatePurchase", "SKU: " + sku);
//            if (sku.equals("basic_plan")) {
//                showToast("Basic Subscription is active.");
//            } else if (sku.equals("standard_subscription")) {
//                showToast("Standard Subscription is active.");
//            } else if (sku.equals("premium_subscription")) {
//                showToast("Premium Subscription is active.");
//            } else {
//                showToast("Invalid SKU: " + sku);
//            }
//        }
//    }


//    private void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//
//
//        plan.setText(message);
//    }

    private void getCurrentSubscriptionPlan() {
        QueryPurchasesParams queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build();

        billingClient.queryPurchasesAsync(queryPurchasesParams, (billingResult, purchasesList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                for (Purchase purchase : purchasesList) {
                    validatePurchase(purchase);
                }
            } else {
                // Handle errors
                Log.e("SubscriptionError", billingResult.getDebugMessage());
            }
        });
    }

    private void validatePurchase(Purchase purchase) {
        String currentSubscriptionPlan = null;
        for (String sku : purchase.getSkus()) {
            if (sku.equals("basic_plan")) {
                currentSubscriptionPlan = "Basic";
            } else if (sku.equals("standard_subscription")) {
                currentSubscriptionPlan = "Standard";
            } else if (sku.equals("premium_subscription")) {
                currentSubscriptionPlan = "Premium";
            } else {
                currentSubscriptionPlan = "Unknown"; // Or handle unknown SKUs appropriately
            }
            showToast(currentSubscriptionPlan);
            // Use currentSubscriptionPlan variable as needed
            Log.d("CurrentPlan", "Current subscription plan: " + currentSubscriptionPlan);
        }
    }



}