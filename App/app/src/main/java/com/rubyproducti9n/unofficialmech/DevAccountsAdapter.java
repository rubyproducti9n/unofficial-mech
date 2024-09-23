package com.rubyproducti9n.unofficialmech;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class DevAccountsAdapter extends RecyclerView.Adapter<DevAccountsAdapter.DevAccountsViewHolder>{

    private List<DevAccountsItems> items;
    Context mContext;
    int stat;

    @NonNull
    @Override
    public DevAccountsAdapter.DevAccountsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dev_accounts_view, parent, false);
        return new DevAccountsAdapter.DevAccountsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevAccountsAdapter.DevAccountsViewHolder holder, int position) {



        DevAccountsItems item = items.get(position);

        String userId = item.getUserId();
        String avatar = item.getUserAvatar();
        String accountName = item.getAccountName();
        String accountDivision = item.getUserDivision();
        String accountRole = item.getUserRole();





        if (accountName != null){
            Picasso.get().load(avatar).into(holder.avatar1);
//            holder.getAvatar(accountName);
        }else{
            holder.getGender(accountName);
        }

        if (stat ==1){
            holder.devView.setVisibility(View.VISIBLE);
            holder.userView.setVisibility(GONE);

            holder.getUserId().setText(userId);

            holder.getAccountName().setText(accountName);

            holder.getDivision().setText(accountDivision + " • " + accountRole);

//            holder.getRole().setText(accountRole);

//            holder.getUserStatus(userId);

            holder.resetBtn.setOnClickListener(v ->{
                resetUser(userId);
            });

            holder.suspendBtn.setOnClickListener(v ->{
                suspendUser(userId);
            });

            holder.verifyUser.setOnClickListener(v ->{
                verifyUser(userId);
            });
        }else{
            holder.Uyear.setVisibility(GONE);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String myId = pref.getString("auth_userId", null);
            if (Objects.equals(myId, item.getUserId())){
                holder.devView.setVisibility(View.GONE);
                holder.userView.setVisibility(View.GONE);
            }else{
                holder.avatar1.setImageResource(R.drawable.round_account_circle_24);
//                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(holder.avatar1);
                holder.devView.setVisibility(GONE);
                holder.userView.setVisibility(View.VISIBLE);

                holder.username.setText(accountName);
                holder.mc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProfileViewsActivity.class);
                        intent.putExtra("username", item.getAccountName());
                        mContext.startActivity(intent);
                    }
                });

                if (item.getUserYear() != null) {
                    holder.Uyear.setVisibility(View.VISIBLE);
                    holder.Uyear.setText(item.getUserYear());
                }


            }
        }


    }
    public DevAccountsAdapter(Context context, List<DevAccountsItems> items, int stat){
        mContext = context;
        this.items = items;
        this.stat = stat;
        //this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class DevAccountsViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout userView, devView;
        private MaterialCardView mc;
        private TextView username, Uyear;
        private ImageView avatar1;
        private MaterialButton viewProfile;

        private TextView userId;
        private ImageView avatar;
        private TextView accountName;
        private TextView division;
        private TextView role;
        private TextView clgEmail;
        private ImageView verified;
        private ImageView suspended;

        private MaterialButton resetBtn;
        private MaterialButton suspendBtn;
        private MaterialButton verifyUser;

        public TextView getUserId(){
            return userId;
        }
        public ImageView getAvatar(){
            return avatar;
        }
        public TextView getAccountName(){
            return  accountName;
        }
        public TextView getDivision(){return division;}
        public TextView getRole(){
            return role;
        }
        public TextView getClgEmail(){return clgEmail;}
        public ImageView getVerified(){return verified;}
        public ImageView getSuspended(){return suspended;}

        public DevAccountsViewHolder(@NonNull View itemView) {
            super(itemView);

            mc = itemView.findViewById(R.id.mc);
            userView = itemView.findViewById(R.id.userView);
            username = itemView.findViewById(R.id.uUsername);
            Uyear = itemView.findViewById(R.id.uYear);
            avatar1 = itemView.findViewById(R.id.uAvatar);
            viewProfile = itemView.findViewById(R.id.uViewProfile);

            devView = itemView.findViewById(R.id.devView);
            userId = itemView.findViewById(R.id.userId);
            avatar = itemView.findViewById(R.id.avatar);
            accountName = itemView.findViewById(R.id.account_holder_name);
            division = itemView.findViewById(R.id.division);
            role = itemView.findViewById(R.id.role);
            verified = itemView.findViewById(R.id.verified);
            suspended = itemView.findViewById(R.id.suspended);

            resetBtn = itemView.findViewById(R.id.resetBtn);
            suspendBtn = itemView.findViewById(R.id.suspendUserBtn);
            verifyUser = itemView.findViewById(R.id.verifyUserBtn);
        }


        private void getAvatar(String userName){
            ImageView profile_avatar = itemView.findViewById(R.id.avatar);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            mDatabase.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            String avatarLink = userSnapshot.child("avatar").getValue(String.class);
                            if (avatarLink != null){
                                Picasso.get().load(avatarLink).into(profile_avatar);
                            }else{
                                getGender(userName);
                            }
                        }
                    }else{
                        Log.d("User not found", "error");
                        getGender(userName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Request declined by Firebase", "error");
                }
            });
        }

        private void getGender(String userName){
            ImageView profile_avatar = itemView.findViewById(R.id.avatar);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            mDatabase.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            String genderCheck = userSnapshot.child("gender").getValue(String.class);
                            if (genderCheck != null){
                                if (genderCheck == "Male"){
                                    Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profile_avatar);
                                }else if (genderCheck == "Female"){
                                    Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profile_avatar);
                                }else{
                                    profile_avatar.setImageResource(R.drawable.round_account_circle_24);
                                }
                            }else{
                                profile_avatar.setImageResource(R.drawable.round_account_circle_24);
                            }
                        }
                    }else{
                        Log.d("User not found", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Request declined by Firebase", "error");
                }
            });
        }


        private void getUserStatus(String userId){

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            userRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Boolean verificationValue = snapshot.child("verified").getValue(Boolean.class);
                        Boolean suspendValue = snapshot.child("suspended").getValue(Boolean.class);
                        if (verificationValue != null && verificationValue){
                            ProjectToolkit.fadeIn(verified);
                        }else{
                            verified.setVisibility(GONE);
                        }
                        if (suspendValue != null && !suspendValue){
                            suspended.setVisibility(GONE);
                        }else{
                            ProjectToolkit.fadeIn(suspended);
                        }
                    }else{
                        Toast.makeText(itemView.getContext(), "Unable to recognize user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("firebase error", "request declined");
                }
            });
        }
    }


    private void suspendUser(String userId){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.child(userId).child("suspended").setValue(true);
    }
    private void resetUser(String userId){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.child(userId).child("suspended").setValue(false);
        userRef.child(userId).child("verified").setValue(false);
    }

    private void verifyUser(String userId){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.child(userId).child("verified").setValue(true);
    }

}
