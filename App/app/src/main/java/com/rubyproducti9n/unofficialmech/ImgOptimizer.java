package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.inputmethod.InputMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImgOptimizer {

    public static Uri compressAndConvertToUri(Context context, Uri inputImageUri, int quality){
        try{
            InputStream inputStream = context.getContentResolver().openInputStream(inputImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap compressedBitmap = compressBitmap(bitmap, quality);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray,Base64.DEFAULT);
            return Uri.parse("data:image/jpeg;base64," + encodedImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap compressBitmap(Bitmap originalBitmap, int quality){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static boolean compressAndSaveImageFromUri(Context context, Uri uri, String outputFileName, int quality){
        try{
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap compressedBitmap = compressBitmap(bitmap, quality);

//            File outputFile = new File(context.getExternalFilesDir(null), "com.rubyproducti9n.unofficialmech/compressed" + outputFileName);
//            FileOutputStream outputStream = new FileOutputStream(outputFile);
//            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            outputStream.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
