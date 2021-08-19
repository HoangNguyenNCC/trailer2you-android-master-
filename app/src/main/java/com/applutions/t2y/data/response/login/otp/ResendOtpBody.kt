package com.applutions.t2y.data.response.login.otp

data class ResendOtpBody(
        val mobile: String,
        val country: String
        //val testMode: Boolean = false
)