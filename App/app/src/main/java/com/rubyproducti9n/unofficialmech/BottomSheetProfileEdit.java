package com.rubyproducti9n.unofficialmech;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class BottomSheetProfileEdit extends BottomSheetDialogFragment {

    DatabaseReference userRef;
    ImageView profile;
    TextInputEditText fn, ln, div, clgEmail, gen, prnno, rollno, perEmail, mob;
    TextView role;
    ProjectToolkit u;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_profile_edit, container, false);
        u = new ProjectToolkit(PreferenceManager.getDefaultSharedPreferences(view.getContext()));

        profile = view.findViewById(R.id.profile);
        fn = view.findViewById(R.id.firstNameEditText);
        ln = view.findViewById(R.id.lastNameEditText);
        div = view.findViewById(R.id.divisionEditText);
        clgEmail = view.findViewById(R.id.clgEmailEditText);
        gen = view.findViewById(R.id.genderEditText);
        prnno = view.findViewById(R.id.prnEditText);
        rollno = view.findViewById(R.id.rollEditText);
        perEmail = view.findViewById(R.id.personalEmailEditText);
        mob = view.findViewById(R.id.mobEditText);
        role = view.findViewById(R.id.role);


//        LottieAnimationView animLogin = view.findViewById(R.id.animDev);
//        animLogin.setAnimation(R.raw.coming_soon);
//        animLogin.playAnimation();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String adminEmailCheck = preferences.getString("auth_email", null);
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        userRef = userDatabase.getReference("users");
        userRef.orderByChild("personalEmail").equalTo(adminEmailCheck).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Email Exists
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        //Retrieve the user data
                        String avatar = userSnapshot.child("avatar").getValue(String.class);
                        String getFirstName = userSnapshot.child("firstName").getValue(String.class);
                        String getLastName = userSnapshot.child("lastName").getValue(String.class);
                        String getOfficialEmail = userSnapshot.child("clgEmail").getValue(String.class);
                        String getPersonalEmail = userSnapshot.child("personalEmail").getValue(String.class);
                        String getPrn = userSnapshot.child("prn").getValue(String.class);
                        String getDivision = userSnapshot.child("div").getValue(String.class);
                        String getGender = userSnapshot.child("gender").getValue(String.class);
                        String getRoll = userSnapshot.child("rollNo").getValue(String.class);
                        String getRole = userSnapshot.child("role").getValue(String.class);
                        String getMob = userSnapshot.child("mob").getValue(String.class);
                        Boolean getStatus = userSnapshot.child("status").getValue(Boolean.class);

                        Picasso.get().load(avatar).into(profile);
                        fn.setText(getFirstName);
                        ln.setText(getLastName);
                        div.setText(getDivision);
                        clgEmail.setText(getOfficialEmail);
                        perEmail.setText(getPersonalEmail);
                        prnno.setText(getPrn);
                        gen.setText(getGender);
                        rollno.setText(getRoll);
                        mob.setText(getMob);
                        role.setText("Assigned role: " + getRole);


                        MaterialButton save = view.findViewById(R.id.save);
                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                updateProfile("Omiii", getLastName, getOfficialEmail, getDivision, getGender, getPrn, getRoll, getPersonalEmail, getMob);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase Error", "Access denied by firebase database");
            }
        });
                return view;
    }

    private void updateProfile(String firstName,
                               String lastName,
                               String clgEmail,
                               String div,
                               String gender,
                               String prn,
                               String roll,
                               String perEmail,
                               String mob){

        String id = u.getUserId("auth_userId");
        String n = u.getUserName("auth_name");
        String[] arr = n.split(" ");
        String fn = arr[0];
        String ln = arr[1];
        String e = u.getUserEmail("auth_email");
        String pass = u.getUserPrnNo("auth_password");
        String p = u.getUserPrnNo("auth_prn");
        String d = u.getUserDivision("auth_division");
        String g = u.getUserGender("auth_gender");
        String r = u.getUserRoll("auth_roll");

        HashMap<String, Object> updates = new HashMap<>();
        if (!firstName.equals(fn)){
            updates.put("firstName", firstName);
        }
        if (!lastName.equals(ln)){
            updates.put("lastName", lastName);
        }
        if (!div.equals(d)){
            if (div.equals("A") || div.equals("B") || div.equals("C")){
                updates.put("div", div);
            }else{
                Toast.makeText(getContext(), "Invalid division, please specify 'A', 'B', 'C'", Toast.LENGTH_SHORT).show();
            }
        }
        if (!gender.equals(g)){
            if (gender.equals("Male") || gender.equals("Female")){
                updates.put("gender", gender);
            }else{
                Toast.makeText(getContext(), "Invalid gender, please specify 'Male' or 'Female'", Toast.LENGTH_SHORT).show();
            }

        }
        if (!roll.equals(r)){
            updates.put("rollNo", roll);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/").child(id);
        for (String key : updates.keySet()){
            databaseReference.updateChildren(updates);
        }

    }

}