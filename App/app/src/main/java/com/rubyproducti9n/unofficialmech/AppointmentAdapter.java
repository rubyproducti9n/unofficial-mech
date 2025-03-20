package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.PhotosViewHolder>{
    private List<AppointmentItem> items;
    private SharedPreferences sharedPreferences;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;
    Context mContext;

    @NonNull
    @Override
    public AppointmentAdapter.PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentAdapter.PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.PhotosViewHolder holder, int position) {

        AppointmentItem item = items.get(position);

        String appointmentId = item.getAppointmentId();
        String facultyId = item.getFacultyId();
        String username = item.getUser_name();
        String div = item.getDiv();
        String caption = item.getCaption();
        String uploadTime = item.getUploadTime();
        String appointmentSlot = item.getAppointmentTime();
        boolean status = item.isStatus();

        //Day Ago System
        String dateString = item.getUploadTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //Calculates days ago from class DayAgoSystem
        String result = DayAgoSystem.getDayAgo(date);
// Get today's date at 00:00:00 hours
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date todayDate = today.getTime();

        // Check if upload time is within the last 24 hours
        long delta = todayDate.getTime() - date.getTime();
        if(delta >= 0 && delta < 24 * 60 * 60 * 1000){
            holder.getUsername().setText(item.getUser_name());
            //holder.getCaption().setText(item.getCaption());

            //MaterialCardView id_container = holder.itemView.findViewById(R.id.id_container);
            //MaterialButton likeButton = holder.itemView.findViewById(R.id.likes);
//        Activity activity = (Activity) context;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
            String userRole = preferences.getString("auth_userole", null);
            //id_container.setVisibility(View.GONE);


            if (userRole != null && userRole.equals("Admin")){
                holder.getAppointmentId().setText(appointmentId);
                //id_container.setVisibility(View.VISIBLE);
                holder.appointmentLayout.setVisibility(View.VISIBLE);
            }else{
                //id_container.setVisibility(View.GONE);
                holder.appointmentLayout.setVisibility(View.VISIBLE);
            }

//        if (username != null){
//            holder.getAvatar(username);
//        }else{
//            holder.getGender(username);
//        }

            //holder.roleCheck(mContext, username, division, result);
            if(username!=null && username.contains("Om Lokhande")){
                holder.getDivision().setText("Admin • " + result);
            }else if(username!=null && username.contains("Aishwarya Kumbhar")){
                holder.getDivision().setText("Class Representative • " + result);
            }else if(username!=null && username.contains("Unofficial Mech")){
                holder.getDivision().setText("Admin • " + result);

                if (userRole != null && userRole.equals("Admin")){
                    holder.appointmentLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.appointmentLayout.setVisibility(View.GONE);
                }
            }else{
                if (div != null){
                    holder.getDivision().setText("From " + div + " division • " + result);
                }else{
                    holder.getDivision().setText(result);
                }
            }
        }else{
            holder.appointmentLayout.setVisibility(View.GONE);
        }



    }
    public AppointmentAdapter(Context context, List<AppointmentItem> items){
        mContext = context;
        this.items = items;
        //this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public AppointmentAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class PhotosViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView appointmentLayout;
        private TextView appointmentId;
        private TextView username;
        private TextView division;
        private TextView caption;
        private TextView date_time;
        private MaterialButton call, whatsapp, mail, approveBtn;
        private Chip appointmentSlot;


        public MaterialCardView getAppointmentLayout() {
            return appointmentLayout;
        }

        public TextView getAppointmentId() {
            return appointmentId;
        }

        public TextView getUsername() {
            return username;
        }

        public TextView getDivision() {
            return division;
        }

        public TextView getCaption() {
            return caption;
        }

        public TextView getDate_time() {
            return date_time;
        }

        public MaterialButton getCall() {
            return call;
        }

        public MaterialButton getWhatsapp() {
            return whatsapp;
        }

        public MaterialButton getMail() {
            return mail;
        }

        public MaterialButton getApproveBtn() {
            return approveBtn;
        }

        public Chip getAppointmentSlot() {
            return appointmentSlot;
        }

        public PhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentLayout = itemView.findViewById(R.id.cardView);
            //card_menu = itemView.findViewById(R.id.card_menu);
            username = itemView.findViewById(R.id.username);

            //appointmentId = itemView.findViewById(R.id.postId);
            //caption = itemView.findViewById(R.id.postCaption);
            division = itemView.findViewById(R.id.div);

            appointmentSlot = itemView.findViewById(R.id.appointment_chip);

            call = itemView.findViewById(R.id.call_chip);
            whatsapp = itemView.findViewById(R.id.wa_chip);
            mail = itemView.findViewById(R.id.mail_chip);
            approveBtn = itemView.findViewById(R.id.approve_chip);


        }

        private void blockCheck(String noticeId){
            TextView caption = itemView.findViewById(R.id.postCaption);
            ImageView post = itemView.findViewById(R.id.img);
            ConstraintLayout socialBtn = itemView.findViewById(R.id.like_comment_share);
            TextView visibility = itemView.findViewById(R.id.visibility);
            MaterialCardView blockView = itemView.findViewById(R.id.taken_down_container);

            DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("posts/");
            noticeRef.child(noticeId).child("blocked").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Boolean value = snapshot.getValue(Boolean.class);
                        if (value){
                            caption.setVisibility(View.GONE);
                            post.setVisibility(View.GONE);
                            socialBtn.setVisibility(View.GONE);
                            visibility.setVisibility(View.GONE);
                            blockView.setVisibility(View.VISIBLE);
                        }else{
                            blockView.setVisibility(View.GONE);
                        }
                    }else{
                        blockView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getUsername().getContext(), "Server not responding", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void share(Context context, String username){
            String deepLink = "unofficial://unofficialmech.com";
            String link = "https://bit.ly/unofficialMech";
            String prj_title = "Project Title: " + username;
            String prj_info = "About: " + caption;
            String prj_resources = "Resources used: ";
            String prf_appMark = "*_Unofficial Mech_*";
            String textToShare = "Check out new post by " + username + " on our community app \n \n Download the app now: " + link;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            //shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }


        private void getAvatar(String userName){
            ImageView profile_avatar = itemView.findViewById(R.id.img_avtar);

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
            ImageView profile_avatar = itemView.findViewById(R.id.img_avtar);

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

        private void roleCheck(Context context, String userName, String userDiv, String result){
            ImageView verifiedBadge = itemView.findViewById(R.id.verifiedBadge);
            MaterialCardView postLayout = itemView.findViewById(R.id.cardView);
            TextView date_time = itemView.findViewById(R.id.txtTime);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String userRole = preferences.getString("auth_userole", null);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            mDatabase.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            String username = userSnapshot.child("userName").getValue(String.class);
                            String roleCheck = userSnapshot.child("role").getValue(String.class);
                            Boolean verified = userSnapshot.child("verified").getValue(Boolean.class);
                            if (roleCheck != null){
                                if (roleCheck.equals("Admin")){
                                    if (username != null){
                                    if (Objects.equals(username, "Aishwarya Kumbhar")){
                                        date_time.setText("Class Representative • " + result);
                                    }
                                    if (username == "Unofficial Mech"){
                                        if (userRole == "Admin"){
                                            postLayout.setVisibility(View.VISIBLE);
                                        }else{
                                            postLayout.setVisibility(View.GONE);
                                        }
                                    }
                                    }else{
                                        date_time.setText(result);
                                        verifiedBadge.setVisibility(View.GONE);
                                    }
                                    verifiedBadge.setVisibility(View.VISIBLE);
                                    date_time.setText("Admin • " + result);
                                }else{
                                    verifiedBadge.setVisibility(View.GONE);
                                    date_time.setText(result);
                                    //profile_avatar.setImageResource(R.drawable.round_account_circle_24);
                                    if (userDiv != null){
                                        date_time.setText("From " + userDiv + " division • " + result);
                                    }else{
                                        date_time.setText(result);
                                    }
                                }
                            }else{
                                date_time.setText(result);
                                verifiedBadge.setVisibility(View.GONE);
                            }
                            if (Boolean.TRUE.equals(verified)){
                                verifiedBadge.setVisibility(View.VISIBLE);
                            }
                        }
                    }else{
                        Log.d("User's role not found", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Request declined by Firebase", "error");
                }
            });
        }

        public void showPopupMenu(View view, String postId){
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.post_menu);

            checkForAdmin(view.getContext(), popupMenu, postId);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.action_report:
                            Toast.makeText(view.getContext(), "Reported!", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.action_delete:
                            deletePost(postId);
                            return true;
                        case R.id.action_block:
                            getBlockedValue(postId);
                            return true;
                        default:
                    }
                    return false;
                }
            });

            popupMenu.show();
        }

        private void checkForAdmin(Context context, PopupMenu popupMenu, String postId){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String role = preferences.getString("auth_userole", null);

            if (role.equals("Admin")){
                Menu menu = popupMenu.getMenu();
                MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
                MenuItem blockMenuItem = menu.findItem(R.id.action_block);
                deleteMenuItem.setVisible(true);
                blockMenuItem.setVisible(true);
            }else{
                Menu menu = popupMenu.getMenu();
                MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
                MenuItem blockMenuItem = menu.findItem(R.id.action_block);
                deleteMenuItem.setVisible(false);
                blockMenuItem.setVisible(false);
            }

        }

        private void deletePost(String postId){
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts");
            postRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = snapshot.child("postId").getValue(String.class);
                    if (userId != null){
                        postRef.child(postId).removeValue()
                                .addOnSuccessListener(runnable -> {
                                    Toast.makeText(itemView.getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(runnable -> {
                                    Toast.makeText(itemView.getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                                });
                    }else{
                        Toast.makeText(itemView.getContext(), "Post not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void getBlockedValue(String postId){
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts/");
            postRef.child(postId).child("blocked").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Boolean blockedValue = snapshot.getValue(Boolean.class);
                        if (blockedValue){
                            blockNotice(postId, false);
                        }else{
                            blockNotice(postId, true);
                        }
                    }
                    else{
                        Toast.makeText(itemView.getContext(), "Value generated, try again", Toast.LENGTH_SHORT).show();
                        postRef.child(postId).child("blocked").setValue(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(itemView.getContext(), "Server not responding", Toast.LENGTH_SHORT).show();
                }
            });
        }
        private void blockNotice(String postId, Boolean blockedValue){
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts/");
            postRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = snapshot.child("postId").getValue(String.class);
                    if (userId != null){
                        postRef.child(postId).child("blocked").setValue(blockedValue);
                    }else{
                        Toast.makeText(username.getContext(), "Notice not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private boolean isVideo(String url){
            String lowerCaseUrl = url.toLowerCase();

            return lowerCaseUrl.endsWith(".mp4");
        }


    }
}
