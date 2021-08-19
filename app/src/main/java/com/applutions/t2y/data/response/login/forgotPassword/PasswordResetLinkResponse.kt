package com.applutions.t2y.data.response.login.forgotPassword

data class PasswordResetLinkResponse(
        val success: Boolean,
        val message: String,
        val errorsList: Array<String> = arrayOf()
)