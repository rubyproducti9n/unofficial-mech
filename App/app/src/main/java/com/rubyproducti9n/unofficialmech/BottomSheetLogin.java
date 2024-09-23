package com.rubyproducti9n.unofficialmech;


import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getSystemAdValue;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.h;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.initiateAds;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isEncoded;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubyproducti9n.smartmech.AlgorithmEngine;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

public class BottomSheetLogin extends BottomSheetDialogFragment {
    BroadcastReceiver broadcastReceiver;
    TextInputEditText materialText;
    TextInputEditText materialEmail;
    MaterialButton mdBtn;
    CircularProgressIndicator progress;
    TextView expiryDateTxtView;
    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.bottom_sheet_login, container, false);


        pref = PreferenceManager.getDefaultSharedPreferences(requireContext());


        initiateAds(requireActivity(), requireContext());

        LottieAnimationView animLogin = view.findViewById(R.id.animLogin);
        animLogin.setAnimation(R.raw.login);
        animLogin.playAnimation();

        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        materialText = view.findViewById(R.id.uniquePass);
        materialEmail = view.findViewById(R.id.uniqueEmail);
        mdBtn = view.findViewById(R.id.checkbtn);
        mdBtn.setEnabled(false);
        MaterialButton help = view.findViewById(R.id.helpbtn);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
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
                loadInterstitialAd(requireActivity());
            }
        });

        return view;
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
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
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
                            if (h(requireActivity(),Password).equals(password) || Password.equals(altPassword)){
                                grant();
                            }
                        }else{
                            MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(requireContext());
                            b.setTitle("Security Alert")
                                    .setMessage("Our system just detected that your password is at risk, and may get breach easily. It is recommended to secure your password. \n\n Secure your account?")
                                    .setPositiveButton("Secure now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            securePass(Email, Password);
                                            if (h(requireContext(), Password).equals(password) || Password.equals(authAltPassword)){
                                            grant();
                                            }else {
                                            Toast.makeText(getContext(), "Access denied, check your credentials and try again", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(getContext(), "Access denied, check your credentials and try again", Toast.LENGTH_SHORT).show();
                                                progress.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                            b.show();
                        }
                    }


//                    //Last login shared preference
//                    SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getContext());
//                    Date currentDate = new Date();
//                    //Update last login time in SharedPreferences
//                    sharedPreferences.edit().putLong("lastLoginTime", currentDate.getTime()).apply();
//
//                    Intent intent = new Intent(getContext(), MainActivity.class);
//                    startActivity(intent);
//                    getActivity().finish();
                }else{
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });

//        if(Password.equals(password) && betaEmail.contains(Email)){
//
//            Intent intent = new Intent(getContext(), MainActivity.class);
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
//            Intent intent = new Intent(getContext(), MainActivity.class);
//            startActivity(intent);
//            getActivity().finish();
//            Toast.makeText(getContext(), "Welcome Admin", Toast.LENGTH_SHORT).show();
//        }
//        else if (authEmail.contains(Email)){
////            SharedPreferences.Editor authEditor = pref.edit();
////            authEditor.putString("auth_email", Email);
////            authEditor.apply();
//
//            Intent intent = new Intent(getContext(), MainActivity.class);
//            startActivity(intent);
//            getActivity().finish();
//        } else if (Password.equals("dev@9693") && adminEmail.contains(Email)) {
//            Intent intent = new Intent(getContext(), DeveloperActivity.class);
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
                        userSnapshot.getRef().child("password").setValue(h(requireActivity(), p));
                        String pa = h(requireActivity(), p);

                        authEditor.putString("auth_password", pa);
                        authEditor.apply();
                    }
                }else{
                    Toast.makeText(getContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void grant(){
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(requireContext());
        Date currentDate = new Date();

        sharedPreferences.edit().putLong("lastLoginTime", currentDate.getTime()).apply();
        sharedPreferences.edit().putBoolean("dashboard", true).apply();

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
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
