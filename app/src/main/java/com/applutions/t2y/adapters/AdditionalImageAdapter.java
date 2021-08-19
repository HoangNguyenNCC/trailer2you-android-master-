package com.applutions.t2y.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.data.items.PhotoObj;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class AdditionalImageAdapter extends RecyclerView.Adapter<AdditionalImageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PhotoObj> urls;
    private ItemClick itemClick;

    public AdditionalImageAdapter(ArrayList<PhotoObj> urls, ItemClick itemClick) {
        this.urls = urls;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public AdditionalImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_additional_images, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdditionalImageAdapter.ViewHolder holder, int position) {
        PhotoObj url = urls.get(position);
        holder.setData(context, url);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_additional);
            imageView.setClipToOutline(true);
        }

        public void setData(Context context, PhotoObj url) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.trailer_image)
                    .error(R.drawable.trailer_image);
            Glide.with(context).load(url.getData()).apply(options).into(imageView);

            imageView.setOnClickListener(v-> itemClick.onItemClicked(v,getAdapterPosition()));
        }
    }


    public interface ItemClick{
        void onItemClicked(View view, int position);
    }
}
