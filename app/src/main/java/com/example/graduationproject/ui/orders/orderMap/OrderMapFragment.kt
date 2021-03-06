package com.example.graduationproject.ui.orders.orderMap

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.locationServices.*
import com.example.graduationproject.components.mapUtils.getMarkerIcon
import com.example.graduationproject.components.mapUtils.zoom
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.ui.details.PopUpDialog
import com.google.android.gms.location.*
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

    private lateinit var lastLocation: LatLng

    private lateinit var locationService: LocationService
    private lateinit var currentLocation: String

    private lateinit var originLocation: String
    private lateinit var destinationLocation: String

    private lateinit var distance: String

    private lateinit var time: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        viewModel.context = requireContext()
        viewModel.activity = requireActivity()

        initView()

        currentOrder = SharedResources.executor.getOrder()!!
        currentCourier = SharedResources.executor.getCourier()!!

        setOnBackButtonPressListener()

        configureShowDetailsButton()

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        configurePlaces()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearLocationUpdater()
    }

    private fun setOnBackButtonPressListener() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    private fun initView() {
        mapView = requireView().findViewById(R.id.mainMap)
    }

    private fun configureMainAttributes() {
        viewModel.fetchRoute(
            originLocation,
            destinationLocation
        ) { route ->
            GoogleDirectionAPI.drawPolyline(route, map!!)
        }

        viewModel.fetchDistance(
            originLocation,
            destinationLocation
        ) {
            distance = it
        }

        viewModel.fetchDuration(
            originLocation,
            destinationLocation,
        ) {
            time = it
        }
    }

    private fun configureAndShowBottomSheetDialog(passedDistance: String) {
        val dialogDetails = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        try {
            currentOrder.orderNumber?.let {
                dialogDetails.setContentView(
                    PopUpDialog.Builder(requireContext())
                        .setOrderNumber(it)
                        .setFrom(currentOrder.placeFrom!!)
                        .setTo(currentOrder.placeTo!!)
                        .setPlace(currentOrder.shopName!!)
                        .setProducts(
                            "${currentOrder.product1},${currentOrder.product2},${currentOrder.product3}"
                        )
                        .setDistance(distance)
                        .setPassedDistance(passedDistance)
                        .setTime(time)
                        .onButtonClick {
                            viewModel.cancelCourierOrder(
                                orderNumber = currentOrder.orderNumber!!,
                                courierId = currentCourier.login.toString()
                            )
                            dialogDetails.dismiss()
                            findNavController().navigate(R.id.orderListFragment2)
                        }
                        .build()
                        .getPopUpDialogView())
            }
            dialogDetails.show()
        } catch (e: Exception) { }
    }

    private fun calculateCourierDistance(courierDistance: String): String {
        val passedDistanceValue = distance.toInt() - courierDistance.toInt()

        var passedDistance = "??????'???? ???? ???? ????????????????"

        if(passedDistanceValue.toString().first() != '-') {
            passedDistance = passedDistanceValue.toString()
        }

        return passedDistance
    }

    private fun configureShowDetailsButton() {
        btnShowDetails.setOnClickListener {

            val dialog = ProgressDialog.show(requireContext(), "???????????????????? ?????????????????? ????????????????", "")

            viewModel.fetchDistance(
                "${lastLocation.latitude},${lastLocation .longitude}",
                destinationLocation,
            ) { courierDistance ->
                dialog.dismiss()

                val passedDistance = calculateCourierDistance(courierDistance)

                configureAndShowBottomSheetDialog(passedDistance)
            }
        }
    }

    @SuppressLint("VisibleForTests")
    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()
        map = googleMap

        val dialog = ProgressDialog.show(requireContext(), "???????????????????????? ?????????????? ...", null)

        viewModel.configureLocationListener() { currentLocation ->

                dialog.dismiss()

                lastLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

                val originLocationLatLong =
                    LatLng(currentOrder!!.latFrom!!.toDouble(), currentOrder!!.longFrom!!.toDouble())
                val destinationLocationLatLong =
                    LatLng(currentOrder!!.latTo!!.toDouble(), currentOrder!!.longTo!!.toDouble())

                map?.let {

                    it.addMarker(
                        MarkerOptions()
                            .position(currentLocation)
                            .title(SharedResources.executor.getCourier()!!.name)
                            .icon(getMarkerIcon("#00FF00")))


                    it.addMarker(
                        MarkerOptions()
                            .position(originLocationLatLong)
                            .title(currentOrder.placeFrom)
                    )

                    it.addMarker(
                        MarkerOptions()
                            .position(destinationLocationLatLong)
                            .title(currentOrder.placeTo)
                            .icon(getMarkerIcon("#A058A7"))
                    )

                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom));
                }
        }
    }

    private fun configurePlaces() {
        viewModel.fetchOriginAndDestinationFromOrder { orderPlaces ->
            originLocation = orderPlaces.originLocation
            destinationLocation = orderPlaces.destinationLocation

            configureMainAttributes()
        }
    }
}