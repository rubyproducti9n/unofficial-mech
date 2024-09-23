package com.rubyproducti9n.unofficialmech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class LockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        LottieAnimationView anim = findViewById(R.id.lottie);
        anim.setAnimation(R.raw.anim_lock_advanced);
        anim.playAnimation();

        TextInputEditText pass = findViewById(R.id.passEditText);
        MaterialButton validate = findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass.getText().toString().equals("9693")){
                    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(LockActivity.this);
                    SharedPreferences.Editor e = p.edit();
                    e.putString("auth_userId", "admin_access");
                    e.putString("auth_name", "Om" + " " + "Lokhande");
                    e.putString("auth_email", "omlokhandemech@sanjivanicoe.org.in");
                    e.putString("auth_password", null);
                    e.putString("auth_prn", "UME21M1075");
                    e.putString("auth_roll", "77");
                    e.putString("auth_division", "B");
                    e.putString("auth_gender", "Male");
                    e.putString("auth_userole", "Admin");
                    e.putString("auth_mob", "7020162178");
                    e.putString("auth_personalEmail", "om.lokhande34@gmail.com");
                    e.apply();
                    startActivity(new Intent(LockActivity.this, DeveloperActivity.class));
                    Toast.makeText(LockActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                }
                if (pass.getText().toString().equals("1980")){

                }
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")){
                    if (s.toString().equals(securityManager(Integer.parseInt(s.toString())))){
                    Toast.makeText(LockActivity.this, "Welcome sir!", Toast.LENGTH_SHORT).show();
                    granted();
                    }
                    if (s.toString().equals(adminManager(Integer.parseInt(s.toString())))){
                        Toast.makeText(LockActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LockActivity.this, DeveloperActivity.class));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public static int generateSecureNumber() {
        Random random = new Random();
        int baseNumber = random.nextInt(25) + 1000; // Generate a number between 1000 and 1024

        String hash = generateHash(baseNumber);
        int secureNumber = hashToNumber(hash);

        return secureNumber;
    }

    private static String generateHash(int number) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(String.valueOf(number).getBytes());
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static int hashToNumber(String hash) {
        // Convert part of the hash to a number within 1000 to 1024 range
        int hashSegment = hash.substring(0, 4).hashCode(); // Use the first 4 characters
        int secureNumber = 1000 + Math.abs(hashSegment) % 25;
        return secureNumber;
    }
    private void granted(){
        BottomSheetFacultyAccount b = new BottomSheetFacultyAccount();
        b.show(getSupportFragmentManager(), "tag");
    }

    private boolean adminManager(int num){
        return num==9693;
    }
    private boolean securityManager(int num){
        return 1000 <= num && 1024 >=num;
    }

}