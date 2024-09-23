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

public class AccessDeniedActivity extends AppCompatActivity {

    TextView errTitle, errMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_denied);

        LottieAnimationView animDenied = findViewById(R.id.animDenied);
        errTitle = findViewById(R.id.errTitle);
        errMsg = findViewById(R.id.errMsg);

        Intent intent = getIntent();
        int anim = intent.getIntExtra("err_anim", 0);
        String title = intent.getStringExtra("err_title");
        String msg = intent.getStringExtra("err_msg");

//        if (intent!=null){
//            animDenied.setAnimation(R.raw.coming_soon);
//            animDenied.loop(true);
//            animDenied.playAnimation();
//            errTitle.setText("Error 500");
//            errMsg.setText("Oops! something went wrong, please try again later");
//        }else{
//
//        }

        animDenied.setAnimation(R.raw.anim_lock_advanced);
        animDenied.playAnimation();
        errTitle.setText("LOCKDOWN");
        errMsg.setText("Due to some technical issues lockdown has been initiated\n You'll receive an update soon");

//To set status bar color to white
        if (Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.DynamicWhite));
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAffinity();
    }
}