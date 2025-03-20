package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFCreator {


    static class TextPosition {
        int x;
        int y;
        String text;

        TextPosition(String text, int x, int y) {
            this.x = x;
            this.y = y;
            this.text = text;
        }
    }
    // Function to create a PDF file with given text positions and content
    public static void createPDF(Context context, String filename, TextPosition[] textPositions) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File letterFolder = new File(directory, "Letter");

        // Check if "Letter" folder exists, if not create it
        if (!letterFolder.exists()) {
            if (!letterFolder.mkdirs()) {
                System.err.println("Failed to create directory: " + letterFolder.getAbsolutePath());
                return;
            }
        }

        File textFile = new File(letterFolder, filename);

        try (FileOutputStream outputStream = new FileOutputStream(textFile)) {
            StringBuilder content = new StringBuilder();

            for (TextPosition position : textPositions) {
                content.append("[")
                        .append(position.x)
                        .append(":")
                        .append(position.y)
                        .append("] ")
                        .append(position.text)
                        .append("\n");
            }

            outputStream.write(content.toString().getBytes());
            System.out.println("Text file created successfully at: " + textFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
