package com.applutions.t2y.data.network;

public class ApiError {

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success;
    private String message;

    public ApiError() {
    }

    public ApiError(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
