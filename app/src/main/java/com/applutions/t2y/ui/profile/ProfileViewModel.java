package com.applutions.t2y.ui.profile;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.ProfileResponse;
import com.applutions.t2y.data.response.trailer.RentalItemResponse;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<ApiResponse<ProfileResponse>> rentalLiveData = new MutableLiveData<>();

    public void getDetails(Context context) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        rentalLiveData.postValue(ApiResponse.getLoadingResponse());
        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NotNull Call<ProfileResponse> call, @NotNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    rentalLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    rentalLiveData.postValue(ApiResponse.getFailureResponse(ErrorUtils.parseError(context, response)));
                }
            }
            @Override
            public void onFailure(@NotNull Call<ProfileResponse> call, @NotNull Throwable t) {
                rentalLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to get rental details")));
                Log.d("HERE",t.toString());
            }
        });
    }

    public LiveData<ApiResponse<ProfileResponse>> getRentalLiveData() {
        return rentalLiveData;
    }

}
