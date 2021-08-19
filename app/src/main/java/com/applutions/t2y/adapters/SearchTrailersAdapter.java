package com.applutions.t2y.adapters;

import android.content.Context;
import android.util.Log;
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

import java.util.List;

public class SearchTrailersAdapter extends RecyclerView.Adapter<SearchTrailersAdapter.ViewHolder> {

    private final SearchTrailerListener mListener;
    private List<TrailerDetails> mData;
    private Context context;

    public SearchTrailersAdapter(List<TrailerDetails> mData, Context context, SearchTrailerListener listener) {
        this.mData = mData;
        this.context = context;
        this.mListener = listener;
        Log.d("here","adapter made");
    }

    @NonNull
    @Override
    public SearchTrailersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_search,parent,false);
        ViewHolder viewHolder = new ViewHolder(viewItemLayout);
        return viewHolder;
    }

    public void setListData(List<TrailerDetails> mData){
        this.mData.clear();
        this.mData = mData;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull SearchTrailersAdapter.ViewHolder holder, int position) {
        TrailerDetails item = mData.get(position);
        holder.licenseeName.setText(item.getLicenseeName());
        holder.trailerName.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.dist.setText(item.getLicenseeDistance());

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        if(item.getPhoto()!=null)
            Glide.with(context).load(item.getPhoto().get(0).getData()).apply(options).into(holder.trailerImage);
        holder.trailerImage.setClipToOutline(true);
        int no = item.getUpsellItems().size();
        holder.upsellNumber.setText(no + " add ons");

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface SearchTrailerListener {
        void onTrailerClicked(TrailerDetails details);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView trailerImage;
        TextView trailerName, licenseeName, price, dist, upsellNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerImage = itemView.findViewById(R.id.trailer_image_search);
            trailerName = itemView.findViewById(R.id.tv_search_trailer_name);
            licenseeName = itemView.findViewById(R.id.tv_licensee_name);
            price = itemView.findViewById(R.id.tv_search_price);
            dist = itemView.findViewById(R.id.tv_dist);
            upsellNumber = itemView.findViewById(R.id.upsell_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION) {
                TrailerDetails item = mData.get(pos);
                mListener.onTrailerClicked(item);
            }
        }
    }
}
