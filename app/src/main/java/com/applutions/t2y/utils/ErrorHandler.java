package com.applutions.t2y.utils;

import com.applutions.t2y.data.network.ApiError;

import retrofit2.Response;

public class ErrorHandler<T> {
    Response<T> response;

    public ApiError getError(Response<T> response){
        ApiError error = new ApiError();
        switch (response.code()){
            case 500:
                error = new ApiError("Authorization Failed");
                break;
            case 400:
                error = new ApiError("User Side Error");
                break;
            default:

        }
        return error;
    }

}
