package com.rubyproducti9n.unofficialmech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class AccessDeniedActivity extends BaseActivity {

    private TextView errTitle, errMsg;
    private LottieAnimationView animDenied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_denied);

        initializeUI();
        setupAnimationAndErrorDetails();
        setStatusBarColor();
    }

    private void initializeUI() {
        animDenied = findViewById(R.id.animDenied);
        errTitle = findViewById(R.id.errTitle);
        errMsg = findViewById(R.id.errMsg);
    }

    private void setupAnimationAndErrorDetails() {
        Intent intent = getIntent();
        int anim = intent.getIntExtra("err_anim", 0);
        String title = intent.getStringExtra("err_title");
        String msg = intent.getStringExtra("err_msg");

        // Set animation and error messages
        animDenied.setAnimation(R.raw.anim_lock_advanced);
        animDenied.playAnimation();
        errTitle.setText("LOCKDOWN");
        errMsg.setText("Due to some technical issues lockdown has been initiated\nYou'll receive an update soon");
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.DynamicWhite));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAffinity();
    }
}