package com.applutions.t2y.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.data.response.search.TrailerDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder>
{
    private List<TrailerDetails> mData = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    private int layoutResource;

    public TrailersAdapter(List<TrailerDetails> data, Context context){
        this.mData = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrailerDetails item = mData.get(position);
        holder.Title.setText(item.getName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.trailer_image)
                .error(R.drawable.trailer_image);
        Glide.with(context).load(item.getPhotos().get(0).getData()).apply(options).into(holder.Photo);
        holder.Photo.setClipToOutline(true);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Desc;
        ImageView Photo;
        ImageView topLayer;
        ViewHolder (View view)
        {
            super(view);
            this.Title = view.findViewById(R.id.item_name);
            this.Desc = view.findViewById(R.id.item_type);
            this.Photo = view.findViewById(R.id.trailer_image);
        }

    }

    TrailerDetails getItem(int id) {
        return mData.get(id);
    }
}
