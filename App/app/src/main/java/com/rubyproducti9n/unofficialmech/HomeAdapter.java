package com.rubyproducti9n.unofficialmech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeItem1> items;
    //    private OnImageLoadListner onImageLoadListner;
    private Context context;

    @NonNull
    @Override
    public HomeAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_1, parent, false);
        return new HomeAdapter.HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.HomeViewHolder holder, int position) {



        HomeItem1 item = items.get(position);
        String dateString = item.getVidDateTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException | java.text.ParseException e) {
            throw new RuntimeException(e);
        }
        //Calculates days ago from class DayAgoSystem
        String result = DayAgoSystem.getDayAgo(date);

        holder.getTitle().setText(item.getVidTitle());
        holder.getChannelName().setText(item.getVidChannelName());
        holder.getTimestamp().setText(item.getVidTimestamp());


        ImageView channelAvatar = holder.itemView.findViewById(R.id.channel_avatar);
        ImageView verifiedBadge = holder.itemView.findViewById(R.id.verifiedBadge);
        CircularProgressIndicator progress = holder.itemView.findViewById(R.id.progressBar);
        Activity activity = (Activity) context;
        progress.setVisibility(View.VISIBLE);

//        if(item.getTitle().contains("Unofficial Mech")){
//            Picasso.get().load("https://rubyproducti9n.github.io/mech/img/avatar 1.png").into(channelAvatar);
//        }else{
//            channelAvatar.setImageResource(R.drawable.round_account_circle_24);
//        }
        Picasso.get().load(item.getVidChannelAvatar()).into(holder.getChannelAvatar(), new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        Picasso.get().load(item.getVidThumbnail()).into(holder.getThumbnail(), new Callback() {
            @Override
            public void onSuccess() {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                //Toast.makeText(mc, "Error while loading memories, try again later", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });



        holder.getDateTime().setText(result);

        holder.bind(item);
    }
    public HomeAdapter(List<HomeItem1> items){
        this.items = items;
    }
    public HomeAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView timestamp;
        private ImageView thumbnail;
        private ImageView channelAvatar;
        private TextView title;
        private TextView channelName;
        private TextView dateTime;
        private String link;


        public  TextView getTimestamp(){
            return timestamp;
        }
        public ImageView getThumbnail(){
            return thumbnail;
        }
        public ImageView getChannelAvatar() {
            return channelAvatar;
        }

        public TextView getChannelName() {
            return channelName;
        }
        public TextView getTitle() {
            return title;
        }
        public TextView getDateTime() {
            return dateTime;
        }
        public String getLink(){ return link;}


        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.timestamp);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            channelAvatar = itemView.findViewById(R.id.channel_avatar);
            title = itemView.findViewById(R.id.title);
            channelName = itemView.findViewById(R.id.channelName);
            dateTime = itemView.findViewById(R.id.date_time);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Patterns.WEB_URL.matcher(link).matches()){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        v.getContext().startActivity(intent);
                    }
                    else{
                        Toast.makeText(v.getContext(), "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

        public void bind(HomeItem1 bindItem){
            link = bindItem.getLink();
        }

    }

    public static class MyUtils{
        public static void startActivity(Intent intent){
            if(intent!=null){
                startActivity(intent);
            }
        }
    }

    
}
