package com.applutions.t2y.ui.auth.forgotpassword;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordBody;
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordLinkBody;
import com.applutions.t2y.data.response.login.forgotPassword.PasswordResetLinkResponse;
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordResponse;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {

    MutableLiveData<ApiResponse<PasswordResetLinkResponse>> passwordResetLinkLiveData = new MutableLiveData<>();
    MutableLiveData<ApiResponse<ResetPasswordResponse>> passwordResetLiveData = new MutableLiveData<>();

    public void sendPasswordResetLink(Context context, String email) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        ResetPasswordLinkBody resetLinkBody = new ResetPasswordLinkBody(email, "android");
        Call<PasswordResetLinkResponse> resetPasswordResponseCall = apiService.resetPasswordLink(resetLinkBody);
        passwordResetLinkLiveData.postValue(ApiResponse.getLoadingResponse());
        resetPasswordResponseCall.enqueue(new Callback<PasswordResetLinkResponse>() {
            @Override
            public void onResponse(@NotNull Call<PasswordResetLinkResponse> call, @NotNull Response<PasswordResetLinkResponse> response) {
                if (response.isSuccessful()) {
                    passwordResetLinkLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    ApiError error = ErrorUtils.parseError(context, response);
                    passwordResetLinkLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<PasswordResetLinkResponse> call, @NotNull Throwable t) {
                passwordResetLinkLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error sending password reset link")));
            }
        });
    }

    public LiveData<ApiResponse<PasswordResetLinkResponse>> getResetLinkListener() {
        return passwordResetLinkLiveData;
    }

    public LiveData<ApiResponse<ResetPasswordResponse>> getResetPasswordListener() {
        return passwordResetLiveData;
    }

    public void resetPassword(Context context, String token, String password) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(token, password);

        Call<ResetPasswordResponse> resetPasswordBodyCall = apiService.resetPassword(resetPasswordBody);
        passwordResetLinkLiveData.postValue(ApiResponse.getLoadingResponse());
        resetPasswordBodyCall.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(@NotNull Call<ResetPasswordResponse> call, @NotNull Response<ResetPasswordResponse> response) {
                if (response.isSuccessful()) {
                    passwordResetLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                } else {
                    ApiError error = ErrorUtils.parseError(context, response);
                    passwordResetLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResetPasswordResponse> call, @NotNull Throwable t) {
                passwordResetLinkLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error resetting password")));
            }
        });
    }
}
