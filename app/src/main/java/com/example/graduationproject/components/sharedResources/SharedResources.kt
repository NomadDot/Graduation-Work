package com.example.graduationproject.components.sharedResources

import android.content.Context
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order

class SharedResources() {

    companion object {
        var executor =  SharedResources()
    }

    private var currentCourier: Courier? = null
    private var currentOrder: Order? = null
    private var context: Context? = null

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

    fun getContext(): Context? {
        return context
    }

    fun setContext(context: Context) {
        this.context = context
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
        context = null
    }
}