package com.example.graduationproject.ui.couriers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.locationServices.GoogleDirectionAPI
import com.example.graduationproject.components.mapUtils.getMarkerIcon
import com.example.graduationproject.components.mapUtils.zoom
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.ui.details.CourierDetailsDialog
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

    private lateinit var distance: String
    private lateinit var time: String

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

        configureDetailButton()

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    private fun configureDetailButton() {
        detailsButton.setOnClickListener {
            GoogleDirectionAPI.fetchDistance(
                origin = "${currentCourier.currentLat},${currentCourier.currentLong}",
                destination =  "${processingOrder!!.latTo!!},${processingOrder.longTo!!}",
                requireContext()
            ) { courierDistance ->

                val passedDistanceValue = distance.toInt() - courierDistance.toInt()
                var passedDistance = "Кур'єр не на маршруті"

                if (passedDistanceValue.toString().first() != '-') {
                    passedDistance = passedDistanceValue.toString()
                }

                    val bottomSheetDialog =
                        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

                    bottomSheetDialog.setContentView(
                        CourierDetailsDialog.Builder(requireContext())
                            .setCourierItemView(currentCourier)
                            .setOrderNumber(processingOrder!!.orderNumber.toString())
                            .setPlace(processingOrder!!.shopName.toString())
                            .setDistance(distance)
                            .setPassedDistance(passedDistance)
                            .setTime(time)
                            .setFrom(processingOrder!!.placeFrom.toString())
                            .setTo(processingOrder!!.placeTo.toString())
                            .setProducts("${processingOrder!!.product1}, ${processingOrder!!.product2}, ${processingOrder!!.product3}")
                            .build()
                            .getPopUpDialogView()
                    )
                bottomSheetDialog.show()
            }
        }
    }

    private fun initView() {
        mapView = requireView().findViewById(R.id.mainMap)
        detailsButton = requireView().findViewById(R.id.btnShowDetails)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()

        map = googleMap

        val userPosition =
            LatLng(currentCourier.currentLat!!.toDouble(), currentCourier.currentLong!!.toDouble())

        FirebaseRDBService.executor.fetchCurrentOrder(currentCourier.order!!.toString()) { order ->

                map?.let {
                    it.setOnMarkerClickListener(this)

                    order?.let {
                        processingOrder = order
                    }

                    locationFromLatLng =
                        LatLng(order!!.latFrom!!.toDouble(), order.longFrom!!.toDouble())
                    locationToLatLng =
                        LatLng(order!!.latTo!!.toDouble(), order!!.longTo!!.toDouble())

                    locationFromString = order.placeFrom.toString()
                    locationToString = order.placeTo.toString()

                    locationToLatLngString = "${order!!.latFrom!!},${order.longFrom!!}"
                    locationFromLatLngString = "${order!!.latTo!!},${order!!.longTo!!}"

                    it.addMarker(
                        MarkerOptions()
                            .position(userPosition)
                            .title("${currentCourier.name} ${currentCourier.lastName}")
                            .icon(getMarkerIcon("#00FF00"))
                    )

                    it.addMarker(
                        MarkerOptions()
                            .position(locationFromLatLng)
                            .title(locationFromString)
                    )

                    it.addMarker(
                        MarkerOptions()
                            .position(locationToLatLng)
                            .title(locationToString)
                            .icon(getMarkerIcon("#A058A7"))
                    )

                    GoogleDirectionAPI.fetchRoute(
                        locationFromLatLngString,
                        locationToLatLngString,
                        requireContext()
                    ) {
                        GoogleDirectionAPI.drawPolyline(it, map!!)
                    }

                    GoogleDirectionAPI.fetchDistance(
                        locationFromLatLngString,
                        locationToLatLngString,
                        requireContext()
                    ) {
                        distance = it
                    }

                    GoogleDirectionAPI.fetchDuration(
                        locationFromLatLngString,
                        locationToLatLngString,
                        requireContext()
                    ) {
                        time = it
                    }

                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, zoom));
                }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return true
    }
}
