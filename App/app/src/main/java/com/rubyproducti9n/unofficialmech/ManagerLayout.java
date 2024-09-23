package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;

public class ManagerLayout extends ConstraintLayout {

    LottieAnimationView anim;

    public ManagerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.manager_layout, this, true);

        ImageView img = findViewById(R.id.ic);
        anim = findViewById(R.id.anim);


        TextView title = findViewById(R.id.title);
        TextView txt = findViewById(R.id.txt);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String role = preferences.getString("userole", null);


        ProjectToolkit.serviceCheck(getContext(), new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                if (result){if (
                        role!=null){
                    if (role.equals("Faculty")) {
                        txt.setVisibility(VISIBLE);
                        anim.setAnimation(R.raw.privacy_policy);
                        img.setVisibility(View.INVISIBLE);
                        anim.loop(false);
                        anim.playAnimation();
                    }
                    title.setVisibility(View.GONE);
                    txt.setVisibility(VISIBLE);
                    anim.setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);
                }else {
                    title.setVisibility(View.VISIBLE);
                    title.setText("Access Limited");
                    txt.setVisibility(VISIBLE);
                    txt.setText("Please log in to unlock all features and enjoy the full app experience.");
                    anim.setVisibility(View.VISIBLE);
                    anim.setAnimation(R.raw.privacy_policy);
                    anim.loop(false);
                    anim.playAnimation();
                    img.setVisibility(View.INVISIBLE);
                }
                }else{
                    title.setVisibility(View.GONE);
                    anim.setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);
                    txt.setVisibility(VISIBLE);
                }
            }
        });



//        if (role==null){
//            title.setVisibility(View.VISIBLE);
//            title.setText("Access Limited");
//            txt.setText("Please log in to unlock all features and enjoy the full app experience.");
//            anim.setVisibility(View.VISIBLE);
//            anim.setAnimation(R.raw.privacy_policy);
//            anim.loop(false);
//            anim.playAnimation();
//            anim.animate();
//            img.setVisibility(View.INVISIBLE);
//        } else if (role.equals("Faculty")) {
//            anim.setAnimation(R.raw.privacy_policy);
//            img.setVisibility(View.INVISIBLE);
//            anim.loop(false);
//            anim.playAnimation();
//            anim.animate();
//        }else{
//            title.setVisibility(View.GONE);
//            txt.setText("Unable to load content, please try again later");
//            img.setVisibility(View.VISIBLE);
//            anim.setVisibility(View.GONE);
//        }


    }

}
