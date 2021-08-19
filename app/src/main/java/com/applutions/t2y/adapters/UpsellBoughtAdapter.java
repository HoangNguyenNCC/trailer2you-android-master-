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
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.ui.notifications.response.Items;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UpsellBoughtAdapter extends RecyclerView.Adapter<UpsellBoughtAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Items> list;

    public UpsellBoughtAdapter(ArrayList<Items> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_upsell, parent,false);
        UpsellBoughtAdapter.ViewHolder viewHolder = new UpsellBoughtAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Items item = list.get(position);
        holder.setData(context, item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView upsellNameQty;
        ImageView upsellImage;
        TextView upsellPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            upsellImage = itemView.findViewById(R.id.upsell_image_recycle);
            upsellNameQty = itemView.findViewById(R.id.upsell_name_qty_recycle);
            upsellPrice = itemView.findViewById(R.id.price_upsell_recycle);
        }

        public void setData(Context context, Items item){
            upsellPrice.setText(item.getTotalCharges().getCharges().getRentalCharges()*item.getTotalCharges().getQuantity()+" AUD");
            upsellNameQty.setText(item.getUnits()+"x "+item.getItemName());
            Glide.with(context)
                    .load(item.getItemPhoto().getData())
                    .into(upsellImage);
        }
    }
}
