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
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateProject extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startActivityForResult;
    LinearProgressIndicator progressBar;
    ImageView img;
    MaterialButton button, addImageBtn;
    TextInputEditText editCaption, editResources;
    int postCount;
    private String selectedImageUriString;
    Uri rawSelectedImageUri;
    boolean uploadEnableStatus;
    Chip patentChip, resourceChip;
    boolean impNotice, resourceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.progressBar);
        ProjectToolkit.fadeIn(progressBar);
        //progressBar.setVisibility(View.VISIBLE);
        img = findViewById(R.id.img);
        button = findViewById(R.id.button);
        addImageBtn = findViewById(R.id.add_image);
        editCaption = findViewById(R.id.caption);

        Intent intent = getIntent();
        if (intent != null){
            String sharedText = intent.getStringExtra("sharedText");
            String sharedImage = intent.getStringExtra("sharedImage");
            if(sharedText!=null){
                editCaption.setText(sharedText);
            }
            if (sharedImage!=null){
                Uri imageUri = Uri.parse(sharedImage);
                img.setImageURI(imageUri);
                rawSelectedImageUri = imageUri;

            }
        }

        ScrollView scrollView = findViewById(R.id.scroll_view);
        ProjectToolkit.fadeOut(scrollView);
        //scrollView.setVisibility(View.GONE);
        ImageView profilePicture = findViewById(R.id.avatar);
        TextView username = findViewById(R.id.username);
        TextView div = findViewById(R.id.div);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreateProject.this);
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


        editResources = findViewById(R.id.resources);
        resourceChip = findViewById(R.id.resourceChip);
        resourceChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                resourceChip.setChecked(b);
                if (b){
                    resourceChip.setText("Remove resources");
                    resourceChip.setChipIcon(getResources().getDrawable(R.drawable.round_block_24));
                    resourceEditText = true;
                    ProjectToolkit.fadeIn(editResources);
//                    resourceEditTextCard.setVisibility(View.VISIBLE);
//                    resourceEditTextCard.startAnimation(fadeInAnim);
                }else{
                    resourceChip.setText("Add resources");
                    resourceChip.setChipIcon(getResources().getDrawable(R.drawable.baseline_add_24));
                    resourceEditText = false;
                    ProjectToolkit.fadeOut(editResources);
//                    impNoticeCard.setVisibility(View.GONE);
//                    impNoticeCard.startAnimation(fadeOutAnim);
                }
            }
        });


        ImageView patentImg = findViewById(R.id.patentImg);
        patentChip = findViewById(R.id.patent);
        patentChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                patentChip.setChecked(b);
                if (b){
                    impNotice = true;
                    ProjectToolkit.fadeIn(patentImg);
//                    impNoticeCard.setVisibility(View.VISIBLE);
//                    impNoticeCard.startAnimation(fadeInAnim);
                }else{
                    impNotice = false;
                    ProjectToolkit.fadeOut(patentImg);
//                    impNoticeCard.setVisibility(View.GONE);
//                    impNoticeCard.startAnimation(fadeOutAnim);
                }
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

        checkEnableUpload();


    }


    private void showPermissionDenied() {
        MaterialAlertDialogBuilder materialAlert = new MaterialAlertDialogBuilder(CreateProject.this);
        materialAlert.setTitle("Warning");
        materialAlert.setMessage("This app requires media permission to function properly, Please grant the permission from the app settings.");
        materialAlert.setPositiveButton("App settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
                Toast.makeText(CreateProject.this, "Enable all permissions to enjoy uninterepted services", Toast.LENGTH_SHORT).show();
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

    private void uploadImage(Uri imgUri) {

        //TODO: Foreground upload test code

//        //Get a reference to the Firebase Storage
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
//        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");
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
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String authName = preferences.getString("auth_name", null);
//        String caption = editCaption.getText().toString();
//
//        String visibility = "private";
//
//        Intent serviceIntent = new Intent(CreateProject.this, UploadService.class);
//        serviceIntent.putExtra("auth_name", authName);
//        serviceIntent.putExtra("file_name", fileName);
//        serviceIntent.putExtra("image_reference", (CharSequence) imgRef);
//        serviceIntent.putExtra("upload_time", uploadTime);
//        serviceIntent.putExtra("post_id", postId);
//        serviceIntent.putExtra("caption", caption);
//        serviceIntent.putExtra("visibility", visibility);
//        serviceIntent.putExtra("rawSelectedImageUri", rawSelectedImageUri);
//
//        serviceIntent.putExtra("progressBar", String.valueOf(progressBar));
//        serviceIntent.putExtra("button", (CharSequence) button);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            ContextCompat.startForegroundService(CreateProject.this, serviceIntent);
//        }


        Toast.makeText(CreateProject.this, "Please wait while the post is uploaded", Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);

        if (imgUri == null && Objects.requireNonNull(editCaption.getText()).toString().isEmpty()) {
            Toast.makeText(CreateProject.this, "Please add a project description or an image", Toast.LENGTH_SHORT).show();
            return;
        }

        //Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("projects/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("projects");
        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");

        String visibility = "private";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreateProject.this);
        String userId = preferences.getString("auth_userId", null);
        String authName = preferences.getString("auth_name", null);
        String caption = editCaption.getText().toString();

        //Creating a unique file name
        String fileName = "proj-" + System.currentTimeMillis() + ".jpg";

        //Creating a reference to the file location in Firebase Storage
        StorageReference imgRef = storageReference.child(fileName);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String uploadTime = currentDateTime.format(dateTimeFormatter);

        String projectId = databaseReference.push().getKey();

        if (rawSelectedImageUri != null){
            //Upload the file to the firebase storage
            UploadTask uploadTask = imgRef.putFile(ProjectToolkit.compress(this, imgUri, 25));


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Image successfully uploaded
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateProject.this, "Posted!", Toast.LENGTH_SHORT).show();
                    //customDialog("Success!", "Your post has been uploaded and will be displayed after a review", "Ok");
                    //Notification("Success!", "Your post uploaded successfully");
                    //Can retrieve the download URL of the uploaded image for further use
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            //Retrieved uploaded image and can be used
                            String authName = preferences.getString("auth_name", "User not found");
                            String authDiv = preferences.getString("auth_division", null);
                            String imageUrl = downloadUri.toString();
                            Map<String, Boolean> likes = new HashMap<>();
                            ProjectItem post = new ProjectItem(projectId, authName, authDiv, imageUrl, caption, uploadTime, false, "No resources were provided. Please contact user for more details");
                            databaseReference.child(projectId).setValue(post);
                            //postCount = newPostCount;
                            setDefault();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Handle error while displaying the uploaded image
                            Toast.makeText(CreateProject.this, "Error while retrieving image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Handle error while uploading
                    Toast.makeText(CreateProject.this, "Error while uploading image", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateProject.this, "Posted!", Toast.LENGTH_SHORT).show();
                    //Notification("Success!", "Your post uploaded successfully");
                    //Retrieved uploaded image and can be used
                    String authName = preferences.getString("auth_name", null);
                    String authDiv = preferences.getString("auth_division", null);
                    String imageUrl = null;
                    Map<String, Boolean> likes = new HashMap<>();
                    ProjectItem post = new ProjectItem(projectId, authName, authDiv, imageUrl, caption, uploadTime, false, "Alphabet, AWS, etc.");
                    databaseReference.child(projectId).setValue(post);
                    //postCount = newPostCount;
                    setDefault();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CreateProject.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }




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

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(CreateProject.this);
        if (ActivityCompat.checkSelfPermission(CreateProject.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CreateProject.this);
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
                                        uploadImage(rawSelectedImageUri);
                                        setDefault();
                                    }
                                });

                            }
                        }
                    });
                }else{
                    button.setEnabled(false);
                    uploadEnableStatus = false;
                    ProjectToolkit.fadeIn(alert);
                    //alert.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateProject.this, "Error 503", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefault(){
        progressBar.setVisibility(View.GONE);
        button.setText("POST");
        img.setImageResource(R.drawable.round_photo_library_24);
        editCaption.setText("");
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//                if (ContextCompat.checkSelfPermission(CreateProject.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            1);
//                } else {
//                    openGallery();
//                }
//            }
//        });
    }

}