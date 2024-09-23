package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ApplicationRoleReceiverAdapter extends RecyclerView.Adapter<ApplicationRoleReceiverAdapter.ApplicationRoleReceiverViewHolder>{

    public List<RolesApplicationActivity.RoleDataStructure> items;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference applicationRef = FirebaseDatabase.getInstance().getReference("roleApplications");

    @NonNull
    @Override
    public ApplicationRoleReceiverAdapter.ApplicationRoleReceiverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_role_receiver_item, parent, false);
        return new ApplicationRoleReceiverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationRoleReceiverAdapter.ApplicationRoleReceiverViewHolder holder, int position) {
        RolesApplicationActivity.RoleDataStructure item = items.get(position);

        String role = item.getAppliedRole();
        String userId = item.getUserId();
        Integer status = item.getStatus();

        holder.title.setText(role);
        String statusMsg = null;
        if (status==-1){
            holder.decline.setText("Decline");
            holder.decline.setEnabled(false);
        }else if (status==1){
            statusMsg = "Pending";
        }else if (status==2){
            statusMsg = "Approved";
            holder.decline.setText("Revoke");
            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    revoke(userId, "Student");
                }
            });
        }else{
            statusMsg = "Revoked";
        }

        holder.info.setText("User ID: " + userId + "\n" + "Status: " + statusMsg);

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserDetails(holder.title.getContext(), userId);
            }
        });

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approve(userId, role);
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decline(userId);
            }
        });




    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ApplicationRoleReceiverAdapter(List<RolesApplicationActivity.RoleDataStructure> items){
//        mContext = context;
        this.items = items;
    }

    public static class ApplicationRoleReceiverViewHolder extends RecyclerView.ViewHolder {

        private TextView title, info;
        private MaterialButton approve, decline, details;

        public TextView getTitle(){return title;}
        public TextView getInfo(){return info;}
        public MaterialButton getApprove(){return approve;}
        public MaterialButton getDecline(){return decline;}
        public MaterialButton getDetails(){return details;}

        public ApplicationRoleReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtRole);
            info = itemView.findViewById(R.id.txtInfo);

            approve = itemView.findViewById(R.id.approve);
            decline = itemView.findViewById(R.id.decline);
            details = itemView.findViewById(R.id.details);

        }

    }

    private void approve(String userId, String updatedRole){
        applicationRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        snap.getRef().child("status").setValue(2);
                        updateRole(userId, updatedRole);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateRole(String userId, String updateRole){
        ref.orderByChild(userId).equalTo("userId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        ref.getRef().child("role").setValue(updateRole);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase database error--", "Connection refused!");
            }
        });
    }
    private void decline(String userId){
        applicationRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        snap.getRef().child("status").setValue(0);
//                        updateRole(userId, "Student");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase database error--", "Connection refused!");
            }
        });
    }
    private void revoke(String userId, String updatedRole){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        applicationRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        applicationRef.child("status").setValue(-1);
                        updateRole(userId, "Student");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase database error--", "Connection refused!");
            }
        });
    }

    private void fetchUserDetails(Context context, String userId){
        ref.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        String firstName = snap.child("firstName").getValue(String.class);
                        String lastName = snap.child("lastName").getValue(String.class);
                        String officialEmail = snap.child("clgEmail").getValue(String.class);
                        String personalEmail = snap.child("personalEmail").getValue(String.class);
                        String prn = snap.child("prn").getValue(String.class);
                        String contact = snap.child("contact").getValue(String.class);
                        String division = snap.child("div").getValue(String.class);
                        String gender = snap.child("gender").getValue(String.class);
                        String roll = snap.child("rollNo").getValue(String.class);
                        String role = snap.child("role").getValue(String.class);
                        String paymentDate = snap.child("lastPaymentDate").getValue(String.class);
                        String details = "Name: " + firstName + " " + lastName + "\n" +
                                "Email: " + personalEmail + "\n" +
                                "Prn: " + prn + "\n" +
                                "Contact: " + contact + "\n" +
                                "Section: " + division + "\n" +
                                "Gender: " + gender + "\n" +
                                "Roll No.: " + roll + "\n" +
                                "Current role: " + role + "\n" +
                                "Payment Date: " + paymentDate + "\n";

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                                .setTitle("Details")
                                .setMessage(details)
                                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Contact", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.show();
                    }
                }else{
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Database access denied!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
