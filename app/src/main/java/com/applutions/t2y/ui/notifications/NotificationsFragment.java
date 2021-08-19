package com.applutions.t2y.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.applutions.t2y.R;
import com.applutions.t2y.customViews.RegularTextView;
import com.applutions.t2y.ui.auth.AuthActivity;
import com.applutions.t2y.ui.notifications.response.RemindersItem;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;

import org.bouncycastle.util.io.TeeOutputStream;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    View mView;
    RecyclerView rsvNotification;
    private NotificationViewModel mViewModel;
    NotificationAdapter notificationAdapter;
    ProgressBar mProgressBar;
    LinearLayout mContentLayout;
    RegularTextView txtNoRecord;
    Context context;

    ArrayList<RemindersItem> mList=new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.fragment_notifications, container, false);
        context = mView.getContext();
        swipeRefreshLayout = mView.findViewById(R.id.refresh_notifications);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.getDetails(context, "reminders");
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rsvNotification=mView.findViewById(R.id.rsvNotification);
        txtNoRecord=mView.findViewById(R.id.txtNoRecord);
        mProgressBar = mView.findViewById(R.id.pb_loading_notifications);
        mContentLayout = mView.findViewById(R.id.linearLayout2);
        notificationAdapter=new NotificationAdapter(context,mList, this.getParentFragmentManager());
        rsvNotification.setAdapter(notificationAdapter);
        return mView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        mViewModel.getDetails(context, "reminders");

        mViewModel.getRentalLiveData().observe(requireActivity(), response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    showLoading();
                    break;
                case FAILED:
                    hideLoading();
                    if (response.getErrorMessage().message().equalsIgnoreCase("User Not found")){
                        Toast.makeText(context, "Authorization Failed", Toast.LENGTH_SHORT).show();
                        SharedPrefs.getInstance(context).setAuthToken("");
                        startActivity(new Intent(getActivity(), AuthActivity.class));
                    }
                    else
                        Toast.makeText(context, response.getErrorMessage().message(), Toast.LENGTH_LONG).show();
//                    SharedPrefs.getInstance(context).setAuthToken("");
//                    startActivity(new Intent(getActivity(), AuthActivity.class));
                    break;
                case SUCCESS:
                    hideLoading();
                    mList.clear();
                    mList=response.getData().getRemindersList();
                    if(mList.size()>0) {
                        txtNoRecord.setVisibility(View.GONE);
                        rsvNotification.setVisibility(View.VISIBLE);
                        notificationAdapter.notifyDataSetChanged(mList);
                    }
                    else{
                        txtNoRecord.setVisibility(View.VISIBLE);
                        rsvNotification.setVisibility(View.GONE);
                    }

                    break;
            }
        });
    }

    private void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
    }
}