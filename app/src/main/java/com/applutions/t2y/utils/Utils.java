package com.applutions.t2y.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;

import com.applutions.t2y.R;
import com.google.android.material.snackbar.Snackbar;

public class Utils {

    public static String getFileNameFromUri(Context context, @NonNull Uri uri) {
        String uriString = uri.toString();
        String fileName = null;
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return fileName;
    }

    public static String getRealPathFromURI(Uri uri, @NonNull Context context) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        // Get the cursor
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        // Move to first row
        assert cursor != null;
        cursor.moveToFirst();
        //Get the column index of MediaStore.Images.Media.DATA
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        //Gets the String value in the column
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        return imgDecodableString;
    }

    public static Boolean validateEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static Snackbar getErrorSnackBar(Context context, View view, String errorMessage) {
        Snackbar snackbar = Snackbar.make(view, errorMessage ,Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(context.getResources().getColor(R.color.red_gradient_start));
        return snackbar;
    }

    public static Snackbar getSuccessSnackBar(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message ,Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(context.getResources().getColor(android.R.color.holo_green_light));
        return snackbar;
    }

}
