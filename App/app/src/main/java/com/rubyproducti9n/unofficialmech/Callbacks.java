package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Callbacks extends AppCompatActivity {


    //Gets UserRoel and returns its value in integer
    public interface UserRoleCallback{
        void onUserRole(int role);
    }
    public static void getUserRole(Context context, UserRoleCallback callback){
        int value = 0;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String role = preferences.getString("auth_userole", null);
        if (role!=null){
            if (role.equals("Admin")){
                value = 1;
            }else if (role.equals("Faculty")){
                value = 2;
            }else if (role.equals("Student")){
                value = 3;
            }
            callback.onUserRole(value);
        }
    }

    public  interface  AdValue{
        void onAdValue(boolean value);
    }
    public static void getAdValue(MaterialCardView ad_container, AdValue callback){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("app-configuration/ad_value");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean val = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                if (val){
                    callback.onAdValue(val);
                    if (ad_container!=null){
                        ProjectToolkit.fadeIn(ad_container);
                    }
                }else{
                    callback.onAdValue(val);
                    if (ad_container!=null){
                        ad_container.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onAdValue(false);
                ad_container.setVisibility(View.GONE);
            }
        });
    }

}
