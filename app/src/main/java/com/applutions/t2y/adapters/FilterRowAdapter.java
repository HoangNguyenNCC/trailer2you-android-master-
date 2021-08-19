package com.applutions.t2y.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.data.response.filter.FilterType;

import java.util.ArrayList;
import java.util.List;

public class FilterRowAdapter extends RecyclerView.Adapter<FilterRowAdapter.ViewHolder> {

    public interface OnItemCheckListener {
        void onItemCheck(FilterType item);
        void onItemUncheck(FilterType item);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    public FilterRowAdapter(@NonNull OnItemCheckListener onItemCheckListener, List<FilterType> list) {
        this.onItemClick = onItemCheckListener;
        this.list = list;
    }

    private List<FilterType> list = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_filters,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterType item = list.get(position);
        holder.filterName.setText(item.getName());
        holder.itemView.setOnClickListener(v -> {
            holder.selected.setChecked(!holder.selected.isChecked());
            if (holder.selected.isChecked()){
                onItemClick.onItemCheck(item);
            }
            else{
                onItemClick.onItemUncheck(item);
            }
        });
        if(item.checked == 1){
            holder.selected.setChecked(true);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView filterName;
        CheckBox selected;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filterName = itemView.findViewById(R.id.tv_filter_name);
            selected = itemView.findViewById(R.id.checkbox_filter);
            selected.setClickable(false);
        }
        private void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
