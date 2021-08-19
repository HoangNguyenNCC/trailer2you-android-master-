package com.applutions.t2y.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.common.Utils;
import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.ProfileResponse;
import com.applutions.t2y.data.response.login.SendEmailVeifyLinkBody;
import com.applutions.t2y.data.response.login.otp.ResendOtpBody;
import com.applutions.t2y.data.response.login.otp.VerifyMobileResponse;
import com.applutions.t2y.data.response.login.signIn.DriverLicense;
import com.applutions.t2y.data.response.login.signUp.SignUpResponse;
import com.applutions.t2y.data.response.login.signUp.SignUpUserModel;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileViewModel extends ViewModel {

    private DriverLicense license;
    private String licenseUriString;
    private MutableLiveData<ApiResponse<ProfileResponse>> rentalLiveData = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<VerifyMobileResponse>> sendEmailVerifyResponseLiveData = new MutableLiveData<>();
   // private MutableLiveData<ApiResponse<Boolean>> signUpLiveData = new MutableLiveData<>();
    private String imageUri;
    private Bitmap mBitmap;
    private final String TAG = "UpdateProfileViewModel";

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public DriverLicense getLicense() {
        return license;
    }

    public void setLicense(DriverLicense license) {
        this.license = license;
    }

    public void setLicenseUri(String licenseUriString) {
        this.licenseUriString = licenseUriString;
    }

    public String getProfileImageUri() {
        return this.imageUri;
    }
    MultipartBody.Part usrLicense;
    MultipartBody.Part usrImage;
    public void UpdateProfile(Context context, JsonObject updateJsonObj) {
       // Log.v(TAG, "Update Profile...");
        //Log.v(TAG, license.toString());

        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
            File imageFile = Utils.Companion.convertBitmapToFile("temp.png",getmBitmap(),context);
            if (!imageFile.exists()) {
                Log.e(TAG, "IMAGE DOESNT EXIST " + imageUri);
                return;
            }
            RequestBody userImageFile = RequestBody.create(MediaType.parse("image/jpg"), imageFile);
            usrImage = MultipartBody.Part.createFormData("photo", imageFile.getName(), userImageFile);


        if(updateJsonObj.has("driverLicense")) {
            File scannedLicense = new File(licenseUriString);
            if (!scannedLicense.exists()) {
                Log.e(TAG, "LICENSE IMAGE DOESNT EXIST " + licenseUriString);
                return;
            }
            RequestBody licenseRequestFile =
                    RequestBody.create(
                            MediaType.parse("image/jpg"),
                            scannedLicense
                    );
            usrLicense = MultipartBody.Part.createFormData("driverLicenseScan", scannedLicense.getName(), licenseRequestFile);
        }

        RequestBody requestBodyInfo  = MultipartBody.create(MediaType.parse("multipart/form-data"), updateJsonObj.toString());

        Call<ProfileResponse> signUpResponseCall =null;
        if(updateJsonObj.has("driverLicense")){
            signUpResponseCall = apiService.UpdateProfile(usrImage, usrLicense, requestBodyInfo);
        }
        else{
            signUpResponseCall = apiService.UpdateProfileWithoutLicense(usrImage, requestBodyInfo);
        }
        rentalLiveData.postValue(ApiResponse.getLoadingResponse());
        signUpResponseCall.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NotNull Call<ProfileResponse> call, @NotNull Response<ProfileResponse> response) {
                if(response.isSuccessful()){
                    rentalLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                }else{
                    ApiError error = ErrorUtils.parseError(context, response);
                    rentalLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<ProfileResponse> call, @NotNull Throwable t) {
                rentalLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error signing up")));
            }
        });

    }



    public LiveData<ApiResponse<ProfileResponse>> getUpdateListener() {
        return rentalLiveData;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public void sendVerifyLink(Context context, String email) {
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);

        Call<VerifyMobileResponse> loginResponseCall = apiService.sendEmailVerifyLink(new SendEmailVeifyLinkBody(email));
        sendEmailVerifyResponseLiveData.postValue(ApiResponse.getLoadingResponse());
        loginResponseCall.enqueue(new Callback<VerifyMobileResponse>() {
            @Override
            public void onResponse(@NotNull Call<VerifyMobileResponse> call, @NotNull Response<VerifyMobileResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getSuccess()) {
                        ApiResponse<VerifyMobileResponse> successResponse = ApiResponse.getSuccessResponse(response.body());
                        sendEmailVerifyResponseLiveData.postValue(successResponse);
                    }
                } else {
                    ApiError error = ErrorUtils.parseError(context, response);
                    Log.d("SignIn", "" + error.isSuccess());
                    sendEmailVerifyResponseLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<VerifyMobileResponse> call, @NotNull Throwable t) {
                sendEmailVerifyResponseLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Unable to verify phone")));
            }
        });
    }
    public LiveData<ApiResponse<VerifyMobileResponse>> getVerifyLinkListner() {
        return sendEmailVerifyResponseLiveData;
    }
}
