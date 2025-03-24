package com.rubyproducti9n.unofficialmech;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CreatePost extends BaseActivity {

    ImageView postImg;
    MaterialCardView postImgContainer;
    EditText captionEditTxt;
    MaterialButton posBtn, addImg;
    Uri rawSelectedImageUri;
    boolean uploadEnableStatus;
    Intent intent;
    private String anonymous = "public";
    MaterialCheckBox box;
    LinearProgressIndicator progressBar;
    ActivityResultLauncher<Intent> startActivityForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postImg = findViewById(R.id.img);
        postImg.setVisibility(View.GONE);
        addImg = findViewById(R.id.addImg);
        captionEditTxt = findViewById(R.id.caption);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        posBtn = findViewById(R.id.post);
        postImgContainer = findViewById(R.id.materialCardView7);

        fetchServer();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        box = findViewById(R.id.anonymous);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    anonymous = "private";
                } else {
                    anonymous = "public";
                }
            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            rawSelectedImageUri = data.getData();
                            String selectedImageUri = rawSelectedImageUri.toString();
                            if (isImage(this, rawSelectedImageUri)){
                                postImg.setImageURI(rawSelectedImageUri);
                                postImgContainer.setVisibility(View.VISIBLE);

                            }
                        }else{
                            rawSelectedImageUri = null;
                        }
                    }
                }
        );

        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                push(captionEditTxt.getText().toString(), rawSelectedImageUri);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rawSelectedImageUri!=null){
            new MaterialAlertDialogBuilder(this)
                    .setMessage(rawSelectedImageUri.toString()).show();
        }
    }

    public static boolean isImage(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();

        // Get MIME type from the Uri
        String mimeType = contentResolver.getType(uri);

        // Check if the MIME type starts with "image/"
        return mimeType != null && mimeType.startsWith("image/");
    }

    private void fetchServer(){
        MaterialCardView alert = findViewById(R.id.alert);
        alert.setVisibility(View.GONE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(uploadsValue)){
                    posBtn.setEnabled(true);
                    alert.setVisibility(View.GONE);
                }else{
                    ProjectToolkit.fadeIn(alert);
                    alert.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                posBtn.setEnabled(false);
            }
        });
    }

    private void loadIntent(){
        Intent i = getIntent();
        if (intent!=null){
            String sharedText = intent.getStringExtra("sharedText");
            String sharedImage = intent.getStringExtra("sharedImage");
            if (sharedText!=null){
                captionEditTxt.setText(sharedText);
            }
            if (sharedImage!=null){
                Uri imageUri = Uri.parse(sharedImage);
                postImg.setImageURI(imageUri);
                postImg.setVisibility(View.VISIBLE);
                postImgContainer.setVisibility(View.VISIBLE);
            }else {
                postImg.setVisibility(View.GONE);
                postImgContainer.setVisibility(View.GONE);
            }
        }
    }

    private void reset(){
        posBtn.setEnabled(false);
        postImg.setVisibility(View.GONE);
        captionEditTxt.setText("");
        postImgContainer.setVisibility(View.GONE);
    }


    private void push(String caption, Uri imgUri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProjectToolkit.fadeIn(progressBar);
                posBtn.setEnabled(false);
            }
        });

        if (imgUri == null && caption.isEmpty()) {
            Snackbar.make(findViewById(R.id.progressBar), "Please add a caption or an image", Snackbar.LENGTH_SHORT).setAnchorView(R.id.anonymous).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreatePost.this);
        String uid = preferences.getString("auth_userId", null);
        String authName = preferences.getString("auth_name", null);



        String fileName = "post" + System.currentTimeMillis() + "." + getFileType(String.valueOf(imgUri));
        StorageReference imgRef = storageReference.child(fileName);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String uploadTime = currentDateTime.format(dateTimeFormatter);

        String postId = databaseReference.push().getKey();

        if (imgUri != null) {
            Uri uri = null;
            if (isImage(this, imgUri)){
                uri = ProjectToolkit.compress(this, imgUri, 25);
            }else{
                uri = imgUri;
            }
            //Snackbar.make(findViewById(R.id.progressBar), "URI: " +uri, Snackbar.LENGTH_SHORT).setAnchorView(R.id.anonymous).show();
            UploadTask uploadTask = imgRef.putFile(uri);
            progressBar.setProgress(80, true);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            String imageUrl = downloadUri.toString();
                            Map<String, Boolean> likes = new HashMap<>();
                            Post post = new Post(postId, uid, authName, imageUrl, caption, uploadTime, anonymous, likes);
                            Map<String, Object> postValues = post.toMap();
                            databaseReference.child(postId).updateChildren(postValues);
                            Snackbar.make(findViewById(R.id.progressBar), "Posted!", Snackbar.LENGTH_SHORT).setAnchorView(R.id.anonymous).show();
                            finish();
                            reset();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(R.id.progressBar), "Oops! An error occurred, try again later", Snackbar.LENGTH_SHORT).setAnchorView(R.id.anonymous).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.progressBar), "Request denied", Snackbar.LENGTH_SHORT).setAnchorView(R.id.anonymous).show();
                        }
                    });
                }
            });
        } else {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Map<String, Boolean> likes = new HashMap<>();
                    Post post = new Post(postId, uid, authName, null, caption, uploadTime, anonymous, likes);
                    databaseReference.child(postId).setValue(post);
                    reset();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.progressBar), "Posted!", Snackbar.LENGTH_SHORT).setAnchorView(R.id.anonymous).show();
                            progressBar.setIndeterminate(true);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            },2000);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.progressBar), "Request denied, try again later", Snackbar.LENGTH_SHORT).setAnchorView(R.id.anonymous).show();
                        }
                    });
                }
            });

        }

    }
    private String getFileType(String urlString) {
        if (urlString.endsWith(".jpg") || urlString.endsWith(".jpeg") || urlString.endsWith(".png") || urlString.endsWith(".gif")) {
            return "image";
        } else if (urlString.endsWith(".pdf")) {
            return "pdf";
        } else if (urlString.endsWith(".doc") || urlString.endsWith(".docx")) {
            return "word";
        } else if (urlString.endsWith(".xls") || urlString.endsWith(".xlsx")) {
            return "excel";
        } else {
            return "unknown";
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult.launch(intent);
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult.launch(intent);

//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//
//        intent.setType("*/*");
//        String[] mimeTypes = {"image/*", "application/pdf",
//                "application/msword",
//                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
//                "application/vnd.ms-excel",
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // XLSX
//
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult.launch(intent);

    }
    private void setProgressBar(View view){
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int progress = progressBar.getProgress();
                if (progress < 90) {
                    progressBar.setProgress(progress + 1, true);
                    handler.postDelayed(this, 10);
                }
                if (progress > 89){
                    progressBar.setProgress(100, true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ProjectToolkit.fadeOut(progressBar);
                            if (view != null) {
                                ProjectToolkit.fadeIn(view);
                            }
                        }
                    }, 1000);
                }
            }
        };
        handler.post(runnable);
    }

}