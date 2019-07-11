package com.example.jsonproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ShibeAdapter extends RecyclerView.Adapter<ShibeAdapter.ShibeViewHolder> {

    private List<String> shibeUrls;
    private Context context;
    private OnShibeClicked listener;

    ShibeAdapter(List<String> shibeUrls, OnShibeClicked onShibeClicked) {
        this.shibeUrls = shibeUrls;
        this.listener = onShibeClicked;
    }

    @NonNull
    @Override
    public ShibeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.shibe_item, parent, false);
        context = parent.getContext();
        return new ShibeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShibeViewHolder holder, int position) {
        final String url = shibeUrls.get(position);
        Glide.with(context).load(url).into(holder.ivPhoto);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.shibeClicked(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shibeUrls.size();
    }

    class ShibeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        ShibeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }

    public interface OnShibeClicked {
        void shibeClicked(String url);
    }
}

