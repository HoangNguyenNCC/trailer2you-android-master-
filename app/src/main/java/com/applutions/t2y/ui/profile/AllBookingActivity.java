package com.applutions.t2y.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.applutions.t2y.R;
import com.applutions.t2y.customViews.RegularTextView;
import com.applutions.t2y.ui.auth.AuthActivity;
import com.applutions.t2y.ui.notifications.NotificationAdapter;
import com.applutions.t2y.ui.notifications.NotificationViewModel;
import com.applutions.t2y.ui.notifications.response.RemindersItem;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;

import java.util.ArrayList;

public class AllBookingActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_booking);
        context = this;
        swipeRefreshLayout = findViewById(R.id.refresh_notifications);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.getDetails(context, "all");
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rsvNotification = findViewById(R.id.rsvNotification);
        txtNoRecord = findViewById(R.id.txtNoRecord);
        mProgressBar = findViewById(R.id.pb_loading_notifications);
        mContentLayout = findViewById(R.id.linearLayout2);
        notificationAdapter=new NotificationAdapter(this,mList,getSupportFragmentManager());
        rsvNotification.setAdapter(notificationAdapter);

        mViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        mViewModel.getDetails(context, "all");

        mViewModel.getRentalLiveData().observe(this, response -> {
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
                        startActivity(new Intent(this, AuthActivity.class));
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