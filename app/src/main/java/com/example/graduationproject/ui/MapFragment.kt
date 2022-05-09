package com.example.graduationproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.graduationproject.R
import com.example.graduationproject.components.location_utils.LocationService
import com.example.graduationproject.components.location_utils.ResponseLocationCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var viewModel: MapViewModel

    private var map: GoogleMap? = null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        initView()

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    private fun initView() {
        mapView = requireView().findViewById(R.id.mainMap)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()
        map = googleMap


        map?.let {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            it.isMyLocationEnabled = true
        }

        LocationService(
            requireContext(),
            requireActivity()
        ).fetchLocation(object : ResponseLocationCallback {
            override fun onSuccessLocationCallback(lastLocation: LocationService.Coordinates2D) {
                val currentLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                map?.let {
                    it.addMarker(MarkerOptions().position(currentLocation).title("Current location"))
                    it.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                    it.animateCamera(CameraUpdateFactory.newLatLng(currentLocation))
                    val list = useGeocoder(lastLocation)

                    list?.get(0)?.let { it1 -> Log.i("MyLocationtag", it1.thoroughfare) }

                    Log.i("MyLocationtag", getLocationFromAddress("Забрідь, вул. Миру, 17").toString())
                    val testLocation = getLocationFromAddress("Zabrid")
                    it.addMarker(MarkerOptions().position(testLocation!!).title("Test location"))
                }
            }

            override fun onFailureLocationCallback() {}
        })
    }

    fun useGeocoder(location: LocationService.Coordinates2D): MutableList<Address>? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
    }

    fun getLocationFromAddress(strAddress: String?): LatLng? {
        val coder = Geocoder(requireContext())
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            location.latitude
            location.longitude
            p1 = LatLng(
                (location.latitude * 1E6),
                (location.longitude * 1E6)
            )
            return p1
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}