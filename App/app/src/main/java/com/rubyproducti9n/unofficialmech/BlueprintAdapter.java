package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BlueprintAdapter extends RecyclerView.Adapter<BlueprintAdapter.BlueprintViewHolder>{

    List<ItemBlueprint> items = new ArrayList<>();
    Context mContext;

    public BlueprintAdapter(List<ItemBlueprint> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BlueprintAdapter.BlueprintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blueprint, parent, false);
        return new BlueprintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlueprintAdapter.BlueprintViewHolder holder, int position) {
        ItemBlueprint item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.summary.setText(item.getSummary());
        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getArticleLink()));
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BlueprintViewHolder extends RecyclerView.ViewHolder {

        private TextView title, summary;
        private MaterialButton link;

        public BlueprintViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            summary = itemView.findViewById(R.id.summary);
            link = itemView.findViewById(R.id.link);
        }
    }
}
