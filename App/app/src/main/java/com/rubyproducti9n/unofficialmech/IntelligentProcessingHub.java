package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
//import com.google.mlkit.vision.barcode.common.Barcode;
//import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
//import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
//import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.text.Text;
//import com.google.mlkit.vision.text.TextRecognition;
//import com.google.mlkit.vision.text.TextRecognizer;
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class IntelligentProcessingHub extends BaseActivity {

    public interface OnTextRecognizedListener{
        void OnTextRecognized(String text);
    }

//    public static void textRecognization(Context context, Uri uri, OnTextRecognizedListener callback){
//        TextRecognizer recognizer =
//                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//        InputImage image = null;
//        try {
//            image = InputImage.fromFilePath(context, uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("=========Image Error========", "Unable to create Image Object!");
//        }
//
//        if (image!=null){
//            Task<Text> result =
//                recognizer.process(image)
//                        .addOnSuccessListener(new OnSuccessListener<Text>() {
//                            @Override
//                            public void onSuccess(Text visionText) {
//                                String extractedText = visionText.getText();
//                                if (callback!=null){
//                                    callback.OnTextRecognized(extractedText);
//                                }
//                            }
//                        })
//                        .addOnFailureListener(
//                                new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        callback.OnTextRecognized(null);
//                                    }
//                                });
//
//            String resultText = result.getResult().getText();
//            for (Text.TextBlock block : result.getResult().getTextBlocks()) {
//                String blockText = block.getText();
//                Point[] blockCornerPoints = block.getCornerPoints();
//                Rect blockFrame = block.getBoundingBox();
//                for (Text.Line line : block.getLines()) {
//                    String lineText = line.getText();
//                    Point[] lineCornerPoints = line.getCornerPoints();
//                    Rect lineFrame = line.getBoundingBox();
//                    for (Text.Element element : line.getElements()) {
//                        String elementText = element.getText();
//                        Point[] elementCornerPoints = element.getCornerPoints();
//                        Rect elementFrame = element.getBoundingBox();
//                        for (Text.Symbol symbol : element.getSymbols()) {
//                            String symbolText = symbol.getText();
//                            Point[] symbolCornerPoints = symbol.getCornerPoints();
//                            Rect symbolFrame = symbol.getBoundingBox();
//                        }
//                    }
//                }
//            }
//        }else{
//            Toast.makeText(context, "No image was found!", Toast.LENGTH_SHORT).show();
//        }
//
//
//
//    }
//
//    public static void googleBarcodeScanner(Context context){
//        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
//                .setBarcodeFormats(
//                        Barcode.FORMAT_QR_CODE,
//                        Barcode.FORMAT_AZTEC)
//                .enableAutoZoom() // available on 16.1.0 and higher
//                .build();
//        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(context, options);
//        scanner.startScan()
//                .addOnSuccessListener(
//                        barcode -> {
//                            // Task completed successfully
//                             String rawValue = barcode.getRawValue();
//                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
//                            builder.setTitle("Title")
//                                    .setMessage(rawValue)
//                                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // Open the link
//                                            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                            builder.show();
//                        })
//                .addOnCanceledListener(
//                        () -> {
//                            // Task canceled
//                            Toast.makeText(context, "Request was cancelled", Toast.LENGTH_SHORT).show();
//                        })
//                .addOnFailureListener(
//                        e -> {
//                            if (Objects.requireNonNull(e.getMessage()).contains("PERMISSION_DENIED")) {
//                            Toast.makeText(context, "Camera permission is required to scan barcodes", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Handle other exceptions
//                                // Task failed with an exception
//                                Toast.makeText(context, "Request failed", Toast.LENGTH_SHORT).show();
//                        }
//                        });
//    }
//
//    //Google AI Studio (Gemini Algorithms)
//    public void initializeGemini(String prompt, TextView txt){
//        GenerativeModel gm = new GenerativeModel("gemini-pro", "AIzaSyDwswACZZRz2015E4yTNmcDn-8GlXyBovk");
//        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//
//        com.google.ai.client.generativeai.type.Content content = new com.google.ai.client.generativeai.type.Content.Builder()
//                .addText(prompt)
//                .build();
//
//        Executor executor = Executors.newFixedThreadPool(2);
//
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//            @Override
//            public void onSuccess(com.google.ai.client.generativeai.type.GenerateContentResponse result) {
//                String resultText = result.getText();
//                runOnUiThread(() -> txt.setText(resultText));
//                System.out.println(resultText);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                t.printStackTrace();
//            }
//        }, executor);
//
//    }

}
