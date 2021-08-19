package com.applutions.t2y.data.response

import com.applutions.t2y.data.items.PhotoObj

data class LicenseeResponse(
        val success: Boolean,
        val message: String,
        val licenseeObj: Licensee
) {
    override fun toString(): String {
        return "LicenseeResponse(success=$success, message='$message', licenseeObj=$licenseeObj)"
    }
}

data class Licensee (
  val id: String,
  val name: String,
  val email: String,
  val mobile: String,
  val address: Address,
  val locations: Array<Address>,
  val proofOfIncorporationVerified: Boolean,
  val logo: PhotoObj,
  val ownerName: String,
  val ownerPhoto: PhotoObj,
  val rating: Integer,
  val workingDays: Array<String>,
  val workingHours: String,
  val rentalItems: Array<RentalItem>

) {
    override fun toString(): String {
        return "Licensee(id='$id', name='$name', email='$email', mobile='$mobile', address=$address, locations=${locations.contentToString()}, proofOfIncorporationVerified=$proofOfIncorporationVerified, logo='$logo', ownerName='$ownerName', ownerPhoto='$ownerPhoto', rating=$rating, workingDays=${workingDays.contentToString()}, workingHours='$workingHours', rentalItems=${rentalItems.contentToString()})"
    }
}

data class Address (
        val text: String,
        val pincode: String,
        val city: String,
        val state: String,
        val country: String,
        val coordinates: Array<Double>

) {
    override fun toString(): String {
        return "Address(text='$text', pincode='$pincode', city='$city', state='$state', country='$country', coordinates=${coordinates.contentToString()})"
    }
}

data class RentalItem(
        val id: String,
        val name: String,
        val type: String,
        val rentalItemType: String,
        val photo: ArrayList<PhotoObj>,
        val price: Price

) {
    override fun toString(): String {
        return "RentalItem(id='$id', name='$name', type='$type', rentalItemType='$rentalItemType', photo='$photo', price=$price)"
    }
}

data class Price(
        val pickUp: String,
        val door2Door: String

) {
    override fun toString(): String {
        return "Price(pickUp='$pickUp', door2Door='$door2Door')"
    }
}