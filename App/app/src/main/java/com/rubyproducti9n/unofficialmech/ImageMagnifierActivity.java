package com.rubyproducti9n.unofficialmech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.mbms.DownloadRequest;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class ImageMagnifierActivity extends AppCompatActivity {
    private Matrix matrix = new Matrix();
    private float scaleFactor = 1.0f;
    ImageView img;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_magnifier);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.matte_black));

        Intent intent = getIntent();
        String url = intent.getStringExtra("link");
        String activity = intent.getStringExtra("activity");

        FloatingActionButton fab = findViewById(R.id.downloadBtn);

        if (activity!=null && activity.equals("notice")){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String location = getFileNameFromUrl(url);
                    openPdf(ImageMagnifierActivity.this, location, "123.jpg");
                    //picassoDownloader(url);
                }
            });
        }else{
            fab.setVisibility(View.GONE);
        }

        img = findViewById(R.id.img);
        if (url!=null){
            Picasso.get().load(url).into(img);
        }else{
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            img.setScaleX(scaleFactor);
            img.setScaleY(scaleFactor);
            return true;
        }
    }

    public static String getFileNameFromUrl(String pdfUrl){
        String[] segments = pdfUrl.split("/");

        int oIndex = -1;
        for(int i = 0; i < segments.length; i++){
            if ("o".equals(segments[i])){
                oIndex = 1;
                break;
            }
        }
        //If "o" is found, getting the filename from the next segment
        if(oIndex!=-1 && oIndex+1<segments.length){
            return segments[oIndex + 1];
        }else{
            //If "o" is not found, use the last segment
            String lastSegment = segments[segments.length - 1];
            int questionMarkIndex = lastSegment.indexOf("?");
            if (questionMarkIndex!=-1){
                return lastSegment.substring(0, questionMarkIndex);
            }else{
                return lastSegment;
            }
        }

    }

    public static void openPdf(Context context, String pdfUrl, String fileName){
        File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        if(pdfFile.exists()){
            openPdfFile(context, pdfFile);
        }else{
            downloadPdf(context, pdfUrl, fileName);
        }
    }

    private static void downloadPdf(final Context context, String pdfUrl, final String fileName){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(pdfUrl);
        File localFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "Unofficial Mech", fileName);

        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            openPdfFile(context, localFile);
        }).addOnFailureListener(exception->{
            Toast.makeText(context, "Failed to download PDF", Toast.LENGTH_SHORT).show();
        });
    }

    public static void openPdfFile(Context context, File pdfFile){
        try{
            Uri pdfUri = Uri.parse(String.valueOf(pdfFile));
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(pdfUri, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            context.startActivity(pdfIntent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void picassoDownloader(String url){
        Context context = getApplicationContext();
        String folderPath = Environment.getExternalStorageDirectory().getPath() + "Unofficial Mech/Notices/";
        
        File folder = new File(folderPath);
        if(!folder.exists()){
            if (Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).mkdirs()) {
                Toast.makeText(context, "Directory created successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show();
            }
            //For below versions of Android 10
            if (folder.mkdirs()){
                Toast.makeText(context, "Directory created!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Um...", Toast.LENGTH_SHORT).show();
            }
        }

        String filename = "notice-" + System.currentTimeMillis() + ".jpg";
        File file = new File(folder, filename);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try{
                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Toast.makeText(context, "Notice saved!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.get()
                .load(url)
                .into(target);
    }

    private void getFile(String url){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String gotUrl = uri.toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageMagnifierActivity.this, "Failed to load media", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void download(String url){
        DownloadManager downloadManager = (DownloadManager) getSystemService(this.DOWNLOAD_SERVICE);
//        DownloadRequest downloadRequest = downloadManager.(Uri.parse(url));
    }

}