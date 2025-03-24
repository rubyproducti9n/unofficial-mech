package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;


public class InternshipNotificationAdapter extends RecyclerView.Adapter<InternshipNotificationAdapter.PhotosViewHolder>{
    private List<InternshipNotificationItem> items;
    private SharedPreferences sharedPreferences;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;
    Context mContext;

    @NonNull
    @Override
    public InternshipNotificationAdapter.PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.internship_notification_item, parent, false);
        return new InternshipNotificationAdapter.PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InternshipNotificationAdapter.PhotosViewHolder holder, int position) {

        InternshipNotificationItem item = items.get(position);

        String caption  = "NULL";
        SpannableString spannableString = new SpannableString(caption);
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);

        Picasso.get().load(item.getImgUrl()).into(holder.ic);
        holder.title.setText(item.getInternship_name());

        if (item.getDescription() == null){
            //Setting description gone
            holder.location.setVisibility(View.GONE);
        }else{
            //Setting description / location
            holder.location.setText(item.getDescription());
        }


        holder.intern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getUrl() == null){
                    Toast.makeText(context, "Internships is Unavailable", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(item.getUrl()));
                    mContext.startActivity(i);
                }
            }
        });

        //MaterialButton likeButton = holder.itemView.findViewById(R.id.likes);
//        Activity activity = (Activity) context;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
        String userRole = preferences.getString("auth_userole", null);



    }
    public InternshipNotificationAdapter(Context context, List<InternshipNotificationItem> items){
        mContext = context;
        this.items = items;
    }
    public InternshipNotificationAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class PhotosViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout intern;
        private TextView postId;
        private TextView username;
        private ImageView ic;
        private TextView title, location;

        public TextView getPostId(){return postId;}
        public TextView getUsername(){
            return username;
        }
        public ImageView getItemImageView() {
            return ic;
        }

        public TextView getPostCaption(){
            return title;
        }


        public PhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            //card_menu = itemView.findViewById(R.id.card_menu);
//            username = itemView.findViewById(R.id.txtUsername);

//            postId = itemView.findViewById(R.id.postId);
            intern = itemView.findViewById(R.id.intern);
            ic = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.txtTitle);
            location = itemView.findViewById(R.id.txtLocation);
            
            String[] adUnitIds = {"ca-app-pub-5180621516690353/6163652319", "ca-app-pub-5180621516690353/6323434418", "ca-app-pub-5180621516690353/8674265587", "ca-app-pub-5180621516690353/2471873213", "ca-app-pub-5180621516690353/7644254948"};
            int randomIndex = new Random().nextInt(adUnitIds.length);
            String adUnitId = adUnitIds[randomIndex];

//            AdView adView = itemView.findViewById(R.id.adView);
//            adView.setVisibility(View.GONE);
//            Callbacks.getAdValue(null, new Callbacks.AdValue() {
//                @Override
//                public void onAdValue(boolean value) {
//                    if (value){
//                        adView.setVisibility(View.VISIBLE);
//                        boolean showAd = Math.random() < 0.75;
//                        adView.setVisibility(showAd ? View.VISIBLE : View.GONE);
//                        if (showAd) {
//                            AdRequest adRequest = new AdRequest.Builder().build();
//                            //adView.setAdUnitId(adUnitId);
//                            adView.loadAd(adRequest);
//                        }
//                    }else{
//                        adView.setVisibility(View.GONE);
//                    }
//                }
//            });

        }


    }
}
