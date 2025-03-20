package com.rubyproducti9n.unofficialmech;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FacultyCheckActivity extends BottomSheetDialogFragment {

    private FacultyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_faculty_check, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postRef = database.getReference("users");
        Query qr = (Query) postRef.orderByChild("role").equalTo("Faculty");

        CircularProgressIndicator progressBar = view.findViewById(R.id.progressBar);
        ProjectToolkit.fadeIn(progressBar);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ProjectToolkit.fadeOut(recyclerView);
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

        List<BottomSheetFacultyAccount.User> items = new ArrayList<>();
        TextView errTxt = view.findViewById(R.id.err);
        errTxt.setVisibility(View.GONE);
        adapter = new FacultyAdapter(getContext(), items);
        qr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    items.clear();
                    long childNumber = snapshot.getChildrenCount();

                    if (snapshot.exists()){
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            String facultyId = postSnapshot.child("facultyId").getValue(String.class);
                            String firstName = postSnapshot.child("firstName").getValue(String.class);
                            String lastName = postSnapshot.child("lastName").getValue(String.class);
                            String initials = postSnapshot.child("initials").getValue(String.class);
                            String clgEmail = postSnapshot.child("clgEmail").getValue(String.class);
                            String gender = postSnapshot.child("gender").getValue(String.class);
                            String personalEmail = postSnapshot.child("personalEmail").getValue(String.class);
                            String contact = postSnapshot.child("contact").getValue(String.class);
                            String password = null;
                            String role = postSnapshot.child("role").getValue(String.class);
                            Boolean status = postSnapshot.child("status").getValue(Boolean.class);
                            String altPassword = null;
                            boolean suspended = Boolean.TRUE.equals(postSnapshot.child("suspended").getValue(Boolean.class));
                            String dateCreated = null;
                            String lastPaymentDate = null;

                            BottomSheetFacultyAccount.User postItem = new BottomSheetFacultyAccount.User(facultyId, firstName, lastName, initials, clgEmail, gender, personalEmail, contact, password, role, status, altPassword, suspended, dateCreated, lastPaymentDate);
                            items.add(postItem);
                        }
                        Collections.reverse(items);
                        ProjectToolkit.fadeOut(progressBar);
                        ProjectToolkit.fadeIn(recyclerView);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        errTxt.setVisibility(View.VISIBLE);
                        ProjectToolkit.fadeOut(progressBar);
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error 503", Toast.LENGTH_SHORT).show();
                }
            });
            recyclerView.setAdapter(adapter);


        MaterialButton inviteBtn = view.findViewById(R.id.invite);
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://bit.ly/unofficialmech";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "> Unofficial Mech \n\nYou're invited on Unofficial Mech App - The students platform." + "\n\n" + "- Download from here: " + link);
//                startActivity(intent);
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, "Share with"));
                } else {
                    Toast.makeText(getContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
