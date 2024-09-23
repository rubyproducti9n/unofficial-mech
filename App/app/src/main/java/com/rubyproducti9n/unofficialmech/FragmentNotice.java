package com.rubyproducti9n.unofficialmech;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
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

import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentNotice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNotice extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ActivityResultLauncher<Intent> startActivityForResult;
    LinearProgressIndicator progressBar;
    MaterialButton upload, addImageBtn;
    MaterialSwitch impSwitch;
    boolean impNotice;
    ImageView img;
    TextInputEditText captionEditTxt, linkEditText;
    String link;
    int noticeCount;
    TextView fileInfo;
    Uri selectedImageUrl;
    boolean uploadEnableStatus;

    public FragmentNotice() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentNotice.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNotice newInstance(String param1, String param2) {
        FragmentNotice fragment = new FragmentNotice();
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
        View view = inflater.inflate(R.layout.fragment_notice, container, false);



        fileInfo = view.findViewById(R.id.fileInfo);
        fileInfo.setText(".jpg or .png files only");

        ImageView pdfPreview = view.findViewById(R.id.pdfPreview);

        progressBar = view.findViewById(R.id.progrssBar);
        progressBar.setVisibility(View.GONE);
        img = view.findViewById(R.id.imgView);
        addImageBtn = view.findViewById(R.id.add_image);
        upload = view.findViewById(R.id.upload);
        upload.setEnabled(false);
        captionEditTxt = view.findViewById(R.id.caption);
        linkEditText = view.findViewById(R.id.link);
        impSwitch = view.findViewById(R.id.impNotice_switch);
        impNotice = false;
        impSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                impNotice = true;
            }
        });

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUrl = data.getData();
//                            img.setImageURI(selectedImageUrl);
                            if (selectedImageUrl != null){
                                if (isPdf(selectedImageUrl)){
                                    img.setVisibility(View.GONE);
                                    Bitmap pdfThumbnail = generateThumbnail(selectedImageUrl);
                                    int pageCount = getPageCount(selectedImageUrl);
                                    String fileName = getFileName(selectedImageUrl);
                                    long fileSizeInBytes = getFileSize(selectedImageUrl);
                                    double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
                                    String sizeString = formatSize(fileSizeInBytes);
                                    String fileType = "PDF";
                                    if (fileSizeInMB <= 5.0){
                                        pdfPreview.setVisibility(View.GONE);
                                        img.setImageBitmap(pdfThumbnail);
                                        fileInfo.setText(fileName + "\n" + pageCount + " pages" + " • " + sizeString + " • " + fileType);
                                    }else {
                                        Toast.makeText(getContext(), "PDF should be less than 5MB", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    //If selected file is not a PDF
                                    img.setVisibility(View.VISIBLE);
                                    img.setImageURI(selectedImageUrl);
                                }
                            }
                        }else {
                            selectedImageUrl = null;
                        }
                    }
                }
        );

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        captionEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkUploadEnabled();
                if (uploadEnableStatus = false){
                    upload.setEnabled(false);
                }else{
                    String toString = captionEditTxt.getText().toString();
                    if (toString.isEmpty()){
                        upload.setEnabled(false);
                    }else{
                        upload.setEnabled(true);
                        upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadImage(selectedImageUrl);
                            }
                        });
                    }
                }


            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*"); //TODO: Image only
//        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "application/pdf"});
        startActivityForResult.launch(intent);
    }

    private void uploadImage(Uri imgUri) {
        String fileName;

        int status = 0;
        Toast.makeText(getContext(), "Please wait while the notice is uploaded", Toast.LENGTH_SHORT).show();

        String linkToString = linkEditText.getText().toString().trim();
        if (linkToString != null){
            link = linkEditText.getText().toString().trim();
        }else{
            link = null;
        }

        //Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notice");
        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String authName = preferences.getString("auth_name", null);
        String caption = captionEditTxt.getText().toString();

        StorageReference imgRef;



        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String uploadTime = currentDate.format(dateTimeFormatter);

        String noticeId = databaseReference.push().getKey();

        if (selectedImageUrl != null){

            if (isPdf(imgUri)){
                fileName = getFileName(imgUri);
                //Creating a reference to the file location in Firebase Storage
                imgRef = storageReference.child("notice/pdfs/" + fileName);
            }else{
                //Creating a unique file name
                fileName = "notice" + System.currentTimeMillis() + ".jpg";
                //Creating a reference to the file location in Firebase Storage
                imgRef = storageReference.child("notice/img/" + fileName);
            }

            //Upload the file to the firebase storage
            UploadTask uploadTask = imgRef.putFile(imgUri);
            progressBar.setVisibility(View.VISIBLE);
            upload.setEnabled(false);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Image successfully uploaded
                    addImageBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    customDialog("Success!", "Your notice has been published and will be displayed shortly", "Ok");
                    //Notification("Success!", "Notice published successfully");
                    //Can retrieve the download URL of the uploaded image for further use
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            //Retrieved uploaded image and can be used
                            String authName = preferences.getString("auth_name", null);
                            String div = preferences.getString("auth_division", null);
                            String imageUrl = downloadUri.toString();
                            Notice notice = new Notice(noticeId, authName, div, imageUrl, caption, link, uploadTime, impNotice);
                            databaseReference.child(noticeId).setValue(notice);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Handle error while displaying the uploaded image
                            Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Handle error while uploading
                    Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Retrieved uploaded image and can be used
                    String authName = preferences.getString("auth_name", null);
                    String div = preferences.getString("auth_division", null);
                    String imageUrl = null;
                    Notice notice = new Notice(noticeId, authName, div, imageUrl, caption, link, uploadTime, impNotice);
                    databaseReference.child(noticeId).setValue(notice);
                    contextCheck();
                    if (status == 1){
                        Toast.makeText(getContext(), "Published!", Toast.LENGTH_SHORT).show();
                        //Notification("Success!", "Notice published successfully");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
    private void Notification(String title, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "notice_upload_channel")
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_MIN);

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
    private boolean isPdf(Uri uri){
        String type = getContext().getContentResolver().getType(uri);
        return type != null && type.startsWith("application/pdf");
    }
    private Bitmap generateThumbnail(Uri uri){
        try {
            ParcelFileDescriptor pdf = getContext().getContentResolver().openFileDescriptor(uri, "r");
            if (pdf != null){
                PdfRenderer renderer = new PdfRenderer(pdf);
                PdfRenderer.Page page = renderer.openPage(0);

                int width = 200;
                int height = 100;

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                page.close();
                renderer.close();
                pdf.close();

                return bitmap;

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private int getPageCount(Uri uri){
        try {
            ParcelFileDescriptor pdf = getContext().getContentResolver().openFileDescriptor(uri, "r");
            if (pdf != null) {

                PdfRenderer renderer = new PdfRenderer(pdf);
                int pageCount = renderer.getPageCount();
                renderer.close();
                pdf.close();
                return pageCount;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private long getFileSize(Uri uri){
        try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null)){
            if (cursor != null && cursor.moveToFirst()){
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (sizeIndex != -1){
                    return cursor.getLong(sizeIndex);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    private String formatSize(long size){
        String sizeString = "";
        if(size < 1024){
            sizeString = size + " B";
        }else if (size < (1024 * 1024)){
            sizeString = String.format(Locale.getDefault(), "%.1f KB", size / 1024.0);
        }else if (size < (1024 * 1024 * 1024)){
            sizeString = String.format(Locale.getDefault(), "%.1f MB", size / (1024.0 * 1024.0));
        }
        return sizeString;
    }
    private String getFileName(Uri uri){
        String fileName = null;
        try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null)){
            if (cursor != null && cursor.moveToFirst()){
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (sizeIndex != -1){
                    fileName = cursor.getString(nameIndex);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return fileName;
    }
    private void checkUploadEnabled(){
        FirebaseDatabase database = (FirebaseDatabase) FirebaseDatabase.getInstance();
        DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);

                if(uploadsValue == true){
                    upload.setEnabled(true);
                    uploadEnableStatus = true;
                }else{
                    upload.setEnabled(false);
                    uploadEnableStatus = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private int contextCheck(){
        Context context = getContext();
        int status = 0;
        if (context !=null){
            status = 1;
        }
        return status;
    }

    class Notice{

        private String noticeId;
        private String authName;
        private String div;
        private String imgUrl;
        private String caption;
        private String link;
        private String uploadTime;
        private boolean impNotice;

        public Notice(String noticeId, String authName, String div, String imgUrl, String caption, String link, String uploadTime, boolean impNotice){
            this.noticeId = noticeId;
            this.authName = authName;
            this.div = div;
            this.imgUrl = imgUrl;
            this.caption = caption;
            this.link = link;
            this.uploadTime = uploadTime;
            this.impNotice = impNotice;
        }

        public String getNoticeId() {
            return noticeId;
        }

        public void setNoticeId(String noticeId) {
            this.noticeId = noticeId;
        }

        public String getAuthName() {
            return authName;
        }

        public void setAuthName(String authName) {
            this.authName = authName;
        }

        public String getAuthDiv() {
            return div;
        }

        public void setAuthDiv(String div) {
            this.div = div;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(String uploadTime) {
            this.uploadTime = uploadTime;
        }

        public boolean isImpNotice() {
            return impNotice;
        }

        public void setImpNotice(boolean impNotice) {
            this.impNotice = impNotice;
        }
    }
            }