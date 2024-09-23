package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder>{

    List<PhotosFragment.StoryItem> items;

    @NonNull
    @Override
    public StoryAdapter.StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_2, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.StoryViewHolder holder, int position) {

        PhotosFragment.StoryItem item = items.get(position);
        holder.username.setText(item.getUsername());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public StoryAdapter(List<PhotosFragment.StoryItem> items){
        this.items = items;
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {

        public TextView username;

        public TextView getUsername() {
            return username;
        }

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.txt_username);
        }
    }

}
