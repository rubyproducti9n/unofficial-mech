package com.rubyproducti9n.unofficialmech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

public class AboutActivity extends AppCompatActivity {

    TextView instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionTxt = findViewById(R.id.version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            versionTxt.setText("v" + version);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        DynamicConstrainLayout dynamicConstrainLayout = new DynamicConstrainLayout(this);
        dynamicConstrainLayout.createConstraintLayout("T1", "Desc", "https://rubyproducti9n.github.io/mech/img/avatar.jgp", "Link Text", "https://google.com");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView expiryDateTxtView;

        instruction = findViewById(R.id.instructions);

        ImageView profile = findViewById(R.id.imageView);
//        Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/dev_avatar.jpg").into(profile);

        MaterialCardView logo = findViewById(R.id.mc1);
        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(AboutActivity.this, LockActivity.class));
                Toast.makeText(AboutActivity.this, "Developer mode enabled!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        MaterialButton google = findViewById(R.id.btnGoogle);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "https://g.dev/omlokhande10";
                Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });

        MaterialButton insta = findViewById(R.id.btnInsta);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/om.lokhande10?igshid=ZTQ3MWpwbHhmYm16"));
                startActivity(intent);
            }
        });

        MaterialButton coffee = findViewById(R.id.btnCoffee);
        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "https://buymeacoffee.com/omlokhande";
                Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });

    }
}