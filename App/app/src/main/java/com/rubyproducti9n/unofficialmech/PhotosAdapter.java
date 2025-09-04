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
import android.text.TextUtils;
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

    private final List<Post> postList;

    @NonNull
    @Override
    public PhotosAdapter.PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
        return new PhotosAdapter.PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosAdapter.PhotosViewHolder holder, int position) {

        Post post = postList.get(position);

        holder.titleTv.setText(post.title);
        holder.companyTv.setText(post.company);
        holder.typeTv.setText(post.type);
        holder.locationTv.setText(post.isRemote ? "Remote" : post.location);
        holder.skillsTv.setText(TextUtils.join(", ", post.skills));

        long daysLeft = (post.deadline - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
        holder.deadlineTv.setText(daysLeft + " days left");

        holder.itemView.setOnClickListener(v -> {
            // Optional: Open detail page or link
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.applyLink));
            v.getContext().startActivity(browserIntent);
        });
    }
    public PhotosAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public static class PhotosViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv, companyTv, typeTv, locationTv, skillsTv, deadlineTv;

        public PhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            companyTv = itemView.findViewById(R.id.companyTv);
            typeTv = itemView.findViewById(R.id.typeTv);
            locationTv = itemView.findViewById(R.id.locationTv);
            skillsTv = itemView.findViewById(R.id.skillsTv);
            deadlineTv = itemView.findViewById(R.id.deadlineTv);
        }

    }
}
