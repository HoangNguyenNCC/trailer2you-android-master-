package com.applutions.t2y.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.applutions.t2y.R;
import com.applutions.t2y.adapters.UpsellBoughtAdapter;
import com.applutions.t2y.common.HelperMethods;
import com.applutions.t2y.data.items.RentalEditReq;
import com.applutions.t2y.ui.booking.FinalActivity;
import com.applutions.t2y.ui.notifications.response.BookingDetailsViewModel;
import com.applutions.t2y.ui.notifications.response.Items;
import com.applutions.t2y.ui.notifications.response.RentalObj;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsActivity;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class BookingDetailsActivity extends AppCompatActivity {

    Button btnRecheduleBooking;
    TextView btnCancelBooking,txtTrailerName,txtRentalStartDate,txtRentalEndDate,txtCustomerAddress,txtShowTrailerDetails;
    ImageView itemPic;
    TextView mTxtRentalCharges,mTxtPriceTaxes, mTxtDamageCharges, mTxtTotalCharges, trailerName, damageTitle;
    String rentalId, bookingId,trailerId;
    TextView licenseeName, startTime, endTime;
    ScrollView summaryPage;
    ProgressBar progressBar;
    String rentalStatus, startDT;
    Intent intent;
    RecyclerView upsellRecycle;

    boolean NOPAYMENT=false;
    BookingDetailsViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        mViewModel=new ViewModelProvider(this).get(BookingDetailsViewModel.class);
        intent = getIntent();
        setInit();

        mViewModel.getRentalDetails(this,intent.getStringExtra("invoiceId"));
        mViewModel.getRentalDetailsLiveData().observe(this, response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    showLoadingUI();
                    break;
                case FAILED:
                    Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    if(response.getData().getRentalObj()!=null) {
                        trailerId=response.getData().getRentalObj().getRentedItems().get(0).getItemId();

                        rentalStatus = response.getData().getRentalObj().getRentalStatus();
                        rentalId = response.getData().getRentalObj().get_id();
                        bookingId = response.getData().getRentalObj().getBookingId();
                        startDT = response.getData().getRentalObj().getRentalPeriod().getStart();
                        txtTrailerName.setText(response.getData().getRentalObj().getRentedItems().get(0).getItemName());
                        HelperMethods.downloadImage(response.getData().getRentalObj().getRentedItems().get(0).getItemPhoto().getData(), this, itemPic);

                        String start=response.getData().getRentalObj().getRentalPeriod().getStart();
                        String end=response.getData().getRentalObj().getRentalPeriod().getEnd();
                        String dateD = end.substring(0,end.indexOf(" "));
                        String dateS = start.substring(0,start.indexOf(" "));

                        SharedPrefs prefs = SharedPrefs.getInstance(this);
                        prefs.setItemId(trailerId);
                        prefs.setStartDate(dateS);
                        prefs.setEndDate(dateD);
                        prefs.setStartTimeUF(start.substring(start.indexOf(" ")));
                        prefs.setEndTimeUF(end.substring(end.indexOf(" ")));
                        prefs.setLat(String.valueOf(response.getData().getRentalObj().getDropOffLocation().getCoordinates().get(0)));
                        prefs.setLong(String.valueOf(response.getData().getRentalObj().getDropOffLocation().getCoordinates().get(1)));

                        try {
                            DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            DateFormat finalDateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
                            Date date = inputDateFormat.parse(dateS);
                            Date endDa = inputDateFormat.parse(dateD);
                            String startD = finalDateFormat.format(date);
                            String endD = finalDateFormat.format(endDa);
                            txtRentalStartDate.setText(startD);
                            txtRentalEndDate.setText(endD);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            DateFormat inputTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                            DateFormat outputTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                            inputTime.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date startD = inputTime.parse(start.substring(start.indexOf(" ")+1));
                            Date endD = inputTime.parse(end.substring(end.indexOf(" ")+1));
                            outputTime.setTimeZone(TimeZone.getDefault());
                            startTime.setText(outputTime.format(startD));
                            endTime.setText(outputTime.format(endD));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                        if(response.getData().getRentalObj().getRentedItems().size()>1) {
                            ArrayList<Items> upsells = new ArrayList<>(response.getData().getRentalObj().getRentedItems().subList(1, response.getData().getRentalObj().getRentedItems().size()));
                            UpsellBoughtAdapter adapter = new UpsellBoughtAdapter(upsells);
                            upsellRecycle.setLayoutManager(layoutManager);
                            upsellRecycle.setAdapter(adapter);
                        }
                        if (intent.getStringExtra("licensee")!=null){
                            licenseeName.setText(intent.getStringExtra("licensee"));
                        }
                        trailerName.setText(response.getData().getRentalObj().getRentedItems().get(0).getItemName());
                        txtCustomerAddress.setText(response.getData().getRentalObj().getDropOffLocation().getText());
                        if(response.getData().getRentalObj().getRevisions().get(response.getData().getRentalObj().getRevisions().size()-1).getRevisionType().equals("cancellation")){
                            setCharges(response.getData().getRentalObj().getCharges());
                        }
                        else {
                            setCharges(response.getData().getRentalObj().getRevisions()
                                    .get(response.getData().getRentalObj().getRevisions().size() - 1)
                                    .getCharges());
                        }
                        hideLoadingUI();
                        setListener();
                    }
                    else{
                        Toast.makeText(this, "Couldnt fetch data, Please try again later!", Toast.LENGTH_SHORT).show();
                        super.onBackPressed();
                    }
                    break;
            }
        });
    }

    private void setCharges(RentalObj.Charges data) {
        Double damage = data.getTrailerCharges().getDlrCharges();
        Double taxes = data.getTrailerCharges().getTaxes();
        for (RentalObj.UpsellCharges i: data.getUpsellCharges()){
            damage += i.getCharges().getDlrCharges()*i.getQuantity();
            taxes += i.getCharges().getTaxes()*i.getQuantity();
        }
        DecimalFormat df = new DecimalFormat("0.00");
        if (damage==0){
            mTxtDamageCharges.setVisibility(View.GONE);
            damageTitle.setVisibility(View.GONE);
        }
        mTxtRentalCharges.setText(df.format(data.getTrailerCharges().getRentalCharges())+" AUD");
        mTxtTotalCharges.setText(df.format(data.getTotalPayableAmount())+" AUD");
        mTxtDamageCharges.setText(df.format(damage)+" AUD");
        mTxtPriceTaxes.setText(df.format(taxes)+" AUD");
        Double finalDamage = damage;

    }

    private void setListener() {
        txtShowTrailerDetails.setOnClickListener(view ->{
            SharedPrefs sharedPrefs = SharedPrefs.getInstance(this);
            sharedPrefs.setItemId(trailerId);
            Intent i = new Intent(this,TrailerDetailsActivity.class);
            i.putExtra("from", "reminders");
            startActivity(i);
        });

        if (rentalStatus.equals("delivered")) {
            btnRecheduleBooking.setText("Extend Booking");
            btnCancelBooking.setVisibility(View.GONE);
            btnRecheduleBooking.setOnClickListener(view -> {
                new RescheduleRequestBottomSheet(rentalId, bookingId, "extend", startDT).show(getSupportFragmentManager(), "");
            });
        }
        else if(rentalStatus.equals("booked") || rentalStatus.equals("approved")) {
            btnRecheduleBooking.setOnClickListener(view -> {
                new RescheduleRequestBottomSheet(rentalId, bookingId, "reschedule", "").show(getSupportFragmentManager(), "");
            });
        }
        else if(rentalStatus.equals("returned")){
            btnRecheduleBooking.setVisibility(View.GONE);
            btnCancelBooking.setVisibility(View.GONE);
        }
        btnCancelBooking.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, Do you want to cancel this request?");
            alertDialogBuilder.setTitle("Cancel Booking?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                           cancelRequestAPICall();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        });
    }

    private void cancelRequestAPICall() {
        RentalEditReq rentalEditReq = new RentalEditReq(rentalId, bookingId,"","","cancel");

        mViewModel.getDetails(this,rentalEditReq);
        mViewModel.getRentalLiveData().observe(this, response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    showLoadingUI();
                    break;
                case FAILED:
                    hideLoadingUI();
                    errorSnack("Something Went wrong please try again later").show();
                    break;
                case SUCCESS:
                    hideLoadingUI();
                    Intent i = new Intent(BookingDetailsActivity.this, FinalActivity.class);
                    i.putExtra("from","refund");
                    startActivity(i);
                    break;
            }
        });
    }

    private void setInit() {
        btnRecheduleBooking=findViewById(R.id.confirm_booking_button);
        btnCancelBooking=findViewById(R.id.btnCancelBooking);
        txtTrailerName=findViewById(R.id.txtTrailerName);
        licenseeName = findViewById(R.id.txtLicenseeName);
        txtRentalStartDate=findViewById(R.id.tv_start_date);
        txtRentalEndDate=findViewById(R.id.tv_end_date);
        txtCustomerAddress=findViewById(R.id.tv_delivery_loc);
        itemPic=findViewById(R.id.trailer_image_book);
        mTxtRentalCharges =findViewById(R.id.price_base_book);
        mTxtPriceTaxes=findViewById(R.id.price_tax_book);
        mTxtDamageCharges = findViewById(R.id.price_damage_book);
        txtShowTrailerDetails=findViewById(R.id.txtShowTrailerDetails);
        summaryPage = findViewById(R.id.summary_page);
        progressBar = findViewById(R.id.summary_loading);
        startTime = findViewById(R.id.tv_start_time);
        endTime = findViewById(R.id.tv_end_time);
        mTxtTotalCharges = findViewById(R.id.price_total_book);
        trailerName = findViewById(R.id.tv_trailer_name_book);
        damageTitle = findViewById(R.id.damage_waiver_text_view);
        upsellRecycle = findViewById(R.id.upsell_items_summary);
        if(intent.getStringExtra("status")!=null) {
            if (Objects.equals(intent.getStringExtra("status"), "2")) {
                btnCancelBooking.setVisibility(View.GONE);
                btnRecheduleBooking.setVisibility(View.GONE);
            }
        }

    }

    void showLoadingUI(){
        summaryPage.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideLoadingUI(){
        summaryPage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public Snackbar errorSnack(String message){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.red_gradient_start));
        return snackbar;
    }

}