package com.applutions.t2y.ui.auth;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.login.signIn.DriverLicense;
import com.applutions.t2y.data.response.login.signUp.SignUpAddressModel;
import com.applutions.t2y.data.response.login.signUp.SignUpResponse;
import com.applutions.t2y.data.response.login.signUp.SignUpUserModel;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private DriverLicense license;
    private String licenseUriString;
    private MutableLiveData<ApiResponse<Boolean>> signUpLiveData = new MutableLiveData<>();
    private String imageUri;
    private final String TAG = "AuthViewModel";

    public DriverLicense getLicense() {
        return license;
    }

    public void setLicense(DriverLicense license) {
        this.license = license;
    }

    public void setLicenseUri(String licenseUriString) {
        this.licenseUriString = licenseUriString;
    }

    public String getLicenseUriString() {
        return licenseUriString;
    }

    public String getProfileImageUri() {
        return this.imageUri;
    }

    public void signUpUser(Context context, SignUpUserModel signUpData) {
        Log.v("AuthViewModel", "Signing up user...");
        Log.v("AuthViewModel", license.toString());

        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        File imageFile = new File(imageUri);
        if (!imageFile.exists()) {
            Log.e(TAG, "IMAGE DOESNT EXIST " + imageUri);
            return;
        }
        File scannedLicense = new File(licenseUriString);
        if (!scannedLicense.exists()) {
            Log.e(TAG, "LICENSE IMAGE DOESNT EXIST " + licenseUriString);
            return;
        }
        RequestBody userImageFile =  RequestBody.create(MediaType.parse("image/jpg"), imageFile);
        MultipartBody.Part usrImage = MultipartBody.Part.createFormData("photo", imageFile.getName() ,userImageFile);

        RequestBody licenseRequestFile =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        scannedLicense
                );
        MultipartBody.Part usrLicense = MultipartBody.Part.createFormData("driverLicenseScan", scannedLicense.getName(), licenseRequestFile);

        String signUpJson = getSignUpJson(signUpData).toString();
        Log.v(TAG, signUpJson);
        RequestBody requestBodyInfo  = MultipartBody.create(MediaType.parse("multipart/form-data"), signUpJson);

        Call<SignUpResponse> signUpResponseCall = apiService.signUp(usrImage, usrLicense, requestBodyInfo);

        signUpLiveData.postValue(ApiResponse.getLoadingResponse());
        signUpResponseCall.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(@NotNull Call<SignUpResponse> call, @NotNull Response<SignUpResponse> response) {
                if(response.isSuccessful()){
                    signUpLiveData.postValue(ApiResponse.getSuccessResponse(true));
                }else{
                    ApiError error = ErrorUtils.parseError(context, response);
                    signUpLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<SignUpResponse> call, @NotNull Throwable t) {
                signUpLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error signing up")));
            }
        });

    }

    public static RequestBody createRequestBody(@NonNull String s) {
        String MULTIPART_FORM_DATA = "multipart/form-data";
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), s);
    }

    public LiveData<ApiResponse<Boolean>> getSignUpListener() {
        return signUpLiveData;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    private JsonObject getSignUpJson(SignUpUserModel signUpData) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", signUpData.getEmail());
        jsonObject.addProperty("password", signUpData.getPassword());
        jsonObject.addProperty("mobile", signUpData.getMobile());
        jsonObject.addProperty("name", signUpData.getName());
        jsonObject.addProperty("dob", signUpData.getDob());


        jsonObject.add("address", signUpData.getAddress());

        JsonObject licenseData = new JsonObject();
        licenseData.addProperty("card", this.license.getCard());
        licenseData.addProperty("expiry", this.license.getExpiry());
        licenseData.addProperty("state", this.license.getState());

        jsonObject.add("driverLicense", licenseData);

        return jsonObject;
    }
}
