package com.applutions.t2y.data.response.login.forgotPassword

data class ResetPasswordLinkBody (
        val email: String,
        val platform: String = "android"
)