package com.example.graduationproject.ui.orders.orderMap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SharedElementCallback
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.locationServices.GoogleDirectionAPI
import com.example.graduationproject.components.locationServices.LocationService
import com.example.graduationproject.components.locationServices.LocationService.Coordinates2D
import com.example.graduationproject.components.locationServices.ResponseLocationCallback
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.model.DirectionResponses
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import kotlin.concurrent.thread

class MapViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var activity: Activity

    private lateinit var client: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    fun fetchDistance(
        origin: String,
        destination: String,
        callback: (String) -> Unit
    ) {
        GoogleDirectionAPI.fetchDistance(
            origin,
            destination,
            context
        ) {
            callback(it)
        }
    }

    fun cancelCourierOrder(
        orderNumber: String,
        courierId: String
    ) {
        FirebaseRDBService.executor.discardCourierOrder(
            orderNumber = orderNumber,
            courierId = courierId
        )
        SharedResources.executor.clearOrder()
    }

    fun fetchDuration(
        originLocation: String,
        destinationLocation: String,
        callback : (String) -> Unit
    ) {
        GoogleDirectionAPI.fetchDuration(
            originLocation,
            destinationLocation,
            context
        ) {
            callback(it)
        }
    }

    fun fetchRoute(
        originLocation: String,
        destinationLocation: String,
        callback: (Response<DirectionResponses>) -> Unit
    ) {
        GoogleDirectionAPI.fetchRoute(
            originLocation,
            destinationLocation,
            context
        ) {
            callback(it)
        }
    }

    fun fetchLastUserLocation(
        callback: (Coordinates2D) -> Unit
    ) {
        val locationService = LocationService(
            context,
            activity,
        )

        with(locationService) {

            fetchLocation(object : ResponseLocationCallback {
                override fun onSuccessLocationCallback(location: Coordinates2D) {
                    callback(location)
                }

                override fun onFailureLocationCallback() {

                }
            })
        }
    }

    fun fetchOriginAndDestinationFromOrder(
        callback: (OrderPlaces) -> Unit
    ) {
        val currentOrder = SharedResources.executor.getOrder()!!

        val originLocation = "${currentOrder.latFrom},${currentOrder.longFrom}"
        val destinationLocation = "${currentOrder.latTo},${currentOrder.longTo}"

        callback(
            OrderPlaces(
                originLocation,
                destinationLocation
            )
        )
    }

    fun configureLocationListener(
        callback: (LatLng) -> Unit
    ) {
        thread {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) { }

            val locationRequest = LocationRequest.create().apply {
                interval = 5000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        FirebaseRDBService.executor.updateCourierLocation(
                            SharedResources.executor.getCourier()!!.login.toString(),
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                        callback(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                }
            }

            client = FusedLocationProviderClient(context)
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    fun clearLocationUpdater() {
        client.removeLocationUpdates(locationCallback)
    }

    override fun onCleared() {
        super.onCleared()
        client.removeLocationUpdates(locationCallback)
    }

}

data class OrderPlaces(
    var originLocation: String,
    var destinationLocation: String
)