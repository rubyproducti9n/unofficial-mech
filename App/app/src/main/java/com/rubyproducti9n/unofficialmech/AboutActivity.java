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

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private TextView instruction;
    private ImageView profile;
    private MaterialButton google, insta, coffee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupVersionInfo();
        setupDynamicLayout();
        setupToolbar();
        setupUIElements();
        setupButtonListeners();
    }

    private void setupVersionInfo() {
        TextView versionTxt = findViewById(R.id.version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionTxt.setText("v" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setupDynamicLayout() {
        DynamicConstrainLayout dynamicConstrainLayout = new DynamicConstrainLayout(this);
        dynamicConstrainLayout.createConstraintLayout(
                "T1",
                "Desc",
                "https://rubyproducti9n.github.io/mech/img/avatar.jpg",
                "Link Text",
                "https://google.com"
        );
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupUIElements() {
        instruction = findViewById(R.id.instructions);
        profile = findViewById(R.id.imageView);
        google = findViewById(R.id.btnGoogle);
        insta = findViewById(R.id.btnInsta);
        coffee = findViewById(R.id.btnCoffee);
    }

    private void setupButtonListeners() {
        google.setOnClickListener(view -> openWebViewActivity("https://g.dev/omlokhande10"));

        insta.setOnClickListener(view -> openExternalLink("https://instagram.com/om.lokhande10?igshid=ZTQ3MWpwbHhmYm16"));

        coffee.setOnClickListener(view -> openWebViewActivity("https://buymeacoffee.com/omlokhande"));
    }

    private void openWebViewActivity(String link) {
        Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
        intent.putExtra("link", link);
        startActivity(intent);
    }

    private void openExternalLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}