package com.applutions.t2y.ui.rentTrailer;

import android.os.Bundle;

import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.applutions.t2y.R;

public class RentAndUpsellTrailerActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_and_upsell_trailer);


        // Enables Always-on
        setAmbientEnabled();
    }
}