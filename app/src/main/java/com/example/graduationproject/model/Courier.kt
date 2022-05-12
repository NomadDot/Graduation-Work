package com.example.graduationproject.model

data class Courier(
    var name: String? = null,
    var lastName: String? = null,
    var login: String? = null,
    var password: String? = null,
    var rate: String? = null,
    var age: String? = null,
    var imageUrl: String? = null,
    var order: String? = null,
    var lat: String? = null,
    var long: String? = null
) {
    data class Location(
        var lat: String? = null,
        var long: String? = null,
    )
}