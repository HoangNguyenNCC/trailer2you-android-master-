package com.applutions.t2y.data.response.login.signIn


data class LoginResponse(
        val dataObj: DataObj,
        val message: String,
        var success: Boolean = false
)