package com.applutions.t2y.ui.search;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.BottomNavActivity;
import com.applutions.t2y.R;
import com.applutions.t2y.adapters.FilterRowAdapter;
import com.applutions.t2y.adapters.SearchTrailersAdapter;
import com.applutions.t2y.data.response.filter.FilterObj;
import com.applutions.t2y.data.response.filter.FilterResponse;
import com.applutions.t2y.data.response.filter.FilterType;
import com.applutions.t2y.data.response.search.SearchResponse;
import com.applutions.t2y.data.response.search.TrailerDetails;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsActivity;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchTrailersAdapter.SearchTrailerListener {

    int wait;
    ProgressBar pb;

    List<TrailerDetails> listData = new ArrayList<>(), unsortedData;

    SearchViewModel mViewModel;

    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;

    Button filterConnect;
    Button sortContent;
    Button sortRequest;
    Button filterRequest;

    LinearLayout typeFilter;
    LinearLayout deliveryFilter;
    LinearLayout modelFilter;
    LinearLayout typeList;
    LinearLayout deliveryList;
    LinearLayout modelList;
    LinearLayout pricesList;
    LinearLayout distanceList;
    LinearLayout ratingList;
    LinearLayout pricesSort;
    LinearLayout distanceSort;
    LinearLayout ratingSort;
    LinearLayout buttonHolder;

    ViewAnimator type;
    ViewAnimator delivery;
    ViewAnimator model;
    ViewAnimator price;
    ViewAnimator distance;
    ViewAnimator rating;

    SharedPrefs sharedPrefs;

    RecyclerView searchItems;
    RecyclerView typeFilterView;
    RecyclerView deliveryFilterView;
    RecyclerView modelFilterView;

    SearchResponse searchResponse;

    FilterResponse filterResponse;

    FilterObj filterObj;

    ArrayList<FilterType> selectedFilter = new ArrayList<>();
    ArrayList<String> filterCodes = new ArrayList<>();
    ArrayList<String> filterNames = new ArrayList<>();
    SearchTrailersAdapter adapter;

    CheckBox l2h, h2l, nearSort, farSort;
    TextView clearSort, clearFilters, noTrailers, rentDates, locationName;
    CardView locHeading;
    int sortStatus = 0, distanceSortStatus = 0;
    // 1 : High to low
    // 2 : Low to High
    int typeApplied = 0, modelApplied = 0;
    boolean typeFilterStatus = false, modelFilterStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);
            findViewsById(0);
            if (bottomSheetView != null) {
                ViewGroup parent = (ViewGroup) bottomSheetView.getParent();
                if (parent != null)
                    parent.removeView(bottomSheetView);
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            adapter = new SearchTrailersAdapter(listData, this, this);
            searchItems.setLayoutManager(layoutManager);
            searchItems.setAdapter(adapter);


            //Get Data
            mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

            sharedPrefs = SharedPrefs.getInstance(this);
            String lat = sharedPrefs.getLat(), longitude = sharedPrefs.getLong(),
                    startDate = sharedPrefs.getStartDate(), endDate = sharedPrefs.getEndDate(), startTime = sharedPrefs.getStartTime(), endTime = sharedPrefs.getEndTime();
            showLoadingUI();
            DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            DateFormat finalDateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(Double.parseDouble(lat), Double.parseDouble(longitude), 1);
            if (addresses.size() > 0) {
                locationName.setText(addresses.get(0).getLocality());
            }
            try {
                Date date = inputDateFormat.parse(sharedPrefs.getStartDate());
                Date end = inputDateFormat.parse(sharedPrefs.getEndDate());
                String startD = finalDateFormat.format(date);
                String endD = finalDateFormat.format(end);
                rentDates.setText(startD + " - " + endD);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mViewModel.sendFiltersRequest(this);
            mViewModel.getFiltersLiveData().observe(this, response -> {
                ApiResponse.ApiResponseStatus status = response.getApiStatus();
                switch (status) {
                    case LOADING:
                        break;
                    case FAILED:
                        Toast.makeText(this, response.getErrorMessage().message(), Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        hideLoadingUI();
                        filterResponse = response.getData();
                        filterObj = filterResponse.getFiltersObj();
                        break;
                }
            });
            mViewModel.sendSearchRequest(this, lat, longitude, startDate, endDate, startTime, endTime);
            mViewModel.getSearchTrailerListener().observe(this, response -> {
                ApiResponse.ApiResponseStatus status = response.getApiStatus();
                switch (status) {
                    case LOADING:
                        break;
                    case FAILED:
                        Toast.makeText(this, response.getErrorMessage().message(), Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        hideLoadingUI();
                        searchResponse = response.getData();
                        listData = searchResponse.getTrailers();
                        unsortedData = new ArrayList<>(listData);
                        if (listData.size() == 0) {
                            searchItems.setVisibility(View.GONE);
                            noTrailers.setVisibility(View.VISIBLE);
                        } else {
                            searchItems.setVisibility(View.VISIBLE);
                            noTrailers.setVisibility(View.GONE);
                        }
                        filterItems();
                        SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData, this, this);
                        searchItems.setAdapter(adapter);
                        break;
                }
            });


            filterConnect.setOnClickListener(v -> showFilterView());
            sortContent.setOnClickListener(v -> showSortView());

            bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
            BottomSheetBehavior behaviorField = bottomSheetDialog.getBehavior();
            behaviorField.setState(BottomSheetBehavior.STATE_EXPANDED);
            behaviorField.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behaviorField.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
        catch (Throwable T){
            Log.d("Error", T.toString());
        }

    }


    void showFilterView(){
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
                        R.layout.bottom_sheet_filter,
                        findViewById(R.id.filter_container)
                );
        findViewsById(1);
        RecyclerView.LayoutManager typeLayoutManager = new LinearLayoutManager(bottomSheetView.getContext());
        RecyclerView.LayoutManager deliveryLayoutManager = new LinearLayoutManager(bottomSheetView.getContext());
        RecyclerView.LayoutManager modelLayoutManager = new LinearLayoutManager(bottomSheetView.getContext());

        FilterRowAdapter typeAdapter = new FilterRowAdapter(new FilterRowAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(FilterType item) {
                typeApplied++;
                item.checked = 1;
                Log.d("here","added");
                selectedFilter.add(item);
                filterCodes.add(item.getCode());
                filterNames.add(item.getName());
                typeFilterStatus = true;
            }

            @Override
            public void onItemUncheck(FilterType item) {
                typeApplied--;
                item.checked = 0;
                Log.d("here","removed");
                selectedFilter.remove(item);
                filterCodes.remove(item.getCode());
                filterNames.remove(item.getName());
                if(typeApplied == 0){
                    typeFilterStatus = false;
                }
            }
        }, filterObj.getTrailerTypesList());

        FilterRowAdapter deliveryAdapter = new FilterRowAdapter(new FilterRowAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(FilterType item) {
                item.checked = 1;
                Log.d("here","added");
                selectedFilter.add(item);
                filterCodes.add(item.getCode());
                filterNames.add(item.getName());
            }

            @Override
            public void onItemUncheck(FilterType item) {
                item.checked = 0;
                Log.d("here","removed");
                selectedFilter.remove(item);
                filterCodes.remove(item.getCode());
                filterNames.remove(item.getName());
            }
        }, filterObj.getDeliveryTypesList());

        FilterRowAdapter modelAdapter = new FilterRowAdapter(new FilterRowAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(FilterType item) {
                modelApplied++;
                item.checked = 1;
                selectedFilter.add(item);
                filterCodes.add(item.getCode());
                filterNames.add(item.getName());
                modelFilterStatus = true;
            }

            @Override
            public void onItemUncheck(FilterType item) {
                modelApplied--;
                item.checked = 0;
                selectedFilter.remove(item);
                filterCodes.remove(item.getCode());
                filterNames.remove(item.getName());
                if (modelApplied == 0)
                    modelFilterStatus = false;
            }
        }, filterObj.getModelTypesList());

        deliveryFilterView.setLayoutManager(deliveryLayoutManager);
        deliveryFilterView.setAdapter(deliveryAdapter);

        typeFilterView.setLayoutManager(typeLayoutManager);
        typeFilterView.setAdapter(typeAdapter);

        modelFilterView.setLayoutManager(modelLayoutManager);
        modelFilterView.setAdapter(modelAdapter);

        typeFilter.setOnClickListener(v -> {
            if(typeList.getVisibility() == View.GONE) {
                typeList.setVisibility(View.VISIBLE);
            }
            else
                typeList.setVisibility(View.GONE);
            type.showNext();
        });
        deliveryFilter.setOnClickListener(v -> {
            if(deliveryList.getVisibility() == View.INVISIBLE)
                deliveryList.setVisibility(View.VISIBLE);
            else
                deliveryList.setVisibility(View.INVISIBLE);
            delivery.showNext();
        });
        modelFilter.setOnClickListener(v->{
            if (modelList.getVisibility() == View.GONE)
                modelList.setVisibility(View.VISIBLE);
            else
                modelList.setVisibility(View.GONE);
            model.showNext();
        });

        filterRequest.setOnClickListener(v -> {
            filterItems();
            if (listData.size() == 0){
                searchItems.setVisibility(View.GONE);
                noTrailers.setVisibility(View.VISIBLE);
            }
            else{
                searchItems.setVisibility(View.VISIBLE);
                noTrailers.setVisibility(View.GONE);
            }
            SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData,this, this);
            searchItems.setAdapter(adapter);
            bottomSheetDialog.cancel();
        });

        clearFilters.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            selectedFilter.clear();
            for (FilterType i : filterObj.getDeliveryTypesList())
                i.resetChecked();
            for (FilterType i : filterObj.getTrailerTypesList())
                i.resetChecked();
            listData = new ArrayList<>(unsortedData);
            if (listData.size() == 0){
                searchItems.setVisibility(View.GONE);
                noTrailers.setVisibility(View.VISIBLE);
            }
            else{
                searchItems.setVisibility(View.VISIBLE);
                noTrailers.setVisibility(View.GONE);
            }
            SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData,this, this);
            searchItems.setAdapter(adapter);
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    void showSortView(){
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
                        R.layout.bottom_sheet_sort,
                        findViewById(R.id.sort_container)
                );
        findViewsById(2);
        if(sortStatus == 1)
            h2l.setChecked(true);
        else if(sortStatus == 2)
            l2h.setChecked(true);

        if (distanceSortStatus == 1)
            farSort.setChecked(true);
        else if (distanceSortStatus == 2)
            nearSort.setChecked(true);

        pricesSort.setOnClickListener(v -> {
            if(pricesList.getVisibility() == View.GONE) {
                pricesList.setVisibility(View.VISIBLE);
            }
            else
                pricesList.setVisibility(View.GONE);
            price.showNext();
        });
        distanceSort.setOnClickListener(v -> {
            if(distanceList.getVisibility() == View.GONE)
                distanceList.setVisibility(View.VISIBLE);
            else
                distanceList.setVisibility(View.GONE);
            distance.showNext();
        });
        ratingSort.setOnClickListener(v -> {
            if(ratingList.getVisibility() == View.INVISIBLE)
                ratingList.setVisibility(View.VISIBLE);
            else
                ratingList.setVisibility(View.INVISIBLE);
            rating.showNext();
        });

        sortRequest.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            if(l2h.isChecked()){
                sortStatus = 2;
                Collections.sort(listData, TrailerDetails.priceComparatorAsc);
                SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData,this, this);
                searchItems.setAdapter(adapter);
            }
            else if(h2l.isChecked()){
                sortStatus = 1;
                Collections.sort(listData, TrailerDetails.priceComparatorDsc);
                SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData,this, this);
                searchItems.setAdapter(adapter);
            }

            if(nearSort.isChecked()){
                distanceSortStatus = 2;
                Collections.sort(listData, TrailerDetails.distComparatorAsc);
                SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData,this, this);
                searchItems.setAdapter(adapter);
            }
            else if(farSort.isChecked()){
                distanceSortStatus = 1;
                Collections.sort(listData, TrailerDetails.distComparatorDsc);
                SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData,this, this);
                searchItems.setAdapter(adapter);
            }
        });

        clearSort.setOnClickListener(v -> {
            sortStatus = 0;
            bottomSheetDialog.cancel();
            l2h.setChecked(false);
            h2l.setChecked(false);
            listData = new ArrayList<>(unsortedData);
            if (listData.size() == 0){
                searchItems.setVisibility(View.GONE);
                noTrailers.setVisibility(View.VISIBLE);
            }
            else{
                searchItems.setVisibility(View.VISIBLE);
                noTrailers.setVisibility(View.GONE);
            }
            SearchTrailersAdapter adapter = new SearchTrailersAdapter(listData,this, this);
            searchItems.setAdapter(adapter);

        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    void findViewsById(int id){
        switch (id) {
            case 1:
                typeFilter = bottomSheetView.findViewById(R.id.type_filter);
                deliveryFilter = bottomSheetView.findViewById(R.id.delivery_filter);
                modelFilter = bottomSheetView.findViewById(R.id.model_filter);

                typeList = bottomSheetView.findViewById(R.id.type_list);
                deliveryList = bottomSheetView.findViewById(R.id.delivery_list);
                modelList = bottomSheetView.findViewById(R.id.model_list);

                type = bottomSheetView.findViewById(R.id.type_animator);
                delivery = bottomSheetView.findViewById(R.id.delivery_animator);
                model = bottomSheetView.findViewById(R.id.model_animator);

                typeFilterView = bottomSheetView.findViewById(R.id.type_filter_view);
                deliveryFilterView = bottomSheetView.findViewById(R.id.delivery_filter_view);
                modelFilterView = bottomSheetView.findViewById(R.id.model_filter_view);

                filterRequest = bottomSheetView.findViewById(R.id.filter_request);
                clearFilters = bottomSheetView.findViewById(R.id.clear_filter);

                break;
            case 2:
                pricesSort = bottomSheetView.findViewById(R.id.price_sort);
                distanceSort = bottomSheetView.findViewById(R.id.distance_sort);
                ratingSort = bottomSheetView.findViewById(R.id.rating_sort);

                pricesList = bottomSheetView.findViewById(R.id.price_list);
                distanceList = bottomSheetView.findViewById(R.id.distance_list);
                ratingList = bottomSheetView.findViewById(R.id.rating_list);

                price = bottomSheetView.findViewById(R.id.prices_animator);
                distance = bottomSheetView.findViewById(R.id.distance_animator);
                rating = bottomSheetView.findViewById(R.id.rating_animator);

                l2h = bottomSheetView.findViewById(R.id.low_to_high);
                h2l = bottomSheetView.findViewById(R.id.high_to_low);

                nearSort = bottomSheetView.findViewById(R.id.distance_near);
                farSort = bottomSheetView.findViewById(R.id.distance_far);

                sortRequest = bottomSheetView.findViewById(R.id.sort_request);
                clearSort = bottomSheetView.findViewById(R.id.clear_sort);

                break;
            case 0:
                filterConnect = findViewById(R.id.button_filters);
                sortContent = findViewById(R.id.button_sort);
                searchItems = findViewById(R.id.search_trailers_items);
                noTrailers = findViewById(R.id.tv_no_trailers);
                locHeading = findViewById(R.id.textView4);
                buttonHolder = findViewById(R.id.linearLayout);
                rentDates = findViewById(R.id.rent_dates_search);
                locationName = findViewById(R.id.location_name);
                break;
        }
    }

    void showLoadingUI(){
        wait = 0;
        pb = findViewById(R.id.pb_loading_search);
        pb.setVisibility(View.VISIBLE);
        filterConnect.setVisibility(View.INVISIBLE);
        sortContent.setVisibility(View.INVISIBLE);
        locHeading.setVisibility(View.INVISIBLE);
        noTrailers.setVisibility(View.INVISIBLE);
        filterConnect.setClickable(false);
        sortContent.setClickable(false);

    }

    void hideLoadingUI(){
        if(wait< 1)
            wait++;
        else{
            pb = findViewById(R.id.pb_loading_search);
            pb.setVisibility(View.GONE);
            filterConnect.setVisibility(View.VISIBLE);
            sortContent.setVisibility(View.VISIBLE);
            locHeading.setVisibility(View.VISIBLE);
            noTrailers.setVisibility(View.VISIBLE);
            filterConnect.setClickable(true);
            sortContent.setClickable(true);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setTitle("Cancel Search");
        builder.setMessage("Are you sure you want to cancel this search?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            startActivity(new Intent(SearchActivity.this, BottomNavActivity.class));
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void filterItems(){
        String type = sharedPrefs.getTrailerType();
        if(!type.equals("")) {
            filterCodes.add(type);
            filterNames.add(type);
            modelFilterStatus = true;
            for (FilterType i : filterObj.getModelTypesList())
                if (i.getName().equals(type)) {
                    i.checked = 1;
                }
        }
        if (filterCodes.size() > 0) {
            //model filter
            ArrayList<TrailerDetails> filtered = new ArrayList<>();
            for(TrailerDetails i : unsortedData){
                if(typeFilterStatus && modelFilterStatus){
                    if (filterHasType(i) && filterHasModel(i)){
                        filtered.add(i);
                    }
                }
                else if (typeFilterStatus){
                    if (filterHasType(i)){
                        filtered.add(i);
                    }
                }
                else if (modelFilterStatus){
                    if (filterHasModel(i)){
                        filtered.add(i);
                    }
                }
            }
            listData = new ArrayList<>(filtered);
            //TODO add delivery filter
        }
        else
        {
            listData = new ArrayList<>(unsortedData);
        }
    }


    @Override
    public void onTrailerClicked(TrailerDetails details) {
        SharedPrefs sharedPrefs = SharedPrefs.getInstance(this);
        sharedPrefs.setItemId(details.getRentalItemId());
        Intent i = new Intent(this, TrailerDetailsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("UPSELL", details.getUpsellItems());
        i.putExtra("BUNDLE",args);
        startActivity(i);
    }

    boolean filterHasType(TrailerDetails item){
        return filterCodes.contains(item.getType());
    }


    boolean filterHasModel(TrailerDetails item){
        return filterNames.contains(item.getName());
    }
}