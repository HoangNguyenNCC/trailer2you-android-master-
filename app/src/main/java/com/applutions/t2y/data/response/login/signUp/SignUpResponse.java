package com.applutions.t2y.data.response.login.signUp;

public class SignUpResponse {
    /**
     * success : true
     * message : Success
     */

    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
