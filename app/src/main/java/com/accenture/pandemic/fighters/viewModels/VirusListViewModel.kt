package com.accenture.pandemic.fighters.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.accenture.pandemic.fighters.Models.Fields
import com.accenture.pandemic.fighters.utils.MyGeocoder
import com.accenture.pandemic.fighters.utils.distanceTo
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class VirusListViewModel  @Inject constructor(
    val myGeocoder: MyGeocoder
): ViewModel() {
    val nearbyLocations = MutableLiveData<List<Fields>>()
    val currentLocation = MutableLiveData<LatLng>()

    fun initValues(currentLocation : LatLng?,nearbyLocations: Array<Fields>?){
        this.nearbyLocations.value = nearbyLocations?.toList()
        this.currentLocation.value = currentLocation
    }

    fun sortByDate(){
        nearbyLocations.value?.let {
            nearbyLocations.postValue(it.sortedByDescending { it.timeStamp.stringValue.toInt() })
        }
    }

    fun sortByLocation(){
        nearbyLocations.value?.let {nearLocations->
            currentLocation.value?.let {currentLocation ->
                nearbyLocations.postValue(nearLocations.sortedBy{
                    LatLng(
                        it.latitude.doubleValue,
                        it.longitude.doubleValue
                    ).distanceTo(currentLocation)
                })
            }
        }
    }
}
