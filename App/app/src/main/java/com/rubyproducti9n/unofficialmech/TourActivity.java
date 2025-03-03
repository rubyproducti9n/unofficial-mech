package com.rubyproducti9n.unofficialmech;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

public class TourActivity extends BaseActivity {

    MaterialButton n1, n2, n3, n4, n5, n6, n7, skipTour;
    ViewFlipper viewFlipper;
    LottieAnimationView lottieWelcome, lottie1, lottie2, lottie3, lottieErp, lottieFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tour);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left));
        n1 = findViewById(R.id.n1);
        n2 = findViewById(R.id.n2);
        n3 = findViewById(R.id.n3);
        n4 = findViewById(R.id.n4);
        n5 = findViewById(R.id.n5);
        n6 = findViewById(R.id.n6);
        n7 = findViewById(R.id.n7);
        skipTour = findViewById(R.id.skipTour);
        lottieWelcome = findViewById(R.id.lottieWelcome);
        lottieErp = findViewById(R.id.lottieErp);
        lottieFinish = findViewById(R.id.lottieFinish);
        lottie1 = findViewById(R.id.lottie1);
        lottie2 = findViewById(R.id.lottie2);
        lottie3 = findViewById(R.id.lottie3);

        lottieWelcome.setAnimation(R.raw.anim_welcome);
        lottieWelcome.loop(false);
        lottie1.setAnimation(R.raw.anim_calendar);
        lottie1.loop(false);
        lottie2.setAnimation(R.raw.anim_gemini_icon);
        lottie2.loop(true);
        lottie3.setAnimation(R.raw.anim_gemini_nm);
        lottie3.loop(true);
        lottieErp.setAnimation(R.raw.anim_erp);
        lottieErp.loop(false);
        lottieFinish.setAnimation(R.raw.anim_finish);
        lottieFinish.loop(false);

        lottieWelcome.playAnimation();


        n1.setOnClickListener(v -> viewFlipper.showNext());
        n2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                lottie1.playAnimation();
            }
        });
        n3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie1.pauseAnimation();
                lottie1.cancelAnimation();
                viewFlipper.showNext();
                lottie2.playAnimation();
            }
        });
        n4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie2.pauseAnimation();
                lottie2.cancelAnimation();
                viewFlipper.showNext();
                lottie3.playAnimation();
            }
        });
        n5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie3.pauseAnimation();
                lottie3.cancelAnimation();
                lottieErp.playAnimation();
                viewFlipper.showNext();
            }
        });
        n6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieErp.cancelAnimation();
                lottieFinish.playAnimation();
                viewFlipper.showNext();
            }
        });
        n7.setOnClickListener(v -> startActivity(new Intent(TourActivity.this, MainActivity.class)));
        skipTour.setOnClickListener(v -> startActivity(new Intent(TourActivity.this, MainActivity.class)));

        ImageView bg2 = findViewById(R.id.bg2);
        translateImageViewY(bg2, -500f);

    } public void translateImageViewY(ImageView imageView, float translateY) {
        // Translate the ImageView along the Y axis to a negative value
        imageView.animate()
                .translationY(translateY)
                .setInterpolator(new AccelerateDecelerateInterpolator())// Moves upwards by translateY value
                .setDuration(900) // Set animation duration in milliseconds
                .start(); // Start the animation
    }
    public void rotateImageViewIndefinitely(ImageView imageView) {
        // Create a rotation animation that rotates the ImageView indefinitely
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotateAnimation.setDuration(10000); // Duration of one full rotation (in milliseconds)
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE); // Repeat indefinitely
        rotateAnimation.setInterpolator(new LinearInterpolator()); // Keep the rotation smooth and constant
        rotateAnimation.start(); // Start the animation
    }
}