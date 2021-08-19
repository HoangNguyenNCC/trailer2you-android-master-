package com.applutions.t2y.ui.notifications.response;

import java.util.ArrayList;

public class NotificationResponse {
    private boolean  success;
    private String  message;
    private ArrayList<RemindersItem> remindersList=new ArrayList<>();

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

    public ArrayList<RemindersItem> getRemindersList() {
        return remindersList;
    }

    public void setRemindersList(ArrayList<RemindersItem> remindersList) {
        this.remindersList = remindersList;
    }
}
