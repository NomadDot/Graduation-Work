package com.example.graduationproject.model

data class Order(
    var shopName: String? = null,
    var productList: ArrayList<String>? = null,
    var latitude: String? = null,
    var longitude: String? = null
)
