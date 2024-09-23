package com.rubyproducti9n.unofficialmech;

//import static com.rubyproducti9n.smartmech.AlgorithmEngine.pref;
import static com.rubyproducti9n.unofficialmech.Callbacks.getAdValue;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.animateVertically;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.disableStatusBar;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getSystemAdValue;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.initiateAds;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadAd;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadBannerAd;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.pref;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.preference.PreferenceManager;

import android.accounts.Account;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AccountInfo extends AppCompatActivity {


    DatabaseReference userRef;
    ActivityResultLauncher<Intent> startActivityForResult;
    ShimmerFrameLayout progress;
    MaterialButton editProfile, panicButton;
    MaterialButton changeAvatar;
    MaterialCardView drag, editCard, panicCard, beta, dev, logOut;
    ConstraintLayout editLayout, finalLayout;
    Boolean status;
    MaterialSwitch statusButton;
    BottomSheetBehavior bottomSheetBehavior;
    SharedPreferences p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        getWindow().setAllowEnterTransitionOverlap(true);
        p = PreferenceManager.getDefaultSharedPreferences(AccountInfo.this);

        Intent i = getIntent();
        String action = i.getAction();
        String type = i.getType();
        String d = i.getStringExtra("user_details");
        if (i.hasExtra("user_details")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BottomSheetAccountMenu bottomSheetAccountMenu = new BottomSheetAccountMenu();
                    bottomSheetAccountMenu.show(getSupportFragmentManager(), "tags");
                }
            }, 500);
        }

        disableStatusBar(AccountInfo.this);

        initiateAds(AccountInfo.this, AccountInfo.this);

        MaterialButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountInfo.this, MainActivity.class));
            }
        });

//        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_container));
        MaterialCardView mc = findViewById(R.id.materialCardView19);
        MaterialCardView mc2 = findViewById(R.id.materialCardView13);
        drag = findViewById(R.id.dragBtn);
        if (Objects.equals(p.getString("auth_userole", null), "Faculty")){
            mc.setOnTouchListener(new View.OnTouchListener() {
                private float startY, initialY;
                private boolean bottomSheetTriggered = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            initialY = drag.getY();
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            float deltaY = event.getY() - startY;

                            // Ensure mc doesn't exceed -112dp even if user drags further
                            deltaY = Math.min(deltaY, -308 - initialY);

                            // Set mc's Y position directly for smooth dragging
                            mc.setY(initialY + deltaY);
                            // Limit dragging to downwards movement with a maximum of 50dp

                            // Restrict up-drag (deltaY positive)
                            if (deltaY > 0) {
                                mc.setY(initialY);  // Reset to original position
                                deltaY = 0;  // Set deltaY to 0 to prevent animation issues
                            }

//                        deltaY = Math.min(deltaY, -126);

//                        drag.setY(initialY + deltaY);
//                        mc.setY(initialY + deltaY);
                            if (-118 > deltaY){
                                mc.animate()
                                        .translationY(-12)
                                        .setDuration(250)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                BottomSheetAccountMenu bottomSheetAccountMenu = new BottomSheetAccountMenu();
                                                bottomSheetAccountMenu.show(getSupportFragmentManager(), "tags");
                                            }
                                        })
                                        .start();
                            }
//                        if (-128 > Math.min(deltaY, -112)){
//                            // Animate mc card with 50dp translationY
//                        mc.animate()
//                                .translationY(-12)
//                                .setDuration(500)
//                                .setInterpolator(new AccelerateDecelerateInterpolator())
//                                .start();
//
//                        }

                            // Trigger bottom sheet only once and animate elements back
                            if (!bottomSheetTriggered && deltaY <= -112) {
//                            updateBottomSheetState();
                                bottomSheetTriggered = true;
//                            animateElementsBack();
                            }

                            return true;
                        case MotionEvent.ACTION_UP:
                            // Reset triggered flag for subsequent drags
                            bottomSheetTriggered = false;
                            return false;
                    }
                    return false;
                }

                private void animateElementsBack() {
                    mc.animate()
                            .translationY(0)  // Assuming mc's original translationY is 0
                            .setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    // Add animation for drag view back to its initial position if needed
                }
            });
            mc.performClick();
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String authUserRole1 = pref.getString("auth_userole", null);
        String authPrn = pref.getString("auth_prn", null);

        TextView yr = findViewById(R.id.yr_txt);
        if (authPrn!=null){
            String getYear = Algorithms.getYear(authPrn);
            yr.setText(getYear + " B.Tech");
        }

        MaterialCardView mcMyDetails, mcBeta, mcConsole, mcFaqs, mcTerms, mcPrivacyPolicy, mcAbout, mcLogout;

        mcMyDetails = findViewById(R.id.mc_myActivity);
        mcBeta = findViewById(R.id.mc_beta);
        mcConsole = findViewById(R.id.mc_console);
        mcFaqs = findViewById(R.id.mc_faq);
        mcTerms = findViewById(R.id.mc_termsCondition);
        mcPrivacyPolicy = findViewById(R.id.mc_privacyPolicy);
        mcAbout = findViewById(R.id.mc_about);
        mcLogout = findViewById(R.id.mc_logout);

        if (pref(AccountInfo.this).getString("auth_name", null) != null){
            mcMyDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetAccountMenu bottomSheetAccountMenu = new BottomSheetAccountMenu();
                    bottomSheetAccountMenu.show(getSupportFragmentManager(), "tags");
                }
            });
        }else {
            mcMyDetails.setEnabled(false);
        }

//        mcMyDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BottomSheetAccountMenu bottomSheetAccountMenu = new BottomSheetAccountMenu();
//                bottomSheetAccountMenu.show(getSupportFragmentManager(), "BottomSheet");
////                startActivity(new Intent(this, ));
//            }
//        });

        mcBeta.setVisibility(View.GONE);
        mcConsole.setVisibility(View.GONE);
        if (authUserRole1!=null){
            if (authUserRole1.equals("Beta tester")){
                mcBeta.setVisibility(View.VISIBLE);
                mcBeta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AccountInfo.this, BetaActivity.class);
                        ActivityOptionsCompat option = ActivityOptionsCompat.makeSceneTransitionAnimation(AccountInfo.this, mcBeta, "beta");
                        startActivity(intent, option.toBundle());
                    }
                });
            }
            if (authUserRole1.equals("Admin")){
                mcConsole.setVisibility(View.VISIBLE);
                mcConsole.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AccountInfo.this, DeveloperActivity.class);
                        startActivity(intent);
                    }
                });
                mcBeta.setVisibility(View.VISIBLE);
                mcBeta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AccountInfo.this, BetaActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }

        mcFaqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountInfo.this, FAQActivity.class));
            }
        });

        mcTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AccountInfo.this);
                builder.setTitle("Terms and Conditions");
                builder.setMessage(R.string.termNconditions);
                builder.setIcon(R.drawable.round_supervisor_account_24);
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
        mcPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AccountInfo.this);
                builder.setTitle("Privacy Policy");
                builder.setMessage(R.string.privacyPolicy);
                builder.setIcon(R.drawable.round_policy_24);
                builder.setPositiveButton("Learn more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/unofficial-mech/privacy-policy")));
                    }
                });
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        mcAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountInfo.this, AboutActivity.class));
            }
        });

        mcLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInterstitialAd(AccountInfo.this);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccountInfo.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(AccountInfo.this, PasswordActivity.class);
                startActivity(intent);
                finishAffinity();
                Toast.makeText(AccountInfo.this, "Logged out", Toast.LENGTH_SHORT).show();
//                showInterstitial();
//                loadAd(AccountInfo.this);
            }
        });



        MaterialButton fab = findViewById(R.id.menu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetAccountMenu bottomSheetAccountMenu = new BottomSheetAccountMenu();
                bottomSheetAccountMenu.show(getSupportFragmentManager(), "BottomSheet");
            }
        });

        MaterialButton panicButton = findViewById(R.id.panicBtn);
        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activatePanicMode();
//                BottomSheetCreateAccount bottomSheetCreateAccount = new BottomSheetCreateAccount();
//                bottomSheetCreateAccount.show(getSupportFragmentManager(), "BottomSheet");
            }
        });


//        MaterialCardView ad_container = findViewById(R.id.ad_container);
//        getAdValue(ad_container, new Callbacks.AdValue() {
//            @Override
//            public void onAdValue(boolean value) {
//                if (value){
//                    AdView adView = findViewById(R.id.adView);
//                    MobileAds.initialize(AccountInfo.this, new OnInitializationCompleteListener() {
//                        @Override
//                        public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//                            if (initializationStatus != null){
//                                Log.d("Admob: ", "Ad received!");
//                                loadBannerAd(adView);
//                                AdRequest adRequest = new AdRequest.Builder().build();
//                                adView.loadAd(adRequest);
//                            }else{
//                                Log.d("Admob: ", "Error receiving ads!");
//                            }
//                        }
//                    });
//                    AdRequest adRequest = new AdRequest.Builder().build();
////                    adView.loadAd(adRequest);
//
//                    InterstitialAd.load(AccountInfo.this, "ca-app-pub-5180621516690353/8609265521", adRequest, new InterstitialAdLoadCallback() {
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            super.onAdFailedToLoad(loadAdError);
//                        }
//
//                        @Override
//                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                            super.onAdLoaded(interstitialAd);
//                            interstitialAd.show(AccountInfo.this);
//                        }
//                    });
//
//                }else{
////                    ad_container.setVisibility(View.GONE);
//                }
//            }
//        });



//        MaterialToolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        changeAvatar = findViewById(R.id.changeAvatar);
//        editLayout = findViewById(R.id.editLayout);
//        finalLayout = findViewById(R.id.finalLayout);
//
//        changeAvatar.setVisibility(View.GONE);
//        editLayout.setVisibility(View.GONE);
//        finalLayout.setVisibility(View.VISIBLE);

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            analyzeImage(selectedImageUri);
                            //uploadToDatabase(selectedImageUri);
//                            progress.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

//        TextInputEditText prnEditText, rollEditText, emailEditText, roleEditText, genderEditText;
//        prnEditText = findViewById(R.id.prnEditText);
//        rollEditText = findViewById(R.id.rollEditText);
//        emailEditText = findViewById(R.id.emailEditText);
//        roleEditText = findViewById(R.id.roleEditText);
//        genderEditText = findViewById(R.id.genderEditText);

        ImageView profile, badge, profileArt;
        ConstraintLayout consPay;
        consPay = findViewById(R.id.cons_pay);
        consPay.setVisibility(View.GONE);
        TextView txtUsername, txtName, txtPrn, txtRoll, txtPersonalEmail, txtDivision, txtContact, txtOfficialEmail, txtEmail, txtGender, txtPaymentDate, txtRole;
        ConstraintLayout mainLayout, progressLayout;
        badge = findViewById(R.id.badge);
        mainLayout = findViewById(R.id.main_layout);
        progress = findViewById(R.id.shimmer);
//        profileArt = findViewById(R.id.profileArt);
        profile = findViewById(R.id.avatar);
        txtUsername = findViewById(R.id.username);
        txtName = findViewById(R.id.name);
        txtEmail = findViewById(R.id.personalEmail);
        txtGender = findViewById(R.id.gender);
        txtDivision = findViewById(R.id.div);
        txtContact = findViewById(R.id.contactNo);
        txtOfficialEmail = findViewById(R.id.officialEmail);
        txtPrn = findViewById(R.id.prn);
//        txtPersonalEmail = findViewById(R.id.personalEmail);
        txtPaymentDate = findViewById(R.id.paymentDate);
        txtRoll = findViewById(R.id.roll);
//        txtRole = findViewById(R.id.role);

        txtPaymentDate.setVisibility(View.GONE);
        badge.setVisibility(View.GONE);
        mainLayout.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
//        progress.setVisibility(View.VISIBLE);
//        profile.setVisibility(View.INVISIBLE);
//        txtName.setVisibility(View.INVISIBLE);
//        txtEmail.setVisibility(View.INVISIBLE);
//        txtPrn.setVisibility(View.INVISIBLE);
//        txtRole.setVisibility(View.INVISIBLE);
//        logOut.setVisibility(View.INVISIBLE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String adminEmailCheck = preferences.getString("auth_email", null);

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        userRef = userDatabase.getReference("users");

        SharedPreferences accountPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String authUserRole = preferences.getString("auth_userole", "");

        if (adminEmailCheck == null){
            progress.setVisibility(View.GONE);
            txtUsername.setText("Guest user");
            txtOfficialEmail.setText("-");
            txtName.setText("Guest");
            txtEmail.setText("-");
            txtGender.setText("-");
            txtPrn.setText("-");
//            txtRole.setText("None");
            txtRoll.setText("-");
            txtOfficialEmail.setText("-");
            txtDivision.setText("-");
            txtContact.setText("-");
            consPay.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }else{
            serviceCheck(this, new ProjectToolkit.ServiceCheckCallBack() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        userRef.orderByChild("personalEmail").equalTo(adminEmailCheck).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    //Email Exists
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                                        //Retrieve the user data
                                        String avatar = userSnapshot.child("avatar").getValue(String.class);
                                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                                        String lastName = userSnapshot.child("lastName").getValue(String.class);
                                        String officialEmail = userSnapshot.child("clgEmail").getValue(String.class);
                                        String personalEmail = userSnapshot.child("personalEmail").getValue(String.class);
                                        String prn = userSnapshot.child("prn").getValue(String.class);
                                        String contact = userSnapshot.child("contact").getValue(String.class);
                                        String division = userSnapshot.child("div").getValue(String.class);
                                        String gender = userSnapshot.child("gender").getValue(String.class);
                                        String roll = userSnapshot.child("rollNo").getValue(String.class);
                                        String role = userSnapshot.child("role").getValue(String.class);
                                        String paymentDate = userSnapshot.child("lastPaymentDate").getValue(String.class);
                                        status = userSnapshot.child("status").getValue(Boolean.class);
                                        if (personalEmail.equals("om.lokhande34@gmail.com")) {
                                            badge.setVisibility(View.VISIBLE);
                                        }
                                        if (personalEmail.equals("aishwaryakumbhar1234@gmail.com")) {
                                            badge.setVisibility(View.VISIBLE);
                                        }
                                        if (avatar != null) {
                                            Picasso.get().load(avatar).into(profile);
                                        } else {
                                            if (gender.equals("Male")) {
                                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profile);
                                            }
                                            if (gender.equals("Female")) {
                                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profile);
                                            }
                                            if (personalEmail.equals("om.lokhande34@gmail.com")) {
                                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/dev_avatar.jpg").into(profile);
                                                badge.setVisibility(View.VISIBLE);
                                            } else if (personalEmail.equals("mechanical.official73@gmail.com")) {
                                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/unofficial.png").into(profile);
                                                badge.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        txtOfficialEmail.setText(officialEmail);
                                        txtEmail.setText(personalEmail);
                                        txtPrn.setText(prn);
                                        txtRoll.setText(roll);
                                        txtUsername.setText(firstName + " " + lastName);
                                        if (gender != null) {
                                            if (gender.equals("Male")) {
                                                txtName.setText("Mr. " + firstName + " " + lastName);
                                            } else {
                                                txtName.setText("Miss. " + firstName + " " + lastName);
                                            }
                                        } else {
                                            txtName.setText(firstName + " " + lastName);
                                        }
                                        txtGender.setText(gender);
                                        txtDivision.setText(division + " section");
                                        txtContact.setText(contact);
                                        Algorithms.paymentServiceCheck(AccountInfo.this, new Algorithms.PaymentServiceCheckCallBack() {
                                            @Override
                                            public void onResult(Boolean result) {
                                                if (result) {
                                                    if (paymentDate != null) {
                                                        txtPaymentDate.setText(paymentDate);
                                                        consPay.setVisibility(View.VISIBLE);
                                                    }
                                                } else {
                                                    consPay.setVisibility(View.GONE);
                                                }
                                            }
                                        });

                                        if (role != null) {
                                            if (role.equals("Admin")) {
                                                badge.setVisibility(View.VISIBLE);
                                            } else {
                                                badge.setVisibility(View.GONE);
                                            }
                                        }

//                                        txtRole.setText("User role: " + role);

//                                        prnEditText.setText(prn);
//                                        rollEditText.setText(roll);
//                                        emailEditText.setText(personalEmail);
//                                        roleEditText.setText(role);
//                                        genderEditText.setText(gender);

                                            ProjectToolkit.fadeIn(mainLayout);
                                            ProjectToolkit.fadeOut(progress);

//                        progress.setVisibility(View.INVISIBLE);
//                        profile.setVisibility(View.VISIBLE);
//                        txtName.setVisibility(View.VISIBLE);
//                        txtEmail.setVisibility(View.VISIBLE);
//                        txtPrn.setVisibility(View.VISIBLE);
//                        txtRole.setVisibility(View.VISIBLE);
//                        logOut.setVisibility(View.VISIBLE);

                                    }
                                }else{
                                    Toast.makeText(AccountInfo.this, adminEmailCheck, Toast.LENGTH_SHORT).show();
                                    progress.setVisibility(View.GONE);
                                    Toast.makeText(AccountInfo.this, "Please try Logging out and Logging In", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(AccountInfo.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        finish();
                        Toast.makeText(AccountInfo.this, "Server is offline, please try again later", Toast.LENGTH_SHORT).show();

//                        String id = preferences.getString("auth_userId", null);
//                        String authName = preferences.getString("auth_name", null);
//                        String authEmail = preferences.getString("auth_email", null);
//                        String authPassword = preferences.getString("auth_password", null);
//                        String authPrn = preferences.getString("auth_prn", null);
//                        String authRoll = preferences.getString("auth_roll", null);
//                        String authDivision = preferences.getString("auth_division", null);
//                        String authGender = preferences.getString("auth_gender", null);
//                        String authUserole = preferences.getString("auth_userole", null);
//                        boolean authDash = preferences.getBoolean("dashboard", false);
//                        if (authEmail.equals("om.lokhande34@gmail.com")) {
//                            badge.setVisibility(View.VISIBLE);
//                        }
//                        if (authEmail.equals("aishwaryakumbhar1234@gmail.com")) {
//                            badge.setVisibility(View.VISIBLE);
//                        }
//
//                        txtName.setText(authName);
//                        txtOfficialEmail.setText(authEmail);
//                        txtEmail.setText(authEmail);
//                        txtPrn.setText(authPrn);
//                        txtRoll.setText(authRoll);
//                        txtGender.setText(authGender);
//
//
//                        ProjectToolkit.fadeIn(mainLayout);
//                        ProjectToolkit.fadeOut(progress);
                    }
                }
            });


        }

    }


    private void activatePanicMode(){
        final String[] items = {"Ambulance", "Fire Brigade", "Security Officer", "Antiragging", "Chief Warden"};

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AccountInfo.this);
        builder.setTitle("Panic mode")
//        builder.setMessage("In case of emergency situations, call the college ambulance and know them your location. \n \n" +
//                "Note: Activating Panic mode will share your details to the authority.");
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                dial("8975900900");
                                shareDetails("Ambulance");
                                break;
                            case 1:
                                dial("8411002749");
                                shareDetails("Fire Brigade");
                                break;
                            case 2:
                                dial("9869201190");
                                shareDetails("Security Officer");
                                break;
                            case 3:
                                dial("9552021276");
                                shareDetails("Antiragging");
                                break;
                            case 4:
                                dial("9923607460");
                                shareDetails("Chief Warden");
                                break;
                        }
                    }
                })
                .setPositiveButton("Learn more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MaterialAlertDialogBuilder learnMore = new MaterialAlertDialogBuilder(AccountInfo.this);
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
    private void updateBottomSheetState() {
       BottomSheetAccountMenu bottomSheetAccountMenu = new BottomSheetAccountMenu();
       bottomSheetAccountMenu.show(getSupportFragmentManager(), "bottom");
    }
    private void dial(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
    private void shareDetails(String service){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccountInfo.this);
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult.launch(intent);
    }

    private void startImageCrop(Uri uri){



    }

    private void dialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AccountInfo.this);
        builder.setTitle("Warning");
        builder.setMessage("Out of service");
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    private void analyzeImage(Uri uri){
        File hiddenDir = new File(getExternalFilesDir(null), "com.rubyproducti9n.unofficialmech/compressed/UM");
        if (!hiddenDir.exists()){
            hiddenDir.mkdirs();
        }
        String compImageName = "okmjhyres8apdt7.jpg";
        int compressionValue = 10; //1-100
        long maxSize = 100 * 1024; //100kb
        try{
            InputStream inputStream = getContentResolver().openInputStream(uri);
            int imageSizeInBytes = inputStream.available();

            if (imageSizeInBytes >= maxSize){
                Uri compressedImg = ImgOptimizer.compressAndConvertToUri(this, uri, compressionValue);
                if (compressedImg != null){
                    uploadToDatabase(compressedImg);
                }else{
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }else{
                uploadToDatabase(uri);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void uploadToDatabase(Uri uri){

        SharedPreferences idPreference = PreferenceManager.getDefaultSharedPreferences(AccountInfo.this);
        SharedPreferences.Editor authEditor = idPreference.edit();
        String storedId = idPreference.getString("auth_userId", null);
        String storedUserName = idPreference.getString("auth_name", null);
        String storedPrn = idPreference.getString("auth_prn", null);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(storedId).child("userName");
        DatabaseReference avatarRef = FirebaseDatabase.getInstance().getReference("users").child(storedId).child("avatar");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile/");
        String fileName = "avtr_" + storedPrn;

        StorageReference imgRef = storageReference.child(fileName);
        UploadTask uploadTask = imgRef.putFile(uri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        String imageUrl = downloadUri.toString();
                        usersRef.setValue(storedUserName);
                        avatarRef.setValue(imageUrl);
                        authEditor.putString("avatar_url", imageUrl);
                        progress.setVisibility(View.GONE);
                        Toast.makeText(AccountInfo.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firebase error", "error: " + e);
                Toast.makeText(AccountInfo.this, "Unable to update profile: " +e, Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void enableEditMode(){
        ProjectToolkit.fadeIn(changeAvatar);
        ProjectToolkit.fadeIn(editLayout);
        ProjectToolkit.fadeOut(finalLayout);

        ProjectToolkit.fadeOut(beta);
        ProjectToolkit.fadeOut(dev);
        ProjectToolkit.fadeOut(panicButton);
        ProjectToolkit.fadeOut(logOut);

        editProfile.setText("Save");
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditMode();
            }
        });

        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void disableEditMode(){
        ProjectToolkit.fadeOut(changeAvatar);
        ProjectToolkit.fadeOut(editLayout);
        ProjectToolkit.fadeIn(finalLayout);

//        ProjectToolkit.fadeIn(beta);
//        ProjectToolkit.fadeIn(dev);
        ProjectToolkit.fadeIn(panicButton);
        ProjectToolkit.fadeIn(logOut);

        editProfile.setText("Edit profile");
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enableEditMode();
            }
        });

    }
    private void updateStatus(Boolean status){
        statusButton = findViewById(R.id.switchStatus_button);
        if(status){
            statusButton.setText("Set as Away");
        }else{
            statusButton.setText("Set as Available");
        }
    }

    private void loadInterstitialAd(Activity context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AccountInfo.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }

}