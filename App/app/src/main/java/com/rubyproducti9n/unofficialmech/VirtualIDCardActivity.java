package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.pref;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class VirtualIDCardActivity extends AppCompatActivity {

    private ImageView qrCodeImageView, barcodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtal_idcard);

        qrCodeImageView = findViewById(R.id.qr_code_image);
        getUser();
        generate("123");
    }

    private void getUser(){
        if (pref(VirtualIDCardActivity.this).getString("auth_userId", null) !=null){
            String uid = pref(VirtualIDCardActivity.this).getString("auth_userId", null);
            String displayName = pref(VirtualIDCardActivity.this).getString("auth_name", null);
            String email = pref(VirtualIDCardActivity.this).getString("auth_personalEmail", null);
            String prn = pref(VirtualIDCardActivity.this).getString("auth_prn", null);
            String roll = pref(VirtualIDCardActivity.this).getString("auth_roll", null);
            String div = pref(VirtualIDCardActivity.this).getString("auth_division", null);
            String gender = pref(VirtualIDCardActivity.this).getString("auth_gender", null);
            String role = pref(VirtualIDCardActivity.this).getString("auth_userole", null);
            String mNum = pref(VirtualIDCardActivity.this).getString("auth_mNum", null);

            String setDetails = "Name: " + displayName
                    + "Branch: " + "B.TECH (MECH)" + "\n" +
                    "PRN: " + prn + "\n\n" +
                    "• Contact" + "\n" +
                    "Mob.: " + mNum + "\n" +
                    "Email: " + email + "\n";

            TextView details = findViewById(R.id.details);
            details.setText(setDetails);
            generate(prn);

        }
    }

    private void generate(String prn){
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(prn, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrCodeImageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}