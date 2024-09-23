package com.rubyproducti9n.unofficialmech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    List<video_item> video_items;

    public VideoAdapter(List<video_item> video_items){
        this.video_items = video_items;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        video_item item = video_items.get(position);
        holder.getUsername().setText(item.getUserName());
    }

    @Override
    public int getItemCount() {
        return video_items.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{

        VideoView videoView;
        TextView username;


        public TextView getUsername(){
            return username;
        }

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            username = itemView.findViewById(R.id.username);
        }


    }

}
