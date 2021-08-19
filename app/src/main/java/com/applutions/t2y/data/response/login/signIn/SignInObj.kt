package com.applutions.t2y.data.response.login.signIn

data class SignInObj(
        val email: String,
        val password: String,
        val fcmDeviceToken: String
)