package com.applutions.t2y.data.response.login.otp

data class VerifyMobileBody(
        val mobile: String,
        val country: String,
        val otp: String
       // val testMode: Boolean = true
)