package com.rubyproducti9n.unofficialmech;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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

public class CreateInternshipNotification extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startActivityForResult;
    LinearProgressIndicator progressBar;
    ImageView img;
    MaterialButton button, addImageBtn, enrollBtn, contactUsBtn;
    TextInputEditText editCaption;
    int postCount;
    private String selectedImageUriString;
    Uri rawSelectedImageUri;
    boolean uploadEnableStatus;

    String enrollText = null;

    String contactUsText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_internship_notification);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        enrollBtn = findViewById(R.id.enroll);
        contactUsBtn = findViewById(R.id.contact_us);
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

        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEditText("Enroll Link", 0);
            }
        });

        contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEditText("Contact No.", 1);
            }
        });

        ScrollView scrollView = findViewById(R.id.scroll_view);
        ProjectToolkit.fadeOut(scrollView);
        //scrollView.setVisibility(View.GONE);
        ImageView profilePicture = findViewById(R.id.avatar);
        TextView username = findViewById(R.id.username);
        TextView div = findViewById(R.id.div);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreateInternshipNotification.this);
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

        checkEnableUpload();


    }


    private void showPermissionDenied() {
        MaterialAlertDialogBuilder materialAlert = new MaterialAlertDialogBuilder(CreateInternshipNotification.this);
        materialAlert.setTitle("Warning");
        materialAlert.setMessage("This app requires media permission to function properly, Please grant the permission from the app settings.");
        materialAlert.setPositiveButton("App settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
                Toast.makeText(CreateInternshipNotification.this, "Enable all permissions to enjoy uninterepted services", Toast.LENGTH_SHORT).show();
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

    private String createEditText(String title, int feature){

        final EditText[] editText = {new EditText(this)};
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText[0].setLayoutParams(layoutParams);

        //Creating a LinearLayout to wrap the EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText[0]);

        //feature = 0 --> enroll
        //feature = 1 --> contact

        if (feature==1){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String contactNo = preferences.getString("auth_contact", null);
            String mail = preferences.getString("auth_email", null);
            if (contactNo!=null){
                editText[0].setText(contactNo);
                contactUsText = contactNo;
                editText[0].setEnabled(false);
            }else{
                editText[0].setText("");
                //TODO: updateContactNo(); when the value is added
            }
        }else if (feature==0){
            if (enrollText!=null){
                editText[0].setText(enrollText);
            }else{
                editText[0].setText("");
            }
        }

        final String[] getString = new String[1];
        //Building dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(title)
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getString[0] = editText[0].getText().toString();
                        if (getString[0].startsWith("https://")){
                            enrollText = getString[0];
                        }else{
                            updateContactNo(getString[0]);
                            contactUsText = getString[0];
                        }
                        //Adding logic ...
                        //TODO: Add a global variable which will store the value and put it in the Data Structure InternshipNotificationItem
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();

        return getString[0];
    }

    private void uploadImage(Uri imgUri) {
        Toast.makeText(CreateInternshipNotification.this, "Please wait while the post is uploaded", Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);


        if (imgUri == null && Objects.requireNonNull(editCaption.getText()).toString().isEmpty()) {
            Toast.makeText(CreateInternshipNotification.this, "Please add a internship description or an image", Toast.LENGTH_SHORT).show();
            return;
        }

        //Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("internships/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("internships");
        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");

        String visibility = "private";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreateInternshipNotification.this);
        String userId = preferences.getString("auth_userId", null);
        String authName = preferences.getString("auth_name", null);
        String contactNo = preferences.getString("auth_contact", null);
        String caption = editCaption.getText().toString();

        //Creating a unique file name
        String fileName = "intern-" + System.currentTimeMillis() + ".jpg";

        //Creating a reference to the file location in Firebase Storage
        StorageReference imgRef = storageReference.child(fileName);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String uploadTime = currentDateTime.format(dateTimeFormatter);

        String internshipId = databaseReference.push().getKey();

        if (rawSelectedImageUri != null){
            //Upload the file to the firebase storage
            UploadTask uploadTask = imgRef.putFile(ProjectToolkit.compress(this, imgUri, 25));


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Image successfully uploaded
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateInternshipNotification.this, "Posted!", Toast.LENGTH_SHORT).show();
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
                            InternshipNotificationItem internNotification = new InternshipNotificationItem(internshipId, authName, imageUrl, caption, null);
                            databaseReference.child(internshipId).setValue(internNotification);
                            //postCount = newPostCount;
                            setDefault();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Handle error while displaying the uploaded image
                            Toast.makeText(CreateInternshipNotification.this, "Error while retrieving image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Handle error while uploading
                    Toast.makeText(CreateInternshipNotification.this, "Error while uploading image", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateInternshipNotification.this, "Posted!", Toast.LENGTH_SHORT).show();
                    //Notification("Success!", "Your post uploaded successfully");
                    //Retrieved uploaded image and can be used
                    String authName = preferences.getString("auth_name", null);
                    String authDiv = preferences.getString("auth_division", null);
                    String imageUrl = null;
                    Map<String, Boolean> likes = new HashMap<>();
                    InternshipNotificationItem internNotification = new InternshipNotificationItem(internshipId, authName, imageUrl, caption, null);
                    databaseReference.child(internshipId).setValue(internNotification);
                    //postCount = newPostCount;
                    setDefault();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CreateInternshipNotification.this, "An error occurred", Toast.LENGTH_SHORT).show();
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

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(CreateInternshipNotification.this);
        if (ActivityCompat.checkSelfPermission(CreateInternshipNotification.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CreateInternshipNotification.this);
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
                                if (enrollText!=null && contactUsText!=null){
                                    button.setEnabled(true);
                                    ProjectToolkit.setButtonEnabledAnim(button);
                                    button.setText("Create");
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            uploadImage(rawSelectedImageUri);
                                            setDefault();
                                        }
                                    });
                                }else{
                                    Toast.makeText(CreateInternshipNotification.this, "Please fill all the input fields", Toast.LENGTH_SHORT).show();
                                }

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
                Toast.makeText(CreateInternshipNotification.this, "Error 503", Toast.LENGTH_SHORT).show();
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


    private void updateContactNo(String contactNo){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String id = preferences.getString("auth_userId", null);
        String mail = preferences.getString("auth_email", null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/");
        reference.orderByChild("personalEmail").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        reference.child(id).child("contact").setValue(contactNo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateInternshipNotification.this, "Unable to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }


}