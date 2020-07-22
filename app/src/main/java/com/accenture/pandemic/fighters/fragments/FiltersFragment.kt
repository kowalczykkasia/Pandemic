package com.accenture.pandemic.fighters.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.viewModels.FiltersViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.filters_fragment.view.*
import javax.inject.Inject

class FiltersFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<FiltersViewModel> { factory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filters_fragment, container, false).apply {
            ltShow.forEach { it.setOnClickListener { view -> clickButton(view as Button) } }
            ltAdded.forEach { it.setOnClickListener { view -> clickButton(view as Button) } }
            setDefaultSelectedButtons(ltAdded, ltShow)

            btnFromDate.setOnClickListener {
                if ((it as Button).currentTextColor != Color.WHITE) {
                    viewModel.setFilters(
                        getClickedChildIndex(ltShow),
                        getClickedChildIndex(ltAdded)
                    )
                    findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToDatePickerFragment())
                }
                else{
                    clickButton(it)
                }
            }
            viewModel.selectedDate.observe(viewLifecycleOwner, Observer {
                btnFromDate.text = "${getString(R.string.from)} $it"
            })

            ivBack.setOnClickListener {
                viewModel.setFilters(
                    getClickedChildIndex(ltShow),
                    getClickedChildIndex(ltAdded)
                )
                findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToMapFragment())
            }
        }
    }

    private fun getClickedChildIndex(viewGroup: ViewGroup) =
        viewGroup.children.indexOfFirst { (it as Button).currentTextColor == Color.WHITE }


    private fun setDefaultSelectedButtons(ltAdded: ViewGroup, ltShow: ViewGroup) {
        viewModel.addedButtonIndex.value?.let {
            clickButton(ltAdded.getChildAt(it) as Button)
        }
        viewModel.showButtonIndex.value?.let {
            clickButton(ltShow.getChildAt(it) as Button)
        }
    }

    private fun clickButton(clickedButton: Button) {
        if (clickedButton.currentTextColor == Color.WHITE) {
            clickedButton.setBackgroundResource(R.drawable.basic_button_shape)
            clickedButton.setTextColor(Color.BLACK)
        } else {
            (clickedButton.parent as ViewGroup).forEach {
                val btn = it as Button
                btn.setBackgroundResource(R.drawable.basic_button_shape)
                btn.setTextColor(Color.BLACK)
            }
            clickedButton.setBackgroundResource(R.drawable.notifications_button_shape)
            clickedButton.setTextColor(Color.WHITE)
        }
    }
}
