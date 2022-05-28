package com.example.graduationproject.ui.flows.admin_flow

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.locationServices.GoogleDirectionAPI
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants
import com.example.graduationproject.core.Constants.Companion.ORDER_STATUS_COMPLETED
import com.example.graduationproject.model.Order

class AdminMapViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context

    fun fetchCompletedOrders(
        callback: (CustomExpandableListAdapter) -> Unit
    ) {
        FirebaseRDBService.executor.fetchAllOrders { allOrders ->
            val completedOrders = ArrayList<Order>()

            for (order in allOrders) {
                if(order.status == ORDER_STATUS_COMPLETED) {
                    completedOrders.add(order)
                }
            }

            fetchDistance(completedOrders) {
                val orderNumbers = createOrderNumberList(it)
                val orders = createExpandedInfoList(it)

                val orderHashMap = bindOrdersToOrdersNumber(
                    orderNumbers,
                    orders
                )

                val expandableListAdapter = CustomExpandableListAdapter(
                    SharedResources.executor.getContext()!!,
                    orderNumbers,
                    orderHashMap
                )

                callback(expandableListAdapter)
            }
        }
    }

    private fun fetchDistance(
        orderList: ArrayList<Order>,
        callback: (ArrayList<Order>) -> Unit
    ) {
        val orderListWithDistance = ArrayList<Order>()
        for(i in orderList.indices) {
            val order = orderList[i]

            val origin = "${orderList[i].latFrom},${orderList[i].longFrom}"
            val destination = "${orderList[i].latTo},${orderList[i].longTo}"

            GoogleDirectionAPI.fetchDistance(
               origin,
                destination,
                SharedResources.executor.getContext()!!
            ) {
                order.distance = it
                orderListWithDistance.add(order)
            }
        }
        callback(orderList)
    }

    private fun createOrderNumberList(orderList: ArrayList<Order>): ArrayList<String> {
        val orderNumberList = ArrayList<String>()

        for(index in orderList.indices) {
            orderNumberList.add(orderList[index].orderNumber!!)
        }
        return orderNumberList
    }

    private fun createExpandedInfoList(orderList: ArrayList<Order>): ArrayList<Order> {
        val answerList = ArrayList<Order>()

        for(index in orderList.indices) {
            answerList.add(orderList[index])
        }
        return answerList
    }

    private fun bindOrdersToOrdersNumber(
        orderList: ArrayList<String>,
        answersList: ArrayList<Order>
    ): HashMap<String, Order> {
        val answerHashMap = HashMap<String, Order>()

        for(index in orderList.indices) {
            answerHashMap.put(
                orderList[index],
                answersList[index]
            )
        }
        return answerHashMap
    }
}