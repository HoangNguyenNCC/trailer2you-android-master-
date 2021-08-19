package com.applutions.t2y.ui.notifications.response;

import java.util.ArrayList;

public class RentalDetailsResponse {
    private boolean  success;
    private String  message;
    private RentalObj rentalObj ;

    public RentalObj getRentalObj() {
        return rentalObj;
    }

    public void setRentalObj(RentalObj rentalObj) {
        this.rentalObj = rentalObj;
    }

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
