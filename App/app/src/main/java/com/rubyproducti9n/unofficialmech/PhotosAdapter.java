package com.rubyproducti9n.unofficialmech;

import static android.content.ContentValues.TAG;
import static com.rubyproducti9n.unofficialmech.MainActivity.triggerAnim;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>{

    private List<Post> items;
    private SharedPreferences sharedPreferences;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;
    Context mContext;

    @NonNull
    @Override
    public PhotosAdapter.PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
        return new PhotosAdapter.PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosAdapter.PhotosViewHolder holder, int position) {

        Post item = items.get(position);


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



        SpannableString spannableString = new SpannableString(item.getCaption());
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);
        holder.getPostCaption().setText(spannableString);
        holder.getPostCaption().setMovementMethod(LinkMovementMethod.getInstance());

        MaterialCardView id_container = holder.itemView.findViewById(R.id.id_container);
        ImageView profile_avatar = holder.itemView.findViewById(R.id.img_avtar);
        ImageView verifiedBadge = holder.itemView.findViewById(R.id.verifiedBadge);
        CircularProgressIndicator progress = holder.itemView.findViewById(R.id.progressBar);
        MaterialButton likeButton = holder.itemView.findViewById(R.id.enroll);
//        Activity activity = (Activity) context;
        progress.setVisibility(View.VISIBLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
        String userRole = preferences.getString("auth_userole", null);
        id_container.setVisibility(View.GONE);

        MaterialCardView extraSlot = holder.itemView.findViewById(R.id.privacyManager);
        ImageView img = holder.itemView.findViewById(R.id.ic);
        TextView title = holder.itemView.findViewById(R.id.title);
        TextView txt = holder.itemView.findViewById(R.id.txt);
        MaterialButton btn = holder.itemView.findViewById(R.id.btn);


        if (userRole != null && userRole.equals("Admin")){
            holder.getPostId().setText(item.getPostId());
            id_container.setVisibility(View.VISIBLE);
            extraSlot.setVisibility(View.GONE);
            holder.postLayout.setVisibility(View.VISIBLE);

        }
//        else if(Objects.equals(userRole, "Faculty")){
//            if (position == 0){
//                holder.postLayout.setVisibility(View.GONE);
//
//                extraSlot.setVisibility(View.VISIBLE);
//                ProjectToolkit.fadeIn(extraSlot);
//                img.setImageResource(R.drawable.round_policy_24);
//                title.setText("Privacy Manager");
//                txt.setText("Feed was blocked by Privacy Manager");
//                btn.setText("Privacy Policy");
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
//                                builder.setTitle("Privacy Policy");
//                                builder.setMessage(R.string.privacyPolicy);
//                                builder.setIcon(R.drawable.round_policy_24);
//                                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//                                builder.show();
//                    }
//                });
//            }else{
//                holder.postLayout.setVisibility(View.GONE);
//            }
//        }
        else{
            id_container.setVisibility(View.GONE);
            extraSlot.setVisibility(View.GONE);
            holder.postLayout.setVisibility(View.VISIBLE);
        }
//        else if(Objects.equals(userRole, "Student")){
//            if (position == 0){
//                holder.postLayout.setVisibility(View.VISIBLE);
//                extraSlot.setVisibility(View.VISIBLE);
//                img.setImageResource(R.drawable.round_policy_24);
//                title.setText("Sponsored");
//                txt.setText("Buy a coffee at a minimum discount of 25% only at DrinCup, Kopargaon");
//                btn.setText("Locate");
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
//                        builder.setTitle("Privacy Policy");
//                        builder.setMessage(R.string.privacyPolicy);
//                        builder.setIcon(R.drawable.round_policy_24);
//                        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        builder.show();
//                    }
//                });
//            }
//            if (position ==3){
//                holder.postLayout.setVisibility(View.VISIBLE);
//                extraSlot.setVisibility(View.VISIBLE);
//                img.setImageResource(R.drawable.round_policy_24);
//                title.setText("Sponsored");
//                txt.setText("Watch Saalar in KCineplex the only multiplex in Kopargaon");
//                btn.setText("Book ticket");
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
//                        builder.setTitle("Privacy Policy");
//                        builder.setMessage(R.string.privacyPolicy);
//                        builder.setIcon(R.drawable.round_policy_24);
//                        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        builder.show();
//                    }
//                });
//            }
//            else{
//                extraSlot.setVisibility(View.GONE);
//            }
//        }

        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(500);
        animation.setStartOffset(position * 50L);
        holder.itemView.startAnimation(animation);
        if (item.getUserName() != null){
            holder.getAvatar(item.getUserName());
        }else{
            holder.getGender(item.getUserName());
        }

        String id = item.getPostId();
        String username = item.getUserName();
        String postUrl = item.getPostUrl();
        String caption = item.getCaption();
        String postTime = item.getUploadTime();
//        String visibility = item.getStateVisibility();

        //holder.blockCheck(id);



        if (item.getUid() != null){
            holder.fetchUserDetails(item.getUid(), new PhotosViewHolder.UserDetailCallback() {
                @Override
                public void onCallback(CreateUser userDetail) {

                    if (userDetail.getAvatar()!=null){
                        Picasso.get().load(userDetail.getAvatar()).into(holder.profile_avatar);
                    }else{
                        holder.profile_avatar.setImageResource(R.drawable.round_account_circle_24);
                    }

                    holder.getUsername().setText(userDetail.getFirstName() + " " + userDetail.getLastName());
                    if (userDetail.getDept() != null){
                        holder.getTxt_tags().setText(userDetail.getDept() + " • " + result);
                    }else{
                        holder.getTxt_tags().setText(result);
                    }

                    if (item.getStateVisibility()!=null && item.getStateVisibility().equals("private")){
                        holder.getUsername().setText("Anonymous User");
                        holder.visibility.setCompoundDrawables(mContext.getDrawable(R.drawable.baseline_lock_24), null,null, null);
                        holder.getTxt_tags().setText(result);
                        Picasso.get().load("https://e0.pxfuel.com/wallpapers/862/171/desktop-wallpaper-having-a-reason-to-live-in-this-cosmos-take-a-look-hitman-suit-thumbnail.jpg").into(holder.profile_avatar);
                        holder.verifiedBadge.setVisibility(View.GONE);
                        holder.mc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(mContext, "User is Anonymous", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        if (userDetail.getRole() != null &&
                                (userDetail.getRole().equals("Admin") ||
                                        userDetail.getRole().equals("Creator") ||
                                        userDetail.getRole().equals("HOD") ||
                                        userDetail.getRole().equals("Faculty"))) {

                            holder.getTxt_tags().setText(userDetail.getRole() + " • " + result);

                            // Check for "Admin" role to show badge
                            if (userDetail.getRole().equals("Admin")) {
                                holder.verifiedBadge.setVisibility(View.VISIBLE);
                            } else {
                                holder.verifiedBadge.setVisibility(View.GONE);
                            }

                        } else {
                            holder.verifiedBadge.setVisibility(View.GONE);
                        }
                        holder.mc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, ProfileViewsActivity.class);
                                intent.putExtra("username", userDetail.getFirstName() + " " + userDetail.getLastName());
                                mContext.startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
            holder.getTxt_tags().setText(result);
        }
        else{
            holder.getUsername().setText(item.getUserName());
            holder.getTxt_tags().setText(result);
            if(item.getUserName().contains("Om Lokhande")){
                verifiedBadge.setVisibility(View.VISIBLE);
                holder.getTxt_tags().setText("Admin • " + result);
            }else if(item.getUserName().contains("Aishwarya Kumbhar")){
                verifiedBadge.setVisibility(View.VISIBLE);
                holder.getTxt_tags().setText("Class Representative • " + result);
            }else if(item.getUserName().contains("Unofficial Mech")){
                verifiedBadge.setVisibility(View.VISIBLE);
                holder.getTxt_tags().setText("Admin • " + result);

                if (userRole != null && userRole.equals("Admin")){
                    holder.postLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.postLayout.setVisibility(View.GONE);
                }
            }else if (item.getUserName().contains("sanjivani_memes_katta")){
                verifiedBadge.setVisibility(View.VISIBLE);
                verifiedBadge.setColorFilter(Color.parseColor("#FFBF00"), PorterDuff.Mode.SRC_IN);
                holder.getTxt_tags().setText("Sponsored • " + result);
                Picasso.get().load("https://instagram.fpnq13-3.fna.fbcdn.net/v/t51.2885-19/310919523_1084197302276804_7199566658537392364_n.jpg?stp=dst-jpg_s150x150&_nc_ht=instagram.fpnq13-3.fna.fbcdn.net&_nc_cat=108&_nc_ohc=oavYnXaSwcIQ7kNvgEJ82r0&edm=AEhyXUkBAAAA&ccb=7-5&oh=00_AYDBsMJD4wh4_B0rkDjnw_jxFOTLndbe9SS46IZWzgMuyA&oe=66C816B2&_nc_sid=8f1549").into(holder.profile_avatar);
            }else if (item.getUserName().contains("inside.sanjivani")){
                verifiedBadge.setVisibility(View.VISIBLE);
                verifiedBadge.setColorFilter(Color.parseColor("#FFBF00"), PorterDuff.Mode.SRC_IN);
                holder.getTxt_tags().setText("Sponsored • " + result);
                Picasso.get().load("https://instagram.fpnq13-3.fna.fbcdn.net/v/t51.2885-19/310919523_1084197302276804_7199566658537392364_n.jpg?stp=dst-jpg_s150x150&_nc_ht=instagram.fpnq13-3.fna.fbcdn.net&_nc_cat=108&_nc_ohc=oavYnXaSwcIQ7kNvgEJ82r0&edm=AEhyXUkBAAAA&ccb=7-5&oh=00_AYDBsMJD4wh4_B0rkDjnw_jxFOTLndbe9SS46IZWzgMuyA&oe=66C816B2&_nc_sid=8f1549").into(holder.profile_avatar);
            }else{
                verifiedBadge.setVisibility(View.INVISIBLE);
                profile_avatar.setImageResource(R.drawable.round_account_circle_24);
                profile_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProfileViewsActivity.class);
                        intent.putExtra("username", item.getUserName());
                        mContext.startActivity(intent);
                    }
                });

            }
        }





        if (Objects.equals(item.getPostUrl(), "null") || item.getPostUrl() == null){
            holder.post.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
            holder.volumeButton.setVisibility(View.GONE);
        }else{
            if (holder.isVideo(item.getPostUrl())){
                holder.videoView.setVideoPath(item.getPostUrl());
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setVolume(1f, 1f);
                        mediaPlayer.start();

                        if (item.isMuted()){
                            mediaPlayer.setVolume(0f, 0f);
                        }
                    }
                });


                holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                        if (i == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                            progress.setVisibility(View.VISIBLE);
                        } else if (i == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            progress.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });

                holder.videoView.setOnErrorListener((mediaPlayer, i, i1) -> {
                    Log.d("Playback error", "Error occurred video playback: " + i + ", " + i1);
                    return true;
                });

                holder.volumeButton.setOnClickListener(view -> {
                    item.toggleMute();
                    notifyDataSetChanged();
                });
                holder.videoView.requestFocus();
                holder.videoView.requestLayout();
                holder.videoView.setVideoPath(item.getPostUrl());

                holder.videoView.setVisibility(View.VISIBLE);
                holder.volumeButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }else{
                holder.videoView.setVisibility(View.GONE);
                holder.volumeButton.setVisibility(View.GONE);

                if (holder.getFileType(item.getPostUrl()).equals("image")){
                    holder.viewPdf.setVisibility(View.GONE);
                    Picasso.get().load(item.getPostUrl()).error(R.drawable.post_error).into(holder.getItemImageView(), new Callback() {
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
                }else if (holder.getFileType(item.getPostUrl()).equals("pdf")){
                    holder.post.setVisibility(View.GONE);
                    holder.viewPdf.setVisibility(View.GONE);
                    holder.viewPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.openFileInDefaultApp(mContext, item.getPostUrl());
                            holder.checkIfPdfAndRetrieveDetails(item.getPostUrl());
                        }
                    });
                }else{
                    holder.post.setVisibility(View.GONE);
                    holder.viewPdf.setVisibility(View.GONE);
                }



                ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(mContext,
                        new ScaleGestureDetector.OnScaleGestureListener() {
                            @Override
                            public boolean onScale(@NonNull ScaleGestureDetector scaleGestureDetector) {
                                float scaleFactor  = scaleGestureDetector.getScaleFactor();
                                holder.post.setScaleX(holder.post.getScaleX() * scaleFactor);
                                holder.post.setScaleY(holder.post.getScaleY() * scaleFactor);
                                return true;
                            }

                            @Override
                            public boolean onScaleBegin(@NonNull ScaleGestureDetector scaleGestureDetector) {
                                return false;
                            }

                            @Override
                            public void onScaleEnd(@NonNull ScaleGestureDetector scaleGestureDetector) {

                            }
                        });
                holder.post.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        scaleGestureDetector.onTouchEvent(motionEvent);
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                            view.performClick();
                        }
                        return true;
                    }
                });

                holder.post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ImageMagnifierActivity.class);
                        intent.putExtra("link", item.getPostUrl());
                        intent.putExtra("activity", "post");
                        mContext.startActivity(intent);
                    }
                });

            }
        }


//        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Likes", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences1.edit();
//        editor.putBoolean("item_" + position, false);
//        editor.apply();


//        int likeCount = item.getLikeCount();
//        likeButton.setText(String.valueOf(likeCount));
//
//            likeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts")
//                            .child(item.getPostId());
//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//                    String userId = preferences.getString("auth_userId", null);
//
//                    boolean isLiked = item.isLikedByCurrentUser(userId);
//                    boolean newLikeState = !isLiked;
//
//                    int likeCount = item.getLikeCount();
//                    if (newLikeState){
//                        likeCount++;
//                    }else{
//                        likeCount--;
//                    }
//                    item.updateLikes(userId, newLikeState);
//                    likeButton.setText(String.valueOf(likeCount));
//
//                    holder.updateLikeButtonIcon(newLikeState);
//                    likeButton.setText(String.valueOf(likeCount));
//
//                    postRef.setValue(item);
//                }
//            });

        //holder.isLiked(item.getPostId());
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(mContext);
        String uid = p.getString("auth_userId", null);
        if (uid!=null){
            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.pushLike(item.getUid(), item.getPostId());
                }
            });
        }else{
            holder.likeBtn.setEnabled(false);
        }


            holder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.share(mContext, item.getUserName());
                }
            });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showPopupMenu(holder.menu, item.getPostId());
            }
        });

        //holder.bind(item);
        //holder.getTxt_tags().setText(result);
    }
    public PhotosAdapter(Context context, List<Post> items){
        mContext = context;
        this.items = items;
        //this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public PhotosAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class PhotosViewHolder extends RecyclerView.ViewHolder {
        private ImageView profile_avatar;
        private MaterialCardView postLayout;
        private ImageView card_menu;
        private TextView postId, pdfName, pdfSize;
        private TextView username;
        private ImageView verifiedBadge;
        private TextView division;
        private ImageView post;
        private TextView caption;
        private TextView date_time;
        private ImageView likeImageView;
        private ImageView menu;
        private TextView visibility;
        boolean isLiked = false;
        private MaterialButton viewPdf;

        private VideoView videoView;
        private MaterialButton volumeButton;

        private Map<String, Boolean> getLike;

        private MaterialButton shareButton;
        boolean showAd = Math.random() < 0.75;
        private MaterialCardView mc;
        MaterialButton likeBtn, commentBtn;
        int likeCount = 0;
        int commentCount = 0;



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

            pdfName = itemView.findViewById(R.id.pdfName);
            pdfSize = itemView.findViewById(R.id.pdfSize);
            viewPdf = itemView.findViewById(R.id.viewBtn);

            profile_avatar = itemView.findViewById(R.id.img_avtar);
            postId = itemView.findViewById(R.id.postId);
            verifiedBadge = itemView.findViewById(R.id.verifiedBadge);
            post = itemView.findViewById(R.id.img);
            caption = itemView.findViewById(R.id.postCaption);
            date_time = itemView.findViewById(R.id.txtTime);
            likeImageView = itemView.findViewById(R.id.img_like);
            menu = itemView.findViewById(R.id.card_menu);
            visibility = itemView.findViewById(R.id.visibility);
            videoView = itemView.findViewById(R.id.videoView);
            volumeButton = itemView.findViewById(R.id.volumeButton);

            likeBtn = itemView.findViewById(R.id.likeBtn);
            likeBtn.setVisibility(View.GONE);
            shareButton = itemView.findViewById(R.id.share);

            String[] adUnitIds = {"ca-app-pub-5180621516690353/6163652319", "ca-app-pub-5180621516690353/6323434418", "ca-app-pub-5180621516690353/8674265587", "ca-app-pub-5180621516690353/2471873213", "ca-app-pub-5180621516690353/7644254948"};
            int randomIndex = new Random().nextInt(adUnitIds.length);
            String adUnitId = adUnitIds[randomIndex];

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
        public interface UserDetailCallback {
            void onCallback(CreateUser userDetail);
            void onFailure(String errorMessage);
        }

        private void isLiked(String postId){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts/" + postId + "/Likes/");
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getUsername().getContext());
            String uid = p.getString("auth_userId", null);
            pullLike(postId, uid);
            ref.orderByChild(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (uid!=null){
                        if (snapshot.exists() && snapshot.hasChild(uid)){
                            likeBtn.setIcon(itemView.getContext().getDrawable(R.drawable.heart_active));
                            likeBtn.setText(String.valueOf(likeCount));
                        }else{
                            pushLike(uid, postId);
                        }
                    }else{
                        //User not logged in
                        likeBtn.setEnabled(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    likeBtn.setEnabled(false);
                    likeBtn.setText("Like");
                }
            });
        }
        private void pullLike(String postId, String userId) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts/" + postId + "/Likes/");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int likeCount = 0; // Reset the count before starting the loop
                    isLiked = false; // Track if the current user has liked the post

                    if (snapshot.exists()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Boolean liked = snap.getValue(Boolean.class); // Get the boolean value of the like
                            if (liked != null && liked) {
                                likeCount++; // Increment the count only if the value is true (liked)
                            }

                            if (snap.getKey().equals(userId) && liked) {
                                isLiked = true; // Check if the current user has liked the post
                            }
                        }

                        // Update the like button text based on the like count
                        if (likeCount > 0) {
                            likeBtn.setText(String.valueOf(likeCount));
                        } else {
                            likeBtn.setText("Like");
                        }

                        // Update the button style to indicate if the user has liked the post
                        if (isLiked) {
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_active, 0, 0, 0); // Liked icon
                        } else {
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_outline, 0, 0, 0); // Default like icon
                        }
                    } else {
                        likeBtn.setText("Like"); // Default text if no likes are found
                        likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_outline, 0, 0, 0); // Default like icon
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    likeBtn.setText("Like"); // Default text in case of error
                }
            });
        }

        private void pushLike(String uid, String postId){
            if (uid != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts/" + postId + "/Likes/");
                if (isLiked){
                    //Dislike feature
                    //ref.child(uid).removeValue();
                }else{
                    ref.child(uid).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        triggerAnim();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getUsername().getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                }


            }

        }

        public void fetchUserDetails(String userId, final UserDetailCallback callback) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
            db.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String id = snapshot.child("userId").getValue(String.class);
                        String avatar = snapshot.child("avatar").getValue(String.class);
                        String first = snapshot.child("firstName").getValue(String.class);
                        String last = snapshot.child("lastName").getValue(String.class);
                        String dept = snapshot.child("dept").getValue(String.class);
                        String officialEmail = snapshot.child("clgEmail").getValue(String.class);
                        String personalEmail = snapshot.child("personalEmail").getValue(String.class);
                        String prn = snapshot.child("prn").getValue(String.class);
                        String contact = snapshot.child("contact").getValue(String.class);
                        String division = snapshot.child("div").getValue(String.class);
                        String gender = snapshot.child("gender").getValue(String.class);
                        String roll = snapshot.child("rollNo").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        String mNum = snapshot.child("contact").getValue(String.class);
                        Boolean suspended = snapshot.child("suspended").getValue(Boolean.class);
                        String paymentDate = snapshot.child("lastPaymentDate").getValue(String.class);
                        String dateCreated = snapshot.child("dateCreated").getValue(String.class);
                        callback.onCallback(new CreateUser(id,
                                avatar,
                                first,
                                last,
                                officialEmail,
                                gender,
                                prn,
                                roll,
                                division,
                                personalEmail,
                                null,
                                role,
                                mNum,
                                null,
                                Boolean.FALSE.equals(suspended),
                                dateCreated,
                                paymentDate,
                                dept));
                    }else {
                        return;
//                        Toast.makeText(caption.getContext(), "User Not Found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure("Connection denied");
                }
            });
        }
        private String getFileType(String urlString) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlString);

            storageReference.getMetadata().addOnSuccessListener(metadata -> {
                String mimeType = metadata.getContentType();  // Get MIME type
                Log.d(TAG, "MIME Type: " + mimeType);

                if (mimeType != null) {
                    if (mimeType.startsWith("image/")) {
                        Log.d(TAG, "File Type: image");
                    } else if ("application/pdf".equals(mimeType)) {
                        Log.d(TAG, "File Type: pdf");
                        pdfName.setText(metadata.getName());
                    } else if ("application/msword".equals(mimeType) ||
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)) {
                        Log.d(TAG, "File Type: word");
                    } else if ("application/vnd.ms-excel".equals(mimeType) ||
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimeType)) {
                        Log.d(TAG, "File Type: excel");
                    } else {
                        Log.d(TAG, "File Type: unknown");
                    }
                } else {
                    Log.d(TAG, "Could not retrieve MIME Type");
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get metadata: ", e);
            });

            return "Checking...";
        }
//        private String getFileType(String urlString) {
//            if (urlString.endsWith(".jpg") || urlString.endsWith(".jpeg") || urlString.endsWith(".png") || urlString.endsWith(".gif")) {
//                return "image";
//            } else if (urlString.endsWith(".pdf")) {
//                return "pdf";
//            } else if (urlString.endsWith(".doc") || urlString.endsWith(".docx")) {
//                return "word";
//            } else if (urlString.endsWith(".xls") || urlString.endsWith(".xlsx")) {
//                return "excel";
//            } else {
//                return "unknown";
//            }
//        }
        private void openFileInDefaultApp(Context context, String urlString) {
            // Get the file type
            String fileType = getFileType(urlString);

            // Create an Intent to open the file
            Intent intent  = new Intent(Intent.ACTION_VIEW).setPackage(context.getPackageName());

            // Set data and type based on the file type
            if (fileType.equals("image")) {
                intent.setDataAndType(Uri.parse(urlString), "image/*");
            } else if (fileType.equals("pdf")) {
                intent.setDataAndType(Uri.parse(urlString), "application/pdf");
            } else if (fileType.equals("word")) {
                intent.setDataAndType(Uri.parse(urlString), "application/msword");
            } else if (fileType.equals("excel")) {
                intent.setDataAndType(Uri.parse(urlString), "application/vnd.ms-excel");
            } else {
                // If unknown, just open as a general file
                intent.setDataAndType(Uri.parse(urlString), "*/*");
            }

            // Grant permission to other apps to read this Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Try to start the intent, handle if no app is available
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // No app found to open this file type
                Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_SHORT).show();
            }
        }
        private void checkIfPdfAndRetrieveDetails(String fileUrl) {
            // Create a StorageReference from the URL
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);

            // Get metadata of the file
            storageReference.getMetadata().addOnSuccessListener(metadata -> {
                String mimeType = metadata.getContentType();  // Get MIME type
                Log.d(TAG, "MIME Type: " + mimeType);

                // Check if the file is a PDF
                if ("application/pdf".equals(mimeType)) {
                    String pName = metadata.getName();
                    long pSize = metadata.getSizeBytes(); // Size in bytes

                    Log.d(TAG, "PDF Name: " + pdfName);
                    Log.d(TAG, "PDF Size: " + pdfSize + " bytes");


                    pdfName.setText(pName);
                    pdfSize.setText((int) pSize);


                    // Now download the file temporarily to get the number of pages
                    try {
                        File localFile = File.createTempFile("tempPdf", ".pdf");
                        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                            int totalPages = getPdfPageCount(localFile);
                            Log.d(TAG, "Total Pages: " + totalPages);
                        }).addOnFailureListener(e -> Log.e(TAG, "Failed to download PDF: ", e));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "The file is not a PDF.");
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Failed to get metadata: ", e));
        }
        private int getPdfPageCount(File pdfFile) {
            int pageCount = 0;
            try {
                ParcelFileDescriptor fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer renderer = new PdfRenderer(fd);
                pageCount = renderer.getPageCount();
                renderer.close();
                fd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pageCount;
        }


        private boolean getAdRate(){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            String userId = preferences.getString("auth_userId", null);
            if (userId!=null) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                String id = snapshot.getValue(String.class);
                                Integer plan = dataSnapshot.child("plan").getValue(Integer.class);
                                if (plan!=null){
                                    if (plan==0){
                                        showAd = Math.random() < 0.99;
                                    }
                                    if (plan==1){
                                        showAd = Math.random() < 0.75;
                                    }
                                    if (plan==2){
                                        showAd = Math.random() < 0.10;
                                    }
                                }else{
                                    showAd = Math.random() < 0.99;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showAd = Math.random() < 0.99;
                    }
                });

            }else{
                showAd = Math.random() < 0.99;
            }
            return showAd;
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
            String textToShare = "Check out new post by " + username + " on our community app \n \n Download the app now: " + link;

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
