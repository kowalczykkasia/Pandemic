package com.accenture.pandemic.fighters.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.accenture.pandemic.fighters.Models.FilterButton
import com.accenture.pandemic.fighters.repository.local.*
import javax.inject.Inject

class FiltersViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    val addedButtonIndex = MutableLiveData<Int>()
    val showButtonIndex = MutableLiveData<Int>()
    val selectedDate = MutableLiveData<String>()

    init {
        when (sharedPreferences.getTimeFilterButton()) {
            FilterButton.PICKER -> {
                addedButtonIndex.value = 0
            }
            FilterButton.H24 -> {
                addedButtonIndex.value = 1
            }
            FilterButton.WEEKS2 -> {
                addedButtonIndex.value = 2
            }
        }
        when (sharedPreferences.getTypeFilter()) {
            FilterButton.ALL -> {
                showButtonIndex.value = 0
            }
            FilterButton.CONFIRMED -> {
                showButtonIndex.value = 1
            }
            FilterButton.SELFREPORTED -> {
                showButtonIndex.value = 2
            }
        }
        selectedDate.value = sharedPreferences.getTimeFilter()
    }

    fun setFilters(typeButtonIndex: Int, timeButtonIndex: Int) {

        when (typeButtonIndex) {
            0 -> sharedPreferences.setTypeFilter(FilterButton.ALL)
            1 -> sharedPreferences.setTypeFilter(FilterButton.CONFIRMED)
            2 -> sharedPreferences.setTypeFilter(FilterButton.SELFREPORTED)
            else -> sharedPreferences.setTypeFilter(FilterButton.NONE)
        }

        when (timeButtonIndex) {
            0 -> {
                sharedPreferences.setTimeFilterButton(FilterButton.PICKER)
                selectedDate.value?.let { sharedPreferences.setTimeFilter(it) }
            }
            1 -> {
                sharedPreferences.setTimeFilterButton(FilterButton.H24)
                sharedPreferences.setTimeFilter(null)
            }
            2 -> {
                sharedPreferences.setTimeFilterButton(FilterButton.WEEKS2)
                sharedPreferences.setTimeFilter(null)
            }
            else -> {
                sharedPreferences.setTimeFilterButton(FilterButton.NONE)
                sharedPreferences.setTimeFilter(null)
            }
        }

    }
}
