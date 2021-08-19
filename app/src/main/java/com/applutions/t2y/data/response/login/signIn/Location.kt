package com.applutions.t2y.data.response.login.signIn

import android.os.Parcelable



data class Location(
    val _id: String,
    val coordinates: List<Double>,
    val text: String,
    val type: String,
    val pincode : Number
)