package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.Algorithms.isFaculty;
import static com.rubyproducti9n.unofficialmech.Algorithms.selectYear;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.animateVertically;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.initiatePanicMode;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.pref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuickMenuActivity extends AppCompatActivity {

    MaterialButton fab;
    MaterialCardView timeTable, panicBtn;
    MaterialCheckBox checkBox;

    String[] timeTABLE = {"https://drive.google.com/file/d/19ykn51CB5uJeiuAvDEPOVyZmoNwXouk0/view?usp=sharing",
            "https://drive.google.com/file/d/1K7Xg0H10juIKu9BOs5_CypK7A_UzQCyu/view?usp=sharing",
            "https://drive.google.com/file/d/1uN1W3gw1h-AnEG0HtdN6x8PXlw9lwQMq/view?usp=sharing"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_menu);

        startAsyncTask();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        String div = preferences.getString("auth_division", null);
        String userRole = preferences.getString("auth_userole", null);
        boolean quickVal = preferences.getBoolean("dashboard", false);

        checkBox = findViewById(R.id.quick_check);
        checkBox.setChecked(quickVal);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox.setChecked(isChecked);
                editor.putBoolean("dashboard", isChecked);
                editor.apply();
                if (!isChecked){
                    Toast.makeText(QuickMenuActivity.this, "You can turn it on anytime from settings", Toast.LENGTH_SHORT).show();
                }
            }
        });


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuickMenuActivity.this, MainActivity.class));
//                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
            }
        });

        TextView txt1 = findViewById(R.id.txt1);
        timeTable = findViewById(R.id.timeTable);
        timeTable.setEnabled(true);
        if (userRole!=null){
            if (isFaculty(QuickMenuActivity.this)){
                txt1.setText("Attendance Tracker");
                timeTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectYear(QuickMenuActivity.this);
                    }
                });
            }else{
                txt1.setText("VIRTUAL ID");
                timeTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (div!=null){
                            LayoutInflater inflater = LayoutInflater.from(QuickMenuActivity.this);
                            View view = inflater.inflate(R.layout.activity_virtal_idcard, null);

                            if (pref(QuickMenuActivity.this).getString("auth_userId", null) !=null){
                                String uid = pref(QuickMenuActivity.this).getString("auth_userId", null);
                                String displayName = pref(QuickMenuActivity.this).getString("auth_name", null);
                                String email = pref(QuickMenuActivity.this).getString("auth_email", null);
                                String prn = pref(QuickMenuActivity.this).getString("auth_prn", null);
                                String roll = pref(QuickMenuActivity.this).getString("auth_roll", null);
                                String div = pref(QuickMenuActivity.this).getString("auth_division", null);
                                String gender = pref(QuickMenuActivity.this).getString("auth_gender", null);
                                String role = pref(QuickMenuActivity.this).getString("auth_userole", null);
                                String mNum = pref(QuickMenuActivity.this).getString("auth_mob", null);

                                String setDetails = "Name: " + displayName + "\n"
                                        + "Branch: " + "B.TECH (MECH)" + "\n" +
                                        "PRN: " + prn + "\n" +
                                        "Mob: " + mNum + "\n" +
                                        "Email: " + email + "\n";

                                TextView details = view.findViewById(R.id.details);
                                details.setText(setDetails);
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
                                    ImageView qr = view.findViewById(R.id.qr_code_image);
                                    qr.setImageBitmap(bitmap);

                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }

                            }

                            MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(QuickMenuActivity.this)
                            .setView(view);
                            b.show();
                        }else{
                            Toast.makeText(QuickMenuActivity.this, "Login to create Virtual ID", Toast.LENGTH_SHORT).show();
                            Log.d("User error", "User was not Logged In");
                        }
                    }
                });
            }
        }else{
            Toast.makeText(QuickMenuActivity.this, "You were logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(QuickMenuActivity.this, PasswordActivity.class));
        }


        panicBtn = findViewById(R.id.panic_button);
        panicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePanicMode(QuickMenuActivity.this);
//                activatePanicMode();
            }
        });


        TransitionManager.beginDelayedTransition((ViewGroup) timeTable.getParent(), new AutoTransition());
//        timeTable.setVisibility(timeTable.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//        panicBtn.setVisibility(panicBtn.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);


        ProjectToolkit.disableStatusBar(this);

        ImageView bgImg1 = findViewById(R.id.bgImg1);
        ImageView bgImg2 = findViewById(R.id.bgImg2);
        animateVertically(this, bgImg2, -250, 2000);
        animateVertically(this, bgImg1, 250, 3000);
    }



    private void activatePanicMode(){
        final String[] items = {"Ambulance", "Fire Brigade", "Security Officer", "Anti-ragging", "Chief Warden"};

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(QuickMenuActivity.this);
        builder.setTitle("Panic mode")
//        builder.setMessage("In case of emergency situations, call the college ambulance and know them your location. \n \n" +
//                "Note: Activating Panic mode will share your details to the authority.");
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                dial("8975900900");
                                shareDetails("Ambulance");
                                break;
                            case 1:
                                dial("8411002749");
                                shareDetails("Fire Brigade");
                                break;
                            case 2:
                                dial("9869201190");
                                shareDetails("Security Officer");
                                break;
                            case 3:
                                dial("9552021276");
                                shareDetails("Antiragging");
                                break;
                            case 4:
                                dial("9923607460");
                                shareDetails("Chief Warden");
                                break;
                        }
                    }
                })
                .setPositiveButton("Learn more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MaterialAlertDialogBuilder learnMore = new MaterialAlertDialogBuilder(QuickMenuActivity.this);
                        learnMore.setTitle("Learn more")
                                .setMessage("In case of any emergency you can directly call emergency services \n\n Note: Activating Panic mode will share your details to the authority.")
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void dial(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
    private void shareDetails(String service){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(QuickMenuActivity.this);
        String currentUser = preferences.getString("auth_name", null);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String serviceTime = currentDateTime.format(dateTimeFormatter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("panic");
        String panicId = databaseReference.push().getKey();
        assert panicId != null;
        String data = currentUser + " used " + service + " service " + " at " + serviceTime;
        databaseReference.child(panicId).setValue(data);

    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Void>{
        private Context context;
        MyAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Thread.sleep(12000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Toast.makeText(context, "Toasted!", Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }
    }
    private void startAsyncTask() {
        new MyAsyncTask(this).execute();
    }

}