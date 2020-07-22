package com.accenture.pandemic.fighters.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.accenture.pandemic.fighters.Models.*
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.adapters.CustomInfoWindowAdapter
import com.accenture.pandemic.fighters.utils.distanceTo
import com.accenture.pandemic.fighters.viewModels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.android.synthetic.main.map_fragment.view.*
import javax.inject.Inject


class MapFragment : DaggerFragment(), OnMapReadyCallback {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<MapViewModel> { factory }
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var map: GoogleMap? = null
    private var locationPermissionGranted = false
    private var locationFound = false


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getLocationPermission()
        mvMap.onCreate(savedInstanceState)
        mvMap.onResume()
        mvMap.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.map_fragment, container, false).apply {
            ivWorld.setOnClickListener {
                findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToVirusListFragment(
                        viewModel.nearbyLocations.value?.toTypedArray(),
                        viewModel.currentLocation.value
                    )
                )
            }

            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        findNavController().navigate(MapFragmentDirections.actionMapFragmentToLoginFragment())
                    }
                })
            ivBack.setOnClickListener {
                viewModel.signOut(this@MapFragment, activity as Activity)
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToLoginFragment())
            }
            ivFilters.setOnClickListener {
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToFiltersFragment())
            }
            btnReportVirus.setOnClickListener {
                findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToReportVirusFragment(
                        viewModel.currentLocation.value,
                        viewModel.nearbyLocations.value?.toTypedArray()
                    )
                )
            }

            ivProfile.setOnClickListener {
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToProfileFragment())
            }
            btnNotifications.setOnClickListener {
                findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToNotificationsFragment(
                        viewModel.currentLocation.value
                    )
                )
            }
            ivCurrentLocation.setOnClickListener {
                if (locationPermissionGranted && locationFound)
                    map?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            viewModel.currentLocation.value
                            , DEFAULT_ZOOM
                        )
                    )
                else Toast.makeText(context, getString(R.string.no_permission), Toast.LENGTH_SHORT)
                    .show()

            }

            with(viewModel) {
                nearbyLocations.observe(viewLifecycleOwner, Observer {
                    it.forEach { nearbyLocation ->
                        LatLng(
                            nearbyLocation.latitude.doubleValue,
                            nearbyLocation.longitude.doubleValue
                        ).let {
                            if (!addedLocations.contains(it)) {
                                map?.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            nearbyLocation.latitude.doubleValue,
                                            nearbyLocation.longitude.doubleValue
                                        )
                                    ).let { marker ->
                                        if (!nearbyLocation.selfReported.booleanValue || nearbyLocation.tested.stringValue == "Tested")
                                            marker.icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.confirmed)
                                            )
                                        else
                                            marker.icon(
                                                BitmapDescriptorFactory.fromResource(R.drawable.self_reported)
                                            )
                                    }
                                )?.tag = nearbyLocation
                                addedLocations.add(it)
                            }
                        }
                    }
                })

                currentLocation.observe(viewLifecycleOwner, Observer { currentLocation ->
                    map?.let {
                        it.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(currentLocation.latitude, currentLocation.longitude)
                                , DEFAULT_ZOOM
                            )
                        )
                        if (locationPermissionGranted && locationFound)
                            it.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        currentLocation.latitude,
                                        currentLocation.longitude
                                    )
                                ).icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.user_location)
                                )
                            )
                        allLocations.observe(viewLifecycleOwner, Observer {
                            searchNearbyLocations(currentLocation)
                        })
                    }
                })

                radius.observe(viewLifecycleOwner, Observer {
                    tvRadius.text =
                        "${getString(R.string.within)} ${it}${getString(R.string.radius)}"
                })
            }
        }

    }

    private fun getLocationPermission() {
        if (this.context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            getLastKnownLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    mFusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireContext())
                    getLastKnownLocation()
                } else setDefaultLocation()

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap

        map?.let {
            it.setOnCameraMoveListener {
                viewModel.updateRadius(it.projection.visibleRegion.farLeft.distanceTo(it.projection.visibleRegion.nearLeft))
                viewModel.searchNearbyLocations(it.cameraPosition.target)
            }
            if (context?.getColor(R.color.colorWhite) != Color.WHITE)
                googleMap?.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        R.raw.style_json
                    )
                )

            it.setInfoWindowAdapter(context?.let {
                CustomInfoWindowAdapter(
                    it,
                    viewModel.myGeocoder
                )
            }).toString()
            it.setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                false
            }
        }
    }

    private fun getLastKnownLocation() =
        mFusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    locationFound = true
                    viewModel.currentLocation.postValue(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                } else setDefaultLocation()
            }.addOnFailureListener {
                setDefaultLocation()
            }

    private fun setDefaultLocation() {
        locationFound = false
        Toast.makeText(context, "location not found", Toast.LENGTH_SHORT).show()
        viewModel.currentLocation.postValue(
            LatLng(
                WARSAW_LATITUDE,
                WARSAW_LONGITUDE
            )
        )

    }
}







