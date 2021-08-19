package com.applutions.t2y.data.response.booking

import com.applutions.t2y.data.items.BookingUpsell

data class BookingResponse (
        val booking: BookingData,
        val stripeClientSecret: String
)

data class BookingData (
        val trailerId: String,
        val upsellItemIds: Array<String>,
        val startDate: String,
        val endDate: String,
        val customerId: String,
        val isPickup: Boolean,
        val trailerCharges: Float,
        val upsellCharges: Float,
        val cancellationCharges: Float,
        val taxes: Float
)

data class Booking (
        val trailerId: String,
        val upsellItems: ArrayList<BookingUpsell>,
        val startDate: String,
        val endDate: String,
        val isPickup: Number = 0,
        val customerLocation: BookingLocation,
        val doChargeDLR: Boolean
)

data class BookingLocation (
        val text: String,
        val pincode: String,
        val coordinates: Array<Double>
)