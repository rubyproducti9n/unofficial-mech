package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.PDFCreator.createPDF;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.disableStatusBar;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.initiateAds;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadAd;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubyproducti9n.smartmech.AlgorithmEngine;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BottomSheetCreateAccount extends AppCompatActivity {
    private ConstraintLayout adContainer;
    private MaterialAutoCompleteTextView divDrop, deptDrop;
    private DatabaseReference databaseReference;
    public static ImageView profilePic;
    MaterialButton facultyAcc, createAccBtn;
    TextInputEditText fName, lName, inPrn, inClgMail, inPEmail, inMobNum, inPassword;
    String inDivision, inDept, inGender = null;
    String inRRoll = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_create_account);
        checkTemp();
//        disableStatusBar(BottomSheetCreateAccount.this);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        profilePic = findViewById(R.id.profilePicture);

        fName = findViewById(R.id.firstNameEditText);
        lName = findViewById(R.id.lastNameEditText);
//        inDiv = findViewById(R.id.divisionEditText);
//        inDept = findViewById(R.id.deptMenu);
        inPrn = findViewById(R.id.prnEditText);
        inClgMail = findViewById(R.id.clgEmailEditText);
        inPEmail = findViewById(R.id.personalEmailEditText);
//        inGender = findViewById(R.id.gender_radio_group);
        inMobNum = findViewById(R.id.mobileNumEditText);
        inPassword = findViewById(R.id.passwordEditText);

//        rollDrop = findViewById(R.id.dropdown_menu);
        divDrop = findViewById(R.id.dropdown_menu2);
        deptDrop = findViewById(R.id.deptMenu);

        facultyAcc = findViewById(R.id.facultyBtn);
        createAccBtn = findViewById(R.id.cretaeAccountBtn);

        String[] items = getResources().getStringArray(R.array.div);
        ArrayAdapter<String> divAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        divDrop.setAdapter(divAdapter);
        divDrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String d = (String) parent.getItemAtPosition(position);
                inDivision = d;
            }
        });

        String[] deptItems = getResources().getStringArray(R.array.dept);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, deptItems);
        deptDrop.setAdapter(deptAdapter);
        deptDrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dept = (String) parent.getItemAtPosition(position);
                inDept = dept;
            }
        });

        RadioGroup radioGroup = findViewById(R.id.gender_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                String selectedOption = selectedRadioButton.getText().toString();
                validGender(selectedOption);
                inGender = selectedOption;
            }
        });



        facultyAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BottomSheetCreateAccount.this, LockActivity.class));
            }
        });

        TextInputEditText rollInput = findViewById(R.id.rollEditText);
        rollInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inRRoll = "0";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isInt = isInt(Objects.requireNonNull(rollInput.getText()).toString());
                if (isInt){
                    inRRoll = rollInput.getText().toString();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createAccBtn.setOnClickListener(view -> checkInput(Objects.requireNonNull(fName.getText()).toString(),
                Objects.requireNonNull(lName.getText()).toString(),
                inDivision, String.valueOf(inRRoll),
                Objects.requireNonNull(inPrn.getText()).toString(),
                Objects.requireNonNull(inClgMail.getText()).toString(),
                inGender, Objects.requireNonNull(inPEmail.getText()).toString(),
                Objects.requireNonNull(inMobNum.getText()).toString(),
                ProjectToolkit.h(BottomSheetCreateAccount.this, Objects.requireNonNull(inPassword.getText()).toString()),
                "Student",
                inDept));

    }

    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;

        }
    }

    private void createAccount(String firstName, String lastName, String div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role, String dept){
        tempUser(null, firstName, lastName, div, roll, prn, oEmail, gen, pEmail, mNum, pass, role,dept);
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreatedAccount = currentDate.format(dateTimeFormatter);

        String lastPaymentDate = currentDate.format(dateTimeFormatter);

        String userId = null;
        try{
            userId = databaseReference.push().getKey();
        }catch (Exception e){
            e.printStackTrace();
        }


        if (userId!=null){
            BottomSheetCreateAccount.User user = new BottomSheetCreateAccount.User(userId, null, firstName, lastName, oEmail, gen, prn, roll, div, pEmail, pass, role, mNum, firstName.toLowerCase() + roll + "@unofficialmech", false, dateCreatedAccount, lastPaymentDate, dept);
            databaseReference.child(userId).setValue(user);
            saveUser(userId, firstName, lastName, div, roll, prn, oEmail, gen, pEmail, mNum, pass, role);
            initiateNewActivity();
        }else{
            MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this)
                    .setTitle("Oops!")
                    .setMessage("Unable to create account, please try again.")
                    .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createAccount(firstName, lastName, div, roll, prn, oEmail, gen, pEmail, mNum, pass, role, dept);
                        }
                    });
            b.show();
        }
    }

    private String containsInvalidFirebaseKey(String key) {
        if (key.startsWith(".") || key.contains("/") || key.contains(".") || key.contains("#") || key.contains("$") || key.contains("[") || key.contains("]")){
            return sanitizeKey(key);
        } else {
            return key;
        }
    }
    private String sanitizeKey(String key) {
        StringBuilder sanitizedKey = new StringBuilder();
        int index;

        while ((index = key.indexOf('/')) != -1 ||
                (index = key.indexOf('.')) != -1 ||
                (index = key.indexOf('#')) != -1 ||
                (index = key.indexOf('$')) != -1 ||
                (index = key.indexOf('[')) != -1 ||
                (index = key.indexOf(']')) != -1) {
            sanitizedKey.append(key.substring(0, index));
            key = key.substring(index + 1);
        }

        sanitizedKey.append(key);
        return sanitizedKey.toString();
    }

    private void tempUser(String uid, String firstName, String lastName, String div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role, String dept){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(BottomSheetCreateAccount.this);
        SharedPreferences.Editor e = p.edit();
        e.putString("temp_userId", uid);
        e.putString("temp_name", firstName + " " + lastName);
        e.putString("temp_email", oEmail);
        e.putString("temp_password", pass);
        e.putString("temp_prn", prn);
        e.putString("temp_roll", roll);
        e.putString("temp_division", div);
        e.putString("temp_gender", gen);
        e.putString("temp_userole", role);
        e.putString("temp_mob", mNum);
        e.putString("temp_personalEmail", pEmail);
        e.putString("temp_dept", dept);
        e.apply();
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }

    private void checkTemp(){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(BottomSheetCreateAccount.this);
        String nm = p.getString("temp_name", null);
        if (nm!=null){
            MaterialAlertDialogBuilder d = new MaterialAlertDialogBuilder(this)
                    .setTitle("Manager")
                    .setMessage("Would you like to autofill your details instead of filling it manually again?" + "\n\n Thi will automatically create your account. If you are not able to create your account please contact us on mechanical.official73@gmail.com as soon as possible.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadData();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            d.show();
        }
    }

    private void loadData(){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(BottomSheetCreateAccount.this);
        String nm = p.getString("temp_name", null);
        if (nm!=null){
            String firstName = p.getString("temp_name", null);
            String lastName = p.getString("temp_name", null);
            String div = p.getString("temp_division", null);
            String roll = p.getString("temp_roll", null);
            String prn = p.getString("temp_prn", null);
            String oEmail = p.getString("temp_email", null);
            String gen = p.getString("temp_gender", null);
            String pEmail = p.getString("temp_personalEmail", null);
            String mNum = p.getString("temp_mob", null);
            String pass = p.getString("temp_password", null);
            String role = p.getString("temp_userole", null);
            String dept = p.getString("temp_dept", null);
            createAccount(firstName, lastName, div, roll, prn, oEmail, gen, pEmail, mNum, pass, role, dept);
        }
    }

    private void saveUser(String uid, String firstName, String lastName, String div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(BottomSheetCreateAccount.this);
        SharedPreferences.Editor e = p.edit();
        e.putString("auth_userId", uid);
        e.putString("auth_name", firstName + " " + lastName);
        e.putString("auth_email", oEmail);
        e.putString("auth_password", pass);
        e.putString("auth_prn", prn);
        e.putString("auth_roll", roll);
        e.putString("auth_division", div);
        e.putString("auth_gender", gen);
        e.putString("auth_userole", role);
        e.putString("auth_mob", mNum);
        e.putString("auth_personalEmail", pEmail);
        e.apply();
    }

    private void initiateNewActivity(){
        Toast.makeText(this, "Successfully created account!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkInput(String firstName, String lastName, String Div, String roll, String prn, String oEmail, String gen, String pEmail, String mNum, String pass, String role, String dept){
        if (firstName == null || firstName.isEmpty()){
            Toast.makeText(this, "Please fill your First name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastName == null || lastName.isEmpty()){
            Toast.makeText(this, "Please fill your Last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Div == null || Div.isEmpty()){
            Toast.makeText(this, "Please select Division", Toast.LENGTH_SHORT).show();
            return;
        }
        if (roll == null || roll.isEmpty() || roll.equals("0")){
            Toast.makeText(this, "Please enter a valid Roll No.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (oEmail == null || oEmail.isEmpty()){
            Toast.makeText(this, "Please enter your Official Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gen == null || gen.isEmpty()){
            Toast.makeText(this, "Please select appropriate gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pEmail == null || pEmail.isEmpty()){
            Toast.makeText(this, "Please enter your personal Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mNum == null || mNum.isEmpty()){
            Toast.makeText(this, "Please enter your mobile number.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass == null || pass.isEmpty()){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }else{

            if (!validateMobileNumber(mNum)) {
                Toast.makeText(this, "Invalid Mobile number, please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidPRN(prn)) {
                Toast.makeText(this, "Invalid PRN number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidGmail(pEmail)) {
                Toast.makeText(this, "Invalid Email address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidSanjivanicoe(oEmail)) {
                Toast.makeText(this, "Invalid Clg Email Address", Toast.LENGTH_SHORT).show();
                return;
            }else {
                createAccount(firstName, lastName, Div, roll, prn, oEmail, gen, pEmail, mNum, pass, role, dept);
            }

        }
    }

    private boolean validateMobileNumber(String mobileNumber) {
        Pattern pattern = Pattern.compile("^[6-9]\\d{9}$");
        Matcher matcher = pattern.matcher(mobileNumber);
        return matcher.matches();
    }

    public static boolean isValidPRN(String prnNumber) {
        Pattern pattern = Pattern.compile("^[A-Z]{3}[0-9]{2}[A-Z]{1}[0-9]{4}$");
        Matcher matcher = pattern.matcher(prnNumber);
        return matcher.matches();
    }

    public static boolean isValidGmail(String emailAddress) {
        Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(gmail\\.com)$");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static boolean isValidSanjivanicoe(String emailAddress) {
        Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@sanjivanicoe\\.org\\.in$");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }
    public static void validGender(String gen){
        if (gen.equals("Male")){
            ProjectToolkit.fadeIn(profilePic);
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profilePic);
        }
        if (gen.equals("Female")){
            ProjectToolkit.fadeIn(profilePic);
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profilePic);
        }
    }

    private void download(){
//        deleteApkFiles(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Updates/")));
        DownloadApk.startDownloadingApk(BottomSheetCreateAccount.this, "https://github.com/rubyproducti9n/group-3/blob/main/ion/IonStudent_base.apk", "update_package.apk");
    }

    private void trigger(){
        PDFCreator.TextPosition[] textPositions = {
                new PDFCreator.TextPosition("This is the title", 100, 750),
                new PDFCreator.TextPosition("Body text goes here", 100, 700 )
        };
        createPDF(BottomSheetCreateAccount.this, "letter.txt", textPositions);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BottomSheetCreateAccount.this, PasswordActivity.class));
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }



    static class User{

        private String userId;
        private String avatar;
        private String firstName;
        private String lastName;
        private String clgEmail;
        private String gender;
        private String prn;
        private String rollNo;
        private String div;
        private String personalEmail;
        private String password;
        private String role;
        private String mNum;
        private String altPassword;
        private boolean suspended;
        private String dateCreated;
        private String lastPaymentDate;
        private String dept;

        public User(String userId, String avatar, String firstName, String lastName, String clgEmail, String gender, String prn, String rollNo, String div, String personalEmail, String password, String role, String mNum, String altPassword, boolean suspended, String dateCreated, String lastPaymentDate, String dept){
            this.userId = userId;
            this.avatar = avatar;
            this.firstName = firstName;
            this.lastName = lastName;
            this.clgEmail = clgEmail;
            this.gender = gender;
            this.prn = prn;
            this.rollNo = rollNo;
            this.div =div;
            this.personalEmail = personalEmail;
            this.password = password;
            this.role = role;
            this.mNum = mNum;
            this.altPassword = altPassword;
            this.suspended = suspended;
            this.dateCreated = dateCreated;
            this.lastPaymentDate = lastPaymentDate;
            this.dept = dept;
        }

        public String getUserId() {

            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getClgEmail() {
            return clgEmail;
        }

        public void setClgEmail(String clgEmail) {
            this.clgEmail = clgEmail;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getPrn() {
            return prn;
        }

        public void setPrn(String prn) {
            this.prn = prn;
        }

        public String getRollNo() {
            return rollNo;
        }

        public void setRollNo(String rollNo) {
            this.rollNo = rollNo;
        }

        public String getDiv() {
            return div;
        }

        public void setDiv(String div) {
            this.div = div;
        }

        public String getPersonalEmail() {
            return personalEmail;
        }

        public void setPersonalEmail(String personalEmail) {
            this.personalEmail = personalEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getmNum() {
            return mNum;
        }

        public void setmNum(String mNum) {
            this.mNum = mNum;
        }

        public String getAltPassword() {
            return altPassword;
        }

        public void setAltPassword(String altPassword) {
            this.altPassword = altPassword;
        }

        public boolean isSuspended() {
            return suspended;
        }

        public void setSuspended(boolean suspended) {
            this.suspended = suspended;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getLastPaymentDate() {
            return lastPaymentDate;
        }

        public void setLastPaymentDate(String lastPaymentDate) {
            this.lastPaymentDate = lastPaymentDate;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }
    }
}
