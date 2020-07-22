package com.accenture.pandemic.fighters.viewModels

import android.content.Context
import android.location.Geocoder
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.utils.MyGeocoder
import com.accenture.pandemic.fighters.utils.toEditable
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class EditVirusLocationViewModel @Inject constructor(private val myGeocoder: MyGeocoder) : ViewModel() {
    val virusLocation = MutableLiveData<LatLng>()
    val searchedLocation = MutableLiveData<LatLng>()


    fun setAddressTextView(address: String, context: Context): String? =
        myGeocoder.getAddressFromTextDescription(address)?.let {
            searchedLocation.postValue(
                LatLng(
                    it.latitude,
                    it.longitude
                )
            )
            myGeocoder.getTextAddressFromLocation(context, LatLng(it.latitude, it.longitude))
        }

    fun setAddressEditView(context: Context, latLng: LatLng): Editable? =
        myGeocoder.getTextAddressFromLocation(context, latLng)?.toEditable()?: context.getString(
            R.string.unknownLocation
        ).toEditable()
}




