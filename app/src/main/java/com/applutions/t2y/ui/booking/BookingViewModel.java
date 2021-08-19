package com.applutions.t2y.ui.booking;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.items.ReqCharges;
import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.booking.Booking;
import com.applutions.t2y.data.response.booking.BookingResponse;
import com.applutions.t2y.ui.notifications.response.RentalObj;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingViewModel extends ViewModel {

    private MutableLiveData<ApiResponse<BookingResponse>> bookingLiveData = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<RentalObj.Charges>> chargesLiveData = new MutableLiveData<>();

    public void getCharges(Context context, ReqCharges reqCharges){
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        chargesLiveData.postValue(ApiResponse.getLoadingResponse());
        apiService.getCharges(reqCharges).enqueue(new Callback<RentalObj.Charges>() {
            @Override
            public void onResponse(Call<RentalObj.Charges> call, Response<RentalObj.Charges> response) {
                if (response.isSuccessful()) {
                    chargesLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    chargesLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                }
            }

            @Override
            public void onFailure(Call<RentalObj.Charges> call, Throwable t) {
                bookingLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error getting booking details")));
            }
        });

    }

    public void requestBooking(Context context, Booking booking) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        bookingLiveData.postValue(ApiResponse.getLoadingResponse());
        apiService.requestBooking(booking).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    bookingLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    bookingLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                bookingLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error getting booking details")));
            }
        });
    }

    public MutableLiveData<ApiResponse<RentalObj.Charges>> getChargesLiveData() {
        return chargesLiveData;
    }

    public LiveData<ApiResponse<BookingResponse>> getBookingLiveData() {
        return this.bookingLiveData;
    }

}
