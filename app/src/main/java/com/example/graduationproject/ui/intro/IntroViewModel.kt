package com.example.graduationproject.ui.intro

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FetchUserCallback
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.model.UserType

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
                    configureFlowWithUserInfo(password, courier) { userType ->
                        when (userType) {
                            UserType.USER_DOES_NOT_EXIST -> callback.onFailure("Помилка. Такого користувача не існує")

                            UserType.ADMIN -> callback.onSuccess(null, courier, true)

                            UserType.COURIER_WITH_ORDER -> {
                                FirebaseRDBService.executor.fetchCurrentOrder(courier.order!!) {
                                    callback.onSuccess(order = it, courier = courier, false)
                                }
                            }

                            UserType.COURIER_WITH_NO_ORDER -> callback.onSuccess(null, courier, false)
                        }
                    }
                }

                override fun onFailureResponse() {
                }
            }
        )
    }

    private fun configureFlowWithUserInfo(password: String, courier: Courier, callback: (UserType) -> Unit) {
        if (password == courier.password) {
            if (courier.password == Constants.ADMIN_PASSWORD) {
                callback(UserType.ADMIN)
            } else {
                if (courier.order == "null") {
                    callback(UserType.COURIER_WITH_NO_ORDER)
                } else {
                    callback(UserType.COURIER_WITH_ORDER)
                }
            }
        } else {
            callback(UserType.USER_DOES_NOT_EXIST)
        }
    }
}

interface AuthResponse {
    fun onSuccess(order: Order?, courier: Courier, isAdmin: Boolean)
    fun onFailure(error: String)
}