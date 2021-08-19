package com.applutions.t2y.ui.dashboard;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.adapters.RatingsAdapter;
import com.applutions.t2y.data.items.RatingItem;
import com.applutions.t2y.data.response.RatingsResponse;
import com.applutions.t2y.ui.notifications.response.RentalObj;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class RatingsBrowseDialog extends BottomSheetDialogFragment {

    RecyclerView rv;
    RatingsAdapter adapter;

    ArrayList<RatingItem> list = new ArrayList<>();
    private RatingsBrowseDialog(ArrayList<RatingsResponse.Obj> response){
        for(RatingsResponse.Obj o: response) {
            RentalObj i = o.getInvoice();
            String startD = i.getRentalPeriod().getStart();
            String endD = i.getRentalPeriod().getEnd();
            String startT, endT;
            startT = startD.substring(startD.indexOf("T") + 1, startD.indexOf("Z"));
            endT = endD.substring(endD.indexOf("T") + 1, endD.indexOf("Z"));
            startD = startD.substring(0, startD.indexOf("T"));
            endD = endD.substring(0, endD.indexOf("T"));

            SimpleDateFormat inputD = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat outputD = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            SimpleDateFormat inputT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);
            SimpleDateFormat outputT = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            inputT.setTimeZone(TimeZone.getTimeZone("UTC"));
            outputT.setTimeZone(TimeZone.getDefault());
            String date = "";
            try {
                date = outputD.format(inputD.parse(startD)) + " " + outputT.format(inputT.parse(startT)) + " - "
                        + outputD.format(inputD.parse(endD)) + " " + outputT.format(inputT.parse(endT));
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            Log.d("HERE", i.get_id());
            list.add(new RatingItem(o.getTrailer().getName(), date, o.getTrailer().getPhotos().get(0), i.get_id(), i.getRentalStatus(), o.getLicensee().getName()));
        }
    }

    public static RatingsBrowseDialog newInstance(ArrayList<RatingsResponse.Obj> response) {

        return new RatingsBrowseDialog(response);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sheet_ratings, container,
                false);
        rv = view.findViewById(R.id.search_trailers_items);
        adapter = new RatingsAdapter(list, getActivity());
        Log.d("here", list.size()+"");
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        // get the views and attach the listener

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }
}
