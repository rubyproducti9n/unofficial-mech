package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Item> items;
    private List<Item> filteredPhotos;
    private Context mContext;
    private com.rubyproducti9n.unofficialmech.FilterReader filterReader;
//    private OnImageLoadListner onImageLoadListner;
    Context mc;


    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ImageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Item item = filteredPhotos.get(position);
        CircularProgressIndicator progress = holder.itemView.findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        Picasso.get().load(item.getImageUrl()).into(holder.getItemImageView(), new Callback() {
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
        holder.getTxt_tags().setText(item.getTags());


        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        animation.setStartOffset(position * 50L);
        holder.itemView.startAnimation(animation);

        holder.itemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageMagnifierActivity.class);
                intent.putExtra("link", item.getImageUrl());
                intent.putExtra("activity", "post");
                mContext.startActivity(intent);
            }
        });

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreatePost.class);
                intent.putExtra("sharedImage", item.getImageUrl());
                intent.putExtra("sharedText", "Throwback: " + item.getTags());
                mContext.startActivity(intent);
            }
        });


//        holder.itemImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mc, ImageActivity.class);
//                intent.putExtra("imageUrl", item.getImageUrl());
//                mc.startActivity(intent);
//            }
//        });
    }

//    public ImageAdapter(OnImageLoadListner listner) {
//        onImageLoadListner = listner;
//    }

//    public void setItems(List<Item> item){
////        this.items = items;
////        notifyDataSetChanged();
////    }
//    public interface OnImageLoadListner{
//        void onImageLoadComplete();
//    }
    public ImageAdapter(Context context, List<Item> items){
        this.items = items;
        mContext = context;
        this.filteredPhotos = new ArrayList<>(items);
    }
    public ImageAdapter(Context context){
        mc = context;
    }

    @Override
    public int getItemCount() {
        return filteredPhotos.size();
    }
    public void setFilterReader(FilterReader filterReader){
        this.filterReader = filterReader;
        filteredPhotos();
    }
    private void filteredPhotos() {
        filteredPhotos.clear();
        if(filterReader == null){
            filteredPhotos.addAll(items);
        }else {
            for (Item item : items){
                if (filterReader.shouldInclude(item)){
                    filteredPhotos.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView privacyManager;

        private MaterialCardView photo;
        private MaterialCardView likeContainer;
        private MaterialCardView photoMetadata;
        private ImageView itemImageView;
        private TextView txt_tags;
        private ImageView likeImageView;
        boolean isLiked = false;

        public ImageView getItemImageView() {
            return itemImageView;
        }

        public TextView getTxt_tags() {
            return txt_tags;
        }
        public ImageView getLikeImageView(){
            return likeImageView;
        }

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            privacyManager = itemView.findViewById(R.id.privacyManager);

            photo = itemView.findViewById(R.id.cardView3);
            likeContainer = itemView.findViewById(R.id.cardView3);
            photoMetadata = itemView.findViewById(R.id.cardView3);
            itemImageView = itemView.findViewById(R.id.img);
            txt_tags = itemView.findViewById(R.id.txtTags);
            likeImageView = itemView.findViewById(R.id.img_like);

//            likeImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    isLiked = !isLiked;
//                    if(isLiked){
//                        likeImageView.setImageResource(R.drawable.heart_active);
//                    }else{
//                        likeImageView.setImageResource(R.drawable.heart_outline);
//                    }
//                    //Add SharedPreference to store isLiked value
//
//                }
//            });
        }
    }
}
