package com.applutions.t2y.ui.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.adapters.RatingsAdapter;
import com.applutions.t2y.data.items.PostRatingBody;
import com.applutions.t2y.data.items.RatingItem;
import com.applutions.t2y.data.response.RatingsResponse;
import com.applutions.t2y.ui.notifications.response.RentalObj;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class RatingsPage extends BottomSheetDialogFragment {

    SharedPrefs prefs;
    RatingBar ratingBar;
    EditText review;
    CircularProgressButton postButton;
    private Context context;
    private View root;
    private DashboardViewModel mViewModel;
    private TextView skip;

    public static RatingsPage newInstance() {
        return new RatingsPage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sheet_stars_rating, container,
                false);
        prefs = SharedPrefs.getInstance(getContext());
        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        mViewModel.getPostRatingsLiveData().observe(requireActivity(), response->{
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status){
                case LOADING:
                    postButton.startAnimation();
                    Log.d("Post Ratings","Loading...");
                    break;
                case SUCCESS:
                    postButton.stopAnimation();
                    Toast.makeText(context, "Thank you for the review!!", Toast.LENGTH_LONG).show();
                    this.dismiss();
                    break;
                case FAILED:
                    postButton.stopAnimation();
                    Log.d("Post Ratings","Failed "+response.getErrorMessage().message());
                    break;
            }
        });
        // get the views and attach the listener
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        context = view.getContext();
        findViews();
        postButton.setOnClickListener(v->mViewModel.postRatings(context, new PostRatingBody(prefs.getItemId(), (int)ratingBar.getRating(), review.getText().toString())));
        skip.setOnClickListener(v-> {
            prefs.setRatings();
            dismiss();
        });
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

    private void findViews(){
        postButton = root.findViewById(R.id.submit_feedback_button);
        ratingBar = root.findViewById(R.id.ratings);
        review = root.findViewById(R.id.reviews);
        skip = root.findViewById(R.id.skip_feedback);
    }
}
