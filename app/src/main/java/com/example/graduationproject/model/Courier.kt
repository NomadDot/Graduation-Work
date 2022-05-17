package com.example.graduationproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Courier(
    var name: String? = null,
    var lastName: String? = null,
    var login: String? = null,
    var password: String? = null,
    var rate: String? = null,
    var age: String? = null,
    var imageUrl: String? = null,
    var order: String? = null,
    var currentLat: String? = null,
    var currentLong: String? = null,
    var phoneNumber: String? = null
) : Parcelable