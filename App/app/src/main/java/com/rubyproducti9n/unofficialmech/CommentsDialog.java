package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubyproducti9n.unofficialmech.databinding.ItemCommentsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class CommentsDialog extends BottomSheetDialogFragment {

    List<Comments> items = new ArrayList<>();

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Comments> commentList;
    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private CommentsDialog binding;

    // TODO: Customize parameters
    public static CommentsDialog newInstance(int itemCount) {
        final CommentsDialog fragment = new CommentsDialog();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottomsheet_comments, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentList = new ArrayList<>();
        adapter = new ItemAdapter(requireContext(), commentList);
        recyclerView.setAdapter(adapter);

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("test/Comments/"); // Replace "comments"
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    String avatar = commentSnapshot.child("avatar").getValue(String.class);
                    String username = commentSnapshot.child("username").getValue(String.class);
                    String commentText = commentSnapshot.child("comment").getValue(String.class);
                    String date = commentSnapshot.child("date").getValue(String.class);
                    Comments comment = new Comments(avatar, username, commentText, date);
                    commentList.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        return view;

    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        final RecyclerView recyclerView = (RecyclerView) view;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new ItemAdapter(requireArguments().getInt(ARG_ITEM_COUNT)));
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, comment;
        private ImageView avatar;

        ViewHolder(ItemCommentsBinding binding) {
            super(binding.getRoot());

            avatar = binding.avatar;
            username = binding.username;
            comment = binding.comment;
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        Context c;
        List<Comments> i = new ArrayList<>();

        ItemAdapter(Context context, List<Comments> item) {
            c = context;
            i = item;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ViewHolder(ItemCommentsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Comments item = items.get(position);

            holder.username.setText(item.getUsername());
            if (item.getAvatar()!=null){
                Picasso.get().load(item.getAvatar()).into(holder.avatar);
            }
            else{
                holder.avatar.setImageResource(R.drawable.round_account_circle_24);
            }
            holder.comment.setText(item.getComment());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

    }
}
class Comments{

    private String avatar;
    private String username;
    private String comment;
    private String date;

    private int like;
    private String subComment;

    public Comments(String avatar, String username, String comment, String date) {
        this.avatar = avatar;
        this.username = username;
        this.comment = comment;
        this.date = date;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}