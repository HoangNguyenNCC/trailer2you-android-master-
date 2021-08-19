package com.applutions.t2y.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.data.items.BookingUpsell;
import com.applutions.t2y.data.response.booking.Booking;
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.data.response.search.UpsellItem;
import com.applutions.t2y.utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UpsellItemsAdapter extends RecyclerView.Adapter<UpsellItemsAdapter.ViewHolder> {

    private List<TrailerViewResponse.Upsell> upsellItems = new ArrayList<>();
    private Context context;
    private UpsellItemsListener mListener;
    private static final int BUTTON_ADD = 944;
    private static final int BUTTON_REMOVE = 723;
    ArrayList<BookingUpsell> bookedItems = new ArrayList<>();
    SharedPrefs sharedPrefs;

    public ArrayList<BookingUpsell> getBookedItems() {
        return bookedItems;
    }

    public UpsellItemsAdapter(UpsellItemsListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        sharedPrefs = SharedPrefs.getInstance(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upsell_trailers_list_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrailerViewResponse.Upsell item = upsellItems.get(position);
        holder.setData(this.context, item);
//        holder.setOnClick();
        if(item.getBtnID()==0) {
            item.setBtnID(BUTTON_ADD);
        }

    }

    @Override
    public int getItemCount() {
        return upsellItems.size();
    }

    public void setupsellItems(List<TrailerViewResponse.Upsell> items) {
        this.upsellItems.clear();
        upsellItems = items;
        notifyDataSetChanged();
    }

    public interface UpsellItemsListener {
        void onClick(UpsellItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvItemName;
        private TextView tvItemDesc;
        private TextView tvItemPrice;
        private Button btnAdd;
        private ImageView imgItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.upsell_item_name);
            tvItemDesc = itemView.findViewById(R.id.upsell_item_descrition);
            tvItemPrice = itemView.findViewById(R.id.upsell_item_price);
            btnAdd = itemView.findViewById(R.id.upsell_item_add);
            imgItem = itemView.findViewById(R.id.upsell_item_image);
            btnAdd.setOnClickListener(this);
        }

        protected void setData(Context context, TrailerViewResponse.Upsell upsellItem) {
            tvItemName.setText(upsellItem.getName());
//            tvItemDesc.setText(upsellItem.get);
            tvItemPrice.setText(upsellItem.getTotalCharges().getTotal() + " AUD");

            if (upsellItem.getPhoto().get(0) != null) {
                Glide.with(context)
                        .load(upsellItem.getPhoto().get(0).getData())
                        .into(imgItem);
            }
            if (upsellItem.getBtnID()==BUTTON_REMOVE){
                btnAdd.setBackground(context.getDrawable(R.drawable.grey_rounded_button));
                btnAdd.setTextColor(context.getColor(R.color.colorPrimaryDark));
                btnAdd.setText("Remove");
            }
        }


        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION){
                TrailerViewResponse.Upsell item = upsellItems.get(pos);
                if(item.getBtnID() == BUTTON_ADD) {
                    PopupMenu popup = new PopupMenu(context,btnAdd);
                    popup.inflate(R.menu.popup);
                    popup.setOnMenuItemClickListener(i -> {
                        Gson gson;
                        String json;
                        item.setBtnID(BUTTON_REMOVE);
                        btnAdd.setBackground(context.getDrawable(R.drawable.grey_rounded_button));
                        btnAdd.setTextColor(context.getColor(R.color.colorPrimaryDark));
                        btnAdd.setText("Remove");
                        switch (i.getItemId()) {
                            case R.id.item1:
                                bookedItems.add(new BookingUpsell(item.get_id(),1, item.getTotal()));
                                gson = new Gson();
                                json = gson.toJson(bookedItems);
                                sharedPrefs.setUpsellItems(json);
                                Toast.makeText(context, "Works", Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.item2:
                                bookedItems.add(new BookingUpsell(item.get_id(),2, item.getTotal()));
                                gson = new Gson();
                                json = gson.toJson(bookedItems);
                                sharedPrefs.setUpsellItems(json);
                                return true;
                            case R.id.item3:
                                bookedItems.add(new BookingUpsell(item.get_id(),3, item.getTotal()));
                                gson = new Gson();
                                json = gson.toJson(bookedItems);
                                sharedPrefs.setUpsellItems(json);
                                return true;
                            case R.id.item4:
                                bookedItems.add(new BookingUpsell(item.get_id(),4, item.getTotal()));
                                gson = new Gson();
                                json = gson.toJson(bookedItems);
                                sharedPrefs.setUpsellItems(json);
                                return true;
                            case R.id.item5:
                                bookedItems.add(new BookingUpsell(item.get_id(),5, item.getTotal()));
                                gson = new Gson();
                                json = gson.toJson(bookedItems);
                                sharedPrefs.setUpsellItems(json);
                                return true;
                            default:
                                return false;
                        }
                    });
                    popup.show();
                }
                else if(item.getBtnID() == BUTTON_REMOVE){
                    item.setBtnID(BUTTON_ADD);
                    String jsonUpsell = sharedPrefs.getUpsellItems();
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<BookingUpsell>>(){}.getType();
                    ArrayList<BookingUpsell> upsellItems= gson.fromJson(jsonUpsell, type);
                    ArrayList<BookingUpsell> copy = new ArrayList<>(upsellItems);
                    for (BookingUpsell i: copy) {
                        if(item.get_id().equals(i.getId())){
                            upsellItems.remove(i);
                            Log.d("Removed Upsell",item.getName());
                        }
                    }
                    String json = gson.toJson(upsellItems);
                    sharedPrefs.setUpsellItems(json);
                    btnAdd.setBackground(context.getDrawable(R.drawable.dark_rounded_button));
                    btnAdd.setTextColor(context.getColor(R.color.white));
                    btnAdd.setText("Add");
                }
            }
        }
    }
}
