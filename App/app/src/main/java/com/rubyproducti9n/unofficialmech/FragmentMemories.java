package com.rubyproducti9n.unofficialmech;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMemories#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMemories extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ActivityResultLauncher<Intent> startActivityForResult;
    private Uri imgUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_REQUEST_CODE = 100;
    //    String[] permission = {"android.permission.READ_EXTERNAL.STORAGE"};
    LinearProgressIndicator progressBar;
    MaterialButton upload;
    ImageView img;

    public FragmentMemories() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMemories.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMemories newInstance(String param1, String param2) {
        FragmentMemories fragment = new FragmentMemories();
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
        View view = inflater.inflate(R.layout.fragment_memories, container, false);

        FirebaseDatabase database = (FirebaseDatabase) FirebaseDatabase.getInstance();
        DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);

                if(uploadsValue == true){
                    upload.setEnabled(true);
                }else{
                    upload.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TextView instructions = view.findViewById(R.id.instructions);
        instructions.setText("1. You can upload one photo at a time \n" +
                "2. Your memory will be displayed after a review"  );

        progressBar = view.findViewById(R.id.progrssBar);
        progressBar.setVisibility(View.GONE);
//        img = view.findViewById(R.id.img);
        upload = view.findViewById(R.id.upload);
        upload.setEnabled(true);


        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        //Bitmap image = null;
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            uploadImage(selectedImageUri);
//                            Bundle extras = data.getExtras();
//                            if (extras != null ){
//                                image = (Bitmap) extras.get("image");
//
//                                FaceDetector faceDetector = new FaceDetector.Builder(getContext()).build();
//
//                                Frame frame = new Frame.Builder().setBitmap(image).build();
//                                List<Face> faces = (List<Face>) faceDetector.detect(frame);
//                                if (faces.size() > 3){
//                                    Toast.makeText(getContext(), "More than 3 faces found", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(getContext(), "You need to upload a group photo", Toast.LENGTH_SHORT).show();
//                                }
//                            }


                        }
                    }
                }
        );

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } else {
                    openGallery();
                }
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult.launch(intent);
    }

    private void showPermissionDenied() {
        MaterialAlertDialogBuilder materialAlert = new MaterialAlertDialogBuilder(getContext());
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

    private void uploadImage(Uri imgUri) {

        //Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("memories/");

        //Creating a unique file name
        String fileName = "memory" + System.currentTimeMillis() + ".jpg";

             //Creating a reference to the file location in Firebase Storage
        StorageReference imgRef = storageReference.child(fileName);

        //Upload the file to the firebase storage
        UploadTask uploadTask = imgRef.putFile(imgUri);
        progressBar.setVisibility(View.VISIBLE);
        upload.setEnabled(false);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Image successfully uploaded
                upload.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                customDialog("Success!", "Your memory has been uploaded and will be displayed after review", "Ok");
                Notification("Success!", "Your memory has been uploaded successfully");
                //Can retrieve the download URL of the uploaded image for further use
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        //Retrieved uploaded image and can be used
                        String imageUrl = downloadUri.toString();
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

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 100) {
//            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                assert data != null;
//                Uri selectedImgUri = data.getData();
//                uploadImage(selectedImgUri);
//                //Permiassion Granted
//                //openGallery();
//
//            } else {
//                //Permission still denied, show dialog and handling according
//                showPermissionDenied();
//            }
//        }
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
            } else {
                //Permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //User denied permission without "Never ask again" selected, show rationale and request again
                    showPermissionDenied();
                } else {
                    //User denied permission with "Never ask again" selected, show rationale and request again
                    showPermissionDenied();
                }
            }
        }

    }

    private static File compressAndUploadImage(Uri imageUri) {


        return null;
    }

//    private String getRealPathFromURI(Uri uri) {
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor == null) {
//            return null;
//        }
//        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String filePath = cursor.getString(columnIndex);
//        cursor.close();
//        return filePath;
//    }


    private void Notification(String title, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "default")
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle(title)
                .setContentText(msg)
                .setSound(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.notification_sound))
                .setPriority(NotificationCompat.PRIORITY_MAX);

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
}