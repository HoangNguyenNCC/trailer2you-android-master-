package com.applutions.t2y.ui.dashboard;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.applandeo.materialcalendarview.CalendarView;
import com.applutions.t2y.R;
import com.applutions.t2y.adapters.RatingsAdapter;
import com.applutions.t2y.adapters.TrailersAdapter;
import com.applutions.t2y.data.items.FeaturedDetails;
import com.applutions.t2y.data.items.RatingItem;
import com.applutions.t2y.data.response.RatingsResponse;
import com.applutions.t2y.data.response.search.SearchResponse;
import com.applutions.t2y.data.response.search.TrailerDetails;
import com.applutions.t2y.ui.auth.AuthActivity;
import com.applutions.t2y.ui.notifications.response.RentalObj;
import com.applutions.t2y.ui.search.SearchActivity;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment implements OnMapReadyCallback {

    SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<TrailerDetails> list = new ArrayList<>();
    private View ChildView;
    private int RecyclerViewItemPosition;
    String apiKey;
    PlacesClient placesClient;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    AutocompleteSupportFragment autocompleteFragment;
    BottomSheetDialog bottomSheetDialog;
    private static final String TAG = "DASHBOARD";
    View bottomSheetView;
    TextView addLine1, addLine2, linkCal, linkCancel, fromDate, toDate, browseTrailer, findTrailer;
    EditText add, fromTime, toTime, houseNum;
    RecyclerView trailers;
    Button linkTime, startSearch;

    CalendarView calendarView;

    SharedPrefs sharedPrefs;

    DashboardViewModel mViewModel;
    SearchResponse searchResponse;
    ProgressBar progressBar;
    Context context;

    View search;
    View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        try {
            if (bottomSheetView != null) {
                ViewGroup parent = (ViewGroup) bottomSheetView.getParent();
                if (parent != null)
                    parent.removeView(bottomSheetView);
            }

            sharedPrefs = SharedPrefs.getInstance(requireActivity());
            mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

            Intent i = getActivity().getIntent();
            Bundle args = i.getBundleExtra("Featured");

            mViewModel.getRatingsLiveData().observe(requireActivity(), response ->{
                ApiResponse.ApiResponseStatus status = response.getApiStatus();
                switch (status){
                    case LOADING:
                        Log.d("Here","Loading");
                        break;
                    case SUCCESS:
                        Log.d("Here", "RequestSuccess");
                        Log.d("Here", sharedPrefs.getRatings());
                        if (response.getData().size()>0 && sharedPrefs.getRatings().equalsIgnoreCase("Show Ratings")) {
//                            showPendingRatings(response.getData());
                            RatingsBrowseDialog dialog = RatingsBrowseDialog.newInstance(response.getData());
                            dialog.show(getParentFragmentManager(), "pending_ratings");
                        }
                        else
                            sharedPrefs.resetRatings();

                        break;
                }
            });

            root = inflater.inflate(R.layout.fragment_dashboard, container, false);
            context = root.getContext();
            apiKey = getString(R.string.google_maps);

            swipeRefreshLayout = root.findViewById(R.id.refresh_dashboard);
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            swipeRefreshLayout.setOnRefreshListener(() -> {
                mViewModel.sendFeaturedRequestList(context);
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            findTrailer = root.findViewById(R.id.tv_find_trailers);
            browseTrailer = root.findViewById(R.id.tv_browse_trailers);
            progressBar = root.findViewById(R.id.pb_dashboard);

            //Recycler View Horizontal Featured items
            trailers = root.findViewById(R.id.featured_trailer);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            LinearLayoutManager horizontal = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(trailers);
            trailers.setLayoutManager(layoutManager);
            trailers.setLayoutManager(horizontal);

            //Map Search Initialisation
            search = root.findViewById(R.id.search_bar);
            if (!Places.isInitialized()) {
                Places.initialize(context, apiKey);
                Log.d("Here", Boolean.toString(Places.isInitialized()));
            }
            placesClient = Places.createClient(context);

            if (args!=null){
                Log.d("Here", "Got Bundle");
                list = (ArrayList<TrailerDetails>) args.getSerializable("Featured");
                if (list.size()>0){
                    hideLoadingUI();
                    Log.d("Here", "hide loading");
                    TrailersAdapter adapter = new TrailersAdapter(list, getActivity());
                    trailers.setAdapter(adapter);
                    mViewModel.getPendingRatings(context);
                }
                else{
                    mViewModel.sendFeaturedRequestList(context);
                }
            }
            else
                mViewModel.sendFeaturedRequestList(context);

            mViewModel.getSearchTrailerListener().observe(requireActivity(), response -> {
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
                        hideLoadingUI();
                        searchResponse = response.getData();
                        list = searchResponse.getTrailers();
                        TrailersAdapter adapter = new TrailersAdapter(list, getActivity());
                        trailers.setAdapter(adapter);
                        mViewModel.getPendingRatings(context);
                        break;
                }
            });

        }
        catch (Exception T){
            Log.d("Error",T.toString());
            errorSnack(T.toString()).show();
        }
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            trailers.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        return false;
                    }
                });

                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    ChildView = rv.findChildViewUnder(e.getX(), e.getY());

                    if (ChildView != null && gestureDetector.onTouchEvent(e)) {

                        //Getting clicked value.
                        RecyclerViewItemPosition = rv.getChildAdapterPosition(ChildView);
                        TrailerDetails i = list.get(RecyclerViewItemPosition);
                        String type = i.getName();
                        sharedPrefs.setTrailerType(type);
                        FeaturedDetails featuredDetails = new FeaturedDetails(i.getName(),i.getType(),i.getDescription(),i.getFeatures(),i.getSize(),i.getTare(),i.getPhotos(), i.getCapacity());
                        Intent intent = new Intent(requireActivity(),FeaturedTrailerDetails.class);
                        intent.putExtra("from","featured");
                        intent.putExtra("details",featuredDetails);
                        startActivity(intent);
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });

            search.setOnClickListener(v -> {
                sharedPrefs.clearData();
//                initializeSearch(root);
                Intent i = new Intent(requireActivity(), FeaturedTrailerDetails.class);
                i.putExtra("from","search");
                startActivity(i);
            });


        }
        catch (Throwable T){
            Log.d("Error",T.toString());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

    }
    void getMap(){
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    void findViewsByID(int id){
        if(id == 1) {
            add = bottomSheetView.findViewById(R.id.et_add);
            addLine1 = bottomSheetView.findViewById(R.id.add_line1);
            addLine2 = bottomSheetView.findViewById(R.id.add_line2);
            linkCal = bottomSheetView.findViewById(R.id.tv_add);
            linkCancel = bottomSheetView.findViewById(R.id.tv_cancel);
            houseNum = bottomSheetView.findViewById(R.id.et_home);
        }
        if(id == 2){
            fromDate = bottomSheetView.findViewById(R.id.et_from_date);
            toDate = bottomSheetView.findViewById(R.id.et_to_date);
            fromTime = bottomSheetView.findViewById(R.id.et_from_time);
            toTime = bottomSheetView.findViewById(R.id.et_to_time);
            startSearch = bottomSheetView.findViewById(R.id.startSearch);
        }
        if(id == 3){
            calendarView = bottomSheetView.findViewById(R.id.calenderView);
            linkTime = bottomSheetView.findViewById(R.id.addDates);
        }

    }


    void showLoadingUI(){
        progressBar.setVisibility(View.VISIBLE);
        findTrailer.setVisibility(View.INVISIBLE);
        browseTrailer.setVisibility(View.INVISIBLE);
        trailers.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        search.setClickable(false);
    }

    private void hideLoadingUI() {
        findTrailer.setVisibility(View.VISIBLE);
        browseTrailer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        trailers.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        search.setClickable(true);
    }

    public Snackbar errorSnack(String message){
        Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(requireActivity(),R.color.red_gradient_start));
        return snackbar;
    }

}