package com.rubyproducti9n.unofficialmech;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RolesApplicationActivity extends BaseActivity {


    Integer status;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("roleApplications");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles_application);

        MaterialCardView betaRole = findViewById(R.id.betaRole);
        betaRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateDialog("Beta Tester", "Msg goes here", "Beta tester");
            }
        });
        MaterialCardView creatorRole = findViewById(R.id.creatorRole);
        creatorRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateDialog("Creator Account", "Msg goes here", "Creator");
            }
        });

    }

    private void initiateDialog(String title, String msg, String role){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RolesApplicationActivity.this);
        builder.setTitle(title);
        getStatus(new OnStatusRetrieved() {
            @Override
            public void onStatusRetrieved(Integer status, String errorMessage) {
                String statusMsg = null;
                boolean isApplied = false;
                if (status!=null){
                    if(status==0){
                        statusMsg = "\n" + "Status: Not applied";
                    }else if (status==1){
                        statusMsg = "\n" + "Status: Pending";
                        isApplied = true;
                    } else if (status == 2) {
                        statusMsg = "\n" + "Status: Approved";
                        isApplied = true;
                    }else{
                        statusMsg = "\n" + "Status: (Undefined)";
                    }

                    if (!isApplied){
                        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                apply(checkUserId(), role);
                            }
                        });
                    }
                    builder.setMessage(msg + statusMsg);
                    builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }else{
                    builder.setMessage(msg + "\n" + "Status: Not applied");
                    builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            apply(checkUserId(), role);
                        }
                    });
                    builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    private void apply(String userId, String role){
        String applicationId = ref.push().getKey();
        assert applicationId != null;
        RoleDataStructure structure = new RoleDataStructure(userId, 1, role);
        ref.child(applicationId).setValue(structure);

        Toast.makeText(this, "Application is under review", Toast.LENGTH_SHORT).show();
    }

    private String checkUserId(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getString("auth_userId", null);
    }

    private void getStatus(final OnStatusRetrieved callback){

        if (checkUserId()!=null){
            ref.orderByChild("userId").equalTo(checkUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot snap : snapshot.getChildren()){
                            String userId = snap.child("userId").getValue(String.class);
                            String role = snap.child("role").getValue(String.class);
                            status = snap.child("status").getValue(Integer.class);
                            callback.onStatusRetrieved(status, "");
                        }

                    }else{
                        callback.onStatusRetrieved(null, "No record found for your user ID");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onStatusRetrieved(null, error.getMessage());
                }
            });
        }else{
            startActivity(new Intent(RolesApplicationActivity.this, PasswordActivity.class));
            Toast.makeText(this, "You were logged out", Toast.LENGTH_SHORT).show();
            callback.onStatusRetrieved(null, "User not logged in");
        }

    }
    public interface OnStatusRetrieved {
        void onStatusRetrieved(Integer status, String errorMessage);
    }


    public static class RoleDataStructure{

        public String userId;
        public Integer status;
        public String appliedRole;

        public RoleDataStructure(String userId, Integer status, String appliedRole) {
            this.userId = userId;
            this.status = status;
            this.appliedRole = appliedRole;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getAppliedRole() {
            return appliedRole;
        }

        public void setAppliedRole(String appliedRole) {
            this.appliedRole = appliedRole;
        }
    }


}