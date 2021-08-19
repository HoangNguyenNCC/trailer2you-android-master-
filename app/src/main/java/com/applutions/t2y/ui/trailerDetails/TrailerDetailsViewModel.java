package com.applutions.t2y.ui.trailerDetails;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.items.TrailerViewReq;
import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.LicenseeResponse;
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.data.response.trailer.RentalItemResponse;
import com.applutions.t2y.data.response.trailer.TrailerObj;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrailerDetailsViewModel extends ViewModel {

    private MutableLiveData<ApiResponse<RentalItemResponse>> rentalLiveData = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<TrailerViewResponse>> viewLiveData = new MutableLiveData<>();
//    private MutableLiveData<ApiResponse<UpsellItemResponse>> upsellItemsLiveData = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<LicenseeResponse>> licenseeLiveData = new MutableLiveData<>();

    public void getRentalDetails(Context context, String id) {
            ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
            rentalLiveData.postValue(ApiResponse.getLoadingResponse());
            apiService.getRentalItem(id).enqueue(new Callback<RentalItemResponse>() {
                @Override
                public void onResponse(@NotNull Call<RentalItemResponse> call, @NotNull Response<RentalItemResponse> response) {
                    if (response.isSuccessful()) {
                        rentalLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                        if (response.body() != null) {
                            TrailerObj trailer = response.body().getTrailerObj();
                            String licenseeId = trailer.getLicenseeId();
                            getLiceseeDetails(context, licenseeId);
                        }
                    } else {
                        rentalLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RentalItemResponse> call, @NotNull Throwable t) {
                    rentalLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to get rental details")));
                }
            });
    }

    public void getTrailerView(Context context, TrailerViewReq req) {
            ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
            viewLiveData.postValue(ApiResponse.getLoadingResponse());
            apiService.getTrailerView(req).enqueue(new Callback<TrailerViewResponse>() {
                @Override
                public void onResponse(Call<TrailerViewResponse> call, Response<TrailerViewResponse> response) {
                    if (response.isSuccessful()){
                        viewLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                    }
                    else {
                        viewLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                    }
                }
                @Override
                public void onFailure(Call<TrailerViewResponse> call, Throwable t) {
                    viewLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to get items")));
                }
            });
    }

    public void getLiceseeDetails(Context context, String licenseeId) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        licenseeLiveData.postValue(ApiResponse.getLoadingResponse());
        apiService.getTrailerLicensee(licenseeId).enqueue(new Callback<LicenseeResponse>() {
            @Override
            public void onResponse(@NotNull Call<LicenseeResponse> call, @NotNull Response<LicenseeResponse> response) {
                if (response.isSuccessful()) {
                    licenseeLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    licenseeLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                }
            }

            @Override
            public void onFailure(@NotNull Call<LicenseeResponse> call, @NotNull Throwable t) {
                licenseeLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to get licensee details")));
                Log.d("HERE", t.toString());
            }
        });
    }

    public LiveData<ApiResponse<RentalItemResponse>> getRentalLiveData() {
        return rentalLiveData;
    }

//    public LiveData<ApiResponse<UpsellItemResponse>> getUpsellItemsLiveData() {
//        return upsellItemsLiveData;
//    }

    public LiveData<ApiResponse<TrailerViewResponse>> getViewLiveData(){
        return viewLiveData;
    }

    public LiveData<ApiResponse<LicenseeResponse>> getLicenseeLiveData() {
        return licenseeLiveData;
    }

}
