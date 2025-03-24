package com.rubyproducti9n.unofficialmech;

import static androidx.core.app.ActivityCompat.finishAffinity;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeIn;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.fadeOut;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.pref;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetAccountMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetAccountMenu extends BottomSheetProfileEdit {

    private CircularProgressIndicator progressInd;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    DatabaseReference userRef;
    ActivityResultLauncher<Intent> startActivityForResult;
    LinearProgressIndicator linearProgress;
    MaterialButton editProfile, panicButton;
    SwitchMaterial statusButton;
    MaterialButton changeAvatar;
    MaterialCardView editCard, panicCard, beta, dev, logOut;
    ConstraintLayout editLayout, finalLayout;
    Boolean status;
    MaterialCardView post, notice, memory, project ,internship;
    ConstraintLayout mainLayout, noUserLayout;
    ImageView closeBtn;

    View view;
    String[] hods = null;
    String id = null;
    TextView txtDetails;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomSheetAccountMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetUploadOptions.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetAccountMenu newInstance(String param1, String param2) {
        BottomSheetAccountMenu fragment = new BottomSheetAccountMenu();
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
        view = inflater.inflate(R.layout.bottom_sheet_account_menu, container, false);

        userRef = FirebaseDatabase.getInstance().getReference("users");
        optimized();

        txtDetails = view.findViewById(R.id.txtDetails);

        ConstraintLayout mc_main = view.findViewById(R.id.mc_main);
        MaterialCardView drag = view.findViewById(R.id.materialCardView9);
        drag.setVisibility(View.GONE);
        progressInd = view.findViewById(R.id.progress);

        fetchUser(pref(requireActivity()).getString("auth_personalEmail", null));
        new Thread(new Runnable() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> fadeIn(progressInd));
                while(progressStatus < 100){
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressInd.setProgress(progressStatus, true);
                        }
                    });
                    try {
                        Thread.sleep(20);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fadeIn(drag);
                                fadeIn(mc_main);
                                fadeOut(progressInd);
                            }
                        });
                    }
                }, 500);
            }
        }).start();


        closeBtn = view.findViewById(R.id.close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        statusButton = view.findViewById(R.id.switchStatus_button);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String adminEmailCheck = preferences.getString("auth_email", null);

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        userRef = userDatabase.getReference("users");

        SharedPreferences accountPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String authUserRole = preferences.getString("auth_userole", "");

        beta = view.findViewById(R.id.mc_beta);
        beta.setVisibility(View.GONE);
        if (authUserRole.equals("Beta tester")){
            beta.setVisibility(View.VISIBLE);
            beta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), BetaActivity.class);
                    startActivity(intent);
                }
            });
        }

        dev = view.findViewById(R.id.mc_dev);
        dev.setVisibility(View.GONE);
        if (authUserRole.equals("Admin")){
            beta.setVisibility(View.VISIBLE);
            beta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), BetaActivity.class);
                    startActivity(intent);
                }
            });
            dev.setVisibility(View.VISIBLE);
            dev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), DeveloperActivity.class);
                    startActivity(intent);
                }
            });
        }

//        MaterialCardView myActivity = view.findViewById(R.id.mc_myActivity);
//        myActivity.setVisibility(View.GONE);
//        myActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog();
//            }
//        });

        MaterialCardView tnc = view.findViewById(R.id.mc_termsCondition);
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                builder.setTitle("Terms and Conditions");
                builder.setMessage(R.string.termNconditions);
                builder.setIcon(R.drawable.round_supervisor_account_24);
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        MaterialCardView privacyPolicy = view.findViewById(R.id.mc_privacyPolicy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                builder.setTitle("Privacy Policy");
                builder.setMessage(R.string.privacyPolicy);
                builder.setIcon(R.drawable.round_policy_24);
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
//                Intent intent = new Intent(requireContext(), PrivacyPolicyActivity.class);
//                startActivity(intent);
            }
        });

        MaterialCardView about = view.findViewById(R.id.mc_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        logOut = view.findViewById(R.id.mc_logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(requireContext(), PasswordActivity.class);
                startActivity(intent);
                finishAffinity(requireActivity());
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
            }
        });

//        panicCard = view.findViewById(R.id.panicCard);
//        panicCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activatePanicMode();
////                Intent intent = new Intent(requireContext(), AppStoreActivity.class);
////                startActivity(intent);
//                //bottomSheetFragment.show(getChildFragmentManager(), "BottomSheet");
//            }
//        });

        return view;
    }


    private void fetchUser(String adminEmailCheck){
        userRef.orderByChild("personalEmail").equalTo(adminEmailCheck).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Email Exists
                    for(DataSnapshot userSnapshot : snapshot.getChildren()){
                        String avatar = userSnapshot.child("avatar").getValue(String.class);
                        String firstName = userSnapshot.child("firstName").getValue(String.class);
                        String lastName = userSnapshot.child("lastName").getValue(String.class);
                        String officialEmail = userSnapshot.child("clgEmail").getValue(String.class);
                        String personalEmail = userSnapshot.child("personalEmail").getValue(String.class);
                        String prn = userSnapshot.child("prn").getValue(String.class);
                        String contact = userSnapshot.child("contact").getValue(String.class);
                        String division = userSnapshot.child("div").getValue(String.class);
                        String gender = userSnapshot.child("gender").getValue(String.class);
                        String roll = userSnapshot.child("rollNo").getValue(String.class);
                        String role = userSnapshot.child("role").getValue(String.class);
                        String paymentDate = userSnapshot.child("lastPaymentDate").getValue(String.class);
                        status = userSnapshot.child("status").getValue(Boolean.class);

                        String details = "\n" + "Roll No:. " + roll + "\n" +
                                "\n" +
                                "Division: " + division + "\n" +
                                "\n" +
                                "PRN: " + prn + "\n" +
                                "\n" +
                                "Gender: " + gender + "\n" +
                                "\n" +
                                "Mob. No.: " + contact + "\n" +
                                "\n" +
                                "Email: " + personalEmail + "\n";

                        txtDetails.setText(details);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void optimized(){

        MaterialButton resign = view.findViewById(R.id.resign);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String role = preferences.getString("auth_userole", null);
        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resign();
            }
        });
        if (role.equals("HOD")){
            resign.setVisibility(View.VISIBLE);
        }else{
            resign.setVisibility(View.GONE);
        }

    }

    private void newHOD(){


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        id = snap.child("userId").getValue(String.class);
                        String initials = snap.child("initials").getValue(String.class);
                        String firstName = snap.child("firstName").getValue(String.class);
                        String lastName = snap.child("lastName").getValue(String.class);
                        hods = new String[]{initials + lastName};
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Select new HOD");
        if (hods!=null){
            builder.setItems(hods, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            reasign(id);
                            Toast.makeText(requireContext(), hods[which] + " were selected as new HOD", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            reasign(id);
                            Toast.makeText(requireContext(), hods[which] + " were selected as new HOD", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }else{
            builder.setMessage("Oops! Something went wrong, please contact Admin or Check the server status in Settings");
        }
        builder.show();
    }

    private void reasign(String userId){
        startActivity(new Intent(requireContext(), MainActivity.class));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String adminEmailCheck = preferences.getString("auth_email", null);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ExecutorService ex = (ExecutorService) Executors.newSingleThreadExecutor();

        ref.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ex.execute(() ->{
                        for (DataSnapshot snap : snapshot.getChildren()){
                            String key = snap.getKey();
                            if (key!=null){
                                ref.child(key).child("role").setValue("HOD");
                            }
                        }
                    });
                    ex.shutdown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to resign, please contact Admin for more details", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void resign(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String adminEmailCheck = preferences.getString("auth_email", null);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ExecutorService ex = Executors.newSingleThreadExecutor();

        ref.orderByChild("personalEmail").equalTo(adminEmailCheck).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean initiate = true;
                        for (DataSnapshot snap : snapshot.getChildren()){
                            String key = snap.getKey();
                            if (key!=null){
                                ref.child(key).child("role").setValue("Faculty");
                            }
                        }
                        if (initiate){
                            newHOD();
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to resign, please contact Admin for more details", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Boolean getStatus(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String adminEmailCheck = preferences.getString("auth_email", null);
        serviceCheck(requireContext(), new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                if (result){
                    userRef.orderByChild("personalEmail").equalTo(adminEmailCheck).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                //Email Exists
                                for(DataSnapshot userSnapshot : snapshot.getChildren()){

//                                    String avatar = userSnapshot.child("avatar").getValue(String.class);
//                                    String firstName = userSnapshot.child("firstName").getValue(String.class);
//                                    String lastName = userSnapshot.child("lastName").getValue(String.class);
//                                    String officialEmail = userSnapshot.child("clgEmail").getValue(String.class);
//                                    String personalEmail = userSnapshot.child("personalEmail").getValue(String.class);
//                                    String prn = userSnapshot.child("prn").getValue(String.class);
//                                    String contact = userSnapshot.child("contact").getValue(String.class);
//                                    String division = userSnapshot.child("div").getValue(String.class);
//                                    String gender = userSnapshot.child("gender").getValue(String.class);
//                                    String roll = userSnapshot.child("rollNo").getValue(String.class);
//                                    String role = userSnapshot.child("role").getValue(String.class);
//                                    String paymentDate = userSnapshot.child("lastPaymentDate").getValue(String.class);
                                    status = userSnapshot.child("status").getValue(Boolean.class);
                                        if (status != null){
                                            updateStatus(status);
                                        }
                                        if (adminEmailCheck!=null){
                                            statusButton.setVisibility(View.GONE);
                                        }else{
                                            statusButton.setVisibility(View.GONE);
                                        }
                                        if (role.equals("Faculty")){
                                            statusButton.setVisibility(View.VISIBLE);
                                            statusButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    status = !status;
                                                    updateStatus(status);
                                                    userSnapshot.getRef().child("status").setValue(status);
                                                }
                                            });
                                        }else{
                                            statusButton.setVisibility(View.GONE);
                                        }


                                    ProjectToolkit.fadeIn(mainLayout);
//                                        ProjectToolkit.fadeOut(progressLayout);

//                        progress.setVisibility(View.INVISIBLE);
//                        profile.setVisibility(View.VISIBLE);
//                        txtName.setVisibility(View.VISIBLE);
//                        txtEmail.setVisibility(View.VISIBLE);
//                        txtPrn.setVisibility(View.VISIBLE);
//                        txtRole.setVisibility(View.VISIBLE);
//                        logOut.setVisibility(View.VISIBLE);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireContext(), "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    String authUserRole = preferences.getString("auth_userole", null);
                    String id = preferences.getString("auth_userId", null);
                    String authName = preferences.getString("auth_name", null);
                    String authEmail = preferences.getString("auth_email", null);
                    String authPassword = preferences.getString("auth_password", null);
                    String authPrn = preferences.getString("auth_prn", null);
                    String authRoll = preferences.getString("auth_roll", null);
                    String authDivision = preferences.getString("auth_division", null);
                    String authGender = preferences.getString("auth_gender", null);
                    String authUserole = preferences.getString("auth_userole", null);
                    boolean authDash = preferences.getBoolean("dashboard", false);

                        if (authUserRole !=null && authUserRole.equals("Faculty")){
                            statusButton.setVisibility(View.VISIBLE);
                            statusButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    status = !status;
                                    //userSnapshot.getRef().child("status").setValue(status);
                                    updateStatus(status);
                                }
                            });
                        }else{
                            statusButton.setVisibility(View.GONE);
                        }


//                        if (authDivision != null){
//                            if (authDivision == "A"){
//                                profileArt.setImageResource(R.drawable.nav_header_img);
//                            }else if (authDivision == "B"){
//                                profileArt.setImageResource(R.drawable.profile_art);
//                            }else if (authDivision == "C"){
//                                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/c_div.jpg").into(profileArt);
//                            }
//                        }else{
//                            profileArt.setImageResource(R.drawable.nav_header_img);
//                        }

//                        if(authGender.equals("Male")){
//                            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profile);
//                        }
//                        if(authGender.equals("Female")){
//                            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profile);
//                        }
//                        if (authEmail.equals("om.lokhande34@gmail.com")){
//                            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/dev_avatar.jpg").into(profile);
//                            badge.setVisibility(View.VISIBLE);
//                        }else if (authEmail.equals("mechanical.official73@gmail.com")) {
//                            Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/unofficial.png").into(profile);
//                            badge.setVisibility(View.VISIBLE);
//                        }


//                        prnEditText.setText(authPrn);
//                        rollEditText.setText(authRoll);
//                        emailEditText.setText(authEmail);
//                        roleEditText.setText(authUserole);
//                        genderEditText.setText(authGender);

                    ProjectToolkit.fadeIn(mainLayout);
//                        ProjectToolkit.fadeOut(progressLayout);
                }
            }
        });
        return status;
    }

    private void dialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Warning");
        builder.setMessage("Out of service");
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }private void activatePanicMode(){
        final String[] items = {"Ambulance", "Fire Brigade", "Security Officer", "Antiragging", "Chief Warden"};

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
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
                        MaterialAlertDialogBuilder learnMore = new MaterialAlertDialogBuilder(requireContext());
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
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


    private void load(Class c){

    }

    private void updateStatus(Boolean status){
        statusButton = view.findViewById(R.id.switchStatus_button);

        statusButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                statusButton.setChecked(isChecked);

                if(status){
                    statusButton.setText("Switched to Away");
                }else{
                    statusButton.setText("Switched to Available");
                }
            }
        });

    }
}