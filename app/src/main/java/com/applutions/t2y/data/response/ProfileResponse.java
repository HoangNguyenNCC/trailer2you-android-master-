package com.applutions.t2y.data.response;

import java.util.ArrayList;

public class ProfileResponse {
    private Boolean  success;
    private String  message;
    private UseObj userObj;
    private ArrayList<String> errorsList = new ArrayList<String>();

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UseObj getUserObj() {
        return userObj;
    }

    public void setUserObj(UseObj userObj) {
        this.userObj = userObj;
    }

    public ArrayList<String> getErrorsList() {
        return errorsList;
    }

    public void setErrorsList(ArrayList<String> errorsList) {
        this.errorsList = errorsList;
    }
}
