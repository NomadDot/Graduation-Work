package com.example.graduationproject.ui.intro

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FetchUserCallback
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants
import com.example.graduationproject.core.Constants.Companion.CURRENT_COURIER
import com.example.graduationproject.core.Constants.Companion.CURRENT_ORDER
import com.example.graduationproject.core.Constants.Companion.IS_APP_LAUNCH_ONCE
import com.example.graduationproject.core.Constants.Companion.IS_USER_AUTHENTICATED
import com.example.graduationproject.core.Constants.Companion.USER_LOGIN
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.model.UserType
import com.example.graduationproject.model.UserType.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class IntroFragment : Fragment() {

    private lateinit var preferences: SharedPreferences

    private lateinit var viewModel: IntroViewModel

    private lateinit var btnAuth: Button
    private lateinit var btnRegister: Button
    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText

    private lateinit var login: String

    private lateinit var dialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.intro_fragment, container, false)
    }

    private fun initView() {
        btnAuth = requireView().findViewById(R.id.btnAuth)
        btnRegister = requireView().findViewById(R.id.btnRegister)
        etLogin = requireView().findViewById(R.id.etLogin)
        etPassword = requireView().findViewById(R.id.etPassword)

        preferences = requireContext().getSharedPreferences(Constants.USER_DATA_STORAGE, Context.MODE_PRIVATE)
        test()
    }

    private fun configureRegisterButton() {
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_registerCourierFragment)
        }
    }

    private fun isUserAuthorized(): Boolean {
        login = preferences.getString(USER_LOGIN, null).toString()
        return preferences.getBoolean(IS_USER_AUTHENTICATED, false)
    }

    private fun setUserCredential(login: String) {
        preferences.edit()
            .putString(USER_LOGIN, login)
            .putBoolean(IS_USER_AUTHENTICATED, true)
            .apply()
    }

    private fun configureAuthButton() {
        btnAuth.setOnClickListener {
            dialog = ProgressDialog.show(requireContext(), "Authorization", "Loading ...")

            FirebaseRDBService.executor.authenticateUser(
                etLogin.text.toString(),
                object : FetchUserCallback {
                    override fun onSuccessResponse(courier: Courier) {
                        configureFlowWithUserInfo(courier)
                    }

                    override fun onFailureResponse() {
                         Toast.makeText(
                                requireContext(),
                        "Error. Something went wrong ",
                        Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()
                    }
                }
            )
        }
    }

    private fun configureFlowWithUserInfo(courier: Courier) {

        viewModel.authUserWithInputData(
            etLogin.text.toString(),
            etPassword.text.toString(),
            object : AuthResponse {
                @SuppressLint("CommitPrefEdits")
                override fun onSuccess(userType: UserType, order: Order?, courier: Courier?) {

                    Toast.makeText(
                        requireContext(),
                        "You've been successfully authorized.",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()

                    when (userType) {
                        ADMIN -> {
                             setUserCredential(courier!!.login.toString())

                            val currentCourier = bundleOf(CURRENT_COURIER to courier)
                            findNavController().navigate(
                                R.id.action_introFragment_to_adminFlowFragment,
                                currentCourier
                            )
                        }

                        COURIER_WITH_NO_ORDER -> {
                            setUserCredential(courier!!.login.toString())

                            findNavController().navigate(R.id.courierFlowFragment)
                        }

                        COURIER_WITH_ORDER -> {
                            setUserCredential(courier!!.login.toString())

                            val bundleObject = bundleOf(
                                Constants.CURRENT_ORDER to order,
                                CURRENT_COURIER to courier
                            )
                            findNavController().navigate(R.id.courierFlowFragment, bundleObject)
                        }
                    }
                }

                override fun onFailure(error: String) {
                    Toast.makeText(
                        requireContext(),
                        "Error. Password or Login is not correct or courier is not exist ",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                }
            }
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[IntroViewModel::class.java]

        initView()

        configureAuthButton()
        configureRegisterButton()

        requireActivity().requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE),
            0
        )

            if (isUserAuthorized()) {
                val dialogAuth = ProgressDialog.show(requireContext(), "Аутетифікація...", null)

                viewModel.fetchUser(login) { userType ->
                    dialogAuth.dismiss()
                    when(userType) {
                        ADMIN -> {
                            val bundleCourier = bundleOf(CURRENT_COURIER to SharedResources.executor.getCourier())
                            findNavController().navigate(R.id.adminFlowFragment, bundleCourier)
                        }
                        COURIER_WITH_ORDER -> {
                            val bundleOfCourierAndOrder = bundleOf(
                                CURRENT_COURIER to SharedResources.executor.getCourier(),
                                CURRENT_ORDER to SharedResources.executor.getOrder()
                            )
                            findNavController().navigate(R.id.courierFlowFragment, bundleOfCourierAndOrder)
                        }
                        COURIER_WITH_NO_ORDER -> {
                            val bundleCourier = bundleOf(CURRENT_COURIER to SharedResources.executor.getCourier())
                            findNavController().navigate(R.id.courierFlowFragment)
                        }
                    }
                }
            }
    }

    private fun test() {
        etLogin.setText("rmnvlshn123")
        etPassword.setText("123")
    }
}