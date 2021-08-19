package com.applutions.t2y.ui.trailerDetails.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.adapters.AdditionalImageAdapter;
import com.applutions.t2y.data.items.PhotoObj;
import com.applutions.t2y.data.response.Licensee;
import com.applutions.t2y.data.response.LicenseeResponse;
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.data.response.trailer.TrailerObj;
import com.applutions.t2y.ui.rentTrailer.BookActivity;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsActivity;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsViewModel;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.applutions.t2y.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


public class TrailerDetailsFragment extends Fragment implements AdditionalImageAdapter.ItemClick {

    private TrailerDetailsViewModel mViewModel;

    private Button btnRent;
    private TextView tvSubtitle;
    private TextView tvTitle;
    private ImageView ivMain;
    private TextView tvDescription;
    private TextView tvSize;
    private ImageView ivInsured;
    private ImageView ivServiced;
    private TextView ageLabel, ageTrailer;
    private TextView capacityLabel, capacityTrailer;
    private TextView tareLabel, tareTrailer;
    private TextView tvTrailerCost;
    private TextView tvShowOwner;
    private TextView tvOwnerName;
    private ProgressBar pbLoading;
    private TrailerDetailsListener mListener;
    private ConstraintLayout container;
    int stat;
    private RatingBar ratingBar;
    ConstraintLayout price;

    ArrayList<PhotoObj> urls;
    RecyclerView addImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trailer_details, container, false);
        return  v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TrailerDetailsListener) {
            mListener = (TrailerDetailsListener) context;
        } else {
            throw new ClassCastException(context.toString() + " should instantiate UpsellFragmentListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TrailerDetailsActivity a = (TrailerDetailsActivity)getActivity();
        mViewModel = a.getmViewModel();
        getViewRefs();

        SharedPrefs sharedPrefs = SharedPrefs.getInstance(requireActivity());
        String rentalId = sharedPrefs.getItemId();

        mViewModel.getLicenseeLiveData().observe(requireActivity(), response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case FAILED:
                    Toast.makeText(requireActivity(), "Unable to get licensee details", Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    break;
                case SUCCESS:
                    tvOwnerName.setText(response.getData().getLicenseeObj().getName());
                    tvSubtitle.setText(response.getData().getLicenseeObj().getName());
                    tvShowOwner.setOnClickListener(v -> {
                        ApiResponse<LicenseeResponse> licenseeResponse = mViewModel.getLicenseeLiveData().getValue();
                        if (licenseeResponse != null) {
                            Licensee licensee = licenseeResponse.getData().getLicenseeObj();
                            mListener.onShowOwnerDetails(licensee);
                        }
                    });
                    break;
            }
        });

        mViewModel.getViewLiveData().observe(requireActivity(), response->{
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    setLoadingUi();
                    break;
                case FAILED:
                    removeLoadingUi();
                    Utils.getErrorSnackBar(requireActivity(), btnRent, response.getErrorMessage().message()).show();
                    break;
                case SUCCESS:
                    removeLoadingUi();
                    stat = a.status();
                    setUiData(response.getData());
                    mViewModel.getLiceseeDetails(getActivity(),response.getData().getLicenseeObj().getLicenseeId());
                    break;
            }
        });

    }

    private void getViewRefs() {
        Activity context = requireActivity();
        container = context.findViewById(R.id.details_container);
        btnRent = context.findViewById(R.id.btn_rent_trailer);
        tvSubtitle = context.findViewById(R.id.tv_subtitle);
        tvTitle = context.findViewById(R.id.tv_title);
        ivMain = context.findViewById(R.id.iv_main);
        tvDescription = context.findViewById(R.id.tv_desc);
        tvSize = context.findViewById(R.id.tv_trailer_size);
        ivInsured = context.findViewById(R.id.iv_insured_status);
        ivServiced = context.findViewById(R.id.iv_serviced_status);
        ageLabel = context.findViewById(R.id.tv_age_label);
        ageTrailer = context.findViewById(R.id.tv_age);
        capacityLabel = context.findViewById(R.id.tv_capacity_label);
        capacityTrailer = context.findViewById(R.id.tv_capacity);
        tareLabel = context.findViewById(R.id.tv_tare_label);
        tareTrailer = context.findViewById(R.id.tv_tare);
        tvTrailerCost = context.findViewById(R.id.tv_trailer_cost);
        pbLoading = context.findViewById(R.id.pb_loading);
        tvShowOwner = context.findViewById(R.id.tv_view_owner);
        tvOwnerName = context.findViewById(R.id.tv_owner_name);
        price = context.findViewById(R.id.view_details);
        addImg = context.findViewById(R.id.extra_img);
        ratingBar = context.findViewById(R.id.ratings_view);
    }

    private void setLoadingUi() {
        pbLoading.setVisibility(View.VISIBLE);
        container.setVisibility(View.INVISIBLE);
    }

    private void removeLoadingUi() {
        pbLoading.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    private void setUiData(TrailerViewResponse response) {
        TrailerObj rentalObj = response.getTrailerObj();

        tvOwnerName.setText(response.getLicenseeObj().getLicenseeName());
        tvSubtitle.setText(response.getLicenseeObj().getLicenseeName());

        if (rentalObj == null) {
            Toast.makeText(requireActivity(), "NULL OBJECTTTT....", Toast.LENGTH_SHORT).show();
            return;
        }

        tvTitle.setText(rentalObj.getName());
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.trailer_image)
                .error(R.drawable.trailer_image);
        Glide.with(requireActivity()).load(rentalObj.getPhotos().get(0).getData()).apply(options).into(ivMain);
        tvDescription.setText(rentalObj.getDescription());
        tvSize.setText(rentalObj.getSize());
        urls = rentalObj.getPhotos();
        AdditionalImageAdapter additionalImageAdapter = new AdditionalImageAdapter(rentalObj.getPhotos(), this);
        addImg.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        addImg.setAdapter(additionalImageAdapter);

        if(rentalObj.getServiced()) {
            ivServiced.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_check_24));
            ivServiced.setColorFilter(ContextCompat.getColor(getActivity(), R.color.c2_light_green), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if(rentalObj.getInsured()){
            ivInsured.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_check_24));
            ivInsured.setColorFilter(ContextCompat.getColor(getActivity(), R.color.c2_light_green), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        ageTrailer.setText(rentalObj.getAge()+" Years");

        capacityTrailer.setText(rentalObj.getCapacity());
        tareTrailer.setText(rentalObj.getTare());
        ratingBar.setRating(rentalObj.getRating());
        //TODO calculate total costs
        tvTrailerCost.setText(rentalObj.getTotalCharges().getTotal() + " AUD");
//        btnRent.setOnClickListener(v -> {
//            Intent i = new Intent(requireActivity(), BookActivity.class);
//            i.putExtra("from","search");
//            startActivity(i);
//            requireActivity().finish();
//        });
        if(stat == 1){
            btnRent.setVisibility(View.GONE);
            price.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(View view, int position) {
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.trailer_image)
                .error(R.drawable.trailer_image);
        if (urls!=null)
            Glide.with(requireActivity()).load(urls.get(position).getData()).apply(options).into(ivMain);
    }

    public interface TrailerDetailsListener {
        void onShowOwnerDetails(Licensee licensee);
    }

}