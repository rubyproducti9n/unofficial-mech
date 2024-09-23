package com.rubyproducti9n.unofficialmech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFacultyAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFacultyAccount extends BottomSheetProfileEdit {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference databaseReference;
    private TextInputEditText first_name_edit_txt, lastNameEditTxt, clgEmailEditTxt, contactEditTxt, initialEditTxt, personalEmailEditTxt, facultyPassEditTxt, passwordEditTxt;
    private RadioButton maleRadio, femaleRadio;
    String setGender = null;
    private RadioGroup genderRadioGroup;

    public BottomSheetFacultyAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetFacultyAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetFacultyAccount newInstance(String param1, String param2) {
        BottomSheetFacultyAccount fragment = new BottomSheetFacultyAccount();
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
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_faculty_account, container, false);

        ImageView profilePic = view.findViewById(R.id.profilePicture);
        first_name_edit_txt = view.findViewById(R.id.firstNameEditText);
        lastNameEditTxt = view.findViewById(R.id.lastNameEditText);
        initialEditTxt = view.findViewById(R.id.initialEditText);
        clgEmailEditTxt = view.findViewById(R.id.clgEmailEditText);
        genderRadioGroup = view.findViewById(R.id.gender_radio_group);
        maleRadio = view.findViewById(R.id.maleRadioButton);
        femaleRadio = view.findViewById(R.id.femaleRadioButton);
        personalEmailEditTxt = view.findViewById(R.id.personalEmailEditText);
        facultyPassEditTxt = view.findViewById(R.id.facultyPassEditText);
        passwordEditTxt = view.findViewById(R.id.passwordEditText);
        contactEditTxt = view.findViewById(R.id.mobEditText);
        String passwordToString = passwordEditTxt.getText().toString();

        MaterialButton createAccount = view.findViewById(R.id.cretaeAccountBtn);
        createAccount.setEnabled(false);

        passwordEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                createAccount.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                createAccount.setEnabled(true);
                createAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int facultyPass = Integer.parseInt(facultyPassEditTxt.getText().toString().trim());
                        checkFacultyPass(facultyPass);
                        //checkIfAccountExists(v);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
                createAccount.setEnabled(true);
                createAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int facultyPass = Integer.parseInt(facultyPassEditTxt.getText().toString().trim());
                        checkFacultyPass(facultyPass);
                        //checkIfAccountExists(v);
                    }
                });
            }
        });

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                if (checkedId == R.id.maleRadioButton){
                    setGender = "Male";
                    Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profilePic);
                }
                if (checkedId == R.id.femaleRadioButton){
                    setGender = "Female";
                    Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profilePic);
                }
            }
        });

        return view;
    }

    public void checkFacultyPass(int pass){
        if (pass >= 1000 && pass <=1500){
            onSignUpClick(getView());
        }else{
            Toast.makeText(getContext(), "Parody accounts are not allowed on this platform", Toast.LENGTH_SHORT).show();
        }
    }
    public void onSignUpClick(View view){
        String firstName = first_name_edit_txt.getText().toString().trim();
        String lastName = lastNameEditTxt.getText().toString().trim();
        String clgEmail = clgEmailEditTxt.getText().toString().trim();
        String personalEmail = personalEmailEditTxt.getText().toString().trim();
        String password = passwordEditTxt.getText().toString().trim();
        String initials = initialEditTxt.getText().toString().trim();
        String contact = "+91" + contactEditTxt.getText().toString().trim();
        String role = "Faculty";
        Boolean status = true;
        String div = null;
        String altPassword = lastName.toLowerCase() + firstName.toLowerCase() + "@unofficialmech";

        if(firstName.isEmpty() || lastName.isEmpty() ||clgEmail.isEmpty() ||personalEmail.isEmpty() ||password.isEmpty()){
            Toast.makeText(getContext(), "Please enter all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateCreatedAccount = currentDate.format(dateTimeFormatter);

        String lastPaymentDate = currentDate.format(dateTimeFormatter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        String facultyId = databaseReference.push().getKey();
        User user = new User(facultyId, firstName, lastName, initials, clgEmail, setGender, personalEmail, contact, password, role, status, altPassword, false, dateCreatedAccount, lastPaymentDate);

        databaseReference.child(facultyId).setValue(user);

        Toast.makeText(getContext(), "Successfully created account!", Toast.LENGTH_SHORT).show();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor authEditor = pref.edit();
        authEditor.putString("facultyId", facultyId);
        authEditor.putString("auth_name", firstName + " " + lastName);
        authEditor.putString("auth_initials", initials);
        authEditor.putString("auth_email", personalEmail);
        authEditor.putString("auth_password", password);
        authEditor.putString("auth_division", div);
        authEditor.putString("auth_gender", setGender);
        authEditor.putString("auth_userole", role);
        authEditor.apply();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

    }

    static class User{

        private String facultyId;
        private String firstName;
        private String lastName;
        private String initials;
        private String clgEmail;
        private String gender;
        private String personalEmail;
        private String contact;
        private String password;
        private String role;
        private Boolean status;
        private String altPassword;
        private boolean suspended;
        private String dateCreated;
        private String lastPaymentDate;

        public User(String facultyId, String firstName, String lastName, String initials, String clgEmail, String gender, String personalEmail, String contact, String password, String role, Boolean status, String altPassword, boolean suspended, String dateCreated, String lastPaymentDate){
            this.facultyId = facultyId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.initials = initials;
            this.clgEmail = clgEmail;
            this.gender = gender;
            this.personalEmail = personalEmail;
            this.contact = contact;
            this.password = password;
            this.role = role;
            this.status = status;
            this.altPassword = altPassword;
            this.suspended = suspended;
            this.dateCreated = dateCreated;
            this.lastPaymentDate = lastPaymentDate;
        }

        public String getUserId() {
            return facultyId;
        }

        public void setUserId(String facultyId) {
            this.facultyId = facultyId;
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

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
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


        public String getPersonalEmail() {
            return personalEmail;
        }

        public void setPersonalEmail(String personalEmail) {
            this.personalEmail = personalEmail;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
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

        public void setRole(String roll) {
            this.role = role;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
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
    }
}