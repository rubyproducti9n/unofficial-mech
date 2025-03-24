package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getSystemAdValue;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.h;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.initiateAds;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isEncoded;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashSet;

public class LoginActivity extends BaseActivity {
    BroadcastReceiver broadcastReceiver;
    TextInputEditText materialText;
    TextInputEditText materialEmail;
    MaterialButton mdBtn;
    CircularProgressIndicator progress;
    TextView expiryDateTxtView;
    private SharedPreferences pref;
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    ViewFlipper viewFlipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        pref = PreferenceManager.getDefaultSharedPreferences(this);

        viewFlipper = findViewById(R.id.flipper);

        initiateAds(this, this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
//            updateUI(account); // If user is already signed in, update the UI
        }

        MaterialButton forgetBtn = findViewById(R.id.forgotPass);
        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        });

        SignInButton bt = findViewById(R.id.signIn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        MaterialButton login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        LottieAnimationView animLogin = findViewById(R.id.animLogin);
        animLogin.setAnimation(R.raw.login);
        animLogin.playAnimation();

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        materialText = findViewById(R.id.uniquePass);
        materialEmail = findViewById(R.id.uniqueEmail);
        mdBtn = findViewById(R.id.checkbtn);
        mdBtn.setEnabled(false);
        MaterialButton help = findViewById(R.id.helpbtn);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this);
                builder.setTitle("Help");
                builder.setMessage("Email: Enter your registered email for beta test \n" +
                        "Unofficial Pass: mech.beta \n \n" +
                        "Instructions: \n" +
                        "1. App may not be stable as in beta stage. \n" +
                        "2. Report any bug/glitch as soon as possible. \n" +
                        "3. You can suggest feature to implement in future updates.");
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });

        materialText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String Password = materialText.getText().toString();
                if(Password.toString().isEmpty()){
                    mdBtn.setEnabled(false);
                }else {
                    mdBtn.setEnabled(true);
                }
            }
        });

        mdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
                mdBtn.setEnabled(false);
                progress.setVisibility(View.VISIBLE);
//                loadAd(requireActivity());
                loadInterstitialAd(LoginActivity.this);
            }
        });


    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleLogin(account.getEmail());
            } catch (ApiException e) {
                // Google Sign-In failed
                Log.w("Google Sign-In", "Google sign in failed", e);
                Snackbar.make(materialText, "Sign-In Failed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void googleLogin(String email){
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = userDatabase.getReference("users");
        userRef.orderByChild("personalEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot userSnapshot : snapshot.getChildren()){
                        String userId = userSnapshot.child("userId").getValue(String.class);
                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                        String lastName = userSnapshot.child("lastName").getValue(String.class);
                        String initials = userSnapshot.child("initials").getValue(String.class);
                        String contact = userSnapshot.child("contact").getValue(String.class);
                        String div = userSnapshot.child("div").getValue(String.class);
                        String email = userSnapshot.child("personalEmail").getValue(String.class);
                        String mob = userSnapshot.child("contact").getValue(String.class);
                        String prn = userSnapshot.child("prn").getValue(String.class);
                        String gender = userSnapshot.child("gender").getValue(String.class);
                        String rollNo = userSnapshot.child("rollNo").getValue(String.class);
                        String userRole = userSnapshot.child("role").getValue(String.class);

                        SharedPreferences.Editor authEditor = pref.edit();
                        authEditor.putString("auth_userId", userId);
                        authEditor.putString("auth_name", firstName + " " + lastName);
                        if (initials!=null){
                            authEditor.putString("auth_initials", initials);
                        }else{
                            authEditor.putString("auth_initials", null);
                        }
                        authEditor.putString("auth_contact", contact);
                        authEditor.putString("auth_division", div);
                        authEditor.putString("auth_email", email);
                        authEditor.putString("auth_mob", mob);
                        authEditor.putString("auth_prn", prn);
                        authEditor.putString("auth_gender", gender);
                        authEditor.putString("auth_rollNo", rollNo);
                        authEditor.putString("auth_userole", userRole);
                        authEditor.apply();
                        startActivity(new Intent(LoginActivity.this, TourActivity.class));
                        finish();
                    }
                }else{
                    Snackbar.make(materialText, "User not registered", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(materialText, "Unable to connect to server, try again later", Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    public void checkPassword(){

        HashSet<String> adminEmail = new HashSet<>();
        adminEmail.add("om.lokhande34@gmail.com");
        adminEmail.add("aishwaryamanojkumbhar@gmail.com");
        adminEmail.add("aishwaryakumbhar1234@gmail.com");

        //Beta test emails
        HashSet<String> betaEmail = new HashSet<>();
        betaEmail.add("84sairajule@gmail.com");
        betaEmail.add("aniket4sonawane5@gmail.com");
        betaEmail.add("sanapnikhil2002@gmail.com");
        betaEmail.add("adityalabhade806@gmail.com");
        betaEmail.add("morekeshav2003@gmail.com");
        betaEmail.add("shubham.vaykar@gmail.com");
        betaEmail.add("atharvakulkarni2423@gmail.com");
        betaEmail.add("prasadpawar1192003@gmail.com");
        betaEmail.add("sanapsarthak91@gmail.com");
        betaEmail.add("landeramdas813@gmail.com");

        //Authorised emails
        HashSet<String> authEmail = new HashSet<>();
        authEmail.add("");

        String password = "mech.beta";


        String Password = materialText.getText().toString();
        String Email = materialEmail.getText().toString();

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = userDatabase.getReference("users");

        userRef.orderByChild("personalEmail").equalTo(Email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

//                    String emailAuth = snapshot.child("email").getValue(String.class);
                    //String passAuth = snapshot.child("password").getValue(String.class);
                    String altPassword = null;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor authEditor = pref.edit();
                    String authPassword = preferences.getString("auth_password", null);
                    String authAltPassword = preferences.getString("auth_altPassword", null);

                    for(DataSnapshot userSnapshot : snapshot.getChildren()){
                        //Retrieve the user data
                        String userId = userSnapshot.child("userId").getValue(String.class);
                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                        String lastName = userSnapshot.child("lastName").getValue(String.class);
                        String initials = userSnapshot.child("initials").getValue(String.class);
                        String contact = userSnapshot.child("contact").getValue(String.class);
                        String div = userSnapshot.child("div").getValue(String.class);
                        String email = userSnapshot.child("personalEmail").getValue(String.class);
                        String mob = userSnapshot.child("contact").getValue(String.class);
                        String password = userSnapshot.child("password").getValue(String.class);
                        altPassword = userSnapshot.child("altPassword").getValue(String.class);
                        String prn = userSnapshot.child("prn").getValue(String.class);
                        String gender = userSnapshot.child("gender").getValue(String.class);
                        String rollNo = userSnapshot.child("rollNo").getValue(String.class);
                        String userRole = userSnapshot.child("role").getValue(String.class);
                        Integer plan = userSnapshot.child("plan").getValue(Integer.class);

                        authEditor.putString("auth_userId", userId);
                        authEditor.putString("auth_name", firstName + " " + lastName);
                        if (initials!=null){
                            authEditor.putString("auth_initials", initials);
                        }else{
                            authEditor.putString("auth_initials", null);
                        }
                        authEditor.putString("auth_contact", contact);
                        authEditor.putString("auth_division", div);
                        authEditor.putString("auth_email", email);
                        authEditor.putString("auth_mob", email);
                        authEditor.putString("auth_password", password);
                        authEditor.putString("auth_altPassword", altPassword);
                        authEditor.putString("auth_prn", prn);
                        authEditor.putString("auth_gender", gender);
                        authEditor.putString("auth_rollNo", rollNo);
                        authEditor.putString("auth_userole", userRole);
                        if (userSnapshot.child("plan").exists()){
                            authEditor.putInt("auth_plan", plan);
                        }else{
                            authEditor.putInt("auth_plan", -1);
                        }
                        authEditor.apply();

                        if (isEncoded(Password)){
                            if (h(LoginActivity.this,Password).equals(password) || Password.equals(altPassword)){
                                grant();
                            }
                        }else{
                            MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(LoginActivity.this);
                            b.setTitle("Security Alert")
                                    .setMessage("Our system just detected that your password is at risk, and may get breach easily. It is recommended to secure your password. \n\n Secure your account?")
                                    .setPositiveButton("Secure now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            securePass(Email, Password);
                                            if (h(LoginActivity.this, Password).equals(password) || Password.equals(authAltPassword)){
                                                grant();
                                            }else {
                                                Toast.makeText(LoginActivity.this, "Access denied, check your credentials and try again", Toast.LENGTH_SHORT).show();
                                                progress.setVisibility(View.GONE);
                                            }
                                        }
                                    })
                                    .setNegativeButton("Remind me later", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Password.equals(authPassword) || Password.equals(authAltPassword)){
                                                grant();
                                            }else {
                                                Toast.makeText(LoginActivity.this, "Access denied, check your credentials and try again", Toast.LENGTH_SHORT).show();
                                                progress.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                            b.show();
                        }
                    }


//                    //Last login shared preference
//                    SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                    Date currentDate = new Date();
//                    //Update last login time in SharedPreferences
//                    sharedPreferences.edit().putLong("lastLoginTime", currentDate.getTime()).apply();
//
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    getActivity().finish();
                }else{
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error 503", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });

//        if(Password.equals(password) && betaEmail.contains(Email)){
//
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            getActivity().finish();
//
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putLong("lastLoginTime", System.currentTimeMillis());
//            editor.apply();
//
//            SharedPreferences.Editor betaEditor = pref.edit();
//            betaEditor.putString("beta_email", Email);
//            betaEditor.apply();
//
//        }else if (adminEmail.contains(Email) && Password.contains(password)){
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("admin_email", Email);
//            editor.apply();
//
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            getActivity().finish();
//            Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
//        }
//        else if (authEmail.contains(Email)){
////            SharedPreferences.Editor authEditor = pref.edit();
////            authEditor.putString("auth_email", Email);
////            authEditor.apply();
//
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            getActivity().finish();
//        } else if (Password.equals("dev@9693") && adminEmail.contains(Email)) {
//            Intent intent = new Intent(LoginActivity.this, DeveloperActivity.class);
//            startActivity(intent);
//        }
    }

    private void securePass(String m, String p){
        FirebaseDatabase.getInstance().getReference("users").orderByChild("personalEmail").equalTo(m).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    SharedPreferences.Editor authEditor = pref.edit();
                    for(DataSnapshot userSnapshot : snapshot.getChildren()){
                        userSnapshot.getRef().child("password").setValue(h(LoginActivity.this, p));
                        String pa = h(LoginActivity.this, p);

                        authEditor.putString("auth_password", pa);
                        authEditor.apply();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error 503", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void grant(){
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        Date currentDate = new Date();

        sharedPreferences.edit().putLong("lastLoginTime", currentDate.getTime()).apply();
        sharedPreferences.edit().putBoolean("dashboard", true).apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
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

}