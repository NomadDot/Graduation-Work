package com.example.graduationproject.components.mapUtils

import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun getMarkerIcon(color: String?): BitmapDescriptor? {
    val hsv = FloatArray(3)
    Color.colorToHSV(Color.parseColor(color), hsv)
    return BitmapDescriptorFactory.defaultMarker(hsv[0])
}

val zoom = 16.0f