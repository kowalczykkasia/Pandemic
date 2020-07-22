package com.accenture.pandemic.fighters.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.adapters.ReportListAdapter
import com.accenture.pandemic.fighters.viewModels.VirusListViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.virus_list_fragment.view.*
import javax.inject.Inject

class VirusListFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val args: VirusListFragmentArgs by navArgs()
    private val reportListAdapter by lazy {ReportListAdapter(viewModel.myGeocoder)}
    private val viewModel by viewModels<VirusListViewModel> { factory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.virus_list_fragment, container, false).apply {

        ivBack.setOnClickListener {
            findNavController().navigate(VirusListFragmentDirections.actionVirusListFragmentToMapFragment())
        }

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(VirusListFragmentDirections.actionVirusListFragmentToMapFragment())
                }
            })

        btnClosestLocation.setOnClickListener {
            handleButtonsColors(btnClosestLocation, btnRecentDate)
            viewModel.sortByLocation()
        }
        btnRecentDate.setOnClickListener {
            handleButtonsColors(btnRecentDate, btnClosestLocation)
            viewModel.sortByDate()
        }

        rvLocationList.adapter = reportListAdapter

        with(viewModel) {
            this.initValues(args.currentLocation, args.nearbyLocations)
            nearbyLocations.observe(viewLifecycleOwner, Observer {
                reportListAdapter.submitList(it)
            })
        }
        btnAdd.setOnClickListener {
            findNavController().navigate(
                VirusListFragmentDirections.actionVirusListFragmentToReportVirusFragment(
                    viewModel.currentLocation.value,viewModel.nearbyLocations.value?.toTypedArray()
                )
            )
        }



    }

    private fun handleButtonsColors(btn1: Button, btn2: Button) {
        btn1.setBackgroundResource(R.drawable.notifications_button_shape)
        btn1.setTextColor(Color.WHITE)
        btn2.setBackgroundResource(R.drawable.basic_button_shape)
        btn2.setTextColor(resources.getColor(R.color.darkGrey))
    }
}
