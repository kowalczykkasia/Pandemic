package com.accenture.pandemic.fighters.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.accenture.pandemic.fighters.R
import com.google.android.gms.maps.model.LatLng


fun LatLng.distanceTo(latlng: LatLng): Int {
    val location1 = Location(LocationManager.GPS_PROVIDER);
    location1.latitude = latlng.latitude
    location1.longitude = latlng.longitude

    val location2 = Location(LocationManager.GPS_PROVIDER);
    location2.latitude = this.latitude
    location2.longitude = this.longitude

    return location1.distanceTo(location2).toInt()
}

fun Address.getShortAddress(context: Context): String {
    val near = context.resources.getString(R.string.near)

    return if (this.locality != null) {
        if (this.thoroughfare != null) {
            if (this.subThoroughfare != null) {
                "${this.thoroughfare} ${this.subThoroughfare}, ${this.locality}"
            } else "$near ${this.thoroughfare}, ${this.locality}"
        } else "$near ${this.locality}"

    } else context.resources.getString(R.string.unknownLocation)
}

fun Address.getRoadWithNumber(context: Context): String {
    return if (this.thoroughfare == null) {
        if (this.locality == null)
            context.resources.getString(R.string.unknownLocation)
        else "${context.resources.getString(R.string.near)} ${ this.locality}"
    } else "${this.thoroughfare} ${this.subThoroughfare}"
}





