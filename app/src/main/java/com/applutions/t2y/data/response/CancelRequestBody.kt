package com.applutions.t2y.data.response

data class CancelRequestBody(
        val rentalId: String,
        val revisionId: String,
        val NOPAYMENT: Boolean
)