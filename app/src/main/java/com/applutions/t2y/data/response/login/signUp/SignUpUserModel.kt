package com.applutions.t2y.data.response.login.signUp

import com.applutions.t2y.data.response.login.signIn.DriverLicense
import com.google.gson.JsonObject

data class SignUpUserModel(
        val email: String,
        val mobile: String,
        val name: String,
        val dob: String,
        val password: String,
        val address: JsonObject,
        var driverLicense: DriverLicense?
    )