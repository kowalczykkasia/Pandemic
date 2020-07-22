package com.accenture.pandemic.fighters.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.accenture.pandemic.fighters.Models.FilterButton
import com.accenture.pandemic.fighters.repository.local.setTimeFilter
import com.accenture.pandemic.fighters.repository.local.setTimeFilterButton
import javax.inject.Inject

class DatePickerViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

   fun setTimeFilter(date: String){
       sharedPreferences.setTimeFilter(date)
       sharedPreferences.setTimeFilterButton(FilterButton.PICKER)
   }
}
