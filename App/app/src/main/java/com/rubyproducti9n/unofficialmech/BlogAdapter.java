package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlogAdapter  extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder>{


    private List<CreateNotice.Notice> items;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;
    Context mContext;
    boolean showAd = Math.random() < 0.99;

    private DatabaseReference databaseReference;
    public void FirebaseAdapter(Context context){
    }

    @NonNull
    @Override
    public BlogAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item, parent, false);
        return new BlogAdapter.BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.BlogViewHolder holder, int position) {


//        BlogItem item = items.get(position);
        CreateNotice.Notice item = items.get(position);


        TextView txt = holder.itemView.findViewById(R.id.txt);
        MaterialCardView cardView = holder.itemView.findViewById(R.id.cardView);

        if(item == null || item.equals("")){
            txt.setText("Empty");
            txt.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
        }
        else{
            txt.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);

            //Day Ago System
            String dateString = item.getUploadTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                date = format.parse(dateString);


                AdView adView = holder.itemView.findViewById(R.id.adview);
                adView.setVisibility(View.VISIBLE);

                long fourWeeksInMilliseconds = 4L * 7 * 24 * 60 * 60 * 1000;
                long currentTime = System.currentTimeMillis();
                if (currentTime - date.getTime() > fourWeeksInMilliseconds) {
                    holder.setExpired();
                    adView.setVisibility(View.GONE);
                }else{
                    Callbacks.getAdValue(null, new Callbacks.AdValue() {
                    @Override
                    public void onAdValue(boolean value) {
                        if (value){
                            adView.setVisibility(View.VISIBLE);
                            boolean showAd = Math.random() < 0.99;
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
                    holder.setTopMarginCardView(48);
                    holder.postLayout.setAlpha(1f);
                    holder.share.setEnabled(true);
                    holder.share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.share(cardView.getContext(), item.getAuthName());
                        }
                    });
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            //Calculates days ago from class DayAgoSystem
            String result = DayAgoSystem.getDayAgo(date);


            Integer year = item.getYear();
            if (year != null && (year == -1 || year == 0 || year == 1 || year == 2 || year == 3 || year == 4)) {
                switch (year) {
                    case -1:
                        holder.year.setText("Common Notice");
                        break;
                    case 0:
                        holder.year.setVisibility(View.GONE);
                        break;
                    case 1:
                        holder.year.setText("FY Notice");
                        break;
                    case 2:
                        holder.year.setText("SY Notice");
                        break;
                    case 3:
                        holder.year.setText("TY Notice");
                        break;
                    case 4:
                        holder.year.setText("B.Tech Notice");
                        break;
                }
            } else {
                holder.year.setVisibility(View.GONE);
            }

            holder.getUsername().setText(item.getAuthName().replaceAll("_", "."));



            ImageView img = holder.itemView.findViewById(R.id.imgView);
            ImageView profile_avatar = holder.itemView.findViewById(R.id.img_avtar);
            ImageView verifiedBadge = holder.itemView.findViewById(R.id.verifiedBadge);
            ImageView verifiedBadgeFaculty = holder.itemView.findViewById(R.id.verifiedBadgeFaculty);
            verifiedBadgeFaculty.setVisibility(View.GONE);
            CircularProgressIndicator progress = holder.itemView.findViewById(R.id.progressBar);
            Activity activity = (Activity) context;
            MaterialCardView impNotice = holder.itemView.findViewById(R.id.impNotice_switch);
            MaterialCardView linkContainer = holder.itemView.findViewById(R.id.link_container);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
            String userRole = preferences.getString("auth_userole", null);

            if (item.isImpNotice()){
                holder.impNotice.setVisibility(View.VISIBLE);
                holder.noticeAlertTxt.setText("Important Notice");
            }else{
                holder.impNotice.setVisibility(View.GONE);
            }

            //holder.blockCheck(item.getNoticeId());


            if (item.getAuthName() != null){
                holder.getAvatar(item.getAuthName());
            }else{
                holder.getGender(item.getAuthName());
            }
            if(item.getAuthName().equals("Om Lokhande")){
                verifiedBadge.setVisibility(View.VISIBLE);
                verifiedBadgeFaculty.setVisibility(View.GONE);
                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/dev_avatar.jpg").into(profile_avatar);
                holder.getBlogDate_time().setText("Admin • " + result);
            }else if(item.getAuthName().equals("Aishwarya Kumbhar")){
                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profile_avatar);
                verifiedBadge.setVisibility(View.VISIBLE);
                verifiedBadgeFaculty.setVisibility(View.GONE);
                holder.getBlogDate_time().setText("Class Representative • " + result);
            }else if(item.getAuthName().equals("Unofficial Mech")){
                Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/unofficial.png").into(profile_avatar);
                verifiedBadge.setVisibility(View.VISIBLE);
                verifiedBadgeFaculty.setVisibility(View.GONE);
                holder.getBlogDate_time().setText("Admin • " + result);

                if (userRole != null && userRole.equals("Admin")){
                    holder.postLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.postLayout.setVisibility(View.GONE);
                }
            }else{
                verifiedBadge.setVisibility(View.INVISIBLE);
                profile_avatar.setImageResource(R.drawable.round_account_circle_24);
                if (item.getAuthDiv() != null){
                    holder.getBlogDate_time().setText("From " + item.getAuthDiv() + " division • " +result);
                }else{
                    if (item.getAuthName().startsWith("Mr_") || item.getAuthName().startsWith("Prof_") || item.getAuthName().startsWith("Dr_")){
                                    verifiedBadgeFaculty.setVisibility(View.VISIBLE);
                                    verifiedBadge.setVisibility(View.GONE);
                                    Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/male.png").into(profile_avatar);
                    }else if( item.getAuthName().startsWith("Mrs_")){
                        Picasso.get().load("https://rubyproducti9n.github.io/mech/avatar/female.png").into(profile_avatar);
                    }else{
                                    verifiedBadgeFaculty.setVisibility(View.GONE);
                    }
//                    String[] nameParts = item.getAuthName().split(" ");
//                    String firstName = nameParts[0];
//                    String lastName = nameParts[1];
//                    holder.checkFaculty(Collections.singletonList(firstName), new BlogViewHolder.FacultyCheckCallback() {
//                        @Override
//                            public void onFacultyChecked(Boolean initials) {
//                            if(initials!=null){
//                                if (initials){
//                                    verifiedBadgeFaculty.setVisibility(View.VISIBLE);
//                                    verifiedBadge.setVisibility(View.GONE);
//                                }else{
//                                    verifiedBadgeFaculty.setVisibility(View.GONE);
//                                    verifiedBadge.setVisibility(View.GONE);
//                                }
//                            }else{
//                                verifiedBadgeFaculty.setVisibility(View.GONE);
//                                verifiedBadge.setVisibility(View.GONE);
//                            }
//                        }
//                    });
                    holder.getBlogDate_time().setText(result);
                }
            }


            TextView textView = holder.pdfInfo;
            //String url = item.getImgUrl();
            TextView pdfDetail = holder.itemView.findViewById(R.id.pdf_info);
            MaterialButton downloadBtn = holder.itemView.findViewById(R.id.download_btn);
            //holder.getMetadata(item.getImgUrl());


            if (item.getImgUrl() == null || item.getImgUrl().equals("null")){
                holder.img.setVisibility(View.GONE);
                holder.pdfContainer.setVisibility(View.GONE);
            }else{
                if (item.getImgUrl().startsWith("http://") || item.getImgUrl().startsWith("https://")){
                    try{
                        URL url1 = new URL(item.getImgUrl());
                        String path = url1.getPath();
                        if (isPdf(item.getImgUrl())){

                            holder.img.setVisibility(View.GONE);
                            holder.pdfContainer.setVisibility(View.VISIBLE);
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(path);
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    pdfDetail.setText("File found! ");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pdfDetail.setText("File not found!");
                                }
                            });
//                                    storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//                                @Override
//                                public void onSuccess(StorageMetadata storageMetadata) {
//                                    String fileName = storageMetadata.getName();
//                                    long fileSizeBytes = storageMetadata.getSizeBytes();
//                                    String contentType = storageMetadata.getContentType();
//
//                                    double fileSizeKB = fileSizeBytes / 1024.0;
//                                    double fileSizeMB = fileSizeKB / 1024.0;
//                                    holder.pdfInfo.setText(fileName);
//                                    Log.d("PDF_INFO", "FileName: " + fileName);
//                                    Log.d("PDF_INFO", "Size: " + fileSizeMB);
//                                    Log.d("PDF_INFO", "Url: " + item.getImgUrl());
//
//                                    Toast.makeText(mContext, "Info: " + fileName + " • " + fileSizeMB +  " • " + item.getImgUrl(), Toast.LENGTH_SHORT).show();
//
//                                    pdfDetail.setText(fileName + "\n • " + fileSizeMB + " • PDF " + contentType);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("PDF_ERROR", "Failed to retrieve PDF metadata: " + e.getMessage() +  item.getImgUrl());
//                                    pdfDetail.setText("[File is missing]" + path);
//                                }
//                            });

                            downloadBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final File localeFile = new File(Environment.getExternalStorageDirectory(), "new.pdf");

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Toast.makeText(mContext, "File found!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "File not found!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

//                                    storageReference.getFile(localeFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                            Toast.makeText(mContext, "File downloaded in: " + localeFile.getPath(), Toast.LENGTH_SHORT).show();
//
//                                            try{
//                                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                                intent.setDataAndType(Uri.fromFile(localeFile), "application/pdf");
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//                                                if (intent.resolveActivity(mContext.getPackageManager()) !=null){
//                                                    mContext.startActivity(intent);
//                                                }else{
//                                                    Toast.makeText(mContext, "No app found to open the file, open it manually from your downloads", Toast.LENGTH_SHORT).show();
//                                                }
//
//                                            }catch (ActivityNotFoundException e){
//                                                e.printStackTrace();
//                                            }
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(mContext, "Download failed, try again later", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                                }
                            });


                        }else{
                            holder.img.setVisibility(View.VISIBLE);
                            holder.pdfContainer.setVisibility(View.GONE);

                            Picasso.get().load(item.getImgUrl()).into(holder.img);
                        }
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(mContext, "Not a full url", Toast.LENGTH_SHORT).show();
                }

            }

//            if (Objects.equals(item.getImgUrl(), "null") || item.getImgUrl() == null){
//                holder.img.setVisibility(View.GONE);
//                holder.pdfContainer.setVisibility(View.GONE);
//            }else {
//                if(isPdf(item.getImgUrl())) {
//                    holder.img.setVisibility(View.GONE);
//                    holder.pdfContainer.setVisibility(View.VISIBLE);
//                    pdfDetail.setText("txt goes here");
//                    holder.pdfContainer.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            downloadPdf(textView, item.getImgUrl());
//                        }
//                    });
//                    holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            downloadPdf(textView, item.getImgUrl());
//                        }
//                    });
//                }else{
//                holder.pdfContainer.setVisibility(View.GONE);
//                holder.img.setVisibility(View.VISIBLE);
//                Picasso.get().load(item.getImgUrl()).into(holder.getImg());
//                }
//            }




            SpannableString spannableString = new SpannableString(item.getCaption());
            Linkify.addLinks(spannableString, Linkify.WEB_URLS);
            holder.getBlogPost().setText(spannableString);
            holder.getBlogPost().setMovementMethod(LinkMovementMethod.getInstance());

            if (item.getLink() == null || item.getLink().isEmpty()){
                holder.linkContainer.setVisibility(View.GONE);
            }else{

                holder.linkContainer.setVisibility(View.VISIBLE);
                holder.getLink().setText(item.getLink());
                holder.link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = item.getLink();
                        if (link != null && (link.startsWith("http://") || link.startsWith("https://"))){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        mContext.startActivity(intent);
                        }
                    }
                });
            }

//            holder.blogPost.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String link = item.getBlogTxt();
//                    if (link != null && (link.startsWith("http://") || link.startsWith("https://"))){
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
//                        context.startActivity(intent);
//                    }
//                }
//            });


            //Listeners
            holder.impNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProjectToolkit.initiateAdapterDialog(holder.impNotice.getContext(), "Important Notice", "Indicates the alert level of the notice so you never miss any important notice(s)");
                    //holder.initiateDialog("Alert level", "Indicates the alert level of the notice so you never miss any important notices");
                }
            });
            verifiedBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProjectToolkit.initiateAdapterDialog(verifiedBadge.getContext(), "Verified user", "Indicates that user is authorised");
                    //holder.initiateDialog("Verified", "Indicates that user is authorised");
                }
            });
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //holder.img.setScaleType(ImageView.ScaleType.CENTER);
                    Intent intent = new Intent(mContext, ImageMagnifierActivity.class);
                    intent.putExtra("link", item.getImgUrl());
                    intent.putExtra("activity", "notice");
                    mContext.startActivity(intent);
                }
            });



            holder.card_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.showPopupMenu(holder.card_menu, item.getNoticeId());
                }
            });



        }



    }

    public BlogAdapter(Context context, List<CreateNotice.Notice> items){
        mContext = context;
        this.items = items;
    }
    public BlogAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView postLayout;
        private ImageView card_menu;
        private TextView username;
        private TextView div;
        private TextView blogPost;
        private TextView link;
        private TextView date_time;
        private ImageView img;
        private ImageView impNotice;
        private TextView noticeAlertTxt;
        private MaterialCardView linkContainer;

        private MaterialCardView pdfContainer;
        private TextView pdfInfo;
        private MaterialButton downloadBtn;
        private Chip year;
        private MaterialCardView expired;
        private MaterialButton share;


        public TextView getUsername(){
            return username;
        }
        public TextView getDiv(){return div;}
        public TextView getBlogPost() {
            return blogPost;
        }
        public TextView getLink(){return link;}
        public ImageView getImg(){return img;}

        public TextView getBlogDate_time() {
            return date_time;
        }
        public ImageView getImpNoticeSwitch(){
            return impNotice;
        }
        public Chip getYear(){return year;}

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);

            expired = itemView.findViewById(R.id.expired);
            expired.setVisibility(View.GONE);
            postLayout = itemView.findViewById(R.id.cardView);
            card_menu = itemView.findViewById(R.id.card_menu);
            username = itemView.findViewById(R.id.txtUsername);

            blogPost = itemView.findViewById(R.id.blog);
            link = itemView.findViewById(R.id.link);
            date_time = itemView.findViewById(R.id.txtTime);
            img = itemView.findViewById(R.id.imgView);
            impNotice = itemView.findViewById(R.id.bookmark);
            noticeAlertTxt = itemView.findViewById(R.id.noticeAlertTxt);

            linkContainer = itemView.findViewById(R.id.link_container);
            pdfContainer = itemView.findViewById(R.id.pdfContainer);
            pdfInfo = itemView.findViewById(R.id.pdf_info);
            downloadBtn = itemView.findViewById(R.id.download_btn);
            year = itemView.findViewById(R.id.year);

            share = itemView.findViewById(R.id.share);

        }

//        private void downloadNotice(Context context, String fileUrl){
//            File localeFile = new File(context.getFilesDir(), "Unofficial Mech/Notices/" + "notice");
//            StorageTask<FileDownloadTask.TaskSnapshot> downlaodTask = fileUrl.
//        }

        public interface FacultyCheckCallback {
                void onFacultyChecked(Boolean initials);
        }
        private void checkFaculty(List<String> userNames, FacultyCheckCallback callback) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            AtomicBoolean foundInitials = new AtomicBoolean(false); // Flag to track if initials found
            final String[] initials = {null}; // Variable to store retrieved initials

            for (String firstName : userNames) {
                DatabaseReference userRef = reference.child(firstName);
                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists() && snapshot.hasChild("initials")) {
                            foundInitials.set(true); // Set flag if found
                            initials[0] = snapshot.child("initials").getValue(String.class);
                            callback.onFacultyChecked(true);
                        }
                    } else {
                        callback.onFacultyChecked(false);
                        Log.e("Error checking initials", task.getException().toString());
                    }

                });
            }
        }

        private void initiateDialog(String title, String msg){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(impNotice.getContext());
            builder.setTitle(title)
                    .setMessage(msg)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.show();
        }

        private void share(Context context, String username){
            String deepLink = "unofficial://unofficialmech.com";
            String link = "https://bit.ly/unofficialMech";
            String textToShare = "\uD83D\uDD34NEW NOTICE!!! " + "\n\n A new notice was published by " + username + " on our community app \n \n Download the app now: " + link;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            //shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }

        private void setExpired(){
            MaterialCardView expiredView = itemView.findViewById(R.id.expired);
            MaterialButton share = itemView.findViewById(R.id.share);
            postLayout.setAlpha(0.3f);
            expiredView.setVisibility(View.VISIBLE);
            share.setVisibility(View.GONE);


        }
       private void setTopMarginCardView(int marginInDp) {
           MaterialCardView expiredView = itemView.findViewById(R.id.expired);
           expiredView.setVisibility(View.GONE);
           expiredView.setTranslationY(48F);
            int marginInPx = dpToPx(expiredView.getContext(), marginInDp);

            ViewGroup.LayoutParams layoutParams = expiredView.getLayoutParams();

            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;

                marginLayoutParams.topMargin = marginInPx;

                expiredView.setLayoutParams(marginLayoutParams);
            }
        }

        private static int dpToPx(Context context, int dp) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }


//        private void blockCheck(String noticeId){
//            TextView caption = itemView.findViewById(R.id.blog);
//            TextView link = itemView.findViewById(R.id.link);
//            MaterialCardView linkContainer = itemView.findViewById(R.id.link_container);
//            MaterialCardView pdfContainer = itemView.findViewById(R.id.pdfContainer);
//            MaterialCardView blockView = itemView.findViewById(R.id.expired);
//
//            DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notice/");
//            noticeRef.child(noticeId).child("blocked").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.exists()){
//                        Boolean value = snapshot.getValue(Boolean.class);
//                        if (value){
//                            caption.setVisibility(View.GONE);
//                            link.setVisibility(View.GONE);
//                            linkContainer.setVisibility(View.GONE);
//                            pdfContainer.setVisibility(View.GONE);
//                            blockView.setVisibility(View.VISIBLE);
//                        }else{
//                            blockView.setVisibility(View.GONE);
//                        }
//                    }else{
//                        blockView.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(getUsername().getContext(), "Server not responding", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

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
                deleteMenuItem.setVisible(true);
            }else{
                Menu menu = popupMenu.getMenu();
                MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
                deleteMenuItem.setVisible(false);
            }
            if (role.equals("Faculty") || role.equals("Admin")){
                Menu menu = popupMenu.getMenu();
                MenuItem blockMenuItem = menu.findItem(R.id.action_block);
                blockMenuItem.setVisible(true);
            }else{
                Menu menu = popupMenu.getMenu();
                MenuItem blockMenuItem = menu.findItem(R.id.action_block);
                blockMenuItem.setVisible(false);
            }

        }
        private void deletePost(String postId){
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("notice");
            postRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = snapshot.child("noticeId").getValue(String.class);
                    if (userId != null){
                        postRef.child(postId).removeValue()
                                .addOnSuccessListener(runnable -> {
                                    Toast.makeText(img.getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(runnable -> {
                                    Toast.makeText(img.getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                                });
                    }else{
                        Toast.makeText(img.getContext(), "Post not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void getBlockedValue(String noticeId){
            DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notice");
            noticeRef.child(noticeId).child("blocked").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Boolean blockedValue = snapshot.getValue(Boolean.class);
                        if (blockedValue){
                            blockNotice(noticeId, false);
                        }else{
                            blockNotice(noticeId, true);
                        }
                    }
                    else{
                        Toast.makeText(itemView.getContext(), "Value generated, try again", Toast.LENGTH_SHORT).show();
                        noticeRef.child(noticeId).child("blocked").setValue(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(itemView.getContext(), "Server not responding", Toast.LENGTH_SHORT).show();
                }
            });
        }
        private void blockNotice(String postId, Boolean blockedValue){
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("notice");
            postRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId = snapshot.child("noticeId").getValue(String.class);
                    if (userId != null){
                        postRef.child(postId).child("blocked").setValue(true);
                    }else{
                        Toast.makeText(img.getContext(), "Notice not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void getMetadata(String url){
            TextView pdfInfo = itemView.findViewById(R.id.pdf_info);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);

            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    String fileName = storageMetadata.getName();
                    long fileSizeBytes = storageMetadata.getSizeBytes();

                    double fileSizeKB = fileSizeBytes / 1024.0;
                    double fileSizeMB = fileSizeKB / 1024.0;
                    pdfInfo.setText(fileName + "\n • " + fileSizeMB + " • PDF");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pdfInfo.setText("(No data)");
                }
            });
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
    }




    private boolean isPdf(String stringUri){
//        Uri uri = Uri.parse(stringUri);
//        String type = mContext.getContentResolver().getType(uri);
//        return type != null && type.startsWith("application/pdf");

//        if (stringUri == null){
//            return false;
//        }
        Uri uri = Uri.parse(stringUri);
        String extenstion = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        return extenstion != null && extenstion.equalsIgnoreCase("pdf");

//        Uri extension = getFileExtension(uri);
//        return extension != null && extension.equalsIgnoreCase("pdf");
    }

    private void getMetadata(TextView pdfInfo, String url){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);

        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String fileName = storageMetadata.getName();
                long fileSizeBytes = storageMetadata.getSizeBytes();

                double fileSizeKB = fileSizeBytes / 1024.0;
                double fileSizeMB = fileSizeKB / 1024.0;
                pdfInfo.setText(fileName + "\n • " + fileSizeMB + " • PDF");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pdfInfo.setText("(No data)");
            }
        });
    }

    private void downloadPdf(TextView pdfInfo, String url){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);

        final File localeFile = new File(Environment.getExternalStorageDirectory(), "new.pdf");
        
        storageReference.getFile(localeFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(mContext, "File downloaded in: " + localeFile.getPath(), Toast.LENGTH_SHORT).show();

                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(localeFile), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    if (intent.resolveActivity(mContext.getPackageManager()) !=null){
                        mContext.startActivity(intent);
                    }else{
                        Toast.makeText(mContext, "No app found to open the file, open it manually from your downloads", Toast.LENGTH_SHORT).show();
                    }

                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Download failed, try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(String fileLink){
        if (fileLink!= null && !fileLink.isEmpty()){
            int dotIndex = fileLink.lastIndexOf(".");
            if (dotIndex> 0 && dotIndex< fileLink.length() -1){
                return fileLink.substring(dotIndex+ 1).toLowerCase(Locale.US);
            }
        }
        return null;
    }


}
