package com.rubyproducti9n.unofficialmech;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.animation.ObjectAnimator;
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
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
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
import com.google.android.material.chip.ChipGroup;
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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class CreateNotice extends BaseActivity {
    private ActivityResultLauncher<Intent> startActivityForResult;
    LinearProgressIndicator progressBar;
    MaterialButton upload, addImageBtn;
    MaterialSwitch impSwitch;
    Chip impChip;
    boolean impNotice;
    ImageView img;
    TextInputEditText captionEditTxt, linkEditText;
    String link;
    int noticeCount;
    TextView fileInfo;
    Uri selectedImageUrl;
    boolean uploadEnableStatus;
    ObjectAnimator alphaAnimator;
    private Chip fyChip;
    private Chip syChip;
    private Chip tyChip;
    private Chip finalYearChip;
    private int setYear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fyChip = findViewById(R.id.fy);
        syChip = findViewById(R.id.sy);
        tyChip = findViewById(R.id.ty);
        finalYearChip = findViewById(R.id.finalYear);

        Chip allChip = findViewById(R.id.all);
        allChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allChip.isChecked()) {
                    fyChip.setChecked(true);
                    syChip.setChecked(true);
                    tyChip.setChecked(true);
                    finalYearChip.setChecked(true);
                } else {
                    fyChip.setChecked(false);
                    syChip.setChecked(false);
                    tyChip.setChecked(false);
                    finalYearChip.setChecked(false);
                }
            }
        });
        // Setting up OnCheckedChangeListener for other chips
        fyChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allChip.setChecked(false);
                    setYear = 1;
                }
            }
        });
        syChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allChip.setChecked(false);
                    setYear = 2;
                }
            }
        });
        tyChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allChip.setChecked(false);
                    setYear = 3;
                }
            }
        });
        finalYearChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allChip.setChecked(false);
                    setYear = 4;
                }
            }
        });

        ChipGroup chipGroup = findViewById(R.id.year_chips);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {

                    String selectedChipText = chip.getText().toString();

                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        Chip otherChip = (Chip) chipGroup.getChildAt(i);
                        if (otherChip != chip && otherChip.isChecked()) {
                            otherChip.setChecked(false);
                        }
                    }
                    if (selectedChipText.equals("All")) {
                        // Disable other chips if "All" is selected
                        setYear = -1;
                        fyChip.setEnabled(false);
                        syChip.setEnabled(false);
                        tyChip.setEnabled(false);
                        finalYearChip.setEnabled(false);
                    } else {
                        // Enable other chips if a chip other than "All" is selected
                        fyChip.setEnabled(true);
                        syChip.setEnabled(true);
                        tyChip.setEnabled(true);
                        finalYearChip.setEnabled(true);
                    }
                    Toast.makeText(CreateNotice.this, "Selected: " + selectedChipText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlphaAnimation fadeInAnim = new AlphaAnimation(0, 1);
        fadeInAnim.setDuration(100);
        AlphaAnimation fadeOutAnim = new AlphaAnimation(1, 0);
        fadeOutAnim.setDuration(100);

        fileInfo = findViewById(R.id.fileInfo);
        fileInfo.setText(".jpg or .png files only");

        ImageView pdfPreview = findViewById(R.id.pdfPreview);

        progressBar = findViewById(R.id.progressBar);
        ProjectToolkit.fadeIn(progressBar);
        //progressBar.setVisibility(View.GONE);
        img = findViewById(R.id.img);
        addImageBtn = findViewById(R.id.add_image);
        upload = findViewById(R.id.button);
        upload.setEnabled(false);
        captionEditTxt = findViewById(R.id.caption);
        linkEditText = findViewById(R.id.link);
        ImageView impNoticeCard = findViewById(R.id.imp_notice);
        impSwitch = findViewById(R.id.impNotice_switch);
        impSwitch.setVisibility(View.GONE);
        impChip = findViewById(R.id.important);
        impNotice = false;
        impNoticeCard.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null){
            String sharedText = intent.getStringExtra("sharedText");
            String sharedImage = intent.getStringExtra("sharedImage");
            if(sharedText!=null){
                captionEditTxt.setText(sharedText);
            }
            if (sharedImage!=null){
                Uri imageUri = Uri.parse(sharedImage);
                img.setImageURI(imageUri);
                selectedImageUrl = imageUri;
            }
        }

        impSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                impSwitch.setChecked(b);
                if (b){
                    impNotice = true;
                    ProjectToolkit.fadeIn(impNoticeCard);
//                    impNoticeCard.setVisibility(View.VISIBLE);
//                    impNoticeCard.startAnimation(fadeInAnim);
                }else{
                    impNotice = false;
                    ProjectToolkit.fadeOut(impNoticeCard);
//                    impNoticeCard.setVisibility(View.GONE);
//                    impNoticeCard.startAnimation(fadeOutAnim);
                }
            }
        });
        impChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                impSwitch.setChecked(b);
                if (b){
                    impNotice = true;
                    ProjectToolkit.fadeIn(impNoticeCard);
//                    impNoticeCard.setVisibility(View.VISIBLE);
//                    impNoticeCard.startAnimation(fadeInAnim);
                }else{
                    impNotice = false;
                    ProjectToolkit.fadeOut(impNoticeCard);
//                    impNoticeCard.setVisibility(View.GONE);
//                    impNoticeCard.startAnimation(fadeOutAnim);
                }
            }
        });

        NestedScrollView scrollView = findViewById(R.id.scroll_view);
        ProjectToolkit.fadeOut(scrollView);
        //scrollView.setVisibility(View.GONE);
        ImageView profilePicture = findViewById(R.id.avatar);
        TextView username = findViewById(R.id.username);
        TextView div = findViewById(R.id.div);
        ImageView verifiedBadge = findViewById(R.id.verifiedBadge);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreateNotice.this);
        String userId = pref.getString("auth_userId", null);
        String initials = pref.getString("auth_initials", null);

            ProjectToolkit.getInitials(this, new ProjectToolkit.InitialCallback() {
                @Override
                public void onInitialsStatus(String gender) {
                    if (gender.equals("Male")){
                        Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profilePicture);
                        verifiedBadge.setImageResource(R.drawable.faculty_verified);
                    }else{
                        Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profilePicture);
                        verifiedBadge.setImageResource(R.drawable.faculty_verified);
                    }
                }
            });

            Callbacks.getUserRole(this, new Callbacks.UserRoleCallback() {
                @Override
                public void onUserRole(int role) {
                    if (role == 1){
                        div.setText("Admin");
                    } else if (role == 2) {
                        div.setText("Faculty");
                    } else if (role == 3) {
                        Log.d("Role info", "Role: Student");
                    }else{
                        div.setText(" ");
                    }
                }
            });

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

                        if (initials!=null){
                            username.setText(initials + " " + lastName);
                            div.setText("Faculty");
                        }else{
                            username.setText(firstName + " " + lastName);
                            div.setText("From " + division + " division");
                        }
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
                                        Toast.makeText(CreateNotice.this, "PDF should be less than 5MB", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    //If selected file is not a PDF
                                    ProjectToolkit.fadeIn(img);
                                    //img.setVisibility(View.VISIBLE);
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

        checkEnableUpload();

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
        Toast.makeText(CreateNotice.this, "Please wait while the notice is uploaded", Toast.LENGTH_SHORT).show();

        String linkToString = linkEditText.getText().toString().trim();
        if (linkToString != null){
            link = linkEditText.getText().toString().trim();
        }else{
            link = null;
        }


        if (imgUri == null && Objects.requireNonNull(captionEditTxt.getText()).toString().isEmpty()) {
            Toast.makeText(CreateNotice.this, "Please add a caption or an image", Toast.LENGTH_SHORT).show();
            return;
        }

        //Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notice");
        DatabaseReference countReference = FirebaseDatabase.getInstance().getReference("counts");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreateNotice.this);
        String authName = preferences.getString("auth_name", null);
        String caption = captionEditTxt.getText().toString();

        StorageReference imgRef;



        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String uploadTime = currentDate.format(dateTimeFormatter);

        String noticeId = databaseReference.push().getKey();

        if (selectedImageUrl != null){
            UploadTask uploadTask;
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/pdf")
                    .setCustomMetadata("author", "my name")
                    .build();
            if (isPdf(imgUri)){
                fileName = getFileName(imgUri);
                //Creating a reference to the file location in Firebase Storage
                imgRef = storageReference.child("notice/pdfs/" + fileName);
                uploadTask = imgRef.putFile(imgUri);
            }else{
                //Creating a unique file name
                fileName = "notice" + System.currentTimeMillis() + ".jpg";
                //Creating a reference to the file location in Firebase Storage
                imgRef = storageReference.child("notice/img/" + fileName);
                uploadTask = imgRef.putFile(ProjectToolkit.compress(this, imgUri, 50), metadata);
            }


            //Upload the file to the firebase storage
            ProjectToolkit.fadeIn(progressBar);
            ProjectToolkit.setButtonDisabledAnim(upload);
            //progressBar.setVisibility(View.VISIBLE);
            //upload.setEnabled(false);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Image successfully uploaded
                    addImageBtn.setEnabled(true);
                    ProjectToolkit.fadeOut(progressBar);
                    //progressBar.setVisibility(View.GONE);
                    customDialog("Success!", "Your notice has been published and will be displayed shortly", "Ok");
                    //Notification("Success!", "Notice published successfully");
                    //Can retrieve the download URL of the uploaded image for further use
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            //Retrieved uploaded image and can be used
                            String authName = preferences.getString("auth_name", null);
                            String[] fullName = authName.split("\\s+");
                            String firstName = fullName[0];
                            String lastName = fullName[1];
                            String authInitials = preferences.getString("auth_initials", null);
                            String div = preferences.getString("auth_division", null);
                            String imageUrl = downloadUri.toString();
                            if (authInitials!=null){
                                authName = authInitials.replaceAll(".", "_") + lastName;
                            }
                            Notice notice = new Notice(noticeId, authName, div, imageUrl, caption, link, uploadTime, impNotice, setYear);
                            databaseReference.child(noticeId).setValue(notice);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Handle error while displaying the uploaded image
                            Toast.makeText(CreateNotice.this, "Error 503", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Handle error while uploading
                    Toast.makeText(CreateNotice.this, "Error 503", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Retrieved uploaded image and can be used
                    String authName = preferences.getString("auth_name", null);
                    String authInitials = preferences.getString("auth_initials", null);
                    String[] fullName = authName.split("\\s+");
                    String firstName = fullName[0];
                    String lastName = fullName[1];
                    String div = preferences.getString("auth_division", null);
                    String imageUrl = null;
                    if (authInitials!=null){
                        authName = authInitials.replaceAll(".", "_") + lastName;

                    }
                    Notice notice = new Notice(noticeId, authName, div, imageUrl, caption, link, uploadTime, impNotice, setYear);
                    databaseReference.child(noticeId).setValue(notice);
                    contextCheck();
                    if (status == 1){
                        Toast.makeText(CreateNotice.this, "Published!", Toast.LENGTH_SHORT).show();
                        //Notification("Success!", "Notice published successfully");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CreateNotice.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
    private void Notification(String title, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(CreateNotice.this, "notice_upload_channel")
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_MIN);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(CreateNotice.this);
        if (ActivityCompat.checkSelfPermission(CreateNotice.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CreateNotice.this);
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
        String type = CreateNotice.this.getContentResolver().getType(uri);
        return type != null && type.startsWith("application/pdf");
    }
    private Bitmap generateThumbnail(Uri uri){
        try {
            ParcelFileDescriptor pdf = CreateNotice.this.getContentResolver().openFileDescriptor(uri, "r");
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
            ParcelFileDescriptor pdf = CreateNotice.this.getContentResolver().openFileDescriptor(uri, "r");
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
        try (Cursor cursor = CreateNotice.this.getContentResolver().query(uri, null, null, null)){
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
        try (Cursor cursor = getContentResolver().query(uri, null, null, null)){
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
    public void checkEnableUpload(){
        MaterialCardView alert = findViewById(R.id.alert);
        ProjectToolkit.fadeOut(alert);
        //alert.setVisibility(View.GONE);

        FirebaseDatabase database = (FirebaseDatabase) FirebaseDatabase.getInstance();
        DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);

                if(uploadsValue){
                    ProjectToolkit.fadeOut(alert);
//                    alert.setVisibility(View.GONE);
                    upload.setEnabled(false);
                    uploadEnableStatus = true;
                    captionEditTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String captionCheck = charSequence.toString();
                            upload.setEnabled(!captionCheck.isEmpty());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (captionEditTxt.getText().toString().isEmpty()) {
                                upload.setEnabled(false);
                                ProjectToolkit.setButtonDisabledAnim(upload);
//                                upload.startAnimation(disableAnim);
                            }else{
                                upload.setEnabled(true);
                                ProjectToolkit.setButtonEnabledAnim(upload);
//                                upload.startAnimation(enableAnim);
                                upload.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uploadImage(selectedImageUrl);
                                        //setDefault();
                                    }
                                });

                            }
                        }
                    });
                }else{
                    alert.setVisibility(View.VISIBLE);
                    ProjectToolkit.setButtonDisabledAnim(upload);
                    //upload.setEnabled(false);
                    uploadEnableStatus = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateNotice.this, "Error 503", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int contextCheck(){
        Context context = CreateNotice.this;
        int status = 0;
        if (context !=null){
            status = 1;
        }
        return status;
    }

    static class Notice{

        private String noticeId;
        private String authName;
        private String div;
        private String imgUrl;
        private String caption;
        private String link;
        private String uploadTime;
        private boolean impNotice;
        private int year;

        public Notice(String noticeId, String authName, String div, String imgUrl, String caption, String link, String uploadTime, boolean impNotice, int year){
            this.noticeId = noticeId;
            this.authName = authName;
            this.div = div;
            this.imgUrl = imgUrl;
            this.caption = caption;
            this.link = link;
            this.uploadTime = uploadTime;
            this.impNotice = impNotice;
            this.year = year;
        }

        public String getDiv() {
            return div;
        }

        public void setDiv(String div) {
            this.div = div;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
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