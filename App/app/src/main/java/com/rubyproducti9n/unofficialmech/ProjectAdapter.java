package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.PhotosViewHolder>{
    private List<ProjectItem> items;
    private SharedPreferences sharedPreferences;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;
    Context mContext;

    @NonNull
    @Override
    public ProjectAdapter.PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        return new ProjectAdapter.PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectAdapter.PhotosViewHolder holder, int position) {

        ProjectItem item = items.get(position);
        if (position<=0){

        }else{

        }

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

        String projectId = item.getProjectId();
        String username = item.getUser_name();
        String div = item.getDiv();
        String imgUrl = item.getImgUrl();
        String caption = item.getCaption();
        String uploadTime = item.getUploadTime();
        Boolean patent = item.getPatent();
        String resources = item.getResources();


        holder.getUsername().setText(username);
        SpannableString spannableString = new SpannableString(caption);
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);
        holder.getPostCaption().setText(spannableString);
        holder.getPostCaption().setMovementMethod(LinkMovementMethod.getInstance());

        MaterialCardView id_container = holder.itemView.findViewById(R.id.id_container);
        ImageView profile_avatar = holder.itemView.findViewById(R.id.img_avtar);
        ImageView verifiedBadge = holder.itemView.findViewById(R.id.verifiedBadge);
        CircularProgressIndicator progress = holder.itemView.findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
        String userRole = preferences.getString("auth_userole", null);
        id_container.setVisibility(View.GONE);

//        MaterialCardView extraSlot = holder.itemView.findViewById(R.id.privacyManager);

        if (patent){
            holder.patent.setVisibility(View.VISIBLE);
        }else{
            holder.patent.setVisibility(View.GONE);
        }

        if (userRole != null && userRole.equals("Admin")){
            holder.getPostId().setText(projectId);
            id_container.setVisibility(View.VISIBLE);
//            extraSlot.setVisibility(View.GONE);
            holder.postLayout.setVisibility(View.VISIBLE);
        }else{
            id_container.setVisibility(View.GONE);
//            extraSlot.setVisibility(View.GONE);
            holder.postLayout.setVisibility(View.VISIBLE);
        }

        if (username != null){
            holder.getAvatar(username);
        }else{
            holder.getGender(username);
        }

        //holder.blockCheck(id);

        holder.mc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileViewsActivity.class);
                intent.putExtra("username", item.getUser_name());
                mContext.startActivity(intent);
            }
        });

        //holder.roleCheck(mContext, username, division, result);
        if(username.contains("Om Lokhande")){
            verifiedBadge.setVisibility(View.VISIBLE);
            holder.getTxt_tags().setText("Admin • " + result);
        }else if(username.contains("Aishwarya Kumbhar")){
            verifiedBadge.setVisibility(View.VISIBLE);
            holder.getTxt_tags().setText("Class Representative • " + result);
        }else if(username.contains("Unofficial Mech")){
            verifiedBadge.setVisibility(View.VISIBLE);
            holder.getTxt_tags().setText("Admin • " + result);

            if (userRole != null && userRole.equals("Admin")){
                holder.postLayout.setVisibility(View.VISIBLE);
            }else{
                holder.postLayout.setVisibility(View.GONE);
            }
        }else{
            verifiedBadge.setVisibility(View.INVISIBLE);
            profile_avatar.setImageResource(R.drawable.round_account_circle_24);
            if (div != null){
                holder.getTxt_tags().setText("From " + div + " division • " + result);
            }else{
                holder.getTxt_tags().setText(result);
            }
        }


        if (Objects.equals(imgUrl, "null") || imgUrl == null){
            holder.post.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
        }else{

            Picasso.get().load(imgUrl).error(R.drawable.post_error).into(holder.getItemImageView(), new Callback() {
                @Override
                public void onSuccess() {
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    //Toast.makeText(mc, "Error while loading memories, try again later", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.VISIBLE);
                }
            });
            holder.post.setVisibility(View.VISIBLE);

        }

        //Producing NullPointerException
//        if (resources!=null || resources.isEmpty()){
//            holder.resources.setEnabled(true);
//            holder.resources.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
//                    builder.setTitle("Resources")
//                            .setMessage(resources)
//                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                }
//                            });
//                    builder.show();
//                }
//            });
//        }else{
//            holder.resources.setEnabled(false);
//        }

        holder.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImageMagnifierActivity.class);
                intent.putExtra("link", imgUrl);
                intent.putExtra("activity", "project");
                mContext.startActivity(intent);
            }
        });


            holder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.share(mContext, username);
                }
            });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showPopupMenu(holder.menu, projectId);
            }
        });

    }
    public ProjectAdapter(Context context, List<ProjectItem> items){
        mContext = context;
        this.items = items;
        //this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public ProjectAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class PhotosViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView postLayout;
        private ImageView card_menu;
        private MaterialCardView mc;
        private TextView postId;
        private TextView username;
        private TextView division;
        private ImageView post;
        private TextView caption;
        private TextView date_time;
        private ImageView likeImageView;
        private ImageView menu;
        private MaterialCardView patent;
        private TextView visibility;
        boolean isLiked = false;

        private VideoView videoView;
        private MaterialButton volumeButton;

        private MaterialButton likeButton;
        private Map<String, Boolean> getLike;

        private MaterialButton resources, shareButton;
        boolean showAd = Math.random() < 0.75;



        public TextView getPostId(){return postId;}
        public TextView getUsername(){
            return username;
        }
        public ImageView getItemImageView() {
            return post;
        }
        public TextView getDivision(){
            return division;
        }

        public TextView getPostCaption(){
            return caption;
        }
        public VideoView getVideoView(){
            return videoView;
        }
        public MaterialButton getVolumeButton(){
            return volumeButton;
        }
        public MaterialCardView getPatent(){return patent;}


        public TextView getTxt_tags() {
            return date_time;
        }
        public ImageView getLikeImageView(){
            return likeImageView;
        }
        public ImageView getMenu(){
            return menu;
        }


        public PhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            postLayout = itemView.findViewById(R.id.cardView);
            //card_menu = itemView.findViewById(R.id.card_menu);
            username = itemView.findViewById(R.id.txtUsername);
            mc = itemView.findViewById(R.id.mc);

            postId = itemView.findViewById(R.id.postId);
            post = itemView.findViewById(R.id.img);
            caption = itemView.findViewById(R.id.postCaption);
            date_time = itemView.findViewById(R.id.txtTime);
            menu = itemView.findViewById(R.id.card_menu);

            resources = itemView.findViewById(R.id.resources);
            shareButton = itemView.findViewById(R.id.share);
            patent = itemView.findViewById(R.id.patentContainer);


            AdView adView = itemView.findViewById(R.id.adView);
            adView.setVisibility(View.GONE);
            Callbacks.getAdValue(null, new Callbacks.AdValue() {
                @Override
                public void onAdValue(boolean value) {
                    if (value){
                        adView.setVisibility(View.VISIBLE);
                        showAd = Math.random() < 0.99;
                        adView.setVisibility(showAd ? View.VISIBLE : View.GONE);
                        if (showAd) {
                            AdRequest adRequest = new AdRequest.Builder().build();
                            //adView.setAdUnitId(adUnitId);
                            adView.loadAd(adRequest);
                        }
                    }else{
                        adView.setVisibility(View.GONE);
                    }
                }
            });
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
        private void updateLikeButtonIcon(boolean isLiked){
            int iconRes = isLiked ? R.drawable.heart_outline : R.drawable.heart_active;
            likeButton.setIconResource(iconRes);
        }

        private void share(Context context, String username){
            String deepLink = "unofficial://unofficialmech.com";
            String link = "https://bit.ly/unofficialMech";
            String prj_title = "*New project got listed!*" + "\n\n";
            String prj_info = "About: " + caption.getText().toString() + "\n";
            String prj_resources = "Resources used: \n";
            String prj_appMark = "\n*_Unofficial Mech_*";
            String textToShare = prj_title +  prj_info + prj_resources + prj_appMark;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            //shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }

        public void bind(Post postItem){

            username.setText(postItem.getUserName());
            Picasso.get().load(postItem.getPostUrl()).into(post);
            caption.setText(postItem.getCaption());
            date_time.setText(postItem.getUploadTime());

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
                                    Toast.makeText(likeImageView.getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(runnable -> {
                                    Toast.makeText(likeImageView.getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                                });
                    }else{
                        Toast.makeText(likeImageView.getContext(), "Post not found", Toast.LENGTH_SHORT).show();
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
