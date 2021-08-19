package com.applutions.t2y.ui.booking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.applutions.t2y.BottomNavActivity;
import com.applutions.t2y.R;

public class FinalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Button b = findViewById(R.id.button);
        TextView tv = findViewById(R.id.tv_message);
        b.setOnClickListener(v->startActivity(new Intent(FinalActivity.this, BottomNavActivity.class)));
        Intent i = getIntent();
        String s = i.getStringExtra("from");
        if (s!=null) {
            switch (s){
                case "payment":
                    tv.setText("Payment completed successfully");
                    break;
                case "refund":
                    tv.setText("The amount of your invoice will be refunded to your account in 7-10 working days");
                    break;
                case "none":
                    tv.setText("Your request has been processed successfully, no further action is required");
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}