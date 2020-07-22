package com.accenture.pandemic.fighters.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.accenture.pandemic.fighters.Models.DEFAULT_ZOOM
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.utils.*
import com.accenture.pandemic.fighters.viewModels.EditVirusLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_virus_location_fragment.view.*
import kotlinx.android.synthetic.main.map_fragment.*
import javax.inject.Inject


class EditVirusLocationFragment : DaggerFragment(), OnMapReadyCallback {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<EditVirusLocationViewModel> { factory }
    private val args: EditVirusLocationFragmentArgs by navArgs()
    private var map: GoogleMap? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mvMap.onCreate(savedInstanceState)
        mvMap.onResume()
        mvMap.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_virus_location_fragment, container, false).apply {
            args.virusLocation?.let {
                etAddress.text = viewModel.setAddressEditView(context,it)
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnConfirmButton.setOnClickListener {
                findNavController().navigate(
                    EditVirusLocationFragmentDirections.actionEditVirusLocationFragmentToReportVirusFragment(
                        viewModel.virusLocation.value?:args.virusLocation, args.nearbyLocations
                    )
                )
            }

            btnSearch.setOnClickListener {
                if (etAddress.text.toString() != "") {
                    tvAddress.text = viewModel.setAddressTextView(etAddress.text.toString(),context)
                    handleVisibleWhileSearching(tvAddress, btnConfirmButton)
                }
                if (tvAddress.text.toString() == "") {
                    Toast.makeText(context, getString(R.string.no_location), Toast.LENGTH_SHORT)
                        .show()
                    btnConfirmButton.visibility = View.GONE
                    tvAddress.visibility = View.GONE
                } else hideKeyboard()
            }

            tvAddress.setOnClickListener {
                etAddress.text = tvAddress.text.toString().toEditable()
                handleVisibleWhileSearching(btnConfirmButton, tvAddress)
                viewModel.virusLocation.postValue(viewModel.searchedLocation.value)
            }

            with(viewModel) {
                virusLocation.observe(viewLifecycleOwner, Observer { virusLocation ->
                    btnConfirmButton.visibility = View.VISIBLE
                    map?.let { googleMap ->
                        googleMap.clear()

                        googleMap.addMarker(
                            MarkerOptions().position(virusLocation).icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.virus_location)
                            )
                        )
                        if (!googleMap.projection.visibleRegion.latLngBounds.contains(virusLocation)) {
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    virusLocation
                                    , DEFAULT_ZOOM
                                )
                            )
                        }
                    }
                    etAddress.text =
                        viewModel.setAddressEditView(
                            context, LatLng(
                                virusLocation.latitude,
                                virusLocation.longitude
                            )
                        )
                })
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {gMap->
            gMap.setOnMapLongClickListener {
                viewModel.virusLocation.postValue(it)
            }
            gMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    args.virusLocation
                    , DEFAULT_ZOOM
                )
            )
            if(context?.getColor(R.color.colorWhite) != Color.WHITE)
                googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))

            args.virusLocation?.let{
                gMap.addMarker(
                    MarkerOptions().position(it).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.virus_location)
                    )
                )
            }
            map = gMap
        }
    }

    private fun handleVisibleWhileSearching(view1: View, view2: View) {
        view1.visibility = View.VISIBLE
        view2.visibility = View.GONE
    }
}
