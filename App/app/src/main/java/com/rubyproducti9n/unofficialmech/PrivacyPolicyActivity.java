package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PrivacyPolicyActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        TextView instruction;
        instruction = findViewById(R.id.instructions);
        instruction.setText(R.string.privacyPolicy);
    }
}