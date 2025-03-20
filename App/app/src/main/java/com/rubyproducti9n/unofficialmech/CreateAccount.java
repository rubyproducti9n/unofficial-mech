package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.h;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccount extends BaseActivity {
    DatabaseReference databaseReference;

    TextInputEditText editFName, editLName, editDiv, editrollNo, editDept, editPrn, editClgMail, editGender, editMail, editMob, editPass;
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    ImageView imgAvatar;
    ViewFlipper viewFlipper;
    SharedPreferences pref;
    SharedPreferences.Editor e;
    private MaterialAutoCompleteTextView divDrop, deptDrop;
    String inDivision, inDept, inGender = null;
    String inRRoll = "0";
    MaterialButton facultybtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        e = pref.edit();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        facultybtn = findViewById(R.id.facultyBtn);
        facultybtn.setOnClickListener(v -> checkFaculty());

        imgAvatar = findViewById(R.id.profilePicture);
        editFName = findViewById(R.id.firstNameEditText);
        editLName = findViewById(R.id.lastNameEditText);
//        //editDiv = findViewById(R.id.divisionEditText);
//        editrollNo = findViewById(R.id.rollEditText);
//        //editDept = findViewById(R.id.firstNameEditText);
        editPrn = findViewById(R.id.prnEditText);
        editClgMail = findViewById(R.id.clgEmailEditText);
//        //editGender = findViewById(R.id.firstNameEditText);
//        editMail = findViewById(R.id.personalEmailEditText);
        editMob = findViewById(R.id.mobileNumEditText);
        editPass = findViewById(R.id.passwordEditText);

        viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left));
        MaterialButton n1 = findViewById(R.id.next1);
        MaterialButton p2 = findViewById(R.id.previous2);
        MaterialButton n2 = findViewById(R.id.next2);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account); // If user is already signed in, update the UI
        }

        SignInButton bt = findViewById(R.id.signIn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        n1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        divDrop = findViewById(R.id.dropdown_menu2);
        deptDrop = findViewById(R.id.deptMenu);
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

        TextInputEditText rollInput = findViewById(R.id.rollEditText);
        rollInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inRRoll = "0";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isInt = isInt(rollInput.getText().toString());
                if (isInt){
                    inRRoll = rollInput.getText().toString();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        });
        n2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inRRoll!="0" && inDivision!=null && inDept!=null && isValidPRN(Objects.requireNonNull(editPrn.getText()).toString())){
                    e.putString("temp_div", inDivision);
                    e.putString("temp_dept", inDept);
                    e.putString("temp_roll", inRRoll);
                    e.putString("temp_prn", Objects.requireNonNull(editPrn.getText()).toString());
                    viewFlipper.showNext();
                }else{
                    Toast.makeText(CreateAccount.this, "Invalid details, please check the details again", Toast.LENGTH_SHORT).show();
                }
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
        MaterialButton p3 = findViewById(R.id.previous3);
        MaterialButton n3 = findViewById(R.id.next3);
        p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        });
        n3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidSanjivanicoe(editClgMail.getText().toString()) && validateMobileNumber(editMob.getText().toString())){
                    e.putString("temp_gender", inGender);
                    e.putString("temp_clgmail", editClgMail.getText().toString());
                    e.putString("temp_mob", editMob.getText().toString());
                    viewFlipper.showNext();
                }else{
                    Toast.makeText(CreateAccount.this, "Invalid details, please check the details again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MaterialButton register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPass.getText()!=null){
                    e.putString("temp_pass", h(CreateAccount.this, editPass.getText().toString()));
                    e.apply();

                    String firstName = pref.getString("temp_first_name", null);
                    String lastName = pref.getString("temp_last_name", null);
                    String mail = pref.getString("temp_mail", null);
                    String clgmail = pref.getString("temp_clgmail", null);
                    String avatar = pref.getString("temp_avatar", null);
                    String div = pref.getString("temp_div", null);
                    String dept = pref.getString("temp_dept", null);
                    String roll = pref.getString("temp_roll", null);
                    String prn = pref.getString("temp_prn", null);
                    String gender = pref.getString("temp_gender", null);
                    String details = "Name: " + firstName + " " + lastName + "\n" +
                            "Section: " + div + "\n" +
                            "Dept.: " + dept + "\n" +
                            "Roll No.: " + roll + "\n" +
                            "PRN: " + prn + "\n" +
                            "Gender: " + gender + "\n" +
                            "Email Id: " + mail + "\n" +
                            "Sanjivanicoe Email: " + clgmail;
                    new MaterialAlertDialogBuilder(CreateAccount.this)
                            .setTitle("Register?")
                            .setMessage(details)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createAccount();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });

    }

    public void createAccount() {
        // Store values in SharedPreferences
        e.putString("auth_first_name", pref.getString("temp_first_name", null));
        e.putString("auth_last_name", pref.getString("temp_last_name", null));
        e.putString("auth_email", pref.getString("temp_mail", null)); // Ensure the key is consistent
        e.putString("auth_clgmail", pref.getString("temp_clgmail", null));
        e.putString("auth_avatar", pref.getString("temp_avatar", null));
        e.putString("auth_division", pref.getString("temp_div", null));
        e.putString("auth_dept", pref.getString("temp_dept", null));
        e.putString("auth_roll", pref.getString("temp_roll", null));
        e.putString("auth_prn", pref.getString("temp_prn", null));
        e.putString("auth_gender", pref.getString("temp_gender", null));
        e.putString("auth_userole", pref.getString("temp_role", "Student"));
        e.putString("auth_password", pref.getString("temp_pass", null));
        e.putString("auth_mobile", pref.getString("temp_mob", null)); // Ensure the key is consistent
        e.apply(); // Save changes before retrieving

        // Retrieve values from SharedPreferences
        String firstName = pref.getString("auth_first_name", null);
        String lastName = pref.getString("auth_last_name", null);
        String name = firstName + " " + lastName;
        String mail = pref.getString("auth_email", null); // Ensure the key is consistent
        String clgmail = pref.getString("auth_clgmail", null);
        String avatar = pref.getString("auth_avatar", null);
        String div = pref.getString("auth_division", null); // Correct key
        String dept = pref.getString("auth_dept", null);
        String roll = pref.getString("auth_roll", null);
        String prn = pref.getString("auth_prn", null);
        String gender = pref.getString("auth_gender", null);
        String password = pref.getString("auth_password", null);
        String mobile = pref.getString("auth_mobile", null); // Ensure the key is consistent

        // Handle date and formatting
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreatedAccount = currentDate.format(dateTimeFormatter);

        // Generate userId
        String id = databaseReference.push().getKey();
        e.putString("auth_userId", id);
        e.apply();

        String lastPaymentDate = currentDate.format(dateTimeFormatter);

        // Create User object
        CreateUser item = new CreateUser(
                id,
                avatar,
                firstName,
                lastName,
                clgmail,
                gender,
                prn,
                roll,
                div,
                mail,
                password,
                "Student",
                mobile,
                firstName + roll + "@unofficialmech",
                false,
                dateCreatedAccount,
                lastPaymentDate,
                dept
        );

        // Store in database
        databaseReference.child(id).setValue(item);

        // Show success message
        Toast.makeText(this, "Successfully created account!", Toast.LENGTH_SHORT).show();

        // Redirect to main activity
        startActivity(new Intent(CreateAccount.this, TourActivity.class));
        finish();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                viewFlipper.showNext();
                updateUI(account);
            } catch (ApiException e) {
                // Google Sign-In failed
                Log.w("Google Sign-In", "Google sign in failed", e);
                Toast.makeText(CreateAccount.this, "Sign-in Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // Set the user's name
            String personName = account.getDisplayName();
            String email = account.getEmail();
            String[] nameParts = personName.split(" ");
            String firstName = nameParts.length > 0 ? nameParts[0] : "First Name";
            String lastName = nameParts.length > 1 ? nameParts[1] : "Last Name";
            e.putString("temp_first_name", firstName);
            e.putString("temp_last_name", lastName);
            e.putString("temp_mail", email);
            editFName.setText(firstName);
            editLName.setText(lastName);
//            editMail.setText(email);

            String personPhotoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;
            if (personPhotoUrl != null) {
                Picasso.get().load(personPhotoUrl).into(imgAvatar);
                imgAvatar.setVisibility(View.VISIBLE);
                e.putString("temp_avatar", personPhotoUrl);
            }
        }
    }

    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;

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

    public static boolean isValidSanjivanicoe(String emailAddress) {
        Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@sanjivanicoe\\.org\\.in$");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    private void validGender(String gen){
        if (gen.equals("Male")){
            ProjectToolkit.fadeIn(imgAvatar);
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(imgAvatar);
        }
        if (gen.equals("Female")){
            ProjectToolkit.fadeIn(imgAvatar);
            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(imgAvatar);
        }
    }

    public static boolean isValidGmail(String emailAddress) {
        Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(gmail\\.com)$");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    private void checkFaculty(){
        startActivity(new Intent(CreateAccount.this, LockActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new MaterialAlertDialogBuilder(this)
                .setTitle("Exit")
                .setMessage("Are you sure want to exit?")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}