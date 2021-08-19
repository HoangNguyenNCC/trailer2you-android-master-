package com.applutions.t2y.data.response.trailer

import com.applutions.t2y.data.items.PhotoObj
import com.applutions.t2y.data.response.search.UpsellItem
import com.applutions.t2y.ui.notifications.response.RentalObj

data class RentalItemResponse (
        val success: String,
        val message: String,
        val trailerObj: TrailerObj,
        val errorsList: Array<String> = arrayOf()
)

data class TrailerObj (
    val _id : String,
    val features: Array<String>,
    val availability: Boolean,
    val isFeatured: Boolean,
    val isDeleted: Boolean,
    val capacity: String,
    val adminRentalItemId: String,
    val tare: String,
    val name: String,
    val size: String,
    val vin: String,
    val description: String,
    val age: Int,
    val type: String,
    val licenseeId: String,
    val createdAt: String,
    val updatedAt: String,
    val trailerRef: String,
    val photos: ArrayList<PhotoObj> = ArrayList(),
    val rating: Float,
    val rentalCharges: RentalCharges,
    val price: String,
    val dlrCharges: Double,
    val rentalsList: Array<RentalItem> = arrayOf(),
    val insured: Boolean,
    val serviced: Boolean,
    val totalCharges: RentalObj.TrailerCharges
)

data class RentalCharges (
  val pickUp: Array<ChargeItem> = arrayOf(),
  val door2Door: Array<ChargeItem> = arrayOf()
)

data class RentalItem (
        val invoiceId: String,
        val start: String,
        val end: String
)

data class ChargeItem (
        val duration: Int,
        val charges: Int
)