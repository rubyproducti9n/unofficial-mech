package com.rubyproducti9n.unofficialmech;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreatePost extends BaseActivity {

    private EditText titleEt, companyEt, locationEt, skillsEt, applyLinkEt, descriptionEt;
    private Spinner typeSpinner;
    private Switch remoteSwitch;
    private Button postBtn;
    private ProgressBar progressBar;

    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        titleEt = findViewById(R.id.titleEt);
        companyEt = findViewById(R.id.companyEt);
        locationEt = findViewById(R.id.locationEt);
        skillsEt = findViewById(R.id.skillsEt);
        applyLinkEt = findViewById(R.id.applyLinkEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        typeSpinner = findViewById(R.id.typeSpinner);
        remoteSwitch = findViewById(R.id.remoteSwitch);
        postBtn = findViewById(R.id.postBtn);
        progressBar = findViewById(R.id.progressBar);

        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        mAuth = FirebaseAuth.getInstance();

        postBtn.setOnClickListener(v -> createPost());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Job", "Internship", "Freelance", "Walk-in"});

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

    }

    private void createPost() {
        String title = titleEt.getText().toString().trim();
        String company = companyEt.getText().toString().trim();
        String location = locationEt.getText().toString().trim();
        String skillsRaw = skillsEt.getText().toString().trim();
        String applyLink = applyLinkEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        boolean isRemote = remoteSwitch.isChecked();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(company) || TextUtils.isEmpty(location)
                || TextUtils.isEmpty(skillsRaw) || TextUtils.isEmpty(applyLink)
                || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> skills = Arrays.asList(skillsRaw.split(","));
        String postId = postsRef.push().getKey();
        String postedBy = mAuth.getCurrentUser() != null ? mAuth.getUid() : "anonymous";

        long postDate = System.currentTimeMillis();
        long deadline = postDate + (7L * 24 * 60 * 60 * 1000); // 7 days

        Post post = new Post(postId, title, type, company, location, isRemote, skills, applyLink, description, postedBy, postDate, deadline);

        progressBar.setVisibility(View.VISIBLE);
        assert postId != null;
        postsRef.child(postId).setValue(post)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("CreatePost", "Error saving post", e);
                });
    }

}