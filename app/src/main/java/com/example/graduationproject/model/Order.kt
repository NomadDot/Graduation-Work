package com.example.graduationproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    var orderNumber: String? = null,
    var placeFrom: String? = null,
    var placeTo: String? = null,
    var latFrom: String? = null,
    var longFrom: String? = null,
    var latTo: String? = null,
    var longTo: String? = null,
    var product1: String? = null,
    var product2: String? = null,
    var product3: String? = null,
    var shopName: String? = null,
    var status: String? = null,
    var distance: String? = null,
    var courier: String? = null
) : Parcelable
