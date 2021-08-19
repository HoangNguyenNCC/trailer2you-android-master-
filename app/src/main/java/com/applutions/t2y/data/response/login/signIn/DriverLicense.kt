package com.applutions.t2y.data.response.login.signIn

data class DriverLicense(
    val expiry: String,
    val card: String,
    val state: String
) {
    override fun toString(): String {
        return "DriverLicense(expiry='$expiry', card='$card', state='$state')"
    }
}