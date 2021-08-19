package com.applutions.t2y.data.response.login.signUp

data class SignUpAddressModel(
        val country:String,
        val text: String,
        val pincode:String,
        val coordinates: List<Double>
)