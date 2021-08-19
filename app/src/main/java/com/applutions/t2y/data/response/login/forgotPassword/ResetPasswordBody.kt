package com.applutions.t2y.data.response.login.forgotPassword

data class ResetPasswordBody (
        val token: String,
        val password: String
)