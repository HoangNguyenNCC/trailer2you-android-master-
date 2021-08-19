package com.applutions.t2y.ui.rentTrailer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.adapters.UpsellBoughtAdapter;
import com.applutions.t2y.data.items.BookingUpsell;
import com.applutions.t2y.data.items.ReqCharges;
import com.applutions.t2y.data.items.TrailerViewReq;
import com.applutions.t2y.data.response.booking.Booking;
import com.applutions.t2y.data.response.booking.BookingEditResponse;
import com.applutions.t2y.data.response.booking.BookingLocation;
import com.applutions.t2y.data.response.booking.BookingResponse;
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.data.response.trailer.TrailerObj;
import com.applutions.t2y.ui.booking.BookingActivity;
import com.applutions.t2y.ui.booking.BookingViewModel;
import com.applutions.t2y.ui.notifications.response.Items;
import com.applutions.t2y.ui.notifications.response.RentalObj;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsActivity;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsViewModel;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.applutions.t2y.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class BookActivity extends AppCompatActivity {

    TrailerDetailsViewModel viewModel;
    private BookingViewModel bookingViewModel;
    Button confirmButton, waiverButton;
    ImageView trailerImage;
    TextView trailerName, startDate, startTime, endDate, endTime, address, priceName, price,
            damagePrice, taxPrice, priceTotal, finalPriceLabel, waiverTag, waiverExplain;
    SharedPrefs sharedPrefs;
    ScrollView summaryPage;
    ProgressBar progressBar;
    private String paymentIntentId;
    int waiver = 1;
    int lock = 0;
    int status = 0;
    /*
     status 1 -> reschedule
     status 0 -> search
    */

    Booking booking;
    RecyclerView upsellItems;
    ArrayList<RentalObj.UpsellCharges> upsellCharges = new ArrayList<>();

    @Override
    public void onBackPressed() {
        if (status==1){
            super.onBackPressed();
        }
        else
            startActivity(new Intent(BookActivity.this, TrailerDetailsActivity.class));
    }

    void createBooking(){
        String trailerId = sharedPrefs.getItemId();
        String startDate = sharedPrefs.getStartDate();
        String endDate = sharedPrefs.getEndDate();
        String lat = sharedPrefs.getLat();
        String lon = sharedPrefs.getLong();
        String jsonUpsell = sharedPrefs.getUpsellItems();
        String addressText = sharedPrefs.getLocation();
        String pincode = sharedPrefs.getPostalCode();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<BookingUpsell>>(){}.getType();
        ArrayList<BookingUpsell> upsellItems= gson.fromJson(jsonUpsell, type);
        if(upsellItems==null) {
            upsellItems = new ArrayList<>();
        }
        Double[] coords = new Double[] {Double.valueOf(lat), Double.valueOf(lon)};

        booking = new Booking(
                trailerId,
                upsellItems,
                startDate+" "+sharedPrefs.getStartTime(),
                endDate+" "+sharedPrefs.getEndTime(),
                0,
                new BookingLocation(
                        addressText,
                        pincode,
                        coords
                ),
                (waiver==1)
        );


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        viewModel = new TrailerDetailsViewModel();
        sharedPrefs = SharedPrefs.getInstance(this);
        String rentalId = sharedPrefs.getItemId();
        Intent i = getIntent();
        findViewsByID();
        String trailerId = sharedPrefs.getItemId();
        String startDate = sharedPrefs.getStartDate();
        String endDate = sharedPrefs.getEndDate();
        String lat = sharedPrefs.getLat();
        String lon = sharedPrefs.getLong();
        ArrayList<String> dates = new ArrayList<>();
        dates.add(startDate);
        dates.add(endDate);
        ArrayList<String> times = new ArrayList<>();
        times.add(sharedPrefs.getStartTime());
        times.add(sharedPrefs.getEndTime());
        ArrayList<Double> cords = new ArrayList<>();
        cords.add(Double.parseDouble(lat));
        cords.add(Double.parseDouble(lon));
        TrailerViewReq req = new TrailerViewReq(rentalId,dates,times,cords,"door2door");
        viewModel.getTrailerView(this, req);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        if(Objects.equals(i.getStringExtra("from"), "search")){
            String jsonUpsell = sharedPrefs.getUpsellItems();
            String addressText = sharedPrefs.getLocation();
            String pincode = sharedPrefs.getPostalCode();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<BookingUpsell>>(){}.getType();
            ArrayList<BookingUpsell> upsellItems= gson.fromJson(jsonUpsell, type);
            if(upsellItems==null) {
                upsellItems = new ArrayList<>();
            }
            ReqCharges reqCharges = new ReqCharges(trailerId, upsellItems,startDate+" "+sharedPrefs.getStartTime(),endDate+" "+sharedPrefs.getEndTime());
            bookingViewModel.getCharges(this, reqCharges);

        }
        else{
            lock=1;
        }

        viewModel.getViewLiveData().observe(this, response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    showLoadingUI();
                    break;
                case FAILED:
                    progressBar.setVisibility(View.GONE);
                    Utils.getErrorSnackBar(this, confirmButton, response.getErrorMessage().message()).show();
                    break;
                case SUCCESS:
                    hideLoadingUI();
                    Log.v("TrailerDetails", response.getData().getTrailerObj().toString());
                    try {
                        if(Objects.equals(i.getStringExtra("from"), "reschedule")){
                            BookingEditResponse e = (BookingEditResponse) i.getSerializableExtra("item");
                            setRescheduleData(response.getData(), e);
                            paymentIntentId = e.getStripeClientSecret();
                        }
                        else
                            setUiData(response.getData());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });

        bookingViewModel.getChargesLiveData().observe(this, response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    Log.d("Loading", "Getting Charges");
                    break;
                case FAILED:
                    Log.d("Failed","To get Charges");
                    break;
                case SUCCESS:
                    setCharges(response.getData());
                    upsellCharges = response.getData().getUpsellCharges();
                    break;
            }
        });
        bookingViewModel.getBookingLiveData().observe(this, apiResponse -> {
            ApiResponse.ApiResponseStatus status = apiResponse.getApiStatus();
            switch (status) {
                case LOADING:
                    showLoadingUI();
                    break;
                case FAILED:
                    hideLoadingUI();
                    Toast.makeText(this, "Please contact the admin to get your Driving License verified", Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS:
                    BookingResponse bookingResponse = apiResponse.getData();
                    paymentIntentId = bookingResponse.getStripeClientSecret();
                    Intent in = new Intent(this, BookingActivity.class);
                    in.putExtra("clientKey", paymentIntentId);
                    startActivity(in);
            }
        });

    }

    private void setCharges(@NotNull RentalObj.Charges data) {
        Double damage = data.getTrailerCharges().getDlrCharges();
        Double taxes = data.getTrailerCharges().getTaxes();
        for (RentalObj.UpsellCharges i: data.getUpsellCharges()){
            damage += i.getCharges().getDlrCharges()*i.getQuantity();
            taxes += i.getCharges().getTaxes()*i.getQuantity();
        }
        DecimalFormat df = new DecimalFormat("0.00");

        price.setText(df.format(data.getTrailerCharges().getRentalCharges())+" AUD");
        priceTotal.setText(df.format(data.getTotalPayableAmount())+" AUD");
        damagePrice.setText(df.format(damage)+" AUD");
        taxPrice.setText(df.format(taxes)+" AUD");
        Double finalDamage = damage;
        waiverButton.setOnClickListener(v -> {
            if(waiver==1){
                waiver=0;
                waiverButton.setBackground(getDrawable(R.drawable.grey_rounded_button));
                waiverButton.setText("Add");
                waiverButton.setTextColor(getColor(R.color.colorPrimaryDark));
                double totalPrice1 = data.getTotalPayableAmount() - finalDamage;
                priceTotal.setText(df.format(totalPrice1) +" AUD");
            }
            else if(waiver == 0){
                waiver=1;
                waiverButton.setBackground(getDrawable(R.drawable.dark_rounded_button));
                waiverButton.setText("Added");
                waiverButton.setTextColor(getColor(R.color.white));
                priceTotal.setText(df.format(data.getTotalPayableAmount()) +" AUD");
            }
        });

    }

    //For Reschedule/Extension flow
    private void setRescheduleData(@NotNull TrailerViewResponse response, BookingEditResponse editResponse) {
        TrailerObj obj = response.getTrailerObj();
        status = 1;
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.trailer_image)
                .error(R.drawable.trailer_image);
        Glide.with(this).load(obj.getPhotos().get(0).getData()).apply(options).into(trailerImage);
        trailerName.setText(obj.getName());
        priceName.setText(obj.getName());
        ArrayList<Items> items = new ArrayList<>();
        for(TrailerViewResponse.Upsell i: response.getUpsellItemsList()){
            for(BookingUpsell j: editResponse.getBooking().getUpsellItems()){
                if (i.get_id().equals(j.getId())){
                    items.add(new Items(i.getName(), j.getQuantity(), i.getPhoto().get(0), new Items.totalCharges(
                            editResponse.getBooking().getCharges().getUpsellCharges().get(editResponse.getBooking().getUpsellItems().indexOf(j)).getCharges()
                            ,editResponse.getBooking().getCharges().getUpsellCharges().get(editResponse.getBooking().getUpsellItems().indexOf(j)).getCharges().getTotal(),
                            j.getQuantity())));
                }
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        UpsellBoughtAdapter adapter = new UpsellBoughtAdapter(items);
        upsellItems.setLayoutManager(linearLayoutManager);
        upsellItems.setAdapter(adapter);
        try {
            DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            DateFormat finalDateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            Date date = inputDateFormat.parse(editResponse.getBooking().getStartDate().substring(0,editResponse.getBooking().getStartDate().indexOf("T")));
            Date end = inputDateFormat.parse(editResponse.getBooking().getEndDate().substring(0,editResponse.getBooking().getEndDate().indexOf("T")));
            String startD = finalDateFormat.format(date);
            String endD = finalDateFormat.format(end);
            startDate.setText(startD);
            endDate.setText(endD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            String startT = editResponse.getBooking().getStartDate().substring(editResponse.getBooking().getStartDate().indexOf("T")+1);
            String endT = editResponse.getBooking().getEndDate().substring(editResponse.getBooking().getEndDate().indexOf("T")+1);
            DateFormat inputTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            DateFormat outputTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            inputTime.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date startD = inputTime.parse(startT);
            Date endD = inputTime.parse(endT);
            outputTime.setTimeZone(TimeZone.getDefault());
            startTime.setText(outputTime.format(startD));
            endTime.setText(outputTime.format(endD));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        address.setText(editResponse.getBooking().getLocation().getText());
        setCharges(editResponse.getBooking().getCharges());
        waiverButton.setVisibility(View.GONE);
        waiverExplain.setVisibility(View.GONE);
        if(Double.valueOf(editResponse.getBooking().getDlrCharges()).equals((double) 0)){
            damagePrice.setVisibility(View.GONE);
            waiverTag.setVisibility(View.GONE);
            waiverExplain.setVisibility(View.GONE);
        }
//        priceTotal.setText(editResponse.getBooking().getCharges().getTotalPayableAmount().toString()+" AUD");
        finalPriceLabel.setText("Amount Payable");
        confirmButton.setText("Confirm Reschedule and Pay");
        confirmButton.setOnClickListener(v -> {
            Intent i = new Intent(this, BookingActivity.class);
            i.putExtra("clientKey", paymentIntentId);
            startActivity(i);
        });
    }

    //For Search Flow
    void setUiData(@NotNull TrailerViewResponse response) throws ParseException {
        TrailerObj obj = response.getTrailerObj();
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.trailer_image)
                .error(R.drawable.trailer_image);
        Glide.with(this).load(obj.getPhotos().get(0).getData()).apply(options).into(trailerImage);
        trailerName.setText(obj.getName());
        priceName.setText(obj.getName());
        if (upsellCharges.size()>0){
            ArrayList<Items> items = new ArrayList<>();
            upsellItems.setLayoutManager(new LinearLayoutManager(this));
            for(TrailerViewResponse.Upsell i: response.getUpsellItemsList()){
                for (RentalObj.UpsellCharges j: upsellCharges){
                    if (j.getId().equals(i.get_id())){
                        items.add(new Items(i.getName(), j.getQuantity(), i.getPhoto().get(0), new Items.totalCharges(j.getCharges(), j.getPayable(), j.getQuantity())));
                    }
                }
            }
            upsellItems.setAdapter(new UpsellBoughtAdapter(items));
        }
        try {
            DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            DateFormat finalDateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            Date date = inputDateFormat.parse(sharedPrefs.getStartDate());
            Date end = inputDateFormat.parse(sharedPrefs.getEndDate());
            String startD = finalDateFormat.format(date);
            String endD = finalDateFormat.format(end);
            startDate.setText(startD);
            endDate.setText(endD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            DateFormat inputTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            DateFormat outputTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            inputTime.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date startD = inputTime.parse(sharedPrefs.getStartTime());
            Date endD = inputTime.parse(sharedPrefs.getEndTime());
            outputTime.setTimeZone(TimeZone.getDefault());
            startTime.setText(outputTime.format(startD));
            endTime.setText(outputTime.format(endD));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        address.setText(sharedPrefs.getLocation());

        confirmButton.setOnClickListener(v -> {
            createBooking();
            bookingViewModel.requestBooking(this, booking);
        });


    }

    void findViewsByID(){
        summaryPage = findViewById(R.id.summary_page);
        progressBar = findViewById(R.id.summary_loading);
        confirmButton = findViewById(R.id.confirm_booking_button);
        trailerImage = findViewById(R.id.trailer_image_book);
        startDate = findViewById(R.id.tv_start_date);
        startTime = findViewById(R.id.tv_start_time);
        endDate = findViewById(R.id.tv_end_date);
        endTime = findViewById(R.id.tv_end_time);
        address = findViewById(R.id.tv_delivery_loc);
        trailerName = findViewById(R.id.tv_trailer_short_desc);
        priceName = findViewById(R.id.tv_trailer_name_book);
        price = findViewById(R.id.price_base_book);
        damagePrice = findViewById(R.id.price_damage_book);
        taxPrice = findViewById(R.id.price_tax_book);
        priceTotal = findViewById(R.id.price_total_book);
        finalPriceLabel = findViewById(R.id.total_price_text_view);
        waiverTag = findViewById(R.id.damage_waiver_text_view);
        waiverExplain = findViewById(R.id.damage_waiver_explain);
        waiverButton = findViewById(R.id.waiver_button);
        upsellItems = findViewById(R.id.upsell_items_summary);
    }

    void showLoadingUI(){
        summaryPage.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideLoadingUI(){
            summaryPage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
    }

}