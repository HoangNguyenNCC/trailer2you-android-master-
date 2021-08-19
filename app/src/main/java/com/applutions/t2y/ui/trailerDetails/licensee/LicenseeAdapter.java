package com.applutions.t2y.ui.trailerDetails.licensee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.adapters.UpsellItemsAdapter;
import com.applutions.t2y.data.response.Licensee;
import com.applutions.t2y.data.response.RentalItem;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LicenseeAdapter extends RecyclerView.Adapter<LicenseeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<RentalItem> licenseeRentalItems = new ArrayList();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentalItem rentalItem = licenseeRentalItems.get(position);
        holder.setData(rentalItem);
    }

    @Override
    public int getItemCount() {
        return licenseeRentalItems.size();
    }

    public void setData(ArrayList<RentalItem> items) {
        this.licenseeRentalItems.clear();
        this.licenseeRentalItems.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgTrailer;
        private TextView tvItemName;
        private TextView tvItemType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTrailer = itemView.findViewById(R.id.trailer_image);
            tvItemName = itemView.findViewById(R.id.item_name);
            tvItemType = itemView.findViewById(R.id.item_type);
        }

        protected void setData(RentalItem rentalItem) {
            if (rentalItem.getPhoto() != null) {
                Glide.with(context)
                        .load(rentalItem.getPhoto().get(0).getData())
                        .into(imgTrailer);
            }

            tvItemName.setText(rentalItem.getName());
            tvItemType.setText(rentalItem.getType());
        }
    }
}
