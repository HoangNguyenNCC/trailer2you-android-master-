package com.applutions.t2y.data.response.login.signIn

data class CreditCard(
        val _id: String,
        val code: Int,
        val expiryDate: String,
        val name: String,
        val number: String
)