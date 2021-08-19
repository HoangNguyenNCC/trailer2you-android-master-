package com.applutions.t2y.data.response.login.signIn

import com.applutions.t2y.data.items.PhotoObj


data class UserObj(
        val __v: Int,
        val _id: String,
        val address: Address,
        val photo: PhotoObj,
        val createdAt: String,
        val dob: String,
        val driverLicense: DriverLicense,
        val creditCard: CreditCard,
        val email: String,
        val mobile: String,
        val name: String,
        val updatedAt: String,
        val isVerified: Boolean,
        val isMobileVerified: Boolean
)