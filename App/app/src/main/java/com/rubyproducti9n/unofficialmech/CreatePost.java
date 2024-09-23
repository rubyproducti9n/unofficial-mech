package com.rubyproducti9n.unofficialmech;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreatePost extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startActivityForResult;
    LinearProgressIndicator progressBar;
    ImageView img;
    MaterialButton button, addImageBtn;
    TextInputEditText editCaption;
    int postCount;
    private String selectedImageUriString;
    Uri rawSelectedImageUri;
    boolean uploadEnableStatus;
    Intent intent;
    private String anonymous = "public";
    MaterialCheckBox box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        checkEnableUpload();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        box = findViewById(R.id.anonymous);

        progressBar = findViewById(R.id.progressBar);
        ProjectToolkit.fadeIn(progressBar);
        //progressBar.setVisibility(View.VISIBLE);
        img = findViewById(R.id.img);
        button = findViewById(R.id.button);
        addImageBtn = findViewById(R.id.add_image);
        editCaption = findViewById(R.id.caption);

        box.setChecked(true);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    anonymous = "private";
                } else {
                    anonymous = "public";
                }
//                Toast.makeText(CreatePost.this, "Anonymous setting: " + anonymous, Toast.LENGTH_SHORT).show();
            }
        });

        ScrollView scrollView = findViewById(R.id.scroll_view);
        ProjectToolkit.fadeOut(scrollView);
        setProgressBar(scrollView);
        //scrollView.setVisibility(View.GONE);
        ImageView profilePicture = findViewById(R.id.avatar);
        TextView username = findViewById(R.id.username);
        TextView div = findViewById(R.id.div);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreatePost.this);
        String userId = pref.getString("auth_userId", null);
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("users");
        profileRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot userSnapshot : snapshot.getChildren()){
                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                        String lastName = userSnapshot.child("lastName").getValue(String.class);
                        String division = userSnapshot.child("div").getValue(String.class);
                        String avatar = userSnapshot.child("avatar").getValue(String.class);
                        Picasso.get().load(avatar).into(profilePicture);
                        username.setText(firstName + " " + lastName);
                        div.setText("From " + division + " division");
                        ProjectToolkit.fadeOut(progressBar);
                        ProjectToolkit.fadeIn(scrollView);
                        //progressBar.setVisibility(View.GONE);
                        //scrollView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase error", "error");
            }
        });



        addImageBtn.setOnClickListener(new View.OnClickListener() {
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
                            img.setImageURI(rawSelectedImageUri);
                            button.setText("Post");
                        }else{
                            rawSelectedImageUri = null;
                        }
                    }
                }
        );



    }


    private void showPermissionDenied() {
        MaterialAlertDialogBuilder materialAlert = new MaterialAlertDialogBuilder(CreatePost.this);
        materialAlert.setTitle("Warning");
        materialAlert.setMessage("This app requires media permission to function properly, Please grant the permission from the app settings.");
        materialAlert.setPositiveButton("App settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
                Toast.makeText(CreatePost.this, "Enable all permissions to enjoy uninterepted services", Toast.LENGTH_SHORT).show();
            }
        });
        materialAlert.setCancelable(false);
        materialAlert.show();
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
    }

//    private void uploadImage(Uri imgUri) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ProjectToolkit.fadeIn(progressBar);
//                button.setEnabled(false);
//            }
//        });
//
//        if (imgUri == null && Objects.requireNonNull(editCaption.getText()).toString().isEmpty()) {
//            Toast.makeText(CreatePost.this, "Please add a caption or an image", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(CreatePost.this, "Please wait while the post is uploaded", Toast.LENGTH_SHORT).show();
//            }
//        });
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts/");
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
//        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");
//
//        String anonymous = "public";
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreatePost.this);
//        String userId = preferences.getString("auth_userId", null);
//        String authName = preferences.getString("auth_name", null);
//        String uid = preferences.getString("auth_userId", null);
//        String caption = editCaption.getText().toString();
//
//        //Creating a unique file name
//        String fileName = "post" + System.currentTimeMillis() + ".jpg";
//
//        //Creating a reference to the file location in Firebase Storage
//        StorageReference imgRef = storageReference.child(fileName);
//
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String uploadTime = currentDateTime.format(dateTimeFormatter);
//
//        String postId = databaseReference.push().getKey();
//
//        if (rawSelectedImageUri != null){
//            if (rawSelectedImageUri.toString().startsWith("https://rubyproducti9n.github.io")){
//                authName = preferences.getString("auth_name", null);
//                String imageUrl = imgUri.toString();
//                Map<String, Boolean> likes = new HashMap<>();
//                Post post = new Post(postId, uid, authName, imageUrl, caption, uploadTime, anonymous, likes);
//                databaseReference.child(postId).setValue(post);
//            }else{
//                //Upload the file to the firebase storage
//                UploadTask uploadTask = imgRef.putFile(ProjectToolkit.compress(this, imgUri, 25));
//                progressBar.setProgress(80, true);
////                setProgressBar(null);
//
////                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
////                    @Override
////                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
////                        double uploadProgress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
////                        progressBar.setProgress((int) uploadProgress, true);
////                    }
////                });
//                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        //Image successfully uploaded
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                ProjectToolkit.fadeOut(progressBar);
////                            }
////                        });
//                        //customDialog("Success!", "Your post has been uploaded and will be displayed after a review", "Ok");
//                        //Notification("Success!", "Your post uploaded successfully");
//                        //Can retrieve the download URL of the uploaded image for further use
//                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri downloadUri) {
//                                //Retrieved uploaded image and can be used
//                                String authName = preferences.getString("auth_name", null);
//                                String imageUrl = downloadUri.toString();
//                                Map<String, Boolean> likes = new HashMap<>();
//                                Post post = new Post(postId, uid, authName, imageUrl, caption, uploadTime, anonymous, likes);
//                                databaseReference.child(postId).setValue(post);
//                                Toast.makeText(CreatePost.this, "Posted!", Toast.LENGTH_SHORT).show();
//                                //postCount = newPostCount;
//                                setDefault();
//                                reset();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                //Handle error while displaying the uploaded image
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(CreatePost.this, "Error while retrieving image", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //Handle error while uploading
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(CreatePost.this, "Error while uploading image", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }
//        }else{
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            setProgressBar(null);
//                            Toast.makeText(CreatePost.this, "Posted!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    //Notification("Success!", "Your post uploaded successfully");
//                    //Retrieved uploaded image and can be used
//                    String authName = preferences.getString("auth_name", null);
//                    String imageUrl = null;
//                    Map<String, Boolean> likes = new HashMap<>();
//                    Post post = new Post(postId, uid, authName, imageUrl, caption, uploadTime, anonymous, likes);
//                    databaseReference.child(postId).setValue(post);
//                    //postCount = newPostCount;
//                    setDefault();
//                    reset();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(CreatePost.this, "An error occurred", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//        }
//
//
//
//
//    }

    private void uploadImage(Uri imgUri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProjectToolkit.fadeIn(progressBar);
                button.setEnabled(false);
            }
        });

        // Retrieve the caption once and store it in a local variable
        String caption = editCaption.getText() != null ? editCaption.getText().toString().trim() : "";

        // Validate the caption and image input
        if (imgUri == null && caption.isEmpty()) {
            Toast.makeText(CreatePost.this, "Please add a caption or an image", Toast.LENGTH_SHORT).show();
            return;
        }

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(CreatePost.this, "Please wait while the post is uploaded", Toast.LENGTH_SHORT).show();
//            }
//        });

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreatePost.this);
        String uid = preferences.getString("auth_userId", null);
        String authName = preferences.getString("auth_name", null);

        String fileName = "post" + System.currentTimeMillis() + ".jpg";
        StorageReference imgRef = storageReference.child(fileName);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String uploadTime = currentDateTime.format(dateTimeFormatter);

        String postId = databaseReference.push().getKey();

        if (imgUri != null) {
            // Check if image is from a specific URL
            if (imgUri.toString().startsWith("https://rubyproducti9n.github.io")) {
                String imageUrl = imgUri.toString();
                Map<String, Boolean> likes = new HashMap<>();
                Post post = new Post(postId, uid, authName, imageUrl, caption, uploadTime, anonymous, likes);
                databaseReference.child(postId).setValue(post);
            } else {
                UploadTask uploadTask = imgRef.putFile(ProjectToolkit.compress(this, imgUri, 25));
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
                                databaseReference.child(postId).setValue(post);
                                Toast.makeText(CreatePost.this, "Posted!", Toast.LENGTH_SHORT).show();
                                setDefault();
                                reset();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreatePost.this, "Error while retrieving image", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CreatePost.this, "Error while uploading image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        } else {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setProgressBar(null);
                            Toast.makeText(CreatePost.this, "Posted!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Map<String, Boolean> likes = new HashMap<>();
                    Post post = new Post(postId, uid, authName, null, caption, uploadTime, anonymous, likes);
                    databaseReference.child(postId).setValue(post);
                    setDefault();
                    reset();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreatePost.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void upload(Uri imgUri){
        ExecutorService e = Executors.newSingleThreadExecutor();
        ProjectToolkit.fadeIn(progressBar);
        button.setEnabled(false);
        if (imgUri == null && Objects.requireNonNull(editCaption.getText()).toString().isEmpty()) {
            Toast.makeText(CreatePost.this, "Please add a caption or an image", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(CreatePost.this, "Please wait while the post is uploaded", Toast.LENGTH_SHORT).show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");


        String anonymous = "public";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreatePost.this);
        String authName = preferences.getString("auth_name", null);
        String caption = editCaption.getText().toString();

        //Creating a unique file name
        String fileName = "post" + System.currentTimeMillis() + ".jpg";
        //Creating a reference to the file location in Firebase Storage
        StorageReference imgRef = storageReference.child(fileName);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String uploadTime = currentDateTime.format(dateTimeFormatter);

        String postId = databaseReference.push().getKey();
        if (rawSelectedImageUri != null){
            if (rawSelectedImageUri.toString().startsWith("https://rubyproducti9n.github.io")){
                authName = preferences.getString("auth_name", null);
                String authDiv = preferences.getString("auth_division", null);
                String imageUrl = imgUri.toString();
                Map<String, Boolean> likes = new HashMap<>();
                Post post = new Post(postId, authName, authDiv, imageUrl, caption, uploadTime, anonymous, likes);
                databaseReference.child(postId).setValue(post);
            }else{
                UploadTask uploadTask = imgRef.putFile(ProjectToolkit.compress(this, imgUri, 25));
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        int progress = progressBar.getProgress();
                        if (progress < 90) {
                            progressBar.setProgress(progress + 1, true);
                            handler.postDelayed(this, 10);
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
//                progressBar.setProgress(80, true);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                String authName = preferences.getString("auth_name", null);
                                String authDiv = preferences.getString("auth_division", null);
                                String imageUrl = downloadUri.toString();
                                Map<String, Boolean> likes = new HashMap<>();
                                e.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Post post = new Post(postId, authName, authDiv, imageUrl, caption, uploadTime, anonymous, likes);
                                        databaseReference.child(postId).setValue(post);
                                    }
                                });
                                e.shutdown();
                                setProgressBar(null);
                                Toast.makeText(CreatePost.this, "Posted!", Toast.LENGTH_SHORT).show();
                                setDefault();
                                reset();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreatePost.this, "Error while retrieving image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreatePost.this, "Error while uploading image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProjectToolkit.fadeIn(progressBar);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setProgressBar(null);
                            Toast.makeText(CreatePost.this, "Posted!", Toast.LENGTH_SHORT).show();
                        }
                    }, 1500);
                    String authName = preferences.getString("auth_name", null);
                    String authDiv = preferences.getString("auth_division", null);
                    String imageUrl = null;
                    Map<String, Boolean> likes = new HashMap<>();
                    Post post = new Post(postId, authName, authDiv, imageUrl, caption, uploadTime, anonymous, likes);
                    databaseReference.child(postId).setValue(post);
                    //postCount = newPostCount;
                    setDefault();
                    reset();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreatePost.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void reset(){
        progressBar.setProgress(0, true);
        ProjectToolkit.fadeOut(progressBar);
    }

    private void Notification(String title, String msg) {
        Context context = null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "post_upload_channel")
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle(title)
                .setContentText(msg)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setTimeoutAfter(1000);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(CreatePost.this);
        if (ActivityCompat.checkSelfPermission(CreatePost.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(0, builder.build());
    }

    private void customDialog(String title, String msg, String negBtnTitle){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CreatePost.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton(negBtnTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void checkEnableUpload(){
        MaterialCardView alert = findViewById(R.id.alert);
        alert.setVisibility(View.GONE);

        FirebaseDatabase database = (FirebaseDatabase) FirebaseDatabase.getInstance();
        DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);

                if(uploadsValue == true){
                    ProjectToolkit.fadeOut(alert);
                    //alert.setVisibility(View.GONE);
                    button.setEnabled(false);
                    uploadEnableStatus = true;
                    if (!editCaption.toString().isEmpty()){
                        intent = getIntent();
                        String sharedImage;
                        if (intent != null){
                            String sharedText = intent.getStringExtra("sharedText");
                            sharedImage = intent.getStringExtra("sharedImage");
                            if(sharedText!=null){
                                editCaption.setText(sharedText);
                            }
                            if (sharedImage!=null){
                                Uri imageUri = Uri.parse(sharedImage);
                                if (sharedImage.startsWith("https://rubyproducti9n.github.io")){
                                    Picasso.get().load(sharedImage).into(img);
                                }else{
                                    img.setImageURI(imageUri);
                                }
                                rawSelectedImageUri = imageUri;

                            }
                            button.setEnabled(true);
                            ProjectToolkit.setButtonEnabledAnim(button);
                            button.setText("POST");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ExecutorService e = Executors.newSingleThreadExecutor();
                                    e.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            uploadImage(rawSelectedImageUri);
                                        }
                                    });
                                    setDefault();
                                }
                            });
                        } else {
                            sharedImage = null;
                        }
                    }else{
                        editCaption.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String captionCheck = charSequence.toString();
                                button.setEnabled(!captionCheck.isEmpty());
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if (editCaption.getText().toString().isEmpty()) {
                                    button.setEnabled(false);
                                    ProjectToolkit.setButtonDisabledAnim(button);
                                }else{
                                    button.setEnabled(true);
                                    ProjectToolkit.setButtonEnabledAnim(button);
                                    button.setText("POST");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ExecutorService e = Executors.newSingleThreadExecutor();
                                            e.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    uploadImage(rawSelectedImageUri);
                                                }
                                            });
                                            setDefault();
                                        }
                                    });

                                }
                            }
                        });
                    }
                }else{
                    button.setEnabled(false);
                    uploadEnableStatus = false;
                    ProjectToolkit.fadeIn(alert);
                    //alert.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreatePost.this, "Error 503", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefault(){
        button.setText("POST");
        img.setImageResource(R.drawable.round_photo_library_24);
        editCaption.setText("");
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//                if (ContextCompat.checkSelfPermission(CreatePost.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            1);
//                } else {
//                    openGallery();
//                }
//            }
//        });
    }


//    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {
//        private Context context;
//        private WeakReference<Context> contextReference;
//        private WeakReference<LinearProgressIndicator> progressBarReference;
//        private WeakReference<MaterialButton> buttonReference;
//        private WeakReference<EditText> editCaptionReference;
//        private WeakReference<Uri> rawSelectedImageUri;
//
//        MyAsyncTask(Context context, LinearProgressIndicator progressBar, MaterialButton button, EditText editCaptionReference, Uri uri) {
//            this.contextReference = new WeakReference<>(context);
//            this.progressBarReference = new WeakReference<>(progressBar);
//            this.buttonReference = new WeakReference<>(button);
//            this.editCaptionReference = new WeakReference<>(editCaptionReference);
//            this.rawSelectedImageUri = new WeakReference<>(uri);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Thread.sleep(12000);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            publishProgress();
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//
//        }
//
//        @Override
//        protected void onPostExecute(Void unused) {
//            Context context = contextReference.get();
//            LinearProgressIndicator progressBar = progressBarReference.get();
//            MaterialButton button = buttonReference.get();
//            EditText editCaption = editCaptionReference.get();
//
//            progressBar.setVisibility(View.VISIBLE);
//            button.setEnabled(false);
//            //Get a reference to the Firebase Storage
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts/");
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
//            DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");
//
//            String visibility = "private";
//
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//            String userId = preferences.getString("auth_userId", null);
//            String authName = preferences.getString("auth_name", null);
//            String caption = editCaption.getText().toString();
//
//            //Creating a unique file name
//            String fileName = "post" + System.currentTimeMillis() + ".jpg";
//
//            //Creating a reference to the file location in Firebase Storage
//            StorageReference imgRef = storageReference.child(fileName);
//
//            LocalDateTime currentDateTime = LocalDateTime.now();
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String uploadTime = currentDateTime.format(dateTimeFormatter);
//
//            String postId = databaseReference.push().getKey();
//
//            if (rawSelectedImageUri != null) {
//                if (rawSelectedImageUri.toString().startsWith("https://rubyproducti9n.github.io")) {
//                    authName = preferences.getString("auth_name", null);
//                    String authDiv = preferences.getString("auth_division", null);
//                    String imageUrl = imgUri.toString();
//                    Map<String, Boolean> likes = new HashMap<>();
//                    Post post = new Post(postId, authName, authDiv, imageUrl, caption, uploadTime, visibility, likes);
//                    databaseReference.child(postId).setValue(post);
//                } else {
//                    //Upload the file to the firebase storage
//                    UploadTask uploadTask = imgRef.putFile(ProjectToolkit.compress(this, imgUri, 25));
//
//
//                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            //Image successfully uploaded
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(context, "Posted!", Toast.LENGTH_SHORT).show();
//                            //customDialog("Success!", "Your post has been uploaded and will be displayed after a review", "Ok");
//                            //Notification("Success!", "Your post uploaded successfully");
//                            //Can retrieve the download URL of the uploaded image for further use
//                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri downloadUri) {
//                                    //Retrieved uploaded image and can be used
//                                    String authName = preferences.getString("auth_name", "User not found");
//                                    String authDiv = preferences.getString("auth_division", null);
//                                    String imageUrl = downloadUri.toString();
//                                    Map<String, Boolean> likes = new HashMap<>();
//                                    Post post = new Post(postId, authName, authDiv, imageUrl, caption, uploadTime, visibility, likes);
//                                    databaseReference.child(postId).setValue(post);
//                                    //postCount = newPostCount;
//                                    setDefault();
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    //Handle error while displaying the uploaded image
//                                    Toast.makeText(context, "Error while retrieving image", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            //Handle error while uploading
//                            Toast.makeText(context, "Error while uploading image", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            } else {
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(context, "Posted!", Toast.LENGTH_SHORT).show();
//                        //Notification("Success!", "Your post uploaded successfully");
//                        //Retrieved uploaded image and can be used
//                        String authName = preferences.getString("auth_name", null);
//                        String authDiv = preferences.getString("auth_division", null);
//                        String imageUrl = null;
//                        Map<String, Boolean> likes = new HashMap<>();
//                        Post post = new Post(postId, authName, authDiv, imageUrl, caption, uploadTime, visibility, likes);
//                        databaseReference.child(postId).setValue(post);
//                        //postCount = newPostCount;
//                        CreatePost.setDefault();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            super.onPostExecute(unused);
//        }
//    }
//
//    private void startAsyncTask() {
//        LinearProgressIndicator progressBar = findViewById(R.id.progressBar); // replace with your actual ProgressBar ID
//        MaterialButton button = findViewById(R.id.button); // replace with your actual Button ID
//        new MyAsyncTask(this, progressBar, button).execute();
//    }

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