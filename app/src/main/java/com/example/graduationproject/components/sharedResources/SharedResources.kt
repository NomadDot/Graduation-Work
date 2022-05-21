package com.example.graduationproject.components.sharedResources

import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order

class SharedResources() {

    companion object {
        var executor =  SharedResources()
    }

    private lateinit var currentCourier: Courier
    private lateinit var currentOrder: Order

    fun getCourier(): Courier? {
        return currentCourier
    }

    fun setCourier(courier: Courier) {
        currentCourier = courier
    }

    fun getOrder(): Order? {
        return currentOrder
    }

    fun setOrder(order: Order) {
        currentOrder = order
    }
}