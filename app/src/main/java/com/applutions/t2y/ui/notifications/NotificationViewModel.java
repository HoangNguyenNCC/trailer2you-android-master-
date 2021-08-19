package com.applutions.t2y.ui.notifications;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.items.RentalEditReq;
import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.RescheduleRequestBody;
import com.applutions.t2y.data.response.booking.BookingEditResponse;
import com.applutions.t2y.data.response.login.CommonResponse;
import com.applutions.t2y.ui.notifications.response.NotificationResponse;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorHandler;
import com.applutions.t2y.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends ViewModel {
    private MutableLiveData<ApiResponse<NotificationResponse>> rentalLiveData = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<BookingEditResponse>> rescheduleLiveData = new MutableLiveData<>();

    public void getDetails(Context context, String type) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        rentalLiveData.postValue(ApiResponse.getLoadingResponse());
        apiService.getReminders(type).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NotNull Call<NotificationResponse> call, @NotNull Response<NotificationResponse> response) {
                if (response.isSuccessful()) {
                    rentalLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    ErrorHandler<NotificationResponse> handler = new ErrorHandler<>();
                    ApiError e = handler.getError(response);
                    if (response.code()/100 == 5){
                        rentalLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Internal Server Error")));
                        ApiError error = ErrorUtils.parseError(context, response);
                        Log.d("Internal Server Error",error.message());
                    }
                    else if (response.code()/100 == 4){
                        rentalLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Check the details entered")));
                        Log.d("User Side Error", response.message());

                    }
                    else {
                        rentalLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<NotificationResponse> call, @NotNull Throwable t) {
                rentalLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to get rental details")));
            }
        });
    }
    public void rescheduleBooking(Context context, String rentalId, String bookingId, String start, String end, String code) throws ParseException {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        rescheduleLiveData.postValue(ApiResponse.getLoadingResponse());
//        RescheduleRequestBody rescheduleRequestBody=new RescheduleRequestBody(invoiceId,revisionId,start,end);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getDefault());
        Date date = null;
        date = df.parse(start);
        Date date2 = df.parse(end);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String UTC = df.format(date);
        String UTCend = df.format(date2);

        RentalEditReq editReq = new RentalEditReq(rentalId, bookingId, UTC, UTCend, code);
        apiService.editBooking(editReq).enqueue(new Callback<BookingEditResponse>() {
            @Override
            public void onResponse(Call<BookingEditResponse> call, Response<BookingEditResponse> response) {
                if (response.isSuccessful()) {
                    rescheduleLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    rescheduleLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));

                }
            }

            @Override
            public void onFailure(Call<BookingEditResponse> call, Throwable t) {
                rescheduleLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Something wrong")));
                Log.d("HERE",t.toString());
            }
        });

    }

    public LiveData<ApiResponse<NotificationResponse>> getRentalLiveData() {
        return rentalLiveData;
    }
    public LiveData<ApiResponse<BookingEditResponse>> getrescheduleLiveData() {
        return rescheduleLiveData;
    }
}
