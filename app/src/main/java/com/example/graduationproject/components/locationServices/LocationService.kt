package com.example.graduationproject.components.locationServices

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task


class LocationService(
    val context: Context,
    val activity: Activity
) {

    data class Coordinates2D(
        var latitude: Double,
        var longitude: Double,
        var accuracy: Float
    )

    fun fetchLocation(callback: ResponseLocationCallback){
        if (isLocationEnabled(context)) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity)
            val task: Task<Location> = fusedLocationProviderClient.lastLocation

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }

            task.addOnSuccessListener {
                if (it != null) {
                    Log.i(
                        "Location Service",
                        "Location is found: Latitude:${it.latitude} / Longitude:${it.longitude} / Accuracy: ${it.accuracy}" )
                    callback.onSuccessLocationCallback(Coordinates2D(it.latitude, it.longitude, it.accuracy))
                } else {
                    Log.i("Location Service", "Location isn't found")
                }
            }

            task.addOnFailureListener {
                Log.i("Location Service", "Something went wrong")
                callback.onFailureLocationCallback()
            }

        } else {
            Log.i("Location Service", "Need to enable GPS or other services")
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}

interface ResponseLocationCallback {
    fun onSuccessLocationCallback(lastLocation: LocationService.Coordinates2D)
    fun onFailureLocationCallback()
}