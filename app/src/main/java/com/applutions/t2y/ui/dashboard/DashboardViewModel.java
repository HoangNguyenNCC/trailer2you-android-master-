package com.applutions.t2y.ui.dashboard;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.items.PostRatingBody;
import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.PostRatingsResponse;
import com.applutions.t2y.data.response.RatingsResponse;
import com.applutions.t2y.data.response.search.SearchResponse;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends ViewModel {
    MutableLiveData<ApiResponse<SearchResponse>> dashboardLiveData = new MutableLiveData<>();
    MutableLiveData<ApiResponse<ArrayList<RatingsResponse.Obj>>> ratingsLiveData = new MutableLiveData<>();
    MutableLiveData<ApiResponse<PostRatingsResponse>> postRatingsLiveData = new MutableLiveData<>();

    public void sendFeaturedRequestList(Context context){
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        Call<SearchResponse> responseCall = apiService.featuredTrailers();
        dashboardLiveData.postValue(ApiResponse.getLoadingResponse());
        responseCall.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                Log.d("Here", "Response");
                if(response.isSuccessful()) {
                    dashboardLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                }
                else{
                    ApiError apiError = ErrorUtils.parseError(context,response);
                    dashboardLiveData.postValue(ApiResponse.getFailureResponse(apiError));
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.d("Here", "Failure??");
                dashboardLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error sending search trailer request")));
            }
        });

    }

    public void getPendingRatings(Context context){
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        Call<ArrayList<RatingsResponse.Obj>> responseCall = apiService.getPendingRatings();
        ratingsLiveData.postValue(ApiResponse.getLoadingResponse());
        responseCall.enqueue(new Callback<ArrayList<RatingsResponse.Obj>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingsResponse.Obj>> call, Response<ArrayList<RatingsResponse.Obj>> response) {
                if(response.isSuccessful()) {
                    ratingsLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                }
                else{
                    ApiError apiError = ErrorUtils.parseError(context,response);
                    ratingsLiveData.postValue(ApiResponse.getFailureResponse(apiError));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RatingsResponse.Obj>> call, Throwable t) {
                ratingsLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error sending search trailer request")));
            }
        });

    }

    public void postRatings(Context context,PostRatingBody body){
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        Call<PostRatingsResponse> responseCall = apiService.postRatings(body);
        postRatingsLiveData.postValue(ApiResponse.getLoadingResponse());
        responseCall.enqueue(new Callback<PostRatingsResponse>() {
            @Override
            public void onResponse(@NotNull Call<PostRatingsResponse> call, @NotNull Response<PostRatingsResponse> response) {
                if(response.isSuccessful()){
                    postRatingsLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                }
                else {
                    ApiError error = ErrorUtils.parseError(context, response);
                    postRatingsLiveData.postValue(ApiResponse.getFailureResponse(error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostRatingsResponse> call, @NotNull Throwable t) {
                postRatingsLiveData.postValue(ApiResponse.getFailureResponse(new ApiError(t.getMessage())));
            }
        });
    }

    public LiveData<ApiResponse<SearchResponse>> getSearchTrailerListener(){
        return dashboardLiveData;
    }

    public LiveData<ApiResponse<ArrayList<RatingsResponse.Obj>>> getRatingsLiveData() {
        return ratingsLiveData;
    }

    public MutableLiveData<ApiResponse<PostRatingsResponse>> getPostRatingsLiveData() {
        return postRatingsLiveData;
    }
}
