package com.accenture.pandemic.fighters.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class MyGeocoder @Inject constructor(
    private val geocoder: Geocoder
) {

    fun getTextAddressFromLocation(context: Context, location: LatLng): String? =
        geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        )?.firstOrNull()?.getShortAddress(context)

    fun getAddressFromTextDescription( placeDescription: String): Address? =
        geocoder.getFromLocationName(placeDescription, 1)
            .firstOrNull()

    fun getRoadWithNumberFromLocation( context: Context, location: LatLng): String? =
        geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        )?.firstOrNull()?.getRoadWithNumber(context)
}