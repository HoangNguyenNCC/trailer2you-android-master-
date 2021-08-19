package com.applutions.t2y.ui.notifications.response;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.items.RentalEditReq;
import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.CancelRequestBody;
import com.applutions.t2y.data.response.ProfileResponse;
import com.applutions.t2y.data.response.booking.BookingEditResponse;
import com.applutions.t2y.data.response.login.CommonResponse;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsViewModel extends ViewModel {
    private MutableLiveData<ApiResponse<BookingEditResponse>> cancelLiveData = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<RentalDetailsResponse>> RentalDetailLiveData = new MutableLiveData<>();

    public void getDetails(Context context, RentalEditReq cancelRequestBody) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        cancelLiveData.postValue(ApiResponse.getLoadingResponse());
        apiService.editBooking(cancelRequestBody).enqueue(new Callback<BookingEditResponse>() {
            @Override
            public void onResponse(Call<BookingEditResponse> call, Response<BookingEditResponse> response) {
                if (response.isSuccessful()) {
                    cancelLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    cancelLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                }
            }

            @Override
            public void onFailure(Call<BookingEditResponse> call, Throwable t) {
                cancelLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to get rental details")));
            }
        });

    }
    public void getRentalDetails(Context context, String id) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        RentalDetailLiveData.postValue(ApiResponse.getLoadingResponse());

        apiService.getRentalDetails(id).enqueue(new Callback<RentalDetailsResponse>() {
            @Override
            public void onResponse(@NotNull Call<RentalDetailsResponse> call, @NotNull Response<RentalDetailsResponse> response) {
                if (response.isSuccessful()) {
                    RentalDetailLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    RentalDetailLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                }
            }
            @Override
            public void onFailure(@NotNull Call<RentalDetailsResponse> call, @NotNull Throwable t) {
                RentalDetailLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to get rental details")));
            }
        });
    }
    public LiveData<ApiResponse<BookingEditResponse>> getRentalLiveData() {
        return cancelLiveData;
    }
    public LiveData<ApiResponse<RentalDetailsResponse>> getRentalDetailsLiveData() {
        return RentalDetailLiveData;
    }

}
