package com.applutions.t2y.ui.auth.otp;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.login.otp.ResendOtpBody;
import com.applutions.t2y.data.response.login.otp.VerifyMobileBody;
import com.applutions.t2y.data.response.login.otp.VerifyMobileResponse;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpViewModel extends ViewModel {

    private MutableLiveData<ApiResponse<VerifyMobileResponse>> verifyPhoneResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<VerifyMobileResponse>> resendOtpResponseLiveData = new MutableLiveData<>();

    public void verifyPhoneNumber(Context context, String mobile, String country, String otp) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);

        Call<VerifyMobileResponse> loginResponseCall = apiService.verifyMobile(new VerifyMobileBody(mobile,country ,otp));
        verifyPhoneResponseLiveData.postValue(ApiResponse.getLoadingResponse());
        loginResponseCall.enqueue(new Callback<VerifyMobileResponse>() {
            @Override
            public void onResponse(@NotNull Call<VerifyMobileResponse> call, @NotNull Response<VerifyMobileResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getSuccess()) {
                        ApiResponse<VerifyMobileResponse> successResponse = ApiResponse.getSuccessResponse(response.body());
                        verifyPhoneResponseLiveData.postValue(successResponse);
                    }
                } else {
                    ApiError error = ErrorUtils.parseError(context, response);
                    Log.d("SignIn", "" + error.isSuccess());
                    verifyPhoneResponseLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<VerifyMobileResponse> call, @NotNull Throwable t) {
                verifyPhoneResponseLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to verify phone")));
            }
        });
    }

    public void resendOtp(Context context, String mobile, String country) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);

        Call<VerifyMobileResponse> loginResponseCall = apiService.resendOtp(new ResendOtpBody(mobile,country));
        resendOtpResponseLiveData.postValue(ApiResponse.getLoadingResponse());
        loginResponseCall.enqueue(new Callback<VerifyMobileResponse>() {
            @Override
            public void onResponse(@NotNull Call<VerifyMobileResponse> call, @NotNull Response<VerifyMobileResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getSuccess()) {
                        ApiResponse<VerifyMobileResponse> successResponse = ApiResponse.getSuccessResponse(response.body());
                        resendOtpResponseLiveData.postValue(successResponse);
                    }
                } else {
                    ApiError error = ErrorUtils.parseError(context, response);
                    Log.d("SignIn", "" + error.isSuccess());
                    resendOtpResponseLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<VerifyMobileResponse> call, @NotNull Throwable t) {
                resendOtpResponseLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to verify phone")));
            }
        });
    }


    public LiveData<ApiResponse<VerifyMobileResponse>> getVerifyMobileListener() {
        return verifyPhoneResponseLiveData;
    }

    public LiveData<ApiResponse<VerifyMobileResponse>> getResendOtpListener() {
        return resendOtpResponseLiveData;
    }

}
