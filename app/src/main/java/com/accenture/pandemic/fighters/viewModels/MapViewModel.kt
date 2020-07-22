package com.accenture.pandemic.fighters.viewModels

import android.app.Activity
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accenture.pandemic.fighters.Models.DEFAULT_RADIUS
import com.accenture.pandemic.fighters.Models.Fields
import com.accenture.pandemic.fighters.Models.FilterButton
import com.accenture.pandemic.fighters.Models.Filters
import com.accenture.pandemic.fighters.repository.Repository
import com.accenture.pandemic.fighters.repository.local.getTimeFilter
import com.accenture.pandemic.fighters.repository.local.getTimeFilterButton
import com.accenture.pandemic.fighters.repository.local.getTypeFilter
import com.accenture.pandemic.fighters.utils.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import javax.inject.Inject


class MapViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences,
    val myGeocoder: MyGeocoder,
    private val firebaseAuthentication: FirebaseAuthentication
) :
    ViewModel() {
    val currentLocation = MutableLiveData<LatLng>()
    val nearbyLocations = MutableLiveData<List<Fields>>()
    val radius = MutableLiveData<Int>()
    val addedLocations: MutableList<LatLng> = mutableListOf()
    val allLocations = MutableLiveData<List<Fields>>()

    init {
        viewModelScope.launch {
            try {
                val locations = repository.getLocationList()
                val locationList = mutableListOf<Fields>()
                locations.documents.forEach { locationList.add(it.fields) }
                allLocations.value = locationList

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        radius.value = DEFAULT_RADIUS
    }

    fun signOut(fragment: Fragment, activity: Activity) =
        firebaseAuthentication.signOut(fragment, activity)


    fun searchNearbyLocations(centerOfMap: LatLng) {
        allLocations.value?.let {
            val locations =
                filterLocations(
                    Filters(
                        sharedPreferences.getTypeFilter().name,
                        getTimestampFromTimeFilter(sharedPreferences.getTimeFilterButton().name)
                    ),
                    it
                )
            nearbyLocations.postValue(locations.filter { fields ->
                LatLng(
                    fields.latitude.doubleValue,
                    fields.longitude.doubleValue
                ).distanceTo(centerOfMap) <= radius.value ?: DEFAULT_RADIUS
            })
        }
    }

    private fun getTimestampFromTimeFilter(timeFilterButton: String): Int =
        when (timeFilterButton) {
            FilterButton.H24.name -> (System.currentTimeMillis() / 1000 - DAY_IN_SECONDS).toInt()
            FilterButton.WEEKS2.name -> (System.currentTimeMillis() / 1000 - TWO_WEEKS_IN_SECONDS).toInt()
            FilterButton.NONE.name -> 0
            else -> getTimeStampFromDate(sharedPreferences.getTimeFilter())
        }


    private fun filterLocations(filters: Filters, locations: List<Fields>): List<Fields> {
        val filteredByDate = filterByDate(filters.timeStampFrom, locations)
        if (filters.type != FilterButton.ALL.name)
            return filterByType(filters.type, filteredByDate)
        return filteredByDate
    }

    private fun filterByDate(from: Int, locations: List<Fields>): List<Fields> =
        locations.filter { it.timeStamp.stringValue.toInt() >= from }

    private fun filterByType(type: String, locations: List<Fields>): List<Fields> =
        locations.filter {
            when (type) {
                FilterButton.CONFIRMED.name -> !it.selfReported.booleanValue
                else -> it.selfReported.booleanValue
            }
        }

    fun updateRadius(diameter: Int) {
        radius.postValue((diameter / 2) / 100 * 100)
    }
}

