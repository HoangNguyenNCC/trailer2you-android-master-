package com.applutions.t2y.ui.booking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.applutions.t2y.BottomNavActivity;
import com.applutions.t2y.R;
import com.applutions.t2y.common.ExpiryDateWatcher;
import com.applutions.t2y.common.PaymentDetailsWatcher;
import com.applutions.t2y.utils.SharedPrefs;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BookingActivity extends AppCompatActivity {

    private Stripe stripe;
    private Button payButton;
    private TextView cancelButton;
    private ProgressBar pbLoading;
    private ConstraintLayout cardLayout;

    private TextInputEditText etCardNumber;
    private TextInputEditText etExpiry;
    private TextInputEditText etCvv;
    private TextInputEditText etCardName;

    private TextInputLayout etCardNumberLayout;
    private TextInputLayout etExpiryLayout;
    private TextInputLayout etCvvLayout;
    private TextInputLayout etCardNameLayout;

    private String paymentIntentId;
    private String paymentIntentClientSecret;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        sharedPrefs = SharedPrefs.getInstance(this);

        findViews();

        Intent i = getIntent();
        paymentIntentId = i.getStringExtra("clientKey");
        stripe = new Stripe(getApplicationContext(), getString(R.string.stripe_publishable_key));
        hideLoading();

        payButton.setOnClickListener(v -> {
            PaymentMethodCreateParams.Card cardDetails = getCardDetails();
            if (cardDetails == null) return;
            showLoading();
            PaymentMethodCreateParams params = PaymentMethodCreateParams.create(cardDetails);
            if (paymentIntentId != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentId);
                stripe.confirmPayment(this, confirmParams);
            }
        });

        cancelButton.setOnClickListener(v -> {
            super.onBackPressed();
        });

    }

    private PaymentMethodCreateParams.Card getCardDetails() {
        String cardNumberWithSpaces = etCardNumber.getText() != null ? etCardNumber.getText().toString().trim() : null;

        if (cardNumberWithSpaces == null || TextUtils.isEmpty(cardNumberWithSpaces)) {
            etCardNumberLayout.setError("Invalid card number");
            return null;
        }

        String cardNumber = cardNumberWithSpaces.replace(" ", "");
        if (cardNumber.length() != 16) {
            etCardNumberLayout.setError("Invalid card number. More than 16 characters");
            return null;
        }

        String expiry = etExpiry.getText() != null ? etExpiry.getText().toString().trim() : null;

        if (expiry == null || TextUtils.isEmpty(expiry)) {
            etExpiryLayout.setError("Invalid expiry date");
            return null;
        }

        String expiryMonth = expiry.split("/")[0];
        String expiryYear = expiry.split("/")[1];

        String cvv = etCvv.getText() != null ? etCvv.getText().toString().trim() : null;
        if (cvv == null || TextUtils.isEmpty(cvv) || cvv.length() != 3) {
            etCvvLayout.setError("CVV invalid");
            return null;
        }

        Log.v("BookingActivity", "cvv "+ cvv);

        return new PaymentMethodCreateParams.Card.Builder()
                .setNumber(cardNumber)
                .setCvc(cvv)
                .setExpiryMonth(Integer.valueOf(expiryMonth))
                .setExpiryYear(Integer.valueOf(expiryYear))
                .build();
    }

    private void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
        cardLayout.setVisibility(View.INVISIBLE);
    }

    private void hideLoading() {
        pbLoading.setVisibility(View.GONE);
        cardLayout.setVisibility(View.VISIBLE);
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
        hideLoading();
        displayAlert("Success", "PAYMENT SUCCESSUFULL.. YAAAYYYY", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void findViews() {
        payButton = findViewById(R.id.payButton);
        cancelButton = findViewById(R.id.tv_cancel_payment);
        pbLoading = findViewById(R.id.booking_pb);
        cardLayout = findViewById(R.id.layout_card);

        etCardNumber = findViewById(R.id.et_card_number);
        etExpiry = findViewById(R.id.et_expiry_card);
        etCvv = findViewById(R.id.et_cvv_card);
        etCardName = findViewById(R.id.et_card_name);

        etCardNumberLayout = findViewById(R.id.card_number_layout);
        etExpiryLayout = findViewById(R.id.expiry_layout);
        etCvvLayout = findViewById(R.id.cvv_layout);
        etCardNameLayout = findViewById(R.id.card_name_layout);

        PaymentDetailsWatcher paymentDetailsWatcher = new PaymentDetailsWatcher(etCardNumber);
        etCardNumber.addTextChangedListener(paymentDetailsWatcher);

        ExpiryDateWatcher expiryDateWatcher = new ExpiryDateWatcher(etExpiry);
        etExpiry.addTextChangedListener(expiryDateWatcher);
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<BookingActivity> activityRef;
        PayCallback(@NonNull BookingActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final BookingActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }
        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final BookingActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }
    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<BookingActivity> activityRef;
        PaymentResultCallback(@NonNull BookingActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final BookingActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Intent i = new Intent(activity, FinalActivity.class);
                i.putExtra("from","payment");
                activity.startActivity(i);
//                activity.displayAlert(
//                        "Payment completed",
//                        "Trailer Successfully booked",
//                        true
//                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
                        true
                );
            }
        }
        @Override
        public void onError(@NonNull Exception e) {
            final BookingActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), true);
        }
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message, Boolean shouldFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (shouldFinish) {
                    startActivity(new Intent(BookingActivity.this, BottomNavActivity.class));
                }
            }
        });
        builder.create().show();
    }
}