package com.applutions.t2y.data.response.login

data class ChangePasswordBody (
    val oldPassword: String,
    val newPassword: String
)