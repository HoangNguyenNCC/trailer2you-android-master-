package com.applutions.t2y.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RatingBar;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SharedPrefs {

    private static SharedPrefs instance = null;
    private SharedPreferences prefs;

    private static final String APP_PREFS = "app_prefs";

    private static final String AUTH_TOKEN = "auth_token";

    private static final String LONGITUDE = "Longitude";
    private static final String LATITUDE = "Latitude";
    private static final String START_DATE = "StartDate";
    private static final String END_DATE = "EndDate";
    private static final String START_TIME = "StartTime";
    private static final String END_TIME = "EndTime";
    private static final String ITEM_ID = "ItemID";
    private static final String LOCATION = "Location";
    private static final String TRAILER_TYPE = "TypeFilter";
    private static final String UPSELL_ITEMS= "UpsellItems";
    private static final String POSTAL = "PostalCode";
    private static final String RATINGS = "Ratings";


    public static SharedPrefs getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefs(context);
        }
        return instance;
    }

    private SharedPrefs(Context context) {
        prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public String getAuthToken() {
        return prefs.getString(AUTH_TOKEN, "");
    }

    public void setAuthToken(@NonNull String token) {
        Log.v("SharedPrefs", token);
        prefs.edit()
                .putString(AUTH_TOKEN, token)
                .apply();
    }

    public void setLong(String longitude){
        prefs.edit()
                .putString(LONGITUDE,longitude)
                .apply();
    }
    public void setLat(String latitude){
        prefs.edit()
                .putString(LATITUDE,latitude)
                .apply();
    }
    public void setLocation(String location){
        prefs.edit()
                .putString(LOCATION,location)
                .apply();
    }

    public void setPostalCode(String postalCode){
        prefs.edit()
                .putString(POSTAL, postalCode)
                .apply();
    }

    public void setRatings(){
        prefs.edit()
                .putString(RATINGS, "Do not show")
                .apply();
    }

    public void resetRatings(){
        prefs.edit()
                .putString(RATINGS, "Show Ratings")
                .apply();
    }

    public String getRatings(){
        return prefs.getString(RATINGS, "Show Ratings");
    }


    public void setStartDate(String startDate) {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        df.setTimeZone(TimeZone.getDefault());
//        Date date = df.parse(startDate);
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String UTC = df.format(date);
        prefs.edit()
                .putString(START_DATE,startDate)
                .apply();
    }
    public void setEndDate(String endDate) {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        df.setTimeZone(TimeZone.getDefault());
//        Date date = df.parse(endDate);
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String UTC = df.format(date);
        prefs.edit()
                .putString(END_DATE,endDate)
                .apply();
    }

    public void setStartTime(String startTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getDefault());
        Date date = df.parse(startTime);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String UTC = df.format(date);
        prefs.edit()
                .putString(START_TIME,UTC)
                .apply();
    }
    public void setEndTime(String endTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getDefault());
        Date date = df.parse(endTime);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String UTC = df.format(date);
        prefs.edit()
                .putString(END_TIME,UTC)
                .apply();
    }

    public void setStartTimeUF(String startTime){
        prefs.edit()
                .putString(START_TIME,startTime)
                .apply();
    }

    public void setEndTimeUF(String endTime){
        prefs.edit()
                .putString(END_TIME,endTime)
                .apply();
    }

    public void setItemId(String itemId){
        prefs.edit()
                .putString(ITEM_ID, itemId)
                .apply();
    }

    public void setTrailerType(String trailerType){
        prefs.edit()
                .putString(TRAILER_TYPE, trailerType)
                .apply();
    }

    public void setUpsellItems(String upsellItems){
        prefs.edit()
                .putString(UPSELL_ITEMS, upsellItems)
                .apply();
    }

    public void clearData(){
        prefs.edit()
                .putString(LONGITUDE, "")
                .putString(LATITUDE, "")
                .putString(LOCATION, "")
                .putString(POSTAL, "")
                .putString(START_TIME, "")
                .putString(START_DATE, "")
                .putString(END_TIME, "")
                .putString(END_DATE, "")
                .putString(ITEM_ID, "")
                .putString(TRAILER_TYPE, "")
                .putString(UPSELL_ITEMS,"")
                .apply();
    }

    public String getLong(){
        return prefs.getString(LONGITUDE, "");
    }

    public String getLat(){
        return prefs.getString(LATITUDE, "");
    }

    public String getStartDate(){
        return prefs.getString(START_DATE, "");
    }

    public String getEndDate(){
        return prefs.getString(END_DATE, "");
    }

    public String getStartTime(){
        return prefs.getString(START_TIME, "");
    }

    public String getEndTime(){
        return prefs.getString(END_TIME, "");
    }

    public String getFormattedStartTime() throws ParseException {
        String UTC = prefs.getString(START_TIME,"");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = df.parse(UTC);
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

    public String getFormattedEndTime() throws ParseException {
        String UTC = prefs.getString(END_TIME,"");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = df.parse(UTC);
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

//    public String getFormattedStartDate() throws ParseException {
//        String UTC = prefs.getString(START_DATE,"");
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date date = df.parse(UTC);
//        df.setTimeZone(TimeZone.getDefault());
//        return df.format(date);
//    }
//
//    public String getFormattedEndDate() throws ParseException {
//        String UTC = prefs.getString(END_DATE,"");
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date date = df.parse(UTC);
//        df.setTimeZone(TimeZone.getDefault());
//        return df.format(date);
//    }

    public String getItemId() {
        return prefs.getString(ITEM_ID, "");
    }

    public String getLocation(){
        return prefs.getString(LOCATION,"");
    }

    public String getTrailerType(){
        return prefs.getString(TRAILER_TYPE, "");
    }

    public String getUpsellItems(){
        return prefs.getString(UPSELL_ITEMS, "");
    }

    public String getPostalCode(){
        return prefs.getString(POSTAL,"");
    }
}
