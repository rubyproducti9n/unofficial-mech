package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
    static List<SearchAdapter.SearchItem> items;
    static List<SearchItem> filteredList;
    Context mContext;

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        SearchAdapter.SearchItem item = filteredList.get(position);
        if (item.getName() != null && !item.getName().isEmpty()) {
            if (item.getAvatar()!=null){
                Picasso.get().load(item.getAvatar()).into(holder.avatar);
            }else{
                holder.avatar.setImageResource(R.drawable.round_account_circle_24);
            }
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(item.getName());
            if (item.getDept()!=null){
                holder.dept.setText(item.getDept());
            }else{
                holder.dept.setText("Mechanical Department");
            }
            holder.mc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileViewsActivity.class);
                    intent.putExtra("username", item.getName());
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.name.setVisibility(View.GONE);
        }
    }
    public SearchAdapter(Context context, List<SearchItem> item){
        mContext = context;
        items = item;
        filteredList = new ArrayList<>(items);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(items); // If no text, show all items
        } else {
            text = text.toLowerCase();
            for (SearchItem item : items) {
                if (item.getName().toLowerCase().contains(text)) {
                    filteredList.add(item); // Add matching items to filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data changes
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView name, dept;
        private MaterialCardView mc;
        private ImageView avatar;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            dept = itemView.findViewById(R.id.dept);
            mc = itemView.findViewById(R.id.mc);

        }
    }

    public static class SearchItem{

        public String avatar;
        public String name;
        public String dept;

        public SearchItem(String avatar, String name, String dept) {
            this.avatar = avatar;
            this.name = name;
            this.dept = dept;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }
    }
}

