package com.applutions.t2y.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.data.items.RatingItem;
import com.applutions.t2y.ui.dashboard.RatingsBrowseDialog;
import com.applutions.t2y.ui.dashboard.RatingsPage;
import com.applutions.t2y.ui.notifications.BookingDetailsActivity;
import com.applutions.t2y.utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {

    private ArrayList<RatingItem> list = new ArrayList<>();
    private Context context;
    private SharedPrefs prefs;

    public RatingsAdapter(ArrayList<RatingItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_ratings,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        prefs = SharedPrefs.getInstance(context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RatingItem item = list.get(position);
        holder.setData(item, context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;
        TextView trailerName;
        TextView dates;
        Button moreInfo;
        Button rate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.trailer_image_review);
            trailerName = itemView.findViewById(R.id.tv_review_trailer_name);
            dates = itemView.findViewById(R.id.tv_booking_dates);
            moreInfo = itemView.findViewById(R.id.ratings_details_button);
            rate = itemView.findViewById(R.id.review_button);
        }

        public void setData(RatingItem item, Context context) {
            trailerName.setText(item.getName());
            dates.setText(item.getDates());
            Glide.with(context)
                    .load(item.getPhoto().getData())
                    .into(pic);
            moreInfo.setOnClickListener(v->{
                Intent i = new Intent(context, BookingDetailsActivity.class);
                i.putExtra("invoiceId", item.getInvoiceId());
                i.putExtra("status", item.getStatus());
                i.putExtra("licensee", item.getLicensee());
                context.startActivity(i);
            });
            rate.setOnClickListener(v->{
                prefs.setItemId(item.getInvoiceId());
                RatingsPage dialog = RatingsPage.newInstance();
                dialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "give_ratings");
            });
        }
    }
}
