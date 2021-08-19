package com.applutions.t2y.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applutions.t2y.BottomNavActivity;
import com.applutions.t2y.R;
import com.applutions.t2y.data.items.FeaturedDetails;
import com.applutions.t2y.data.response.search.SearchResponse;
import com.applutions.t2y.ui.search.SearchActivity;
import com.applutions.t2y.utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FeaturedTrailerDetails extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Details";

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    AutocompleteSupportFragment autocompleteFragment;
    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;
    TextView addLine1, addLine2, linkCal, linkCancel, fromDate, toDate, licensee, trailer, desc, size, tare, capacity;
    EditText add, fromTime, toTime, houseNum;
    Button linkTime, startSearch, searchFeatured;
    ImageView imageView;

    CalendarView calendarView;

    SharedPrefs sharedPrefs;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_trailer_details);
        context = this;
        sharedPrefs = SharedPrefs.getInstance(this);
        Intent i = getIntent();
        ProgressBar pb = findViewById(R.id.pb_loading);
        ConstraintLayout layout = findViewById(R.id.details_container);
        licensee = findViewById(R.id.tv_subtitle);
        trailer = findViewById(R.id.tv_title);
        desc = findViewById(R.id.tv_desc);
        size = findViewById(R.id.tv_trailer_size);
        tare = findViewById(R.id.tv_tare);
        capacity = findViewById(R.id.tv_capacity);
        searchFeatured = findViewById(R.id.btn_rent_trailer);
        imageView = findViewById(R.id.iv_main);
        if (Objects.equals(i.getStringExtra("from"), "featured")) {
            FeaturedDetails featuredDetails = (FeaturedDetails) i.getSerializableExtra("details");
            pb.setVisibility(View.GONE);
            trailer.setText(featuredDetails.getName());
            desc.setText(featuredDetails.getDescription());
            size.setText(featuredDetails.getSize());
            tare.setText(featuredDetails.getTare());
            capacity.setText(featuredDetails.getCapacity());
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.trailer_image)
                    .error(R.drawable.trailer_image);
            Glide.with(context).load(featuredDetails.getPhotos().get(0).getData()).apply(options).into(imageView);
            imageView.setClipToOutline(true);
            searchFeatured.setOnClickListener(v->{
                initializeSearch();
            });
        } else if (Objects.equals(i.getStringExtra("from"), "search")) {
            layout.setVisibility(View.INVISIBLE);
            searchFeatured.setVisibility(View.INVISIBLE);
            initializeSearch();
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
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initializeSearch() {
        try {
            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            BottomSheetBehavior behaviorField = bottomSheetDialog.getBehavior();
            behaviorField.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
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
            bottomSheetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == android.view.KeyEvent.KEYCODE_BACK){
                        startActivity(new Intent(FeaturedTrailerDetails.this,BottomNavActivity.class));
                        return true;
                    }
                    return false;
                }
            });
            bottomSheetView = getLayoutInflater()
                    .inflate(
                            R.layout.layout_bottom_sheet,
                            findViewById(R.id.bottomSheetContainer),
                            false
                    );
            findViewsByID(1);
            autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            if (autocompleteFragment != null) {
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));
            }
            getMap();

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NotNull Place place) {
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    LatLng l = place.getLatLng();
                    if (l != null) {
                        sharedPrefs.setLat(Double.toString(l.latitude));
                        sharedPrefs.setLong(Double.toString(l.longitude));
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(l).title("Marker in" + place.getName()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
                        Log.d("here", place.getLatLng().toString());
                    }
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> addresses;
                        Address address;
                        if (l != null) {
                            addresses = geocoder.getFromLocation(
                                    l.latitude,
                                    l.longitude,
                                    1);
                            address = addresses.get(0);
                            String zip = address.getPostalCode();
                            sharedPrefs.setPostalCode(zip);
                            addLine2.setText(address.getCountryName());
                            add.setText(address.getAddressLine(0));
                            addLine1.setText(place.getName());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        addLine2.setText("");
                    }

                }

                @Override
                public void onError(@NotNull Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

            linkCal.setOnClickListener(v1 -> {
                String addr = add.getText().toString();
                String houseNo = houseNum.getText().toString();
                boolean valid = true;
                if (TextUtils.isEmpty(houseNo)) {
                    houseNum.setError("Required");
                    valid = false;
                } else
                    houseNum.setError(null);
                if (TextUtils.isEmpty(addr)) {
                    add.setError("Required");
                    valid = false;
                } else
                    add.setError(null);

                if (valid) {
                    bottomSheetDialog.cancel();
                    sharedPrefs.setLocation(houseNo + ", " + addr);
                    showDatePickerDialog();
                }
            });
            linkCancel.setOnClickListener(v12 -> {
                bottomSheetDialog.cancel();
                startActivity(new Intent(FeaturedTrailerDetails.this,BottomNavActivity.class));
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.setOnCancelListener(dialog -> {
                getSupportFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
                getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
//                startActivity(new Intent(this, BottomNavActivity.class));
            });
            bottomSheetDialog.show();
        }
        catch (Exception e){
//            Toast.makeText(requireActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
            Log.d("Error",e.toString());
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

    public void showDatePickerDialog() {
        try {
            bottomSheetView = getLayoutInflater()
                    .inflate(
                            R.layout.bottom_sheet_cal,
                            findViewById(R.id.calContainer),
                            false
                    );
            findViewsByID(3);

            Calendar today = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            max.add(Calendar.MONTH,6);
            calendarView.setMaximumDate(max);
            today.add(Calendar.DATE,1);
            calendarView.setMinimumDate(today);


            linkTime.setOnClickListener(v -> {
                List<Calendar> dates = calendarView.getSelectedDates();
                if (dates.size() >= 2) {
                    bottomSheetDialog.cancel();
                    String myFormat = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        sharedPrefs.setStartDate(sdf.format(dates.get(0).getTime()));
                        sharedPrefs.setEndDate(sdf.format(dates.get(dates.size() - 1).getTime()));

                    showTimePicker();
                } else {
                    errorSnack("Please select two or more days").show();
                }
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }
        catch (Exception e){
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show();
            Log.d("Error",e.toString());
        }


    }

    void showTimePicker(){
        try {
            bottomSheetView = getLayoutInflater()
                    .inflate(
                            R.layout.bottom_sheet_time,
                            findViewById(R.id.bottomSheetCal),
                            false
                    );

            findViewsByID(2);
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
            fromDate.setText(output.format(input.parse(sharedPrefs.getStartDate())));
            toDate.setText(output.format(input.parse(sharedPrefs.getEndDate())));
            fromTime.setOnClickListener(v -> {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                    String min;
                    try {
                        sharedPrefs.setStartTime(selectedHour + ":" + selectedMinute);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (selectedMinute < 10) {
                        min = "0" + selectedMinute;
                    } else
                        min = "" + selectedMinute;
                    if (selectedHour == 0) {
                        fromTime.setText(12 + ":" + min + " AM");
                    } else if (selectedHour == 12) {
                        fromTime.setText(selectedHour + ":" + min + " PM");
                    } else if (selectedHour > 12) {
                        fromTime.setText(selectedHour - 12 + ":" + min + " PM");
                    } else {
                        fromTime.setText(selectedHour + ":" + min + " AM");
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            });

            toTime.setOnClickListener(v -> {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                    String min;
                    try {
                        sharedPrefs.setEndTime(selectedHour + ":" + selectedMinute);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (selectedMinute < 10) {
                        min = "0" + selectedMinute;
                    } else
                        min = "" + selectedMinute;
                    if (selectedHour == 0) {
                        toTime.setText(12 + ":" + min + " AM");
                    } else if (selectedHour == 12) {
                        toTime.setText(selectedHour + ":" + min + " PM");
                    } else if (selectedHour > 12) {
                        toTime.setText(selectedHour - 12 + ":" + min + " PM");
                    } else {
                        toTime.setText(selectedHour + ":" + min + " AM");
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            });

            startSearch.setOnClickListener(v -> {
                boolean valid = true;
                if (TextUtils.isEmpty(fromTime.getText())) {
                    fromTime.setError("Required");
                    valid = false;
                } else
                    fromTime.setError(null);
                if (TextUtils.isEmpty(toTime.getText())) {
                    toTime.setError("Required");
                    valid = false;
                } else
                    toTime.setError(null);
                if (valid) {
                    bottomSheetDialog.cancel();
                    startActivity(new Intent(FeaturedTrailerDetails.this, SearchActivity.class));
                }
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }
        catch (Exception error){
            Toast.makeText(this,"Something went Wrong",Toast.LENGTH_LONG).show();
            Log.d("Error", error.toString());
        }
    }

    public Snackbar errorSnack(String message){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this ,R.color.red_gradient_start));
        return snackbar;
    }

}