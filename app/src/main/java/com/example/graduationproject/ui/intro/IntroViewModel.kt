package com.example.graduationproject.ui.intro

import androidx.lifecycle.ViewModel
import com.example.graduationproject.components.FirebaseRDBService.FetchUserCallback
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.model.UserType
import com.example.graduationproject.model.UserType.*

class IntroViewModel : ViewModel() {

    fun authUserWithInputData(
        login: String,
        password: String,
        callback: AuthResponse
    ) {
        FirebaseRDBService.executor.authenticateUser(
            login,
            object : FetchUserCallback {
                override fun onSuccessResponse(courier: Courier) {
                    configureFlowWithUserInputData(password, courier) { userType ->
                        when (userType) {
                            USER_DOES_NOT_EXIST -> callback.onFailure("Помилка. Такого користувача не існує")

                            ADMIN -> {
                                SharedResources.executor.setCourier(courier)

                                callback.onSuccess(ADMIN, null, courier)
                            }

                            COURIER_WITH_ORDER -> {
                                FirebaseRDBService.executor.fetchCurrentOrder(courier.order!!) {
                                    SharedResources.executor.setOrder(it!!)
                                    SharedResources.executor.setCourier(courier)

                                    callback.onSuccess(COURIER_WITH_ORDER,order = it, courier = courier)
                                }
                            }

                            COURIER_WITH_NO_ORDER -> {
                                SharedResources.executor.setCourier(courier)

                                callback.onSuccess(COURIER_WITH_NO_ORDER, null, courier)
                            }
                        }
                    }
                }

                override fun onFailureResponse() {
                    callback.onFailure("Error. Turn on internet connection")
                }
            }
        )
    }

    private fun configureFlowWithUserInputData(password: String, courier: Courier, callback: (UserType) -> Unit) {
        if (password == courier.password) {
            if (courier.password == Constants.ADMIN_PASSWORD) {
                callback(ADMIN)
            } else {
                if (courier.order == "null") {
                    callback(COURIER_WITH_NO_ORDER)
                } else {
                    callback(COURIER_WITH_ORDER)
                }
            }
        } else {
            callback(USER_DOES_NOT_EXIST)
        }
    }

    fun fetchUser(
        login: String,
        callback : (UserType) -> Unit
    ) {
        FirebaseRDBService.executor.fetchCurrentCourier(login) {
            when {
                it!!.password == Constants.ADMIN_PASSWORD -> {
                    SharedResources.executor.setCourier(it)
                    callback(ADMIN)
                }

                it.order == "null" -> {
                    SharedResources.executor.setCourier(it)
                    callback(COURIER_WITH_NO_ORDER)
                }

                it.order != "null" -> {
                    FirebaseRDBService.executor.fetchCurrentOrder(it.order.toString()) { order ->
                        SharedResources.executor.setOrder(order!!)
                        SharedResources.executor.setCourier(it)

                        callback(COURIER_WITH_ORDER)
                    }
                }
            }
        }
    }
}

interface AuthResponse {
    fun onSuccess(type: UserType, order: Order?, courier: Courier?)
    fun onFailure(error: String)
}