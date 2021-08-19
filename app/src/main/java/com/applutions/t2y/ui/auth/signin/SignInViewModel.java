package com.applutions.t2y.ui.auth.signin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.BottomNavActivity;
import com.applutions.t2y.R;
import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.login.signIn.LoginResponse;
import com.applutions.t2y.data.response.login.signIn.SignInObj;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends ViewModel {

    private MutableLiveData<ApiResponse<LoginResponse>> apiResponseLiveData = new MutableLiveData<>();

    public void signInUser(Context context, SignInObj signInData) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        Call<LoginResponse> loginResponseCall = apiService.login(signInData);
        apiResponseLiveData.postValue(ApiResponse.getLoadingResponse());
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                Log.d("SignIn", "" + response.body());
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getSuccess()) {
                        ApiResponse<LoginResponse> successResponse = ApiResponse.getSuccessResponse(response.body());
                        apiResponseLiveData.postValue(successResponse);
                    }
                } else {
                    // TODO need to review error implementation. Find the error response pattern
                    ApiError error = ErrorUtils.parseError(context, response);
                    Log.d("SignIn", "" + error.isSuccess());
                    apiResponseLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Log.d("HERE",t.getMessage());
                ApiError error = new ApiError("Failed to login user");
                apiResponseLiveData.postValue(ApiResponse.getFailureResponse(error));
            }
        });
    }


    public LiveData<ApiResponse<LoginResponse>> getLoginApiListener() {
        return apiResponseLiveData;
    }

}
