package com.example.graduationproject.components.FirebaseRDBService

import android.util.Log
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRDBService() {

    private lateinit var database: FirebaseDatabase

    companion object {
        val executor: FirebaseRDBService = FirebaseRDBService()
    }

    fun configureFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
    }

    fun authenticateUser(key: String, callback: FetchUserCallback) {
        val couriers = database.getReference("couriers")

        couriers.child(key).get().addOnSuccessListener {
            if(it.exists()) {
                val courier = Courier(
                    name = it.child("name").value.toString(),
                    lastName = it.child("last_name").value.toString(),
                    password = it.child("password").value.toString(),
                    login = key,
                    rate = it.child("rate").toString(),
                    age = it.child("age").toString(),
                    lat = it.child("location").child("lat").toString()
                )
                callback.onSuccessResponse(courier)
            } else {
                callback.onFailureResponse()
            }
        }
    }

    fun fetchCouriers(callback: FetchCouriers)  {
        val arrayOfCouriers = ArrayList<Courier>()
        val couriersReference = database.getReference("couriers")
        couriersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (courierSnapshot in snapshot.children) {
                        if (courierSnapshot.hasChild("lastElement")) {
                            callback.onSuccessResponse(arrayOfCouriers)
                        } else {
                            val courier = courierSnapshot.getValue(Courier::class.java)
                            courier?.let {
                                arrayOfCouriers.add(courier)
                            }
                        }
                    }
                }
             }
             override fun onCancelled(error: DatabaseError) {}
         })
    }

    fun setCourierOrder(orderNumber: String, courierId: String) {
        database.getReference("couriers").child(courierId).child("order").setValue(orderNumber)
    }


    fun fetchAllOrders(callback: (ArrayList<Order>) -> Unit)  {
        val arrayOfOrders = ArrayList<Order>()
        val ordersReference = database.getReference("orders")
        ordersReference.addValueEventListener(object :  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        arrayOfOrders.add(order!!)
                        callback.invoke(arrayOfOrders)
                        Log.i("MyTag", order.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchCurrentOrder(orderNumber: String, callback: (Order?) -> Unit) {
        val ordersReference = database.getReference("orders")
        ordersReference.addValueEventListener(object :  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        if (orderSnapshot.key == orderNumber) {
                            val order = orderSnapshot.getValue(Order::class.java)
                            callback.invoke(order)
                            Log.i("MyTag", order.toString())
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

interface FetchCouriers {
    fun onSuccessResponse(arrayList: ArrayList<Courier>)
    fun onFailureResponse()
}

interface FetchUserCallback {
    fun onSuccessResponse(courier: Courier)
    fun onFailureResponse()
}