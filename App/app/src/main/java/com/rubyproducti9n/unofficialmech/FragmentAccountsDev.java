package com.rubyproducti9n.unofficialmech;

import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAccountsDev#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAccountsDev extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DevAccountsAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentAccountsDev() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMemoriesDev.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAccountsDev newInstance(String param1, String param2) {
        FragmentAccountsDev fragment = new FragmentAccountsDev();
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

        View view =inflater.inflate(R.layout.fragment_accounts_dev, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postRef = database.getReference("users");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
//        List<PostItem> items = Arrays.asList(
//                new PostItem(
//                        "1",
//                        "Aishwarya Kumbhar",
//                        "https://rubyproducti9n.github.io/mech/memories/ahmed sir/a1.jpg",
//                        "Congratulations on your retirement, sir! we wish you all the best in your next chapter.😢",
//                        "2023-05-25"),
//                new PostItem(
//                        "1",
//                        "Aishwarya Kumbhar",
//                        "https://rubyproducti9n.github.io/mech/memories/ahmed sir/a2.jpg",
//                        "We will miss your sir!.. 😢😢😢",
//                        "2023-05-25"),
//                new PostItem(
//                        "1",
//                        "Om Lokhande",
//                        "https://rubyproducti9n.github.io/mech/memories/verul/g6.jpg",
//                        "Successfully completed verul trip..",
//                        "2023-04-29"),
//                new PostItem(
//                        "1",
//                        "Aishwarya Kumbhar",
//                        "https://rubyproducti9n.github.io/mech/img/post 1.jpg",
//                        "Railway workshop , Manmad",
//                        "2023-02-19")
//        );

        List<DevAccountsItems> items = new ArrayList<>();
        postRef.orderByChild("rollNo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                items.clear();
                long childNumber = snapshot.getChildrenCount();

                for (DataSnapshot accountsSnapshot : snapshot.getChildren()) {
                    String postId = accountsSnapshot.child("userId").getValue(String.class);
                    String avatar = accountsSnapshot.child("avatar").getValue(String.class);
                    String firstName = accountsSnapshot.child("firstName").getValue(String.class);
                    String lastName = accountsSnapshot.child("lastName").getValue(String.class);
                    String userName = firstName + " " + lastName;
                    String div = accountsSnapshot.child("div").getValue(String.class);
                    String prn = accountsSnapshot.child("prn").getValue(String.class);
                    String role = accountsSnapshot.child("role").getValue(String.class);
                    String clgEmail = accountsSnapshot.child("clgEmail").getValue(String.class);
                    Boolean verified = accountsSnapshot.child("verified").getValue(Boolean.class);
                    Boolean suspended = accountsSnapshot.child("suspended").getValue(Boolean.class);

                    DevAccountsItems accountDetails = new DevAccountsItems(postId, avatar , userName, div, prn, role, clgEmail, verified, suspended);
                    items.add(accountDetails);
                }
                Collections.reverse(items);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = (DevAccountsAdapter) new DevAccountsAdapter(getContext(), items, 1);
        recyclerView.setAdapter(adapter);

        CircularProgressIndicator progressIndicator = view.findViewById(R.id.circularProgressIndicator);
        TextView txt = view.findViewById(R.id.registeredUserTxt);
        countUsers(txt, progressIndicator);

        CircularProgressIndicator progressIndicator0 = view.findViewById(R.id.circularProgressIndicator0);
        TextView txt0 = view.findViewById(R.id.registeredFacultyTxt);
        countFaculties(txt0, progressIndicator0);

        TextView maleCount, femaleCount;
        maleCount = view.findViewById(R.id.maleCount);
        femaleCount = view.findViewById(R.id.femaleCount);
        countUsersByGender(maleCount, femaleCount);

        TextView a, b, c;
        CircularProgressIndicator cpiA, cpiB, cpiC;
        cpiA = view.findViewById(R.id.circularProgressIndicatorA);
        cpiB = view.findViewById(R.id.circularProgressIndicatorB);
        cpiC = view.findViewById(R.id.circularProgressIndicatorC);
        a = view.findViewById(R.id.registeredUserTxtA);
        b = view.findViewById(R.id.registeredUserTxtB);
        c = view.findViewById(R.id.registeredUserTxtC);
        countUsersByDivision(a, b, c, cpiA, cpiB, cpiC);

        return view;
    }

    public void countUsers(final TextView userCountTextView, final CircularProgressIndicator progressBar) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        // Use addListenerForSingleValueEvent for a one-time count
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                ValueAnimator animator = ValueAnimator.ofInt(0, count);
                animator.setDuration(1000); // Adjust duration as needed (in milliseconds)
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int progress = (int) animation.getAnimatedValue();
                        progressBar.setProgress(progress, true);
                        userCountTextView.setText(String.valueOf(progress));
                    }
                });
                animator.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                userCountTextView.setText("Error fetching user count");
            }
        });
    }

    public void countFaculties(final TextView userCountTextView, final CircularProgressIndicator progressBar) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        // Use addListenerForSingleValueEvent for a one-time count
        userRef.orderByChild("role").equalTo("Faculty").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                ValueAnimator animator = ValueAnimator.ofInt(0, count);
                animator.setDuration(1000); // Adjust duration as needed (in milliseconds)
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int progress = (int) animation.getAnimatedValue();
                        progressBar.setProgress(progress, true);
                        userCountTextView.setText(String.valueOf(progress));
                    }
                });
                animator.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                userCountTextView.setText("Error fetching user count");
            }
        });
    }
    public void countUsersByGender(final TextView maleCountTextView, final TextView femaleCountTextView) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maleCount = 0;
                int femaleCount = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String gender = userSnapshot.child("gender").getValue(String.class);
                    if (gender != null) {
                        if (gender.equalsIgnoreCase("Male")) {
                            maleCount++;
                        } else if (gender.equalsIgnoreCase("Female")) {
                            femaleCount++;
                        }
                    }
                }

                String maleCountText = String.valueOf(maleCount);
                maleCountTextView.setText(maleCountText);

                String femaleCountText = String.valueOf(femaleCount);
                femaleCountTextView.setText(femaleCountText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                maleCountTextView.setText("Error fetching male count");
                femaleCountTextView.setText("Error fetching female count");
            }
        });
    }

    public void countUsersByDivision(final TextView divisionATextView, final TextView divisionBTextView, final TextView divisionCTextView, CircularProgressIndicator cpiA, CircularProgressIndicator cpiB, CircularProgressIndicator cpiC) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int divisionACount = 0;
                int divisionBCount = 0;
                int divisionCCount = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String division = userSnapshot.child("div").getValue(String.class);
                    if (division != null) {
                        if (division.equalsIgnoreCase("A")) {
                            divisionACount++;
                        } else if (division.equalsIgnoreCase("B")) {
                            divisionBCount++;
                        } else if (division.equalsIgnoreCase("C")) {
                            divisionCCount++;
                        }
                    }
                }

                String divisionAText = String.valueOf(divisionACount);
                divisionATextView.setText(divisionAText);
                cpiA.setProgress(divisionACount, true);

                String divisionBText = String.valueOf(divisionBCount);
                divisionBTextView.setText(divisionBText);
                cpiB.setProgress(divisionBCount, true);

                String divisionCText = String.valueOf(divisionBCount);
                divisionCTextView.setText(divisionCText);
                cpiC.setProgress(divisionCCount, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                divisionATextView.setText("Error fetching Division A count");
                divisionBTextView.setText("Error fetching Division B count");
            }
        });
    }
}