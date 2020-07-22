package com.accenture.pandemic.fighters.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.viewModels.DatePickerViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.date_picker_fragment.view.*
import javax.inject.Inject

class DatePickerFragment: DaggerFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<DatePickerViewModel> { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.date_picker_fragment, container, false).apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnOk.setOnClickListener {
                viewModel.setTimeFilter("${picker.dayOfMonth}.${picker.month + 1}.${picker.year}")
                findNavController().navigate(DatePickerFragmentDirections.actionDatePickerFragmentToFiltersFragment())
            }
        }
    }


}
