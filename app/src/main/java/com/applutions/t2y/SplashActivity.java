package com.applutions.t2y;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.applutions.t2y.adapters.TrailersAdapter;
import com.applutions.t2y.ui.auth.AuthActivity;
import com.applutions.t2y.ui.dashboard.DashboardViewModel;
import com.applutions.t2y.ui.search.SearchActivity;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsActivity;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.applutions.t2y.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    DashboardViewModel vm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        vm = new ViewModelProvider(this).get(DashboardViewModel.class);
        vm.sendFeaturedRequestList(this);
        vm.getSearchTrailerListener().observe(this, response->{
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    break;
                case FAILED:
                    if (response.getErrorMessage().message().equalsIgnoreCase("User Not found")) {
                        Toast.makeText(this, "Authorization Failed", Toast.LENGTH_SHORT).show();
                        SharedPrefs.getInstance(this).setAuthToken("");
                        startActivity(new Intent(this, AuthActivity.class));
                    } else
                        Toast.makeText(this, response.getErrorMessage().message(), Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS:
                    Intent i = new Intent(this, BottomNavActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("Featured", response.getData().getTrailers());
                    i.putExtra("Featured", args);
                    startActivity(i);
                    break;
            }
        });
    }
}