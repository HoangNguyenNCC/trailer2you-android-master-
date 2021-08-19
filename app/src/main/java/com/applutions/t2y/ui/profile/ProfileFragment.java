package com.applutions.t2y.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.applutions.t2y.R;
import com.applutions.t2y.customViews.BoldTextView;
import com.applutions.t2y.customViews.RegularTextView;
import com.applutions.t2y.ui.auth.AuthActivity;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ConcurrentModificationException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.NONE;

public class ProfileFragment extends Fragment {

    View mView;
    BoldTextView mName,mAddress;
    CircleImageView mImg;
    RegularTextView mTxtLogout;
    NestedScrollView profilePage;
    ProgressBar pb;
    Context context;
    CardView AboutUs, NotifView;
    SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = mView.getContext();
        mName=mView.findViewById(R.id.userName);
        mAddress=mView.findViewById(R.id.userAddress);
        mImg=mView.findViewById(R.id.userProfileImageView);
        mTxtLogout=mView.findViewById(R.id.logout);
        AboutUs = mView.findViewById(R.id.cardViewAboutUs);
        NotifView = mView.findViewById(R.id.cardViewNotification);
        AboutUs.setOnClickListener(v->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.trailers2000.com.au/who-we-are"));
            startActivity(browserIntent);
        });
        NotifView.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), AllBookingActivity.class));
        });
        mTxtLogout.setOnClickListener(view -> {

            SharedPrefs.getInstance(requireContext()).setAuthToken("");
            startActivity(new Intent(getActivity(), AuthActivity.class));
        });
        mImg.setOnClickListener(view -> startActivity(new Intent(getActivity(),EditProfileActivity.class)));
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileViewModel mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        pb = view.findViewById(R.id.pb_profile);
        profilePage = view.findViewById(R.id.profilePage);
        view.findViewById(R.id.imgAboutUs).setClipToOutline(true);
        view.findViewById(R.id.imgNotification).setClipToOutline(true);

        swipeRefreshLayout = view.findViewById(R.id.profileSwipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.getDetails(context);
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mViewModel.getDetails(context);

        mViewModel.getRentalLiveData().observe(requireActivity(), response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    showLoadingUI();
                    break;
                case FAILED:
                    hideLoadingUI();
                    if (response.getErrorMessage().message().equalsIgnoreCase("User Not found")){
                        Toast.makeText(context, "Authorization Failed", Toast.LENGTH_SHORT).show();
                        SharedPrefs.getInstance(context).setAuthToken("");
                        startActivity(new Intent(getActivity(), AuthActivity.class));
                    }
                    else
                        Toast.makeText(context, response.getErrorMessage().message(), Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS:
                    //Toast.makeText(requireContext(), "GOT DATA....", Toast.LENGTH_SHORT).show();
                    mName.setText(response.getData().getUserObj().getName());
                    mAddress.setText(response.getData().getUserObj().getAddress().getText());
                    Glide.with(view.getContext())
                            .load(response.getData().getUserObj().getPhoto().getData())
                            .diskCacheStrategy(NONE)
                            .into(mImg);
                    hideLoadingUI();
                    break;
            }
        });
    }

    void showLoadingUI(){
        pb.setVisibility(View.VISIBLE);
        profilePage.setVisibility(View.INVISIBLE);
    }

    void hideLoadingUI(){
        pb.setVisibility(View.GONE);
        profilePage.setVisibility(View.VISIBLE);
    }
}