package com.rubyproducti9n.unofficialmech;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCreatePost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCreatePost extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ActivityResultLauncher<Intent> startActivityForResult;
    LinearProgressIndicator progressBar;
    ImageView img;
    MaterialButton button, addImageBtn;
    TextInputEditText editCaption;
    int postCount;
    private String selectedImageUriString;
    Uri rawSelectedImageUri;
    boolean uploadEnableStatus;

    public FragmentCreatePost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCreatePost.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCreatePost newInstance(String param1, String param2) {
        FragmentCreatePost fragment = new FragmentCreatePost();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);



        progressBar = view.findViewById(R.id.progressBar);
        img = view.findViewById(R.id.img);
        button = view.findViewById(R.id.button);
        addImageBtn = view.findViewById(R.id.add_image);
        editCaption = view.findViewById(R.id.caption);

        button.setEnabled(false);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        setDefault();

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

        // Inflate the layout for this fragment
        return view;
    }

    private void showPermissionDenied() {
        MaterialAlertDialogBuilder materialAlert = new MaterialAlertDialogBuilder(getActivity());
        materialAlert.setTitle("Warning");
        materialAlert.setMessage("This app requires media permission to function properly, Please grant the permission from the app settings.");
        materialAlert.setPositiveButton("App settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
                Toast.makeText(getContext(), "Enable all permissions to enjoy uninterepted services", Toast.LENGTH_SHORT).show();
            }
        });
        materialAlert.setCancelable(false);
        materialAlert.show();
    }
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
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
//        Intent serviceIntent = new Intent(getContext(), UploadService.class);
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
//            ContextCompat.startForegroundService(getContext(), serviceIntent);
//        }





        Toast.makeText(getContext(), "Please wait while the post is uploaded", Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        //Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts/");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");

//        String visibility = "private";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String authName = preferences.getString("auth_name", "User not found");
        String uid = preferences.getString("auth_userId", null);
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
            //Upload the file to the firebase storage
            UploadTask uploadTask = imgRef.putFile(imgUri);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Image successfully uploaded
                    button.setEnabled(true);
                    button.setText("Select");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Posted!", Toast.LENGTH_SHORT).show();
                    //customDialog("Success!", "Your post has been uploaded and will be displayed after a review", "Ok");
                    //Notification("Success!", "Your post uploaded successfully");
                    //Can retrieve the download URL of the uploaded image for further use
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            //Retrieved uploaded image and can be used
                            String authName = preferences.getString("auth_name", "User not found");
                            String imageUrl = downloadUri.toString();
                            Map<String, Boolean> likes = new HashMap<>();
                            Post post = new Post(postId, uid, authName, imageUrl, caption, uploadTime, "public", likes);
                            databaseReference.child(postId).setValue(post);
                            //postCount = newPostCount;
                            setDefault();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Handle error while displaying the uploaded image
                            Toast.makeText(getContext(), "Error while retrieving image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Handle error while uploading
                    Toast.makeText(getContext(), "Error while uploading image", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Posted!", Toast.LENGTH_SHORT).show();
                    //Notification("Success!", "Your post uploaded successfully");
                    //Retrieved uploaded image and can be used
                    String authName = preferences.getString("auth_name", null);
                    String imageUrl = null;
                    Map<String, Boolean> likes = new HashMap<>();
                    Post post = new Post(postId, uid, authName, imageUrl, caption, uploadTime, "public", likes);
                    databaseReference.child(postId).setValue(post);
                    //postCount = newPostCount;
                    setDefault();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
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
                .setSound(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.notification_sound))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setTimeoutAfter(1000);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
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
        FirebaseDatabase database = (FirebaseDatabase) FirebaseDatabase.getInstance();
        DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);

                if(uploadsValue == true){
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
                            }else{
                                button.setEnabled(true);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefault(){
        progressBar.setVisibility(View.GONE);
        button.setText("Post");
        img.setImageResource(R.drawable.round_photo_library_24);
        editCaption.setText("");
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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