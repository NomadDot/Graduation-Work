package com.example.graduationproject.ui.orders.orderMap

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SharedElementCallback
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.locationServices.GoogleDirectionAPI
import com.example.graduationproject.components.locationServices.LocationService
import com.example.graduationproject.components.locationServices.LocationService.Coordinates2D
import com.example.graduationproject.components.locationServices.ResponseLocationCallback
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.model.DirectionResponses
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Response

class MapViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var activity: Activity

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
}

data class OrderPlaces(
    var originLocation: String,
    var destinationLocation: String
)