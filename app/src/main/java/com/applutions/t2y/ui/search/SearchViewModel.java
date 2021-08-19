package com.applutions.t2y.ui.search;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applutions.t2y.data.network.ApiError;
import com.applutions.t2y.data.network.ApiService;
import com.applutions.t2y.data.network.RetrofitInstance;
import com.applutions.t2y.data.response.filter.FilterResponse;
import com.applutions.t2y.data.response.search.SearchResponse;
import com.applutions.t2y.data.response.search.SearchTrailerBody;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.ErrorUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {

    MutableLiveData<ApiResponse<SearchResponse>> searchTrailerLiveData = new MutableLiveData<>();
    MutableLiveData<ApiResponse<FilterResponse>> filtersLiveData = new MutableLiveData<>();

    public void sendSearchRequest(Context context, String lat, String longitude, String StartDate, String EndDate, String StartTime, String EndTime){
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);

        Double[] loc = {Double.parseDouble(lat), Double.parseDouble(longitude)};
        ArrayList<String> date = new ArrayList<>();
        date.add(StartDate);
        date.add(EndDate);
        ArrayList<String> time = new ArrayList<>();
        time.add(StartTime);time.add(EndTime);
        SearchTrailerBody trailerBody = new SearchTrailerBody(loc, date ,time);
        Call<SearchResponse> responseCall = apiService.searchTrailers(trailerBody);
        searchTrailerLiveData.postValue(ApiResponse.getLoadingResponse());
        responseCall.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if(response.isSuccessful()) {
                    searchTrailerLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                }
                else{
                    ApiError apiError = ErrorUtils.parseError(context,response);
                    searchTrailerLiveData.postValue(ApiResponse.getFailureResponse(apiError));
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                    searchTrailerLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error sending search trailer request")));
            }
        });
    }

    public void sendFiltersRequest(Context context){
        ApiService apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService.class);
        Call<FilterResponse> responseCall = apiService.getFilters();
        filtersLiveData.postValue(ApiResponse.getLoadingResponse());
        responseCall.enqueue(new Callback<FilterResponse>() {
            @Override
            public void onResponse(Call<FilterResponse> call, Response<FilterResponse> response) {
                if(response.isSuccessful()){
                    filtersLiveData.postValue(ApiResponse.getSuccessResponse(response.body()));
                }
                else {
                    ApiError apiError = ErrorUtils.parseError(context, response);
                    filtersLiveData.postValue(ApiResponse.getFailureResponse(apiError));
                }
            }

            @Override
            public void onFailure(Call<FilterResponse> call, Throwable t) {
                filtersLiveData.postValue(ApiResponse.getFailureResponse(new ApiError("Error sending filter request")));
            }
        });
    }

    public LiveData<ApiResponse<SearchResponse>> getSearchTrailerListener(){
        return searchTrailerLiveData;
    }

    public MutableLiveData<ApiResponse<FilterResponse>> getFiltersLiveData() {
        return filtersLiveData;
    }
}
