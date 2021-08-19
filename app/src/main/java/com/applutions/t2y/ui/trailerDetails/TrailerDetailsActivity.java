package com.applutions.t2y.ui.trailerDetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.data.items.TrailerViewReq;
import com.applutions.t2y.data.response.Licensee;
import com.applutions.t2y.data.response.RentalItem;
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.data.response.search.UpsellItem;
import com.applutions.t2y.ui.rentTrailer.BookActivity;
import com.applutions.t2y.ui.search.SearchActivity;
import com.applutions.t2y.ui.trailerDetails.details.TrailerDetailsFragment;
import com.applutions.t2y.ui.trailerDetails.licensee.LicenseeAdapter;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.applutions.t2y.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TrailerDetailsActivity extends AppCompatActivity implements LifecycleOwner, TrailerDetailsFragment.TrailerDetailsListener, OnMapReadyCallback {

    private NavController navController;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private TrailerDetailsViewModel mViewModel;
    private ArrayList<UpsellItem> upsellItems;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    LatLng l;
    String loc;

    TrailerViewResponse TvResponse;
    LinearLayout bottomNav;
    Button btnRent, btnUpsell, btnBook;

    int status = 0;
    @Override
    public void onShowOwnerDetails(Licensee licensee) {
         showOwnerDetails(licensee);
    }

    enum FragmentOption {
        TRAILER_DETAILS,
        UPSELL_ITEMS
    }

    private FragmentOption currentFragment = FragmentOption.TRAILER_DETAILS;

    public int status(){
        return status;
    }

    public TrailerDetailsViewModel getmViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_details);
        bottomNav = findViewById(R.id.bottomNavigationView);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        mViewModel = new ViewModelProvider(this).get(TrailerDetailsViewModel.class);
        SharedPrefs sharedPrefs = SharedPrefs.getInstance(this);
        String rentalId = sharedPrefs.getItemId();
        if (args!=null) {
            upsellItems = (ArrayList<UpsellItem>) args.getSerializable("UPSELL");
        }
        if(Objects.equals(intent.getStringExtra("from"), "reminders")){
            bottomNav.setVisibility(View.GONE);
            status = 1;
        }
            ArrayList<String> dates = new ArrayList<>();
            dates.add(sharedPrefs.getStartDate());
            dates.add(sharedPrefs.getEndDate());
            ArrayList<String> times = new ArrayList<>();
            times.add(sharedPrefs.getStartTime());
            times.add(sharedPrefs.getEndTime());
            ArrayList<Double> loc = new ArrayList<>();
            loc.add(Double.parseDouble(sharedPrefs.getLat()));
            loc.add(Double.parseDouble(sharedPrefs.getLong()));
            TrailerViewReq req = new TrailerViewReq(rentalId, dates, times, loc, "door2door");
            mViewModel.getTrailerView(this, req);

//        mViewModel.getViewLiveData().observe(this, response->{
//            ApiResponse.ApiResponseStatus status = response.getApiStatus();
//            switch (status) {
//                case LOADING:
////                    setLoadingUi();
//                    break;
//                case FAILED:
////                    removeLoadingUi();
//                    Utils.getErrorSnackBar(this, bottomNav, response.getErrorMessage().message()).show();
//                    break;
//                case SUCCESS:
////                    removeLoadingUi();
//                    TvResponse = response.getData();
//                    break;
//            }
//        });

        navController = Navigation.findNavController(this,R.id.nav_host_fragment_trailer_details_activity);

        btnRent = findViewById(R.id.btn_rent);
        btnUpsell = findViewById(R.id.btn_upsell);
        btnBook = findViewById(R.id.btn_rent_trailer);

        btnRent.setOnClickListener(v -> {
            if (currentFragment != FragmentOption.TRAILER_DETAILS) {
                navController.navigate(R.id.action_trailerUpsellFragment_to_trailerDetailsFragment2);
                currentFragment = FragmentOption.TRAILER_DETAILS;
                btnRent.setBackground(getDrawable(R.drawable.dark_rounded_button));
                btnRent.setTextColor(getColor(R.color.white));
                btnUpsell.setTextColor(getColor(R.color.colorPrimaryDark));
                btnUpsell.setBackground(getDrawable(R.drawable.grey_rounded_button));
            }
        });

        btnUpsell.setOnClickListener(v -> {
            if (currentFragment != FragmentOption.UPSELL_ITEMS) {
                navController.navigate(R.id.action_trailerDetailsFragment_to_trailerUpsellFragment2);
                currentFragment = FragmentOption.UPSELL_ITEMS;
                btnUpsell.setBackground(getDrawable(R.drawable.dark_rounded_button));
                btnUpsell.setTextColor(getColor(R.color.white));
                btnRent.setTextColor(getColor(R.color.colorPrimaryDark));
                btnRent.setBackground(getDrawable(R.drawable.grey_rounded_button));
            }
        });

        btnBook.setOnClickListener(v -> {
            Intent i = new Intent(this, BookActivity.class);
            i.putExtra("from","search");
            startActivity(i);
        });


    }

    public void showOwnerDetails(@NonNull Licensee licensee) {
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
        BottomSheetBehavior behaviorField = bottomSheetDialog.getBehavior();
        behaviorField.setState(BottomSheetBehavior.STATE_EXPANDED);
        behaviorField.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING){
                    behaviorField.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        bottomSheetView = getLayoutInflater()
                .inflate(
                        R.layout.bottom_sheet_owner_profile,
                        findViewById(R.id.owner_profile_container)
                );

        ImageView trailer_icons = bottomSheetView.findViewById(R.id.trailer_icon);
        ImageView ownerProfile = bottomSheetView.findViewById(R.id.profile_photo_owner);
        TextView tvLicenseeName = bottomSheetView.findViewById(R.id.licensee_name);
        TextView tvTrailerName = bottomSheetView.findViewById(R.id.trailer_name);
        TextView tvOwnerName = bottomSheetView.findViewById(R.id.owner_name);
        TextView tvOwnerAddress = bottomSheetView.findViewById(R.id.owner_address);
        TextView tvOwnerWorkTimings = bottomSheetView.findViewById(R.id.onwer_work_timings);
        TextView tvOwnerDays = bottomSheetView.findViewById(R.id.tv_find_trailers);
        TextView tvOwnerCompanyName = bottomSheetView.findViewById(R.id.owner_company_name);
        RecyclerView rvOwnerTrailers = bottomSheetView.findViewById(R.id.rv_owner_trailers);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.owner_location);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LicenseeAdapter adapter = new LicenseeAdapter();

        rvOwnerTrailers.setLayoutManager(layoutManager);
        rvOwnerTrailers.setHasFixedSize(true);
        rvOwnerTrailers.setAdapter(adapter);

        Log.v("TrailerDetailsActivity", licensee.toString());

        if (licensee != null) {
            tvTrailerName.setText("Licensee");
            tvOwnerName.setText(licensee.getName());
            tvOwnerAddress.setText(licensee.getAddress().getCountry());
            String working = licensee.getWorkingDays()[0] +  "-" + licensee.getWorkingDays()[licensee.getWorkingDays().length - 1];
            tvOwnerDays.setText(working);
            ArrayList<String> days = new ArrayList<>(Arrays.asList(licensee.getWorkingDays()));
            if (licensee.getWorkingDays().length==7){
                tvOwnerDays.setText("All Days");
            }
            tvOwnerWorkTimings.setText(licensee.getWorkingHours());

            licensee.getAddress().getCoordinates();
            l = new LatLng(licensee.getAddress().getCoordinates()[0],licensee.getAddress().getCoordinates()[1]);
            loc = licensee.getAddress().getCity();

            tvOwnerCompanyName.setText(licensee.getOwnerName());
            ArrayList<RentalItem> rentalItems = new ArrayList(Arrays.asList(licensee.getRentalItems()));
            Glide.with(this)
                    .load(rentalItems.get(0).getPhoto().get(0).getData())
                    .into(trailer_icons);
            adapter.setData(rentalItems);

            tvLicenseeName.setText(licensee.getName());

            if (licensee.getOwnerPhoto().getData() != null) {
                Glide.with(this)
                        .load(licensee.getOwnerPhoto().getData())
                        .into(ownerProfile);
            }
        }
        bottomSheetDialog.setOnCancelListener(dialog -> getSupportFragmentManager().beginTransaction().remove(mapFragment).commit());

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(status == 1){
            super.onBackPressed();
        }
        else {
            if (currentFragment == FragmentOption.UPSELL_ITEMS) {
                navController.navigate(R.id.action_trailerUpsellFragment_to_trailerDetailsFragment2);
                currentFragment = FragmentOption.TRAILER_DETAILS;
                btnRent.setBackground(getDrawable(R.drawable.dark_rounded_button));
                btnRent.setTextColor(getColor(R.color.white));
                btnUpsell.setTextColor(getColor(R.color.colorPrimaryDark));
                btnUpsell.setBackground(getDrawable(R.drawable.grey_rounded_button));
            } else if (currentFragment == FragmentOption.TRAILER_DETAILS) {
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(l!=null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(l).title("Marker in" + loc));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
        }

    }
}