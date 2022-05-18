package com.example.graduationproject.ui.couriers

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.location_utils.GoogleDirectionAPI
import com.example.graduationproject.components.map_utils.getMarkerIcon
import com.example.graduationproject.components.map_utils.zoom
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.ui_components.CourierDetailsDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog

class CourierMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var map: GoogleMap? = null
    lateinit var mapView: MapView

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private lateinit var currentCourier: Courier
    private lateinit var processingOrder: Order

    private lateinit var locationFromLatLng: LatLng
    private lateinit var locationToLatLng: LatLng

    private lateinit var locationFromString: String
    private lateinit var locationToString: String

    private lateinit var locationFromLatLngString: String
    private lateinit var locationToLatLngString: String

    private lateinit var detailsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        currentCourier = arguments?.getParcelable(Constants.CURRENT_COURIER)!!

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    private fun initView() {
        mapView = requireView().findViewById(R.id.mainMap)
        detailsButton = requireView().findViewById(R.id.btnShowDetails)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()

        map = googleMap

        val userPosition = LatLng(currentCourier.currentLat!!.toDouble(), currentCourier.currentLong!!.toDouble())

        FirebaseRDBService.executor.fetchCurrentOrder(currentCourier.order!!.toString()) {  order ->
            map?.let {
                it.setOnMarkerClickListener(this)

                order?.let {
                    processingOrder = order
                }

                detailsButton.setOnClickListener {
                    val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
                    bottomSheetDialog.setContentView(
                        CourierDetailsDialog.Builder(requireContext())
                            .setCourierItemView(currentCourier)
                            .setOrderNumber(order!!.orderNumber.toString())
                            .setPlace(order!!.shopName.toString())
                            .setDistance(getDistance(DistanceType.DISTANCE)[0].toInt().toString())
                            .setPassedDistance(getDistance(DistanceType.PASSED_DISTANCE)[0].toInt().toString())
                            .setFrom(order!!.placeFrom.toString())
                            .setTo(order!!.placeTo.toString())
                            .setProducts("${order!!.product1}, ${order!!.product2}, ${order!!.product3}")
                            .build()
                            .getPopUpDialogView()
                    )
                    bottomSheetDialog.show()
                }

                locationFromLatLng = LatLng(order!!.latFrom!!.toDouble(), order.longFrom!!.toDouble())
                locationToLatLng = LatLng(order!!.latTo!!.toDouble(), order!!.longTo!!.toDouble())

                locationFromString = order.placeFrom.toString()
                locationToString =  order.placeTo.toString()

                locationToLatLngString = "${order!!.latFrom!!},${order.longFrom!!}"
                locationFromLatLngString =  "${order!!.latTo!!},${order!!.longTo!!}"

                it.addMarker(
                    MarkerOptions()
                        .position(userPosition)
                        .title("${currentCourier.name} ${currentCourier.lastName}")
                        .icon(getMarkerIcon("#00FF00")))

                it.addMarker(
                    MarkerOptions()
                        .position(locationFromLatLng)
                        .title(locationFromString))

                it.addMarker(
                    MarkerOptions()
                        .position(locationToLatLng)
                        .title(locationToString)
                        .icon(getMarkerIcon("#A058A7")))

                GoogleDirectionAPI.fetchRoute(locationFromLatLngString, locationToLatLngString, requireContext()) {
                    GoogleDirectionAPI.drawPolyline(it, map!!)
                }

                it.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, zoom));
            }
        }
    }

    fun getDistance(type: DistanceType): FloatArray {
        val result = FloatArray(1)
        if(type == DistanceType.DISTANCE) {
            Location.distanceBetween(
                locationFromLatLng.latitude,
                locationFromLatLng.longitude,
                locationToLatLng.latitude,
                locationToLatLng.longitude,
                result
            )
        } else {
            Location.distanceBetween(
                currentCourier!!.currentLat!!.toDouble(),
                currentCourier!!.currentLong!!.toDouble(),
                locationToLatLng.latitude,
                locationToLatLng.longitude,
                result
            )
            result[0] = getDistance(DistanceType.DISTANCE)[0] - result[0]
        }

        return result
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return true
    }
}

enum class DistanceType(value: Boolean) {
    PASSED_DISTANCE(true),
    DISTANCE(false)
}