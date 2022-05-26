package com.example.graduationproject.components.sharedResources

import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order

class SharedResources() {

    companion object {
        var executor =  SharedResources()
    }

    private var currentCourier: Courier? = null
    private var currentOrder: Order? = null

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

    fun clearOrder() {
        currentOrder = null
    }

    fun clearCourier() {
        currentCourier = null
    }

    fun clearConfig() {
        currentCourier = null
        currentOrder = null
    }
}