package com.applutions.t2y.data.response

data class RescheduleRequestBody(
        val rentalId: String,
        val revisionId: String,
        val start: String,
        val end:String
)