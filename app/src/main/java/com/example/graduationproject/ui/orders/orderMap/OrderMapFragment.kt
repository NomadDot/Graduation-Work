package com.example.graduationproject.ui.orders.orderMap

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.location_utils.*
import com.example.graduationproject.components.map_utils.getMarkerIcon
import com.example.graduationproject.components.map_utils.zoom
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.ui.couriers.DistanceType
import com.example.graduationproject.ui_components.PopUpDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.map_fragment.*

class OrderMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MapViewModel

    private var map: GoogleMap? = null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private lateinit var currentOrder: Order
    private lateinit var currentCourier: Courier

    private lateinit var locationFromLatLng: LatLng
    private lateinit var locationToLatLng: LatLng

    private lateinit var locationFromString: String
    private lateinit var locationToString: String

    private lateinit var locationFromLatLngString: String
    private lateinit var locationToLatLngString: String

    private lateinit var lastLocation: LatLng

    private lateinit var locationService: LocationService

    private lateinit var detailsButton: Button

    private lateinit var button: Button

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

        currentOrder = arguments?.getParcelable(Constants.CURRENT_ORDER)!!
        currentCourier = arguments?.getParcelable(Constants.CURRENT_COURIER)!!

        configureShowDetailsButton()

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    private fun initView() {
        mapView = requireView().findViewById(R.id.mainMap)
    }

    private fun configureShowDetailsButton() {
        btnShowDetails.setOnClickListener {
            val dialog = ProgressDialog.show(requireContext(), "Підрахунок пройденої відстані", "")
            getCurrentDistance { passedDistance ->
                dialog.dismiss()
                val dialogDetails = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

                dialogDetails.setContentView(
                    PopUpDialog.Builder(requireContext())
                        .setOrderNumber(currentOrder!!.orderNumber!!)
                        .setFrom(currentOrder!!.placeFrom!!)
                        .setTo(currentOrder!!.placeTo!!)
                        .setPlace(currentOrder!!.shopName!!)
                        .setProducts("${currentOrder!!.product1!!}|${currentOrder!!.product2!!}|${currentOrder!!.product3!!}")
                        .setDistance(getDistance(DistanceType.DISTANCE)[0].toInt().toString())
                        .setPassedDistance(passedDistance)
                        .onButtonClick {
                            FirebaseRDBService.executor.discardCourierOrder(courierId = currentCourier!!.login!!)
                            dialogDetails.dismiss()
                            val courierBundle = bundleOf(Constants.CURRENT_COURIER to currentCourier)
//                            findNavController().navigate(
//                                R.id.action_mapFragment_to_orderListFragment,
//                                courierBundle
//                            )
                        }
                        .build()
                        .getPopUpDialogView()
                )
                dialogDetails.show()
            }
        }
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

        locationService = LocationService(
            requireContext(),
            requireActivity()
        )

        locationService.fetchLocation(object : ResponseLocationCallback {
            override fun onSuccessLocationCallback(location: LocationService.Coordinates2D) {

                lastLocation = LatLng(location.latitude, location.longitude)

                val originLocationLatLong = LatLng(currentOrder!!.latFrom!!.toDouble(), currentOrder!!.longFrom!!.toDouble())
                val destinationLocationLatLong =  LatLng(currentOrder!!.latTo!!.toDouble(),currentOrder!!.longTo!!.toDouble())

                locationFromLatLng = LatLng(currentOrder!!.latFrom!!.toDouble(), currentOrder!!.longFrom!!.toDouble())
                locationToLatLng = LatLng(currentOrder!!.latTo!!.toDouble(), currentOrder!!.longTo!!.toDouble())

                locationFromString = currentOrder!!.placeFrom.toString()
                locationToString =  currentOrder!!.placeTo.toString()

                locationToLatLngString = "${currentOrder!!.latFrom!!},${currentOrder!!.longFrom!!}"
                locationFromLatLngString =  "${currentOrder!!.latTo!!},${currentOrder!!.longTo!!}"


                map?.let {
                    it.addMarker(
                        MarkerOptions()
                            .position(originLocationLatLong)
                            .title(currentOrder.placeFrom))

                    it.addMarker(
                        MarkerOptions()
                            .position(destinationLocationLatLong)
                            .title(currentOrder.placeTo)
                            .icon(getMarkerIcon("#A058A7")))


                    var originLocation = "${currentOrder!!.latFrom},${currentOrder!!.longFrom}"
                    var destinationLocation = "${currentOrder!!.latTo},${currentOrder!!.longTo}"


                    GoogleDirectionAPI.fetchRoute(
                        originLocation,
                        destinationLocation,
                        requireContext()) {
                        GoogleDirectionAPI.drawPolyline(it, map!!)
                    }

                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(originLocationLatLong, zoom));
                }
            }
            override fun onFailureLocationCallback() {}
        })
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

    private fun getCurrentDistance(callback: (String) -> Unit) {

        val result = FloatArray(1)

        locationService.fetchLocation(object : ResponseLocationCallback {
            override fun onSuccessLocationCallback(lastLocation: LocationService.Coordinates2D) {

                Location.distanceBetween(
                    lastLocation.latitude,
                    lastLocation.longitude,
                    currentOrder.latTo!!.toDouble(),
                    currentOrder.longTo!!.toDouble(),
                    result
                )

                result[0] = getDistance(DistanceType.DISTANCE)[0] - result[0]

                val passedDistance = result[0].toInt().toString()

                if(passedDistance.first() == '-') {
                    callback.invoke("Кур'єр не на маршруті")
                } else {
                    callback.invoke(passedDistance)
                }
            }

            override fun onFailureLocationCallback() {

            }
        })
    }

}