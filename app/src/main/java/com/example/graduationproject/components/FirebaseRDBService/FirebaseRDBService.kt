package com.example.graduationproject.components.FirebaseRDBService

import com.example.graduationproject.model.Courier
import com.google.firebase.database.FirebaseDatabase

class FirebaseRDBService() {

    private lateinit var database: FirebaseDatabase

    companion object {
        val executor: FirebaseRDBService = FirebaseRDBService()
    }

    fun configureFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
    }

    fun fetchCourierFromDatabase(key: String, callback : FetchUserCallback) {
        val data = database.getReference("couriers")
        data.child(key).get().addOnSuccessListener {
            if (it.exists()) {

            } else {
                callback.onFailureResponse()
            }
        }
    }

    fun authenticateUser(key: String, callback: FetchUserCallback) {
        val couriers = database.getReference("couriers")

        couriers.child(key).get().addOnSuccessListener {
            if(it.exists()) {
                val courier = Courier(
                    _name = it.child("name").value.toString(),
                    _lastName = it.child("last_name").value.toString(),
                    _password = it.child("password").value.toString(),
                    _login = key
                )
                callback.onSuccessResponse(courier)
            } else {
                callback.onFailureResponse()
            }
        }
    }
}

interface FetchUserCallback {
    fun onSuccessResponse(courier: Courier)
    fun onFailureResponse()
}