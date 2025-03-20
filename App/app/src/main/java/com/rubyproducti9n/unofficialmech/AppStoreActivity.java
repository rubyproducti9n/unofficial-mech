package com.rubyproducti9n.unofficialmech;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.BuildConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppStoreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_store);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MaterialCardView card1 = findViewById(R.id.card1);
        card1.setVisibility(View.VISIBLE);
        ImageView unofficialIc = findViewById(R.id.app_ic_1);
        TextView unofficialAppTitle = findViewById(R.id.app_name_1);
        unofficialAppTitle.setText("Unofficial Mech");
        MaterialButton install1 = findViewById(R.id.button1);
        install1.setText("Installed");
        install1.setEnabled(false);

        MaterialCardView card2 = findViewById(R.id.card2);
        card2.setVisibility(View.GONE);
        ImageView geminiIc = findViewById(R.id.gemini_ic);
        TextView geminiAppTitle = findViewById(R.id.app_name_2);
        geminiAppTitle.setText("Gemini Pro");
        MaterialButton install2 = findViewById(R.id.button2);
        install2.setText("Coming soon");
        install2.setEnabled(false);
    }

    private void checkForUpdates(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("app-configuration/version");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String latestVersion = snapshot.getValue(String.class);
                String currentVersion = BuildConfig.VERSION_NAME;

                if(latestVersion != null && !latestVersion.equals(currentVersion)){
                    //performing actions for updating the app
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Handle the errors
//                Toast.makeText(MainActivity.this, "An error occured while checking for updates", Toast.LENGTH_SHORT).show();
            }
        });
    }

}