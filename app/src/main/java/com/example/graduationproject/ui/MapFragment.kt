package com.example.graduationproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.graduationproject.R
import com.example.graduationproject.components.location_utils.*
import com.example.graduationproject.model.DirectionResponses
import com.example.graduationproject.model.Order
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var viewModel: MapViewModel

    private var map: GoogleMap? = null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var testLocation: LatLng? = null
    private var currentOrder: Order? = null

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

        currentOrder = arguments?.getParcelable("CURRENT_ORDER")

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

        // to show current location
        LocationService(
            requireContext(),
            requireActivity()
        ).fetchLocation(object : ResponseLocationCallback {
            override fun onSuccessLocationCallback(lastLocation: LocationService.Coordinates2D) {

                val currentLocationLatLong = LatLng(currentOrder!!.latFrom!!.toDouble(), currentOrder!!.longFrom!!.toDouble())
                val destinationLocationLatLong =  LatLng(currentOrder!!.latTo!!.toDouble(),currentOrder!!.longTo!!.toDouble())

                map?.let {
                    it.addMarker(MarkerOptions().position(currentLocationLatLong).title("Place from"))
                    it.addMarker(MarkerOptions().position(destinationLocationLatLong).title(currentOrder!!.orderNumber))
                    it.moveCamera(CameraUpdateFactory.newLatLng(currentLocationLatLong))

                    var originLocation = "${currentOrder!!.latFrom},${currentOrder!!.longFrom}"
                    var destinationLocation = "${currentOrder!!.latTo},${currentOrder!!.longTo}"

                    GoogleDirectionAPI.fetchRoute(
                        originLocation,
                        destinationLocation,
                        requireContext()) {
                        drawPolyline(it)
                    }
                }
            }
            override fun onFailureLocationCallback() {}
        })
    }

    private fun drawPolyline(response: Response<DirectionResponses> ) {

        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points

        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
            .geodesic(true)
            .clickable(true)

        map?.let {
            it.setOnPolylineClickListener {
                // TODO: Distance of polyline
            }
            it.addPolyline(polyline)
        }

    }
}